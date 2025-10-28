package ru.nikskul.tftp.handler.impl.data;

import ru.nikskul.logger.SystemLogger;
import ru.nikskul.tftp.file.tftp.writer.TftpFileWriter;
import ru.nikskul.tftp.handler.DataTftpHandlerChain;
import ru.nikskul.tftp.packet.DataTftpPacket;
import ru.nikskul.tftp.packet.ErrorTftpPacket;
import ru.nikskul.tftp.packet.TftpPacket;
import ru.nikskul.tftp.packet.factory.TftpPacketFactory;
import ru.nikskul.tftp.send.TftpSendUseCase;

import java.io.IOException;

public class DataTftpHandlerWrite
    extends DataTftpHandler {

    private final TftpFileWriter fileWriter;
    private final TftpSendUseCase sendUseCase;
    private final TftpPacketFactory packetFactory;

    public DataTftpHandlerWrite(
        DataTftpHandlerChain next,
        TftpFileWriter fileWriter,
        TftpSendUseCase sendUseCase, TftpPacketFactory packetFactory
    ) {
        super(next);
        this.fileWriter = fileWriter;
        this.sendUseCase = sendUseCase;
        this.packetFactory = packetFactory;
    }

    @Override
    public boolean handle(TftpPacket packet) {
        if (!(packet instanceof DataTftpPacket dataTftpPacket)) return false;

        byte[] data = dataTftpPacket.getData();
        boolean lastPacket = data.length < 512;
        if (!write(packet, data)) return false;

        var ack = packetFactory.ack(
            packet.getTid(),
            (short) dataTftpPacket.getBlock()
        );
        if (lastPacket) {
            fileWriter.unlock(packet.getTid());
            sendUseCase.sendLast(ack);
            SystemLogger.log(
                "Operation completed! Total block: "
                    + dataTftpPacket.getBlock(),
                getClass()
            );
        } else {
            sendUseCase.send(ack);
        }

        return getNext() == null || getNext().handle(packet);
    }

    private boolean write(
        TftpPacket packet,
        byte[] data
    ) {
        try {
            fileWriter.writeToFile(packet.getTid(), data);
            return true;
        } catch (IOException e) {
            SystemLogger.log(e, getClass());
            var tftpError = packetFactory.error(
                packet.getTid(),
                ErrorTftpPacket.ERROR.ACCESS_VIOLATION
            );
            sendUseCase.sendLast(tftpError);
            return false;
        }
    }
}
