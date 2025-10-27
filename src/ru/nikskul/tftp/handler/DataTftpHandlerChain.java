package ru.nikskul.tftp.handler;

import ru.nikskul.tftp.packet.DataTftpPacket;
import ru.nikskul.tftp.packet.TftpPacket;

public interface DataTftpHandlerChain extends TftpHandlerChain {
    @Override
    DataTftpHandlerChain getNext();

    @Override
    default boolean canHandle(TftpPacket packet) {
        return packet instanceof DataTftpPacket;
    };

    @Override
    boolean handle(TftpPacket packet);
}
