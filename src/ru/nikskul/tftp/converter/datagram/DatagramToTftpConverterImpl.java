package ru.nikskul.tftp.converter.datagram;

import ru.nikskul.logger.SystemLogger;
import ru.nikskul.tftp.packet.AckTftpPacket;
import ru.nikskul.tftp.packet.DataTftpPacket;
import ru.nikskul.tftp.packet.ErrorTftpPacket;
import ru.nikskul.tftp.packet.RequestTftpPacket;
import ru.nikskul.tftp.packet.TftpPacket;
import ru.nikskul.tftp.packet.impl.AckTftpPacketImpl;
import ru.nikskul.tftp.packet.impl.DataTftpPacketImpl;
import ru.nikskul.tftp.packet.impl.ErrorTftpPacketImpl;
import ru.nikskul.tftp.packet.impl.RrqTftpPacketImpl;
import ru.nikskul.tftp.packet.impl.WrqTftpPacketImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;

public class DatagramToTftpConverterImpl
    implements DatagramToTftpConverter {

    @Override
    public TftpPacket convert(int tid, DatagramPacket packet) {
        try (
            ByteArrayInputStream bais = new ByteArrayInputStream(
                packet.getData(), 0, packet.getLength()
            );
            DataInputStream in = new DataInputStream(bais)
        ) {
            short op = in.readShort();
            return switch (op) {
                case 1, 2 -> tftRequest(op, in, tid);
                case 3 -> tftpData(in, tid);
                case 4 -> tftpAck(in, tid);
                case 5 -> tftpError(in, tid);
                default -> throw new IllegalStateException(
                    "Unexpected value: " + op
                );
            };
        } catch (IOException e) {
            SystemLogger.log(e, getClass());
            throw new RuntimeException(e);
        }
    }

    private ErrorTftpPacket tftpError(
        DataInputStream in,
        int address
    ) throws IOException {
        short errorCode = in.readShort();
        String errMsg = new String(readString(in), StandardCharsets.US_ASCII);
        return new ErrorTftpPacketImpl(address, errorCode, errMsg);
    }

    private AckTftpPacket tftpAck(
        DataInputStream in,
        int tid
    ) throws IOException {
        short block = in.readShort();
        return new AckTftpPacketImpl(tid, block);
    }

    private DataTftpPacket tftpData(
        DataInputStream in,
        int tid
    ) throws IOException {
        short block = in.readShort();
        byte[] data = readBytes(in);
        return new DataTftpPacketImpl(tid, block, data);
    }

    private RequestTftpPacket tftRequest(
        short op,
        DataInputStream in,
        int tid
    ) throws IOException {
        String filename = new String(readString(in), StandardCharsets.US_ASCII);
        String mode = new String(readString(in), StandardCharsets.US_ASCII);

        if (op == 1) {
            return new RrqTftpPacketImpl(tid, filename, mode);
        } else {
            return new WrqTftpPacketImpl(tid, filename, mode);
        }
    }

    private static byte[] readString(
        DataInputStream in
    ) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(512);
        for (int b = in.read(); b != 0; b = in.read()) {
            out.write(b);
        }
        return out.toByteArray();
    }

    private static byte[] readBytes(
        DataInputStream in
    ) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(512);
        for (int b = in.read(); b != -1; b = in.read()) {
            out.write(b);
        }
        return out.toByteArray();
    }
}
