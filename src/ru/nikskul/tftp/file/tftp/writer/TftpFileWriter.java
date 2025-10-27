package ru.nikskul.tftp.file.tftp.writer;

import java.io.IOException;

public interface TftpFileWriter {

    void writeToFile(int tid, byte[] data) throws IOException;

    void startWrite(int tid, String filename) throws IOException;

    void unlock(int tid);
}
