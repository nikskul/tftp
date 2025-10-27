package ru.nikskul.tftp.api.session.shared;

import java.io.IOException;
import java.net.DatagramPacket;

public interface TftpSession extends Runnable {

    int getSourceTid();
    int getTargetTid();

    void send(DatagramPacket packet) throws IOException;
    void sendAndClose(DatagramPacket packet) throws IOException;

    void close();
}
