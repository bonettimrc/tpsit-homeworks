package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import common.WebRequest;

public class GingleSoftClient implements Runnable {
    private JTextArea jTextArea = new JTextArea();
    private JButton inviaRichiestaButton = new JButton("Invia Richiesta");
    private JLabel clientConnessiJLabel = new JLabel("Client connessi:0");
    private JButton attaccoButton = new JButton("Attacco!");
    private JComboBox<common.Type> jComboBox = new JComboBox<common.Type>(common.Type.CHOOSABLE_TYPES);
    protected static final int REQUEST_AMOUNT = 20;
    private int connectedClients = 0;

    @Override
    public void run() {
        JFrame jFrame = new JFrame();
        jFrame.setLayout(new BorderLayout());
        // Initialize north panel and add components
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        jPanel.add(jComboBox);
        jPanel.add(inviaRichiestaButton);
        jPanel.add(clientConnessiJLabel);
        jPanel.add(attaccoButton);
        // Initialize center scrollPane
        JScrollPane jScrollPane = new JScrollPane(jTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Add listeners
        inviaRichiestaButton.addActionListener(new InviaRichiestaButtonAction());
        attaccoButton.addActionListener(new AttaccoButtonAction());
        // Add north and center panes to this frame
        jFrame.add(jPanel, BorderLayout.NORTH);
        jFrame.add(jScrollPane, BorderLayout.CENTER);
        // By default the frame is maximized
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setBounds(700, 0, 700, 400);
        jFrame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        GingleSoftClient gingleSoftClient = new GingleSoftClient();
        // Schedules the application to be run at the correct time in the event queue.
        SwingUtilities.invokeLater(gingleSoftClient);
    }

    private class InviaRichiestaButtonAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            common.Type type = (common.Type) jComboBox.getSelectedItem();
            SendRequestWorker sendRequestWorker = new SendRequestWorker(type);
            clientConnected();
            sendRequestWorker.setCallback(GingleSoftClient.this::clientDisconnected);
            Thread thread = new Thread(sendRequestWorker);
            thread.start();
        }
    }

    private class AttaccoButtonAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            AttackMaster attackMaster = new AttackMaster(REQUEST_AMOUNT);
            attackMaster.setOnNewWebRequest(GingleSoftClient.this::clientDisconnected);
            attackMaster.setOnNewWorker(GingleSoftClient.this::clientConnected);
            Thread thread = new Thread(attackMaster);
            thread.start();
        }
    }

    private synchronized void clientDisconnected(WebRequest webRequest) {
        connectedClients--;
        String format1 = String.format("Client connessi:%d", connectedClients);
        clientConnessiJLabel.setText(format1);
        jTextArea.append(webRequestToString(webRequest));
        jTextArea.append("\n");
    }

    private synchronized void clientConnected() {
        connectedClients++;
        String format = String.format("Client connessi:%d", connectedClients);
        clientConnessiJLabel.setText(format);
    }

    private String webRequestToString(WebRequest webRequest) {
        String format = String.format("%s - %s - %s - %ds - %s",
                webRequest.getDataOra().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")),
                webRequest.getSeriale(),
                webRequest.getTipologia().toString(),
                webRequest.getDurata() / 1000,
                webRequest.getEsito().toString());
        return format;
    }
}
