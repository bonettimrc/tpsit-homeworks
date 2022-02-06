package server;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import common.WebRequest;
import common.util.ArrayListEvent;
import common.util.ObservableList;

public class GingleSoftServer extends JFrame implements ActionListener {
    static ObservableList<WebRequest> webRequests = new ObservableList<WebRequest>();
    private static final File DEFAULT_FILE = new File("./default.txt");
    private MasterThread masterThread = new MasterThread();
    private JButton startStopJButton = new JButton("Start/Stop");
    private JButton esportaJButton = new JButton("Esporta...");
    private JLabel clientConnessiJLabel = new JLabel("Client connessi:0");
    private JTextArea jTextArea = new JTextArea();

    public GingleSoftServer() {
        setLayout(new BorderLayout());
        // Initialize north panel and add components
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        jPanel.add(startStopJButton);
        jPanel.add(clientConnessiJLabel);
        jPanel.add(esportaJButton);
        // Initialize center scrollPane
        JScrollPane jScrollPane = new JScrollPane(jTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Add listeners
        webRequests.addListener(ObservableList.ON_ADD, (ArrayListEvent e) -> {
            WebRequest webRequest = (WebRequest) e.getEventData();
            jTextArea.append(webRequest.toString());
            jTextArea.append("\n");
        });
        masterThread.workerThreads.addListener(ObservableList.ON_SIZE_CHANGED, (ArrayListEvent e) -> {
            int size = (int) e.getEventData();
            String format = String.format("Client connessi:%d", size);
            clientConnessiJLabel.setText(format);
        });
        startStopJButton.addActionListener(this);
        esportaJButton.addActionListener(this);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                serializeWebRequests(DEFAULT_FILE);
                if (masterThread.isAlive()) {
                    masterThread.interrupt();
                }
                super.windowClosing(e);
            }
        });
        // Add north and center panes to this frame
        add(jPanel, BorderLayout.NORTH);
        add(jScrollPane, BorderLayout.CENTER);
        // // By default the frame is maximized
        deserializeWebRequests();
        masterThread.start();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        GingleSoftServer gingleSoftServer = new GingleSoftServer();
        if (args.length != 0) {
            gingleSoftServer.setExtendedState(java.awt.Frame.NORMAL);
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int width = Integer.parseInt(args[2]);
            int height = Integer.parseInt(args[3]);
            gingleSoftServer.setBounds(x, y, width, height);
        }
    }

    private static void deserializeWebRequests() {
        try (FileInputStream fileInputStream = new FileInputStream(DEFAULT_FILE);) {
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Object readObject = objectInputStream.readObject();
            List<WebRequest> asList = (List<WebRequest>) readObject;
            webRequests.addAll(asList);
        } catch (IOException | ClassNotFoundException e) {
            // webRequests is already initialized
            e.printStackTrace();
        }
    }

    private static void serializeWebRequests(File file) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(webRequests);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == startStopJButton) {
            toggleMasterServer();
        }
        if (source == esportaJButton) {
            serializeToChoosenFile();
        }

    }

    private void serializeToChoosenFile() {
        JFileChooser jFileChooser = new JFileChooser();
        int i = jFileChooser.showOpenDialog(null);
        if (i == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile();
            serializeWebRequests(file);
        }
    }

    private void toggleMasterServer() {
        if (masterThread.isAlive()) {
            masterThread.interrupt();
            System.out.println("Server spento");
        } else {
            masterThread = new MasterThread();
            masterThread.start();
            System.out.println("Server acceso");
        }
    }
}
