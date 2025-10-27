package ru.nikskul.tftp.handler;

import ru.nikskul.tftp.packet.ErrorTftpPacket;
import ru.nikskul.tftp.packet.TftpPacket;

public interface ErrorTftpHandlerChain extends TftpHandlerChain {
    @Override
    ErrorTftpHandlerChain getNext();

    @Override
    default boolean canHandle(TftpPacket packet) {
        return packet instanceof ErrorTftpPacket;
    };

    @Override
    boolean handle(TftpPacket packet);
}
