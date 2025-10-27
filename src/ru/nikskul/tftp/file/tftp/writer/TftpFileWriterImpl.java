package ru.nikskul.tftp.file.tftp.writer;

import ru.nikskul.logger.SystemLogger;
import ru.nikskul.tftp.file.provider.TftpFileProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.StampedLock;

public class TftpFileWriterImpl
    implements TftpFileWriter {

    private final StampedLock lock;
    private final TftpFileProvider fileProvider;

    private final long[] stamps = new long[0xffff];

    public TftpFileWriterImpl(StampedLock lock, TftpFileProvider fileProvider) {
        this.lock = lock;
        this.fileProvider = fileProvider;
    }

    @Override
    public void writeToFile(
        int tid, byte[] data
    ) throws IOException {
        try {
            var path = fileProvider.getFile(tid);
            Files.write(
                path, data,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            unlock(tid);
            throw e;
        }
    }

    @Override
    public void startWrite(int tid, String filename) throws IOException {
        try {
            SystemLogger.log("Try Lock write %d", getClass(), tid);
            long stamp = lock.writeLock();
            stamps[tid] = stamp;
            SystemLogger.log("Lock write %d", getClass(), tid);

            var path = Path.of(filename);
            SystemLogger.log("Create file: " + path, getClass());
            Files.deleteIfExists(path);
            Files.createFile(path);

            fileProvider.linkFile(tid, path);
        } catch (IOException e) {
            unlock(tid);
            throw e;
        }
    }

    @Override
    public synchronized void unlock(int tid) {
        if (stamps[tid] != 0) {
            SystemLogger.log("Unlock write %d", getClass(), tid);
            lock.unlockWrite(stamps[tid]);
            stamps[tid] = 0;
        }
        fileProvider.unlinkFile(tid);
    }
}
