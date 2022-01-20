package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JFileChooser;

import common.WebRequest;

public class GingleSoftServer {
    static ArrayList<WebRequest> webRequests;
    static File file;

    public static void main(String[] args) {
        file = new File("./default.txt");
        deserializeWebRequests();
        MasterThread masterThread = new MasterThread();
        masterThread.start();
        try (Scanner scanner = new Scanner(System.in)) {
            int choice;
            do {
                System.out.println("0. USCITA");
                System.out.println("1. VISUALIZZA");
                System.out.println("2. AVVIA/ARRESTA");
                System.out.println("3. ESPORTA");
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 0:
                        serializeWebRequests();
                        if (masterThread.isAlive()) {
                            masterThread.interrupt();
                        }
                        break;
                    case 1:
                        for (WebRequest webRequest : webRequests) {
                            System.out.println(webRequest.toString());
                        }
                        break;
                    case 2:
                        if (masterThread.isAlive()) {
                            masterThread.interrupt();
                            System.out.println("Server spento");
                        } else {
                            masterThread = new MasterThread();
                            masterThread.start();
                            System.out.println("Server acceso");
                        }
                        break;
                    case 3:
                        JFileChooser jFileChooser = new JFileChooser();
                        int i = jFileChooser.showOpenDialog(null);
                        if (i == JFileChooser.APPROVE_OPTION) {
                            file = jFileChooser.getSelectedFile();
                        }
                        serializeWebRequests();
                    default:
                        break;
                }
            } while (choice != 0);
        }
    }

    private static void deserializeWebRequests() {
        try (FileInputStream fileInputStream = new FileInputStream(file);) {
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            webRequests = new ArrayList<>(Arrays.asList((WebRequest[]) objectInputStream.readObject()));
        } catch (IOException | ClassNotFoundException e) {
            webRequests = new ArrayList<WebRequest>();
        }
    }

    private static void serializeWebRequests() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(webRequests.toArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
