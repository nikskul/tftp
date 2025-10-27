package ru.nikskul.tftp.handler;

import ru.nikskul.tftp.packet.AckTftpPacket;
import ru.nikskul.tftp.packet.TftpPacket;

public interface AckTftpHandlerChain extends TftpHandlerChain {
    @Override
    AckTftpHandlerChain getNext();

    @Override
    default boolean canHandle(TftpPacket packet) {
        return packet instanceof AckTftpPacket;
    };

    @Override
    boolean handle(TftpPacket packet);
}
