package ru.nikskul.tftp.send;

import ru.nikskul.tftp.packet.TftpPacket;

public interface TftpSendUseCase {

    void send(TftpPacket packet);
    void sendLast(TftpPacket packet);
}
