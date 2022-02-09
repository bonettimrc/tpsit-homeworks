package server;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import common.WebRequest;

public class GingleSoftServer implements Runnable {
    static List<WebRequest> webRequests = Collections.synchronizedList(new ArrayList<WebRequest>());
    private static final File DEFAULT_FILE = new File("./default.txt");
    private Master master = new Master();
    private Thread masterThread;
    private JButton startStopJButton = new JButton("Start/Stop");
    private JButton esportaJButton = new JButton("Esporta...");
    private JLabel clientConnessiJLabel = new JLabel("Client connessi:0");
    private JTextArea jTextArea = new JTextArea();

    @Override
    public void run() {
        JFrame jFrame = new JFrame();
        jFrame.setLayout(new BorderLayout());
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
        master.setOnNewWebRequest((WebRequest webRequest) -> {
            synchronized (jTextArea) {
                webRequests.add(webRequest);
                jTextArea.append(webRequestToString(webRequest));
                jTextArea.append("\n");
            }
        });
        master.setOnWorkerNumberChanged((Integer size) -> {
            synchronized (clientConnessiJLabel) {
                String format = String.format("Client connessi:%d", size);
                clientConnessiJLabel.setText(format);
            }
        });
        startStopJButton.addActionListener(new StartStopJButtonAction());
        esportaJButton.addActionListener(new EsportaJButtonAction());
        jFrame.addWindowListener(new JFrameWindowOnClosing());
        // Add north and center panes to this frame
        jFrame.add(jPanel, BorderLayout.NORTH);
        jFrame.add(jScrollPane, BorderLayout.CENTER);
        // Finish up server and frame initialization
        deserializeWebRequests();
        masterThread = new Thread(master);
        masterThread.start();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setBounds(0, 0, 700, 400);
        jFrame.setVisible(true);
    }

    public static void main(String[] args) {
        GingleSoftServer gingleSoftServer = new GingleSoftServer();
        // Schedules the application to be run at the correct time in the event queue.
        SwingUtilities.invokeLater(gingleSoftServer);
    }

    private void deserializeWebRequests() {
        try (FileInputStream fileInputStream = new FileInputStream(DEFAULT_FILE);) {
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Object readObject = objectInputStream.readObject();
            List<WebRequest> asList = (List<WebRequest>) readObject;
            for (WebRequest webRequest : asList) {
                jTextArea.append(webRequestToString(webRequest));
                jTextArea.append("\n");
            }
            webRequests.addAll(asList);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void serializeWebRequests(File file) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(webRequests);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class StartStopJButtonAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (masterThread.isAlive()) {
                masterThread.interrupt();
                System.out.println("Server spento");
            } else {
                masterThread = new Thread(master);
                masterThread.start();
                System.out.println("Server acceso");
            }
        }

    }

    private class EsportaJButtonAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser jFileChooser = new JFileChooser();
            int i = jFileChooser.showOpenDialog(null);
            if (i == JFileChooser.APPROVE_OPTION) {
                File file = jFileChooser.getSelectedFile();
                serializeWebRequests(file);
            }
        }

    }

    private class JFrameWindowOnClosing extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            serializeWebRequests(DEFAULT_FILE);
            super.windowClosing(e);
        }
    }

    private String webRequestToString(WebRequest webRequest) {
        String format = String.format("%s - %s - %s - %s - %ds - %s",
                webRequest.getDataOra().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")),
                "127.16.50.127",
                webRequest.getSeriale(),
                webRequest.getTipologia().toString(),
                webRequest.getDurata() / 1000,
                webRequest.getEsito().toString());
        return format;
    }
}
