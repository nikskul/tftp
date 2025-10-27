package ru.nikskul.tftp.packet.impl;

import ru.nikskul.tftp.packet.AbstractTftpPacket;
import ru.nikskul.tftp.packet.ErrorTftpPacket;

/**
 * Error TFTP Packet.
 * For the structure of packet see {@link ErrorTftpPacket}.
 */
public class ErrorTftpPacketImpl
    extends AbstractTftpPacket
    implements ErrorTftpPacket {

    /**
     * Error code.
     */
    private final short errorCode;

    /**
     * Error message.
     */
    private final String errMsg;


    public ErrorTftpPacketImpl(int tid, short errorCode, String errMsg) {
        super(tid);
        this.errorCode = errorCode;
        this.errMsg = errMsg;
    }

    @Override
    public int getErrorCode() {
        return ((int) errorCode) & 0xffff;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }
}
