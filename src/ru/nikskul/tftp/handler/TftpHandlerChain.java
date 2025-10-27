package ru.nikskul.tftp.handler;

import ru.nikskul.tftp.packet.TftpPacket;

public interface TftpHandlerChain {

    TftpHandlerChain getNext();

    boolean canHandle(TftpPacket packet);

    boolean handle(TftpPacket packet);
}
