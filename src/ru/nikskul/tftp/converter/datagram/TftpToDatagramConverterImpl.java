package ru.nikskul.tftp.converter.datagram;

import ru.nikskul.logger.SystemLogger;
import ru.nikskul.tftp.packet.AckTftpPacket;
import ru.nikskul.tftp.packet.DataTftpPacket;
import ru.nikskul.tftp.packet.ErrorTftpPacket;
import ru.nikskul.tftp.packet.RequestTftpPacket;
import ru.nikskul.tftp.packet.TftpPacket;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class TftpToDatagramConverterImpl implements TftpToDatagramConverter {
    @Override
    public DatagramPacket convert(TftpPacket packet) {
        Objects.requireNonNull(packet);
        try {
            return switch (packet) {
                case RequestTftpPacket request -> convertRequest(request);
                case DataTftpPacket data -> convertData(data);
                case AckTftpPacket ack -> convertAck(ack);
                case ErrorTftpPacket error -> convertError(error);
                default -> throw new IllegalStateException(
                    "Unexpected value: " + packet.getOpcode()
                );
            };
        } catch (IOException e) {
            SystemLogger.log(e, getClass());
            throw new RuntimeException(e);
        }
    }

    private DatagramPacket convertError(ErrorTftpPacket error) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);

        out.writeShort(error.getOpcode());
        out.writeShort(error.getErrorCode());
        out.write(error.getErrMsg().getBytes(StandardCharsets.US_ASCII));
        out.write(0);

        return new DatagramPacket(
            baos.toByteArray(),
            baos.size()
        );
    }

    private DatagramPacket convertAck(AckTftpPacket ack) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);

        out.writeShort(ack.getOpcode());
        out.writeShort(ack.getBlock());

        return new DatagramPacket(
            baos.toByteArray(),
            baos.size()
        );
    }

    private DatagramPacket convertData(DataTftpPacket data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);

        out.writeShort(data.getOpcode());
        out.writeShort(data.getBlock());
        out.write(data.getData());

        return new DatagramPacket(
            baos.toByteArray(),
            baos.size()
        );
    }

    private DatagramPacket convertRequest(RequestTftpPacket packet) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);

        out.writeShort(packet.getOpcode());
        out.write(packet.getFilename().getBytes(StandardCharsets.US_ASCII));
        out.write(0);
        out.write(packet.getMode().getBytes(StandardCharsets.US_ASCII));
        out.write(0);

        return new DatagramPacket(
            baos.toByteArray(),
            baos.size()
        );
    }
}
