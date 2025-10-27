package ru.nikskul.tftp.handler.impl.rrq;

import ru.nikskul.tftp.handler.RrqTftpHandlerChain;

public abstract class RrqTftpHandler implements RrqTftpHandlerChain {

    private final RrqTftpHandlerChain next;

    protected RrqTftpHandler(RrqTftpHandlerChain next) {
        this.next = next;
    }

    @Override
    public RrqTftpHandlerChain getNext() {
        return next;
    }
}
