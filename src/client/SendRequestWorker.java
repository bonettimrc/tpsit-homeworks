package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.function.Consumer;

import common.Type;
import common.WebRequest;

public class SendRequestWorker implements Runnable {
    private final Random RANDOM = new Random();
    private Type type;
    private Consumer<WebRequest> callback;

    public void setCallback(Consumer<WebRequest> callback) {
        this.callback = callback;
    }

    public SendRequestWorker(Type type) {
        this.type = type;
    }

    public SendRequestWorker() {
    }

    @Override
    public void run() {
        try (Socket socket = new Socket("localhost", 8082)) {
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            long millis = (RANDOM.nextInt(9) + 1) * 1000;
            Thread.sleep(millis);
            WebRequest webRequest;
            if (type != null) {
                webRequest = new WebRequest(type);
            } else {
                webRequest = new WebRequest();
            }
            objectOutputStream.writeObject(webRequest);
            objectOutputStream.flush();
            callback.accept(webRequest);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
