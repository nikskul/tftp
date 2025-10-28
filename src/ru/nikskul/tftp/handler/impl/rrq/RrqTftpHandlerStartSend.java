package ru.nikskul.tftp.handler.impl.rrq;

import ru.nikskul.logger.SystemLogger;
import ru.nikskul.tftp.file.tftp.reader.TftpFileReader;
import ru.nikskul.tftp.handler.RrqTftpHandlerChain;
import ru.nikskul.tftp.packet.RequestTftpPacket;
import ru.nikskul.tftp.packet.TftpPacket;
import ru.nikskul.tftp.packet.factory.TftpPacketFactory;
import ru.nikskul.tftp.packet.impl.ErrorTftpPacketImpl;
import ru.nikskul.tftp.send.TftpSendUseCase;

import java.io.IOException;

public class RrqTftpHandlerStartSend
    extends RrqTftpHandler {

    private final TftpFileReader fileReader;
    private final TftpSendUseCase sendUseCase;
    private final TftpPacketFactory packetFactory;

    public RrqTftpHandlerStartSend(
        RrqTftpHandlerChain next,
        TftpFileReader fileReader,
        TftpSendUseCase sendUseCase, TftpPacketFactory packetFactory
    ) {
        super(next);
        this.fileReader = fileReader;
        this.sendUseCase = sendUseCase;
        this.packetFactory = packetFactory;
    }

    @Override
    public boolean handle(TftpPacket packet) {
        if (!canHandle(packet)) return false;

        RequestTftpPacket rrqPacket = (RequestTftpPacket) packet;
        String filename = rrqPacket.getFilename();

        try {
            byte[] data = fileReader.startRead(packet.getTid(), filename, 512);
            var lastPacket = data.length < 512;

            var dataTftpPacket =packetFactory.data(
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
            var tftpError = ErrorTftpPacketImpl.fileNotFound(packet.getTid());
            sendUseCase.sendLast(tftpError);
            return false;
        }

        return getNext() == null || getNext().handle(packet);
    }
}
