package ru.nikskul.tftp.api.client;

import java.net.InetSocketAddress;

public interface TftpClient {

    int sendRrq(InetSocketAddress address, String filename);

    int sendWrq(InetSocketAddress address, String filename);

    void setBinaryMode();
    void setNetasciiMode();
}
