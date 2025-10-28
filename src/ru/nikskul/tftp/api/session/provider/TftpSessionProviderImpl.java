package ru.nikskul.tftp.api.session.provider;

import ru.nikskul.tftp.api.session.shared.TftpSession;
import ru.nikskul.tftp.packet.TftpSessionPacket;

import java.util.concurrent.ConcurrentMap;

public class TftpSessionProviderImpl
    implements TftpSessionProvider {

    private final ConcurrentMap<Integer, TftpSession> sessionMap;

    public TftpSessionProviderImpl(ConcurrentMap<Integer, TftpSession> sessionMap) {
        this.sessionMap = sessionMap;
    }

    @Override
    public TftpSession getSession(TftpSessionPacket packet) {
        return sessionMap.get(packet.getTid());
    }

    @Override
    public void addSession(TftpSession session) {
        sessionMap.put(session.getSourceTid(), session);
    }

    @Override
    public void removeSession(TftpSession session) {
        sessionMap.remove(session.getSourceTid());
    }

}
