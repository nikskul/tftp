package ru.nikskul.tftp.api.session.provider;

import ru.nikskul.tftp.api.session.shared.TftpSession;
import ru.nikskul.tftp.packet.TftpPacket;

public interface TftpSessionProvider {

    TftpSession getSession(TftpPacket packet);

    void addSession(TftpSession session);
    void removeSession(TftpSession session);
}
