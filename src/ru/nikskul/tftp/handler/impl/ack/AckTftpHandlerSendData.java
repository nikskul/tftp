package ru.nikskul.tftp.handler.impl.ack;

import ru.nikskul.logger.SystemLogger;
import ru.nikskul.tftp.file.tftp.reader.TftpFileReader;
import ru.nikskul.tftp.handler.AckTftpHandlerChain;
import ru.nikskul.tftp.packet.AckTftpPacket;
import ru.nikskul.tftp.packet.ErrorTftpPacket;
import ru.nikskul.tftp.packet.TftpPacket;
import ru.nikskul.tftp.packet.factory.TftpPacketFactory;
import ru.nikskul.tftp.send.TftpSendUseCase;

import java.io.IOException;

public class AckTftpHandlerSendData
    extends AckTftpHandler {

    private final TftpSendUseCase sendUseCase;
    private final TftpFileReader fileReader;
    private final TftpPacketFactory packetFactory;

    public AckTftpHandlerSendData(
        AckTftpHandlerChain next,
        TftpSendUseCase sendUseCase,
        TftpFileReader fileReader, TftpPacketFactory packetFactory
    ) {
        super(next);
        this.sendUseCase = sendUseCase;
        this.fileReader = fileReader;
        this.packetFactory = packetFactory;
    }

    @Override
    public boolean handle(TftpPacket packet) {
        if (!canHandle(packet)) return false;

        int block = ((AckTftpPacket) packet).getBlock();
        int length = 512;
        int offset = block * length;
        try {
            byte[] nextBLock = fileReader.readFromFile(
                packet.getTid(),
                offset,
                length
            );
            boolean lastPacket = nextBLock.length < 512;

            var dataPacket = packetFactory.data(
                packet.getTid(),
                (short) (block + 1),
                nextBLock
            );
            if (lastPacket) {
                fileReader.unlock(packet.getTid());
                sendUseCase.sendLast(dataPacket);
                SystemLogger.log(
                    "Operation completed! Total block: " + (block + 1),
                    getClass()
                );
            } else {
                sendUseCase.send(dataPacket);
            }
        } catch (IOException e) {
            SystemLogger.log(e, getClass());
            var tftpError = packetFactory.error(
                packet.getTid(),
                ErrorTftpPacket.ERROR.ACCESS_VIOLATION
            );
            sendUseCase.sendLast(tftpError);
            return false;
        }
        return getNext() == null || getNext().handle(packet);
    }
}
