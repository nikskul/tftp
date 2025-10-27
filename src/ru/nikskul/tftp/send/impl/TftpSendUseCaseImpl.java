package ru.nikskul.tftp.send.impl;

import ru.nikskul.logger.SystemLogger;
import ru.nikskul.tftp.api.session.provider.TftpSessionProvider;
import ru.nikskul.tftp.api.session.shared.TftpSession;
import ru.nikskul.tftp.converter.datagram.TftpToDatagramConverter;
import ru.nikskul.tftp.packet.TftpPacket;
import ru.nikskul.tftp.send.TftpSendUseCase;

import java.io.IOException;

public class TftpSendUseCaseImpl
    implements TftpSendUseCase {

    private final TftpSessionProvider sessionProvider;
    private final TftpToDatagramConverter converter;

    public TftpSendUseCaseImpl(
        TftpSessionProvider sessionProvider,
        TftpToDatagramConverter converter
    ) {
        this.sessionProvider = sessionProvider;
        this.converter = converter;
    }

    @Override
    public void send(TftpPacket packet) {
        convertAndSend(packet, false);
    }

    @Override
    public void sendLast(TftpPacket packet) {
        convertAndSend(packet, true);
    }

    private void convertAndSend(TftpPacket packet, boolean lastPacket) {
        TftpSession session = sessionProvider.getSession(packet);
        var datagram = converter.convert(packet);
        try {
            if (lastPacket) {
                session.sendAndClose(datagram);
            } else {
                session.send(datagram);
            }
        } catch (IOException e) {
            SystemLogger.log(e, getClass());
        }
    }

}
