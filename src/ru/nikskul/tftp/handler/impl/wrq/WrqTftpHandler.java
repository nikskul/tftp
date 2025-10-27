package ru.nikskul.tftp.handler.impl.wrq;

import ru.nikskul.tftp.handler.WrqTftpHandlerChain;

public abstract class WrqTftpHandler
    implements WrqTftpHandlerChain {

    private final WrqTftpHandlerChain next;

    protected WrqTftpHandler(WrqTftpHandlerChain next) {
        this.next = next;
    }

    @Override
    public WrqTftpHandlerChain getNext() {
        return next;
    }
}
