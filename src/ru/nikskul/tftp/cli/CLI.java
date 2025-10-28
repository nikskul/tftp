package ru.nikskul.tftp.cli;

import ru.nikskul.tftp.api.client.TftpClient;
import ru.nikskul.tftp.api.session.provider.TftpSessionProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.StringTokenizer;

public class CLI {

    private static final List<String> EXIT_CMD = List.of(
        "exit", "quit", "q"
    );

    private InetSocketAddress address;

    private final TftpClient client;
    private final TftpSessionProvider sessionProvider;

    public CLI(TftpClient client, TftpSessionProvider sessionProvider) {
        this.client = client;
        this.sessionProvider = sessionProvider;
    }

    public void start(String... args) {
        try (
            var pr = new BufferedReader(new InputStreamReader(System.in));
            var pw = new PrintWriter(new OutputStreamWriter(System.out))
        ) {
            if (args.length > 0) {
                address = new InetSocketAddress(
                    args[0],
                    69
                );
            } else {
                synchronized (System.out) {
                    pw.print("address: ");
                    pw.flush();
                    address = new InetSocketAddress(
                        pr.readLine(),
                        69
                    );
                }
            }


            String cmd = "";
            while (!EXIT_CMD.contains(cmd)) {
                synchronized (System.out) {
                    pw.print("tftp> ");
                    pw.flush();
                    cmd = pr.readLine();
                }

                StringTokenizer tokens = new StringTokenizer(cmd);
                if (!tokens.hasMoreTokens()) continue;
                switch (tokens.nextToken()) {
                    case "get" -> startGet(tokens);
                    case "put" -> startPut(tokens);
                    case "binary" -> setBinaryMode();
                    case "netascii" -> setNetasciiMode();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setBinaryMode() {
        client.setBinaryMode();
    }

    private void setNetasciiMode() {
        client.setNetasciiMode();
    }

    private void startPut(StringTokenizer tokens) {
        var tid = client.sendWrq(address, tokens.nextToken());
        waitUntilSessionEnd(tid);
    }

    private void startGet(StringTokenizer tokens) {
        var tid = client.sendRrq(address, tokens.nextToken());
        waitUntilSessionEnd(tid);
    }

    private void waitUntilSessionEnd(int tid) {
        while (sessionProvider.getSession(() -> tid) != null) {
            Thread.onSpinWait();
        }
    }

}
