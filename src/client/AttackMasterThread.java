package client;

import common.util.ObservableList;

public class AttackMasterThread extends Thread {
    private int requestsAmount;
    protected ObservableList<AttackClientThread> attackClientThreads;

    public AttackMasterThread(int requestsAmount) {
        super();
        this.requestsAmount = requestsAmount;
        attackClientThreads = new ObservableList<AttackClientThread>();
    }

    @Override
    public void run() {
        for (int i = 0; i < requestsAmount; i++) {
            AttackClientThread attackClientThread = new AttackClientThread();
            attackClientThread.setOnEnd(() -> {
                attackClientThreads.remove(attackClientThread);
            });
            attackClientThread.start();
            attackClientThreads.add(attackClientThread);
        }
    }

}
