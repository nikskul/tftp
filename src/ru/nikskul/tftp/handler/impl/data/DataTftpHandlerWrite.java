package ru.nikskul.tftp.handler.impl.data;

import ru.nikskul.logger.SystemLogger;
import ru.nikskul.tftp.file.tftp.writer.TftpFileWriter;
import ru.nikskul.tftp.handler.DataTftpHandlerChain;
import ru.nikskul.tftp.packet.DataTftpPacket;
import ru.nikskul.tftp.packet.TftpPacket;
import ru.nikskul.tftp.packet.impl.AckTftpPacketImpl;
import ru.nikskul.tftp.packet.impl.ErrorTftpPacketImpl;
import ru.nikskul.tftp.send.TftpSendUseCase;

import java.io.IOException;

public class DataTftpHandlerWrite
    extends DataTftpHandler {

    private final TftpFileWriter fileWriter;
    private final TftpSendUseCase sendUseCase;

    public DataTftpHandlerWrite(
        DataTftpHandlerChain next,
        TftpFileWriter fileWriter,
        TftpSendUseCase sendUseCase
    ) {
        super(next);
        this.fileWriter = fileWriter;
        this.sendUseCase = sendUseCase;
    }

    @Override
    public boolean handle(TftpPacket packet) {
        if (!(packet instanceof DataTftpPacket)) return false;

        DataTftpPacket dataTftpPacket = (DataTftpPacket) packet;
        byte[] data = dataTftpPacket.getData();
        boolean lastPacket = data.length < 512;
        if (!write(packet, data)) return false;

        var ack = new AckTftpPacketImpl(
            packet.getTid(),
            (short) (dataTftpPacket.getBlock())
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
            var tftpError = new ErrorTftpPacketImpl(
                packet.getTid(),
                (short) 2, "Access violation."
            );
            sendUseCase.sendLast(tftpError);
            return false;
        }
    }
}
