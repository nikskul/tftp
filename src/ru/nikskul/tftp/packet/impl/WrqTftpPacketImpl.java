package ru.nikskul.tftp.packet.impl;

import ru.nikskul.tftp.packet.AbstractTftpPacket;
import ru.nikskul.tftp.packet.RequestTftpPacket;

/**
 * WRQ (Write Request) TFTP Packet.
 * For the structure of packet see {@link RequestTftpPacket}.
 */
public class WrqTftpPacketImpl
    extends AbstractTftpPacket
    implements RequestTftpPacket {

    /**
     * File name.
     */
    private final String filename;

    /**
     * Mode.
     */
    private final String mode;


    public WrqTftpPacketImpl(int tid, String filename, String mode) {
        super(tid);
        this.filename = filename;
        this.mode = mode;
    }

    @Override
    public int getOpcode() {
        return 0x02;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public String getMode() {
        return mode;
    }
}
