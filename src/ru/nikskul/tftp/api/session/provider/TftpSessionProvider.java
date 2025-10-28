package ru.nikskul.tftp.api.session.provider;

import ru.nikskul.tftp.api.session.shared.TftpSession;
import ru.nikskul.tftp.packet.TftpSessionPacket;

public interface TftpSessionProvider {

    TftpSession getSession(TftpSessionPacket packet);

    void addSession(TftpSession session);
    void removeSession(TftpSession session);
}
