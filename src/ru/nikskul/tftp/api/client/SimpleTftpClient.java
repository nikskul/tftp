package ru.nikskul.tftp.api.client;

import ru.nikskul.logger.SystemLogger;
import ru.nikskul.tftp.api.session.factory.TftpSessionFactory;
import ru.nikskul.tftp.api.session.shared.TftpSession;
import ru.nikskul.tftp.converter.datagram.TftpToDatagramConverter;
import ru.nikskul.tftp.file.tftp.reader.TftpFileReader;
import ru.nikskul.tftp.file.tftp.writer.TftpFileWriter;
import ru.nikskul.tftp.packet.TftpPacket;
import ru.nikskul.tftp.packet.factory.TftpPacketFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SimpleTftpClient
    implements TftpClient {

    private enum Mode {
        NETASCII,
        OCTET,
    }

    private Mode mode = Mode.NETASCII;

    private final TftpSessionFactory sessionFactory;
    private final TftpPacketFactory tftpPacketFactory;
    private final TftpToDatagramConverter toDatagramConverter;
    private final TftpFileWriter fileWriter;
    private final TftpFileReader fileReader;

    public SimpleTftpClient(
        TftpSessionFactory sessionFactory,
        TftpPacketFactory tftpPacketFactory,
        TftpToDatagramConverter toDatagramConverter,
        TftpFileWriter fileWriter,
        TftpFileReader fileReader
    ) {
        this.sessionFactory = sessionFactory;
        this.tftpPacketFactory = tftpPacketFactory;
        this.toDatagramConverter = toDatagramConverter;
        this.fileWriter = fileWriter;
        this.fileReader = fileReader;
    }

    @Override
    public int sendRrq(InetSocketAddress address, String filename) {
        var session = startSession(address);

        int tid = session.getSourceTid();
        try {
            fileWriter.startWrite(tid, filename);

            TftpPacket tftpPacket = tftpPacketFactory.rrq(tid, filename, mode.name());
            sendInitialPacket(tftpPacket, session);
        } catch (IOException e) {
            session.close();
            throw new RuntimeException(e);
        }

        return tid;
    }

    @Override
    public int sendWrq(InetSocketAddress address, String filename) {
        var session = startSession(address);

        int tid = session.getSourceTid();
        try {
            fileReader.startRead(tid, filename, 512);

            TftpPacket tftpPacket = tftpPacketFactory.wrq(tid, filename, mode.name());
            sendInitialPacket(tftpPacket, session);
        } catch (IOException e) {
            session.close();
            throw new RuntimeException(e);
        }

        return tid;
    }

    @Override
    public void setNetasciiMode() {
        mode = Mode.NETASCII;
    }

    @Override
    public void setBinaryMode() {
        mode = Mode.OCTET;
    }

    private TftpSession startSession(
        InetSocketAddress address
    ) {
        try {
            return sessionFactory.newInstance(address).start();
        } catch (Exception e) {
            SystemLogger.log(
                "Fail to make session [%d]! ",
                getClass(),
                address.getPort()
            );
            throw e;
        }
    }

    private void sendInitialPacket(
        TftpPacket packet,
        TftpSession session
    ) throws IOException {
        var datagram = toDatagramConverter.convert(packet);
        session.send(datagram);
    }
}
