package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import common.Result;
import common.WebRequest;

public class WorkerThread extends Thread {
    Socket socket;

    public WorkerThread(Socket socket) {
        this.socket = socket;
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
            GingleSoftServer.webRequests.add(webRequest);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
