package ru.nikskul.tftp.handler.impl.wrq;

import ru.nikskul.logger.SystemLogger;
import ru.nikskul.tftp.file.tftp.writer.TftpFileWriter;
import ru.nikskul.tftp.handler.WrqTftpHandlerChain;
import ru.nikskul.tftp.packet.RequestTftpPacket;
import ru.nikskul.tftp.packet.TftpPacket;
import ru.nikskul.tftp.packet.impl.AckTftpPacketImpl;
import ru.nikskul.tftp.packet.impl.ErrorTftpPacketImpl;
import ru.nikskul.tftp.send.TftpSendUseCase;

import java.io.IOException;

public class WrqTftpHandlerStartWrite
    extends WrqTftpHandler {

    private final TftpFileWriter fileWriter;
    private final TftpSendUseCase sendUseCase;

    public WrqTftpHandlerStartWrite(
        WrqTftpHandlerChain next,
        TftpFileWriter fileWriter,
        TftpSendUseCase sendUseCase
    ) {
        super(next);
        this.fileWriter = fileWriter;
        this.sendUseCase = sendUseCase;
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
            var tftpError = new ErrorTftpPacketImpl(
                packet.getTid(),
                (short) 6,
                "File already exists."
            );
            sendUseCase.sendLast(tftpError);
            return false;
        }

        AckTftpPacketImpl ackTftpPacket = new AckTftpPacketImpl(
            packet.getTid(),
            (short) 0
        );

        sendUseCase.send(ackTftpPacket);

        return getNext() == null || getNext().handle(packet);
    }
}
