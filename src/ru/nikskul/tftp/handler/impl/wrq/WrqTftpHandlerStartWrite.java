package ru.nikskul.tftp.handler.impl.wrq;

import ru.nikskul.logger.SystemLogger;
import ru.nikskul.tftp.file.tftp.writer.TftpFileWriter;
import ru.nikskul.tftp.handler.WrqTftpHandlerChain;
import ru.nikskul.tftp.packet.ErrorTftpPacket;
import ru.nikskul.tftp.packet.RequestTftpPacket;
import ru.nikskul.tftp.packet.TftpPacket;
import ru.nikskul.tftp.packet.factory.TftpPacketFactory;
import ru.nikskul.tftp.send.TftpSendUseCase;

import java.io.IOException;

public class WrqTftpHandlerStartWrite
    extends WrqTftpHandler {

    private final TftpFileWriter fileWriter;
    private final TftpSendUseCase sendUseCase;
    private final TftpPacketFactory packetFactory;

    public WrqTftpHandlerStartWrite(
        WrqTftpHandlerChain next,
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
        if (!canHandle(packet)) return false;

        RequestTftpPacket wrqPacket = (RequestTftpPacket) packet;
        String filename = wrqPacket.getFilename();
        try {
            fileWriter.startWrite(packet.getTid(), filename);
        } catch (IOException e) {
            SystemLogger.log(e, getClass());
            var tftpError = packetFactory.error(
                packet.getTid(),
                ErrorTftpPacket.ERROR.FILE_ALREADY_EXIST
            );
            sendUseCase.sendLast(tftpError);
            return false;
        }

        var ackTftpPacket = packetFactory.ack(packet.getTid(), (short) 0);

        sendUseCase.send(ackTftpPacket);

        return getNext() == null || getNext().handle(packet);
    }
}
