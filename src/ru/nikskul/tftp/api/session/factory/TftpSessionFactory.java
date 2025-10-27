package ru.nikskul.tftp.api.session.factory;

import ru.nikskul.tftp.api.session.shared.TftpSession;

import java.net.DatagramPacket;

public interface TftpSessionFactory {

    TftpSession newInstance(DatagramPacket packet);
}
