package testit;

import java.util.function.Consumer;

public class Command {

    private String command;
    private Consumer<String> responseConsumer;

    private boolean gotResponse;

    public Command(String command, Consumer<String> responseConsumer) {
        this.command = command;
        this.responseConsumer = responseConsumer;
    }

    public String getCommand() {
        return command;
    }

    public void cleanUp() {
        if (!gotResponse)
            sendResponse(null); // null indicating command successfully returned but no output
    }

    public void sendResponse(String response) {
        gotResponse = true;
        responseConsumer.accept(response);
    }

}