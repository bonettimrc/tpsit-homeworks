package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.function.Consumer;

import common.WebRequest;

public class Worker implements Runnable {
    private Socket socket;
    private Consumer<WebRequest> callback;

    public Worker(Socket socket) {
        this.socket = socket;
    }

    public void setCallback(Consumer<WebRequest> callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream()) {
            long startTime = System.nanoTime();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            WebRequest webRequest = (WebRequest) objectInputStream.readObject();
            long elapsedNanos = System.nanoTime() - startTime;
            int durata = (int) (elapsedNanos / 1000000);
            webRequest.setDurata(durata);
            callback.accept(webRequest);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
