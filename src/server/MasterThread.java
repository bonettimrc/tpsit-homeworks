package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MasterThread extends Thread {
    ArrayList<WorkerThread> workerThreads;
    ServerSocket serverSocket;

    public MasterThread() {
        workerThreads = new ArrayList<WorkerThread>();
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
                workerThread.start();
                workerThreads.add(workerThread);
            }
        } catch (IOException e) {
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        for (WorkerThread workerThread : workerThreads) {
            try {
                workerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        workerThreads.clear();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
