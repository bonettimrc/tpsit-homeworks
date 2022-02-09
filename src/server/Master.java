package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import common.WebRequest;

public class Master implements Runnable {
    protected List<Worker> workers;
    private ServerSocket serverSocket;
    private Consumer<WebRequest> onNewWebRequest;
    private Consumer<Integer> onWorkerNumberChanged;

    public Master() {
        workers = Collections.synchronizedList(new ArrayList<Worker>());
    }

    public void setOnNewWebRequest(Consumer<WebRequest> onNewWebRequest) {
        this.onNewWebRequest = onNewWebRequest;
    }

    public void setOnWorkerNumberChanged(Consumer<Integer> onWorkerNumberChanged) {
        this.onWorkerNumberChanged = onWorkerNumberChanged;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(8082);
            while (!Thread.currentThread().isInterrupted()) {
                Socket accept = serverSocket.accept();
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                Worker worker = new Worker(accept);
                workers.add(worker);
                onWorkerNumberChanged.accept(workers.size());
                worker.setCallback((WebRequest webRequest) -> {
                    onNewWebRequest.accept(webRequest);
                    workers.remove(worker);
                    onWorkerNumberChanged.accept(workers.size());
                });
                Thread workerThread = new Thread(worker);
                workerThread.start();
            }
            serverSocket.close();
            workers.clear();
            onWorkerNumberChanged.accept(workers.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
