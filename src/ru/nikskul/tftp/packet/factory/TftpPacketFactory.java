package ru.nikskul.tftp.packet.factory;

import ru.nikskul.tftp.packet.ErrorTftpPacket;
import ru.nikskul.tftp.packet.TftpPacket;

public interface TftpPacketFactory {

    TftpPacket rrq(int tid, String filename, String mode);

    TftpPacket wrq(int tid,String filename, String mode);

    TftpPacket ack(int tid, short block);

    TftpPacket data(int tid, short block, byte[] data);

    TftpPacket error(int tid, ErrorTftpPacket.ERROR type);

    TftpPacket error(int tid, short errCode, String errMsg);
}
