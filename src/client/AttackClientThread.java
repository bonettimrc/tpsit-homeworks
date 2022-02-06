package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

import common.Type;
import common.WebRequest;

public class AttackClientThread extends Thread {
    private final Random RANDOM = new Random();
    private Type type;

    public AttackClientThread(Type type) {
        super();
        this.type = type;
    }

    public AttackClientThread() {
        super();
    }

    private Runnable onEnd;

    public void setOnEnd(Runnable onEnd) {
        this.onEnd = onEnd;
    }

    @Override
    public void run() {
        WebRequest webRequest;
        if (type != null) {
            webRequest = new WebRequest(type);
        } else {
            webRequest = new WebRequest();
        }
        try (Socket socket = new Socket("localhost", 8082)) {
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            long millis = (RANDOM.nextInt(9) + 1) * 1000;
            Thread.sleep(millis);
            objectOutputStream.writeObject(webRequest);
            objectOutputStream.flush();
            synchronized (GingleSoftClient.class) {
                GingleSoftClient.webRequests.add(webRequest);
            }
            onEnd.run();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
