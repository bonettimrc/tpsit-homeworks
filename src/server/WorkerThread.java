package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import common.Result;
import common.WebRequest;

public class WorkerThread extends Thread {
    private Socket socket;

    public WorkerThread(Socket socket) {
        this.socket = socket;
    }

    private Runnable onEnd;

    public void setOnEnd(Runnable onEnd) {
        this.onEnd = onEnd;
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
            Result esito = Result.randomResult();
            webRequest.setEsito(esito);
            synchronized (GingleSoftServer.class) {
                GingleSoftServer.webRequests.add(webRequest);
            }
            onEnd.run();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
