package ru.nikskul.tftp.handler.impl.error;

import ru.nikskul.tftp.handler.ErrorTftpHandlerChain;

public abstract class ErrorTftpHandler implements ErrorTftpHandlerChain {

    private final ErrorTftpHandlerChain next;

    protected ErrorTftpHandler(ErrorTftpHandlerChain next) {
        this.next = next;
    }

    @Override
    public ErrorTftpHandlerChain getNext() {
        return next;
    }
}
