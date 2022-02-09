package client;

import java.util.function.Consumer;

import common.WebRequest;

public class AttackMaster implements Runnable {
    private int requestsAmount;
    private Consumer<WebRequest> onNewWebRequest;
    private Runnable onNewWorker;

    public void setOnNewWebRequest(Consumer<WebRequest> onNewWebRequest) {
        this.onNewWebRequest = onNewWebRequest;
    }

    public void setOnNewWorker(Runnable onNewWorker) {
        this.onNewWorker = onNewWorker;
    }

    public AttackMaster(int requestsAmount) {
        super();
        this.requestsAmount = requestsAmount;
    }

    @Override
    public void run() {
        for (int i = 0; i < requestsAmount; i++) {
            SendRequestWorker sendRequestWorker = new SendRequestWorker();
            onNewWorker.run();
            sendRequestWorker.setCallback((WebRequest webRequest) -> {
                onNewWebRequest.accept(webRequest);
            });
            Thread thread = new Thread(sendRequestWorker);
            thread.start();
        }
    }

}
