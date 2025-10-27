package ru.nikskul.tftp.file.tftp.reader;

import java.io.IOException;

public interface TftpFileReader {

    byte[] readFromFile(int tid, int offset, int length) throws IOException;

    byte[] startRead(int tid, String filename, int length) throws IOException;

    void unlock(int tid);
}
