package ru.nikskul.tftp.converter.datagram;

import ru.nikskul.tftp.packet.TftpPacket;

import java.net.DatagramPacket;

public interface DatagramToTftpConverter {

    TftpPacket convert(int tid, DatagramPacket packet);

}
