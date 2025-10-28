package ru.nikskul;

import ru.nikskul.tftp.api.client.SimpleTftpClient;
import ru.nikskul.tftp.api.server.TftpServer;
import ru.nikskul.tftp.api.session.factory.SimpleTftpSessionFactory;
import ru.nikskul.tftp.api.session.provider.TftpSessionProviderImpl;
import ru.nikskul.tftp.cli.CLI;
import ru.nikskul.tftp.converter.datagram.DatagramToTftpConverterImpl;
import ru.nikskul.tftp.converter.datagram.TftpToDatagramConverterImpl;
import ru.nikskul.tftp.datagram.receiver.DatagramReceiverImpl;
import ru.nikskul.tftp.file.provider.TftpFileProviderImpl;
import ru.nikskul.tftp.file.tftp.reader.TftpFileReaderImpl;
import ru.nikskul.tftp.file.tftp.writer.TftpFileWriterImpl;
import ru.nikskul.tftp.handler.impl.ack.AckTftpHandlerSendData;
import ru.nikskul.tftp.handler.impl.data.DataTftpHandlerWrite;
import ru.nikskul.tftp.handler.impl.error.ErrorTftpHandlerCloseSession;
import ru.nikskul.tftp.handler.impl.rrq.RrqTftpHandlerStartSend;
import ru.nikskul.tftp.handler.impl.wrq.WrqTftpHandlerStartWrite;
import ru.nikskul.tftp.packet.factory.TftpPacketFactoryImpl;
import ru.nikskul.tftp.resolver.TftpPacketResolverImpl;
import ru.nikskul.tftp.send.impl.TftpSendUseCaseImpl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;

public class Main {

    public static void main(String[] args) {
        var sessionProvider = new TftpSessionProviderImpl(new ConcurrentHashMap<>());

        var tftpToDatagramConverter = new TftpToDatagramConverterImpl();
        var sendUseCase = new TftpSendUseCaseImpl(
            sessionProvider,
            tftpToDatagramConverter
        );

        var fileProvider = new TftpFileProviderImpl(new ConcurrentHashMap<>());
        var fileStampedLock = new StampedLock();
        var tftpFileReader = new TftpFileReaderImpl(
            fileStampedLock,
            fileProvider
        );
        var tftpFileWriter = new TftpFileWriterImpl(
            fileStampedLock,
            fileProvider
        );

        var packetFactory = new TftpPacketFactoryImpl();
        var error = new ErrorTftpHandlerCloseSession(null, sessionProvider);
        var rrq = new RrqTftpHandlerStartSend(
            null,
            tftpFileReader,
            sendUseCase, packetFactory
        );
        var wrq = new WrqTftpHandlerStartWrite(
            null,
            tftpFileWriter,
            sendUseCase, packetFactory
        );
        var ack = new AckTftpHandlerSendData(
            null,
            sendUseCase,
            tftpFileReader, packetFactory
        );
        var data = new DataTftpHandlerWrite(
            null,
            tftpFileWriter,
            sendUseCase, packetFactory
        );


        var startChainList = List.of(data, ack, rrq, wrq, error);
        var tftpPacketResolver = new TftpPacketResolverImpl(startChainList);
        var datagramToTftpConverter = new DatagramToTftpConverterImpl();
        var datagramReceiver = new DatagramReceiverImpl(
            datagramToTftpConverter,
            tftpPacketResolver
        );
        var sessionFactory = new SimpleTftpSessionFactory(
            datagramReceiver,
            sessionProvider,
            tftpToDatagramConverter
        );

        var server = new TftpServer(sessionFactory);
        Thread.ofVirtual().name("TFTP-Server").start(server);


        var client = new SimpleTftpClient(
            sessionFactory,
            packetFactory,
            tftpToDatagramConverter,
            tftpFileWriter,
            tftpFileReader
        );

        var cli = new CLI(client, sessionProvider);
        cli.start(args);
    }
}
