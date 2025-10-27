package ru.nikskul.tftp.packet;

/**
 * TFTP supports five types of packets,
 * all of which have been mentioned above:
 * <pre>
 * opcode  operation
 *   1     Read request (RRQ)
 *   2     Write request (WRQ)
 *   3     Data (DATA)
 *   4     Acknowledgment (ACK)
 *   5     Error (ERROR)
 * </pre>
 */
public interface TftpPacket extends TftpSessionPacket {

    /**
     * Common TFTP packet value.
     * Describe packet type.
     * See {@link TftpPacket}
     *
     * @return packet type
     */
    int getOpcode();

}
