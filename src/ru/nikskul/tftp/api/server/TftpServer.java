package ru.nikskul.tftp.api.server;

import ru.nikskul.logger.SystemLogger;
import ru.nikskul.tftp.api.session.factory.TftpSessionFactory;
import ru.nikskul.tftp.api.session.shared.TftpSession;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class TftpServer
    implements Runnable {

    // Todo: ThreadPool

    private static final int MAX_UDP_SIZE = 0xffff;

    private static final int TFTP_PORT = 69;

    private final TftpSessionFactory sessionFactory;

    public TftpServer(
        TftpSessionFactory sessionFactory
    ) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void run() {
        Thread.Builder.OfVirtual sessionVirtualThread = Thread.ofVirtual()
            .name(TftpSession.class.getSimpleName(), 1);
        try (var socket = new DatagramSocket(TFTP_PORT)) {
            SystemLogger.log(
                "TFTP Server started on port %d",
                getClass(),
                TFTP_PORT
            );
            while (!Thread.interrupted()) {
                DatagramPacket packet = new DatagramPacket(
                    new byte[MAX_UDP_SIZE],
                    MAX_UDP_SIZE
                );
                socket.receive(packet);

                try {
                    TftpSession session = sessionFactory.newInstance(packet);
                    sessionVirtualThread.start(session);
                } catch (Exception e) {
                    SystemLogger.log("Fail to make session [%d]! ",
                        getClass(),
                        packet.getPort()
                    );
                }
            }
        } catch (IOException e) {
            SystemLogger.log(e, getClass());
        } finally {
            SystemLogger.log("Server shutdown!", getClass());
        }
    }
}
