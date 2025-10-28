package ru.nikskul.tftp.packet;

/**
 * An ERROR packet (opcode 5).
 * An ERROR packet can be the acknowledgment
 * of any other type of packet.
 * <h5>ERROR Packet Structure</h5>
 * <pre>
 *  2 bytes     2 bytes      string    1 byte
 *  -----------------------------------------
 * | Opcode |  ErrorCode |   ErrMsg   |   0  |
 *  -----------------------------------------
 * </pre>
 */
public interface ErrorTftpPacket extends TftpPacket {

    /**
     * The error code is an integer indicating the nature of the error.
     * <h5>Error Codes</h5>
     * <pre>
     * Value     Meaning
     *   0       Not defined, see error message (if any).
     *   1       File not found.
     *   2       Access violation.
     *   3       Disk full or allocation exceeded.
     *   4       Illegal TFTP operation.
     *   5       Unknown transfer ID.
     *   6       File already exists.
     *   7       No such user.
     * </pre>
     *
     * @return error code
     */
    int getErrorCode();

    /**
     * The error message is intended for human consumption,
     * and should be in netascii. Like all other strings,
     * it is terminated with a zero byte.
     *
     * @return error message
     */
    String getErrMsg();

    @Override
    default int getOpcode() {
        return 0x05;
    }

    enum ERROR {
        NOT_DEFINED,
        FILE_NOT_FOUND,
        ACCESS_VIOLATION,
        DISK_FULL,
        ILLEGAL_TFTP_OPERATION,
        UNKNOWN_TRANSFER_ID,
        FILE_ALREADY_EXIST,
        NO_SUCH_USER
    }
}
