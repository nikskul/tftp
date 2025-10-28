package ru.nikskul.tftp.api.server;

import ru.nikskul.logger.SystemLogger;
import ru.nikskul.tftp.api.session.factory.TftpSessionFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TftpServer
    implements Runnable {

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
        try (var server = new DatagramSocket(TFTP_PORT)) {
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
                server.receive(packet);

                try {
                    var session = sessionFactory.newInstance(
                        new InetSocketAddress(
                            packet.getAddress().getHostAddress(),
                            packet.getPort()
                        )
                    );
                    session.start().receive(packet);
                } catch (Exception e) {
                    SystemLogger.log("Fail to make session [%d]! ",
                        getClass(),
                        packet.getPort()
                    );
                    SystemLogger.log(e, getClass());
                }
            }
        } catch (IOException e) {
            SystemLogger.log(e, getClass());
        } finally {
            SystemLogger.log("Server shutdown!", getClass());
        }
    }
}
