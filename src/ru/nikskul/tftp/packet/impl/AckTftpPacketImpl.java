package ru.nikskul.tftp.packet.impl;

import ru.nikskul.tftp.packet.AbstractTftpPacket;
import ru.nikskul.tftp.packet.AckTftpPacket;

/**
 * Ack TFTP Packet.
 * For the structure of packet see {@link AckTftpPacket}.
 */
public class AckTftpPacketImpl
    extends AbstractTftpPacket
    implements AckTftpPacket {

    /**
     * Sequence number of block.
     */
    private final short block;

    public AckTftpPacketImpl(int tid, short block) {
        super(tid);
        this.block = block;
    }

    @Override
    public int getBlock() {
        return ((int) block) & 0xffff;
    }
}
