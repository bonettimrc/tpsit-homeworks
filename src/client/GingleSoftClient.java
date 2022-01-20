package client;

import java.util.Scanner;

public class GingleSoftClient {
    public static void main(String[] args) throws Exception {

        try (Scanner scanner = new Scanner(System.in)) {
            int choice;
            do {
                System.out.println("0. USCITA");
                System.out.println("1. INVIA RICHIESTA");
                System.out.println("2. ATTAK!");
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 0:
                        break;
                    case 1:
                        new AttackClientThread().start();
                        break;
                    case 2:
                        System.out.println("Inserisci numero di richieste da eseguire:");
                        int requestsAmount = Integer.parseInt(scanner.nextLine());
                        new AttackMasterThread(requestsAmount).start();
                        break;
                    default:
                        break;
                }
            } while (choice != 0);
        }
    }
}
