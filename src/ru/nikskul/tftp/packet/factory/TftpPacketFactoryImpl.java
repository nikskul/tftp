package ru.nikskul.tftp.packet.factory;

import ru.nikskul.tftp.packet.ErrorTftpPacket;
import ru.nikskul.tftp.packet.TftpPacket;
import ru.nikskul.tftp.packet.impl.AckTftpPacketImpl;
import ru.nikskul.tftp.packet.impl.DataTftpPacketImpl;
import ru.nikskul.tftp.packet.impl.ErrorTftpPacketImpl;
import ru.nikskul.tftp.packet.impl.RrqTftpPacketImpl;
import ru.nikskul.tftp.packet.impl.WrqTftpPacketImpl;

public class TftpPacketFactoryImpl
    implements TftpPacketFactory {

    @Override
    public TftpPacket rrq(int tid, String filename, String mode) {
        return new RrqTftpPacketImpl(tid, filename, mode);
    }

    @Override
    public TftpPacket wrq(int tid, String filename, String mode) {
        return new WrqTftpPacketImpl(tid, filename, mode);
    }

    @Override
    public TftpPacket ack(int tid, short block) {
        return new AckTftpPacketImpl(tid, block);
    }

    @Override
    public TftpPacket data(int tid, short block, byte[] data) {
        return new DataTftpPacketImpl(tid, block, data);
    }

    @Override
    public TftpPacket error(int tid, ErrorTftpPacket.ERROR type) {
        return switch (type) {
            case FILE_NOT_FOUND -> ErrorTftpPacketImpl.fileNotFound(tid);
            case ACCESS_VIOLATION -> ErrorTftpPacketImpl.accessViolation(tid);
            case DISK_FULL -> ErrorTftpPacketImpl.diskFull(tid);
            case ILLEGAL_TFTP_OPERATION ->
                ErrorTftpPacketImpl.illegalOperation(tid);
            case UNKNOWN_TRANSFER_ID -> ErrorTftpPacketImpl.unknownTid(tid);
            case FILE_ALREADY_EXIST ->
                ErrorTftpPacketImpl.fileAlreadyExists(tid);
            case NO_SUCH_USER -> ErrorTftpPacketImpl.noSuchUser(tid);
            default ->
                throw new IllegalStateException("No factory for code: " + tid);
        };
    }

    @Override
    public TftpPacket error(int tid, short errCode, String errMsg) {
        return new ErrorTftpPacketImpl(tid, errCode, errMsg);
    }
}
