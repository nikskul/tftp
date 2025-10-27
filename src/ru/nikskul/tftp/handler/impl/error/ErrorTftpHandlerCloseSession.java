package ru.nikskul.tftp.handler.impl.error;

import ru.nikskul.tftp.api.session.provider.TftpSessionProvider;
import ru.nikskul.tftp.handler.ErrorTftpHandlerChain;
import ru.nikskul.tftp.packet.TftpPacket;

public class ErrorTftpHandlerCloseSession
    extends ErrorTftpHandler {

    private final TftpSessionProvider sessionProvider;

    public ErrorTftpHandlerCloseSession(
        ErrorTftpHandlerChain next,
        TftpSessionProvider sessionProvider
    ) {
        super(next);
        this.sessionProvider = sessionProvider;
    }

    @Override
    public boolean handle(TftpPacket packet) {
        if (!canHandle(packet)) return false;

        sessionProvider.getSession(packet).close();

        return getNext() == null || getNext().handle(packet);
    }
}
