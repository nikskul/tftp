package ru.nikskul.tftp.handler;

import ru.nikskul.tftp.packet.RequestTftpPacket;
import ru.nikskul.tftp.packet.TftpPacket;

public interface WrqTftpHandlerChain extends TftpHandlerChain {
    @Override
    WrqTftpHandlerChain getNext();

    @Override
    default boolean canHandle(TftpPacket packet) {
        return packet instanceof RequestTftpPacket &&
            packet.getOpcode() == 2;
    };

    @Override
    boolean handle(TftpPacket packet);
}
