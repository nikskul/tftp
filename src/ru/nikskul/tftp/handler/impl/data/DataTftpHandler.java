package ru.nikskul.tftp.handler.impl.data;

import ru.nikskul.tftp.handler.DataTftpHandlerChain;

public abstract class DataTftpHandler implements DataTftpHandlerChain {

    private final DataTftpHandlerChain next;

    protected DataTftpHandler(DataTftpHandlerChain next) {
        this.next = next;
    }

    @Override
    public DataTftpHandlerChain getNext() {
        return next;
    }
}
