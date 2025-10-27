package ru.nikskul.tftp.file.tftp.reader;

import ru.nikskul.logger.SystemLogger;
import ru.nikskul.tftp.file.provider.TftpFileProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.StampedLock;

public class TftpFileReaderImpl
    implements TftpFileReader {

    private final StampedLock lock;
    private final TftpFileProvider fileProvider;

    private final long[] stamps = new long[0xffff];

    public TftpFileReaderImpl(StampedLock lock, TftpFileProvider fileProvider) {
        this.lock = lock;
        this.fileProvider = fileProvider;
    }

    @Override
    public byte[] readFromFile(int tid, int offset, int length)
    throws IOException {
        try {
            var path = fileProvider.getFile(tid);

            var baos = new ByteArrayOutputStream();
            try (
                var fis = FileChannel.open(path, StandardOpenOption.READ)
            ) {
                var buffer = ByteBuffer.allocate(length);
                int len = fis.read(buffer, offset);
                if (len != -1) baos.write(buffer.array(), 0, len);
            }

            return baos.toByteArray();
        } catch (IOException e) {
            unlock(tid);
            throw e;
        }
    }

    @Override
    public byte[] startRead(
        int tid,
        String filename,
        int length
    )
    throws IOException {
        try {
            SystemLogger.log("Try Lock read %d", getClass(), tid);
            long stamp = lock.readLock();
            stamps[tid] = stamp;
            SystemLogger.log("Lock read %d", getClass(), tid);

            var path = Path.of(filename);

            fileProvider.linkFile(tid, path);

            return readFromFile(tid, 0, length);
        } catch (IOException e) {
            unlock(tid);
            throw e;
        }
    }

    @Override
    public synchronized void unlock(int tid) {
        if (stamps[tid] != 0) {
            lock.unlockRead(stamps[tid]);
            stamps[tid] = 0;
            SystemLogger.log("Unlock read %d", getClass(), tid);
        }
        fileProvider.unlinkFile(tid);
    }
}
