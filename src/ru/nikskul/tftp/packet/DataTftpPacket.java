package ru.nikskul.tftp.packet;

/**
 * Data is actually transferred in DATA packets.
 * DATA packets (opcode = 3) have a block number and data field.
 * <h5>DATA Packet Structure</h5>
 * <pre>
 *   2 bytes     string    1 byte     string   1 byte
 *   ------------------------------------------------
 *  | Opcode |  Filename  |   0  |    Mode    |   0  |
 *   ------------------------------------------------
 *                            ^                   ^-- end of Mode.
 *                            |-- end of Filename
 * </pre>
 */
public interface DataTftpPacket extends TftpPacket {

    /**
     * The block numbers on data packets begin with one
     * and increase by one for each new block of data.
     *
     * @return block number
     */
    int getBlock();

    /**
     * The data field is from zero to 512 bytes long.
     * <ul>
     *     <li>
     *         If it is 512 bytes long,
     *         the block is not the last block of data.
     *         </li>
     *      <li>
     *          If it is from zero to 511 bytes long,
     *          it signals the end of the transfer.
     *          </li>
     * </ul>
     *
     * @return 512 bytes block of data
     */
    byte[] getData();

    @Override
    default int getOpcode() {
        return 0x03;
    }
}
