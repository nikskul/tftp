package ru.nikskul.tftp.datagram.receiver;

import ru.nikskul.tftp.converter.datagram.DatagramToTftpConverter;
import ru.nikskul.tftp.resolver.TftpPacketResolver;

import java.net.DatagramPacket;

public class DatagramReceiverImpl implements DatagramReceiver {

    private final DatagramToTftpConverter converter;
    private final TftpPacketResolver tftpPacketResolver;

    public DatagramReceiverImpl(
        DatagramToTftpConverter converter,
        TftpPacketResolver tftpPacketResolver
    ) {
        this.converter = converter;
        this.tftpPacketResolver = tftpPacketResolver;
    }

    @Override
    public void receive(int tid, DatagramPacket packet) {
        var tftpPacket = converter.convert(tid, packet);
        tftpPacketResolver.handle(tftpPacket);
    }

}
