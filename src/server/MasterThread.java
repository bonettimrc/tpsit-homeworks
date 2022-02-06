package server;

import java.net.ServerSocket;
import java.net.Socket;

import common.util.ObservableList;

public class MasterThread extends Thread {
    protected ObservableList<WorkerThread> workerThreads;
    private ServerSocket serverSocket;

    public MasterThread() {
        workerThreads = new ObservableList<WorkerThread>();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(8082);
            while (!isInterrupted()) {
                Socket accept = serverSocket.accept();
                if (isInterrupted()) {
                    break;
                }
                WorkerThread workerThread = new WorkerThread(accept);
                workerThread.setOnEnd(() -> {
                    workerThreads.remove(workerThread);
                });
                workerThread.start();
                workerThreads.add(workerThread);
            }
            for (WorkerThread workerThread : workerThreads) {
                workerThread.join();
                workerThreads.remove(workerThread);
            }
            serverSocket.close();
        } catch (Exception e) {
        }
    }
}
