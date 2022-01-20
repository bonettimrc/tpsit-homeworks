package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

import common.WebRequest;

public class AttackClientThread extends Thread {
    private final Random RANDOM = new Random();

    @Override
    public void run() {
        WebRequest webRequest = new WebRequest();
        try (Socket socket = new Socket("localhost", 8082)) {
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            long millis = (RANDOM.nextInt(9) + 1) * 1000;
            Thread.sleep(millis);
            objectOutputStream.writeObject(webRequest);
            objectOutputStream.flush();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
