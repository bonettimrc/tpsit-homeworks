package client;

public class AttackMasterThread extends Thread {
    private int requestsAmount;

    public AttackMasterThread(int requestsAmount) {
        super();
        this.requestsAmount = requestsAmount;
    }

    @Override
    public void run() {
        for (int i = 0; i < requestsAmount; i++) {
            new AttackClientThread().start();
        }
    }

}
