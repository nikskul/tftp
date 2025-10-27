package ru.nikskul.tftp.api.session.factory;

import ru.nikskul.tftp.api.session.provider.TftpSessionProvider;
import ru.nikskul.tftp.api.session.shared.SimpleTftpSession;
import ru.nikskul.tftp.api.session.shared.TftpSession;
import ru.nikskul.tftp.datagram.receiver.DatagramReceiver;

import java.net.DatagramPacket;

public class SimpleTftpSessionFactory
    implements TftpSessionFactory {

    private final DatagramReceiver receiver;
    private final TftpSessionProvider sessionProvider;

    public SimpleTftpSessionFactory(
        DatagramReceiver receiver,
        TftpSessionProvider sessionProvider
    ) {
        this.receiver = receiver;
        this.sessionProvider = sessionProvider;
    }

    @Override
    public TftpSession newInstance(DatagramPacket packet) {
        return new SimpleTftpSession(sessionProvider, packet, receiver);
    }
}
