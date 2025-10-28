package ru.nikskul.tftp.api.session.factory;

import ru.nikskul.tftp.api.session.provider.TftpSessionProvider;
import ru.nikskul.tftp.api.session.shared.SimpleTftpSession;
import ru.nikskul.tftp.api.session.shared.TftpSession;
import ru.nikskul.tftp.converter.datagram.TftpToDatagramConverter;
import ru.nikskul.tftp.datagram.receiver.DatagramReceiver;

import java.net.InetSocketAddress;

public class SimpleTftpSessionFactory
    implements TftpSessionFactory {

    private final DatagramReceiver receiver;
    private final TftpSessionProvider sessionProvider;
    private final TftpToDatagramConverter toDatagramConverter;

    public SimpleTftpSessionFactory(
        DatagramReceiver receiver,
        TftpSessionProvider sessionProvider,
        TftpToDatagramConverter toDatagramConverter
    ) {
        this.receiver = receiver;
        this.sessionProvider = sessionProvider;
        this.toDatagramConverter = toDatagramConverter;
    }

    @Override
    public TftpSession newInstance(InetSocketAddress address) {
        return new SimpleTftpSession(
            sessionProvider,
            address,
            receiver,
            toDatagramConverter
        );
    }
}
