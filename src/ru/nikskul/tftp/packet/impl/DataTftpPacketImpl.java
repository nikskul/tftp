package ru.nikskul.tftp.packet.impl;

import ru.nikskul.tftp.packet.AbstractTftpPacket;
import ru.nikskul.tftp.packet.DataTftpPacket;

/**
 * Data TFTP Packet.
 * For the structure of packet see {@link DataTftpPacket}.
 */
public class DataTftpPacketImpl
    extends AbstractTftpPacket
    implements DataTftpPacket {

    /**
     * Sequence number of block.
     */
    private final short block;

    /**
     * Data of block.
     */
    private final byte[] data;


    public DataTftpPacketImpl(int tid, short block, byte[] data) {
        super(tid);
        this.block = block;
        this.data = data;
    }

    @Override
    public int getBlock() {
        return ((int) block) & 0xffff;
    }

    @Override
    public byte[] getData() {
        return data;
    }
}
