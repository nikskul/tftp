package ru.nikskul.tftp.api.session.shared;

import ru.nikskul.logger.SystemLogger;
import ru.nikskul.tftp.api.session.provider.TftpSessionProvider;
import ru.nikskul.tftp.datagram.receiver.DatagramReceiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleTftpSession
    implements TftpSession {

    public static final int RETRY_ATTEMPTS = 5;
    private static final int MAX_UDP_SIZE = 0xffff;
    public static final int TIMEOUT = Duration.ofSeconds(5).toMillisPart();

    private final AtomicBoolean active = new AtomicBoolean(true);

    private final TftpSessionProvider sessionProvider;
    private final DatagramPacket initialPacket;
    private final DatagramReceiver receiver;

    private int targetTid = -1; // TODO: if received packet not from session target TID, Error "5 Unknown transfer ID" should be sent.
    private int sourceTid = -1;
    private DatagramSocket socket;

    public SimpleTftpSession(
        TftpSessionProvider sessionProvider,
        DatagramPacket initialPacket,
        DatagramReceiver receiver
    ) {
        this.sessionProvider = sessionProvider;
        this.initialPacket = initialPacket;
        this.receiver = receiver;
    }

    @Override
    public void run() {
        InetSocketAddress socketAddress = new InetSocketAddress(
            initialPacket.getAddress().getHostAddress(),
            initialPacket.getPort()
        );
        try (var ignore = socket = new DatagramSocket()) {

            // initial configuration
            socket.connect(socketAddress);
            targetTid = socket.getPort();
            sourceTid = socket.getLocalPort();
            socket.setSoTimeout(TIMEOUT);

            SystemLogger.log(
                "Session [s:%d -> d:%d] started!",
                getClass(),
                sourceTid,
                targetTid
            );

            // register session and apply initial packet
            sessionProvider.addSession(this);
            receiver.receive(sourceTid, initialPacket);

            // start main loop
            DatagramPacket packet = new DatagramPacket(
                new byte[MAX_UDP_SIZE],
                MAX_UDP_SIZE
            );
            int cycle = 0;
            while (!Thread.interrupted() && active.get()) {
                try {
                    socket.receive(packet);
                } catch (SocketTimeoutException e) {
                    if (RETRY_ATTEMPTS < cycle) {
                        throw e;
                    }

                    SystemLogger.log("[%d] Retry #%d", getClass(), sourceTid, cycle);

                    cycle++;
                    receiver.receive(sourceTid, packet);
                    continue;
                }
                cycle = 0;
                receiver.receive(sourceTid, packet);
            }
        } catch (SocketException e) {
            if (active.get()) {
                SystemLogger.log(e, getClass());
            }
        } catch (IOException e) {
            SystemLogger.log(e, getClass());
        } finally {
            close();
            SystemLogger.log("Session closed!", getClass());
        }
    }

    @Override
    public void send(DatagramPacket packet) throws IOException {
        socket.send(packet);
    }

    @Override
    public void sendAndClose(DatagramPacket packet) throws IOException {
        try {
            send(packet);
        } finally {
            close();
        }
    }

    @Override
    public void close() {
        active.set(false);
        sessionProvider.removeSession(this);
        socket.close();
    }

    @Override
    public int getSourceTid() {
        return sourceTid;
    }

    @Override
    public int getTargetTid() {
        return targetTid;
    }
}
