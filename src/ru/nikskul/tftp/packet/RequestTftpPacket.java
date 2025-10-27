package ru.nikskul.tftp.packet;

/**
 * A one of known Requests TFTP Packet (RRQ\WRQ).
 * <h5>RRQ\WRQ Packet Structure</h5>
 * <pre>
 *  2 bytes     string    1 byte     string   1 byte
 *  ------------------------------------------------
 * | Opcode |  Filename  |   0  |    Mode    |   0  |
 *  ------------------------------------------------
 *                           ^                   ^-- end of Mode.
 *                           |-- end of Filename
 * </pre>
 */
public interface RequestTftpPacket extends TftpPacket {

    /**
     * File name is a sequence of bytes in
     * <i>netascii</i> terminated by a zero byte.
     *
     * @return file name
     */
    String getFilename();

    /**
     * The mode field contains the
     * string "netascii", "octet", or "mail" (or any combination of upper
     * and lower case, such as "NETASCII", NetAscii", etc.) in netascii
     * indicating the three modes defined in the protocol.
     *
     * @return mode
     */
    String getMode();
}
