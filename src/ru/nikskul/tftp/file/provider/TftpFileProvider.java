package ru.nikskul.tftp.file.provider;

import java.nio.file.Path;

public interface TftpFileProvider {
    Path getFile(int tid);
    void linkFile(int tid, Path file);
    void unlinkFile(int tid);
}
