package ru.nikskul.tftp.datagram.receiver;

import java.net.DatagramPacket;

public interface DatagramReceiver {

    void receive(int tid, DatagramPacket packet);
}
