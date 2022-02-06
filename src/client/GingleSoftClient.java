package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import common.WebRequest;
import common.util.ArrayListEvent;
import common.util.ObservableList;

public class GingleSoftClient extends JFrame implements ActionListener {
    private JTextArea jTextArea = new JTextArea();
    private JButton inviaRichiestaButton = new JButton("Invia Richiesta");
    private JLabel clientConnessiJLabel = new JLabel("Client connessi:0");
    private JButton attaccoButton = new JButton("Attacco!");
    private JComboBox<common.Type> jComboBox = new JComboBox<common.Type>(CHOOSABLE_TYPES);
    protected static final int REQUEST_AMOUNT = 20;
    protected static final common.Type[] CHOOSABLE_TYPES = new common.Type[] { common.Type.Aggiornamento,
            common.Type.Attivazione, common.Type.Feedback };
    static ObservableList<WebRequest> webRequests = new ObservableList<WebRequest>();
    private int connectedClients = 0;

    public GingleSoftClient() {
        setLayout(new BorderLayout());
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
        inviaRichiestaButton.addActionListener(this);
        attaccoButton.addActionListener(this);
        webRequests.addListener(ObservableList.ON_ADD, (ArrayListEvent e) -> {
            WebRequest webRequest = (WebRequest) e.getEventData();
            jTextArea.append(webRequest.toString());
            jTextArea.append("\n");
        });
        // Add north and center panes to this frame
        add(jPanel, BorderLayout.NORTH);
        add(jScrollPane, BorderLayout.CENTER);
        // By default the frame is maximized
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        GingleSoftClient gingleSoftClient = new GingleSoftClient();
        if (args.length != 0) {
            gingleSoftClient.setExtendedState(java.awt.Frame.NORMAL);
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int width = Integer.parseInt(args[2]);
            int height = Integer.parseInt(args[3]);
            gingleSoftClient.setBounds(x, y, width, height);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == attaccoButton) {
            AttackMasterThread attackMasterThread = new AttackMasterThread(REQUEST_AMOUNT);
            connectedClients += REQUEST_AMOUNT;
            attackMasterThread.attackClientThreads.addListener(ObservableList.ON_REMOVE,
                    (ArrayListEvent arrayListEvent) -> {
                        connectedClients--;
                        String format = String.format("Client connessi:%d", connectedClients);
                        clientConnessiJLabel.setText(format);
                    });
            attackMasterThread.start();
        }
        if (source == inviaRichiestaButton) {
            common.Type type = (common.Type) jComboBox.getSelectedItem();
            AttackClientThread attackClientThread = new AttackClientThread(type);
            connectedClients++;
            String format = String.format("Client connessi:%d", connectedClients);
            clientConnessiJLabel.setText(format);
            attackClientThread.setOnEnd(() -> {
                connectedClients--;
                String format1 = String.format("Client connessi:%d", connectedClients);
                clientConnessiJLabel.setText(format1);
            });
            attackClientThread.start();
        }

    }
}
