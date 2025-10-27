package ru.nikskul.tftp.resolver;

import ru.nikskul.tftp.packet.TftpPacket;

public interface TftpPacketResolver {

    void handle(TftpPacket packet);
}
