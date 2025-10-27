package ru.nikskul.tftp.handler.impl.rrq;

import ru.nikskul.logger.SystemLogger;
import ru.nikskul.tftp.file.tftp.reader.TftpFileReader;
import ru.nikskul.tftp.handler.RrqTftpHandlerChain;
import ru.nikskul.tftp.packet.RequestTftpPacket;
import ru.nikskul.tftp.packet.TftpPacket;
import ru.nikskul.tftp.packet.impl.DataTftpPacketImpl;
import ru.nikskul.tftp.packet.impl.ErrorTftpPacketImpl;
import ru.nikskul.tftp.send.TftpSendUseCase;

import java.io.IOException;

public class RrqTftpHandlerStartSend
    extends RrqTftpHandler {

    private final TftpFileReader fileReader;
    private final TftpSendUseCase sendUseCase;

    public RrqTftpHandlerStartSend(
        RrqTftpHandlerChain next,
        TftpFileReader fileReader,
        TftpSendUseCase sendUseCase
    ) {
        super(next);
        this.fileReader = fileReader;
        this.sendUseCase = sendUseCase;
    }

    @Override
    public boolean handle(TftpPacket packet) {
        if (!canHandle(packet)) return false;

        RequestTftpPacket rrqPacket = (RequestTftpPacket) packet;
        String filename = rrqPacket.getFilename();

        try {
            byte[] data = fileReader.startRead(packet.getTid(), filename, 512);
            var lastPacket = data.length < 512;

            DataTftpPacketImpl dataTftpPacket = new DataTftpPacketImpl(
                packet.getTid(), (short) 1, data
            );

            if (lastPacket) {
                fileReader.unlock(packet.getTid());
                sendUseCase.sendLast(dataTftpPacket);
            } else {
                sendUseCase.send(dataTftpPacket);
            }
        } catch (IOException e) {
            SystemLogger.log(e, getClass());
            var tftpError = new ErrorTftpPacketImpl(
                packet.getTid(), (short) 1, "File not found.");
            sendUseCase.sendLast(tftpError);
            return false;
        }

        return getNext() == null || getNext().handle(packet);
    }
}
