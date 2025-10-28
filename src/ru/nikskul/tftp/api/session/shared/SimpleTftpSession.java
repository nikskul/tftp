package ru.nikskul.tftp.api.session.shared;

import ru.nikskul.logger.SystemLogger;
import ru.nikskul.tftp.api.session.provider.TftpSessionProvider;
import ru.nikskul.tftp.converter.datagram.TftpToDatagramConverter;
import ru.nikskul.tftp.datagram.receiver.DatagramReceiver;
import ru.nikskul.tftp.packet.impl.ErrorTftpPacketImpl;

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
    private final InetSocketAddress address;
    private final DatagramReceiver receiver;
    private final TftpToDatagramConverter toDatagramConverter;

    private volatile int targetTid = -1;
    private volatile int sourceTid = -1;
    private DatagramSocket socket;

    public SimpleTftpSession(
        TftpSessionProvider sessionProvider,
        InetSocketAddress address,
        DatagramReceiver receiver, TftpToDatagramConverter toDatagramConverter
    ) {
        this.sessionProvider = sessionProvider;
        this.address = address;
        this.receiver = receiver;
        this.toDatagramConverter = toDatagramConverter;
    }

    @Override
    public TftpSession start() {
        try {
            // initial configuration
            socket = new DatagramSocket();
            targetTid = address.getPort();
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

            // start main loop
            Thread.ofVirtual().start(this::loop);
        } catch (SocketException e) {
            if (active.get()) {
                SystemLogger.log(e, getClass());
            }
        }
        return this;
    }

    private void loop() {
        try {
            DatagramPacket packet = new DatagramPacket(
                new byte[MAX_UDP_SIZE],
                MAX_UDP_SIZE
            );
            int cycle = 0;
            while (!Thread.interrupted() && active.get()) {
                try {
                    socket.receive(packet);
                    validate(packet);
                    cycle = 0;
                    receive(packet);
                } catch (SocketTimeoutException e) {
                    cycle = retry(e, cycle);
                    receive(packet);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close();
            SystemLogger.log("Session closed!", getClass());
        }
    }

    private int retry(
        SocketTimeoutException e,
        int cycle
    ) throws SocketTimeoutException {
        if (RETRY_ATTEMPTS < cycle) {
            throw e;
        }

        SystemLogger.log(
            "[%d] Retry #%d",
            getClass(),
            sourceTid,
            cycle
        );

        return ++cycle;
    }

    private void validate(DatagramPacket packet) throws IOException {
        if (targetTid == 69) {
            targetTid = packet.getPort();
            SystemLogger.log(
                "Session [s:%d -> d:%d] changed!",
                getClass(),
                sourceTid,
                targetTid
            );
        } else if (targetTid != packet.getPort()) {
            var datagram = toDatagramConverter.convert(
                ErrorTftpPacketImpl.unknownTid(sourceTid)
            );
            sendAndClose(datagram);
        }
    }

    @Override
    public void receive(DatagramPacket packet) {
        waitSessionReady();
        receiver.receive(sourceTid, packet);
    }

    private void waitSessionReady() {
        while (sessionProvider.getSession(() -> sourceTid) == null) {
            Thread.onSpinWait();
        }
    }

    @Override
    public void send(DatagramPacket packet) throws IOException {
        waitSessionReady();
        if (packet.getAddress() == null) {
            packet.setAddress(address.getAddress());
            packet.setPort(targetTid);
        }
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

}
