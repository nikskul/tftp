package ru.nikskul.tftp.packet;

public abstract class AbstractTftpPacket implements TftpPacket {

    private final int tid;

    protected AbstractTftpPacket(int tid) {
        this.tid = tid;
    }

    @Override
    public int getTid() {
        return tid;
    }
}
