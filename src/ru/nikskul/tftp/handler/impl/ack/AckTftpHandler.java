package ru.nikskul.tftp.handler.impl.ack;

import ru.nikskul.tftp.handler.AckTftpHandlerChain;

public abstract class AckTftpHandler implements AckTftpHandlerChain {

    private final AckTftpHandlerChain next;

    protected AckTftpHandler(AckTftpHandlerChain next) {
        this.next = next;
    }

    @Override
    public AckTftpHandlerChain getNext() {
        return next;
    }
}
