package ru.nikskul.tftp.file.provider;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentMap;

public class TftpFileProviderImpl
    implements TftpFileProvider {

    private final ConcurrentMap<Integer, Path> fileMap;

    public TftpFileProviderImpl(ConcurrentMap<Integer, Path> fileMap) {
        this.fileMap = fileMap;
    }

    @Override
    public Path getFile(int tid) {
        return fileMap.get(tid);
    }

    @Override
    public void linkFile(int tid, Path file) {
        fileMap.put(tid, file);
    }

    @Override
    public void unlinkFile(int tid) {
        fileMap.remove(tid);
    }
}
