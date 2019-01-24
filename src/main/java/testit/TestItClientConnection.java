package testit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class TestItClientConnection implements Runnable {

    private final BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<>();

    private final String hostname;
    private final int port;

    private Socket socket;
    private final Consumer<String> connectionEndedConsumer;

    private boolean active;

    public TestItClientConnection(String hostname, int port, Consumer<String> connectionEndedConsumer) {
        this.hostname = hostname;
        this.port = port;

        this.connectionEndedConsumer = connectionEndedConsumer;
    }

    public void connect() throws IOException {
        this.socket = new Socket(hostname, port);

        Thread networkingThread = new Thread(this);
        networkingThread.start();
    }

    public boolean isActive() {
        return active;
    }

    public void enqueueCommand(String command) {
        commandQueue.add(new Command(command, response -> {
            if (response != null)
                System.out.println("Missed response: " + response);
        }));
    }

    public void enqueueCommand(String command, Consumer<String> responseConsumer) {
        commandQueue.add(new Command(command, responseConsumer));
    }

    @Override
    public void run() {
        active = true;
        TestItClientState state = TestItClientState.UNDEFINED;
        StringBuilder builder = new StringBuilder();

        final Socket socketCopy = socket;
        try (socketCopy) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            Command currentCommand = null;

            int character = -2;
            while (active && (character = in.read()) != -1) {
                builder.append((char) character);

                switch (state) {
                    case WAITING_FOR_PROMT:
                        if (character == ' ') {
                            builder = new StringBuilder();

                            if (currentCommand != null)
                                currentCommand.cleanUp();

                            // promt was sent -> we send command, if there is any?
                            try {
                                currentCommand = commandQueue.take();
                            } catch (InterruptedException e) {
                                break;
                            }

                            System.out.println("Executing command: " +  currentCommand.getCommand());
                            out.println(currentCommand.getCommand());
                            out.flush();

                            state = TestItClientState.UNDEFINED; // maybe it's a command with response, maybe not
                            break;
                        }
                        // when it is not a space ' ' whe got unexpectedly into WAITING_FOR_PROMT -> forward to WAITING_FOR_OUTPUT
                    case UNDEFINED:
                        if (state.equals(TestItClientState.UNDEFINED) && character == '>') {
                            state = TestItClientState.WAITING_FOR_PROMT;
                            break;
                        }
                        // otherwise it is probably some output content -> jump to the next case
                    case WAITING_FOR_OUTPUT:
                        state = TestItClientState.WAITING_FOR_OUTPUT;
                        if (character == '\n') {
                            builder.deleteCharAt(builder.length() -1 ); // delete last char (\n)
                            String output = builder.toString();
                            builder = new StringBuilder();

                            if (currentCommand != null)
                                currentCommand.sendResponse(output);
                            else
                                System.err.println("Got unexpected output from client before command was executed: " + output);

                            state = TestItClientState.UNDEFINED; // don't know what comes next, maybe a command promt?
                        }
                        break;
                }
            }

            if (character == -1)
                System.out.println("End of stream");
        } catch (IOException e) {
            active = false;
            connectionEndedConsumer.accept("Error establishing connection: " + e.getMessage());
            return;
        }

        active = false;
        connectionEndedConsumer.accept(null);
    }

}