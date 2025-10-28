package ru.nikskul.tftp.api.session.factory;

import ru.nikskul.tftp.api.session.shared.TftpSessionStarter;

import java.net.InetSocketAddress;

public interface TftpSessionFactory {

    TftpSessionStarter newInstance(InetSocketAddress address);
}
