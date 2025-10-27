package ru.nikskul.tftp.packet;

/**
 * <p>
 * All packets other than duplicate ACK's and those used for
 * termination are acknowledged unless a timeout occurs.
 * </p>
 *
 * <h5>Ack Packet Structure</h5>
 * <pre>
 *  2 bytes     2 bytes
 *  ---------------------
 * | Opcode |   Block #  |
 *  ---------------------
 * </pre>
 *
 * <p>
 * The WRQ and DATA packets are acknowledged by ACK or ERROR packets,
 * while RRQ and ACK packets are acknowledged by DATA or ERROR packets.
 * </p>
 */
public interface AckTftpPacket extends TftpPacket {

    /**
     * The block number in an Ack echoes
     * the block number of the DATA packet
     * being acknowledged.
     *
     * @return block number
     */
    int getBlock();

    @Override
    default int getOpcode() {
        return 0x04;
    }
}
