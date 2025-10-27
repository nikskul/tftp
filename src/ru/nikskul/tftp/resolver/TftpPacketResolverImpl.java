package ru.nikskul.tftp.resolver;

import ru.nikskul.tftp.handler.TftpHandlerChain;
import ru.nikskul.tftp.packet.TftpPacket;

import java.util.List;
import java.util.Objects;

public class TftpPacketResolverImpl implements TftpPacketResolver {

    private final List<TftpHandlerChain> chainStartList;

    public TftpPacketResolverImpl(List<TftpHandlerChain> chainStartList) {
        this.chainStartList = Objects.requireNonNull(chainStartList);
    }

    @Override
    public void handle(TftpPacket packet) {
        for (var handler : chainStartList) {
            if (handler.canHandle(packet))
                handler.handle(packet);
        }
    }
}
