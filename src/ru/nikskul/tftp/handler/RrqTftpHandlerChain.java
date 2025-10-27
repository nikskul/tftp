package ru.nikskul.tftp.handler;

import ru.nikskul.tftp.packet.RequestTftpPacket;
import ru.nikskul.tftp.packet.TftpPacket;

public interface RrqTftpHandlerChain extends TftpHandlerChain {
    @Override
    RrqTftpHandlerChain getNext();

    @Override
    default boolean canHandle(TftpPacket packet) {
        return packet instanceof RequestTftpPacket &&
            packet.getOpcode() == 1;
    }

    @Override
    boolean handle(TftpPacket packet);
}
