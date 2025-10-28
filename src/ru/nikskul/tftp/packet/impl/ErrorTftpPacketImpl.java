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

    public  static ErrorTftpPacketImpl fileNotFound(int tid) {
        return new ErrorTftpPacketImpl(tid, (short) 1, "File not found.");
    }

    public static ErrorTftpPacketImpl accessViolation(int tid) {
        return new ErrorTftpPacketImpl(tid, (short) 2, "Access violation.");
    }

    public static ErrorTftpPacketImpl diskFull(int tid) {
        return new ErrorTftpPacketImpl(
            tid,
            (short) 3,
            "Disk full or allocation exceeded."
        );
    }

    public static ErrorTftpPacketImpl illegalOperation(int tid) {
        return new ErrorTftpPacketImpl(
            tid,
            (short) 4,
            "Illegal TFTP operation."
        );
    }

    public static ErrorTftpPacketImpl unknownTid(int tid) {
        return new ErrorTftpPacketImpl(tid, (short) 5, "Unknown transfer ID.");
    }

    public static ErrorTftpPacketImpl fileAlreadyExists(int tid) {
        return new ErrorTftpPacketImpl(tid, (short) 6, "File already exists.");
    }

    public static ErrorTftpPacketImpl noSuchUser(int tid) {
        return new ErrorTftpPacketImpl(tid, (short) 7, "No such user.");
    }
}
