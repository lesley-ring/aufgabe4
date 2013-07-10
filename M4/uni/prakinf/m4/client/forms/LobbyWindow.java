package uni.prakinf.m4.client.forms;

import com.sun.deploy.util.ArrayUtil;
import uni.prakinf.m4.client.Client;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

public class LobbyWindow extends JFrame {
    private Client client;
    private JTextPane messagePane;
    private JPanel panel1;
    private JTextField messageTextField;
    private JButton mitspielenButton;
    private JButton neuesSpielButton;
    private JList playerList;
    private JButton abmeldenButton;
    private JLabel statusLabel;

    public LobbyWindow(Client client_) {
        this.client = client_;

        setContentPane(panel1);


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.onLobbyWindowExit();
            }
        });
        messageTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.onMessageLineEnter(messageTextField.getText());
                messageTextField.setText("");
            }
        });
        neuesSpielButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.onNewGame();
            }
        });
        mitspielenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playerList.getSelectedValue() instanceof String) {
                    String s = (String) playerList.getSelectedValue();
                    if (!s.isEmpty())
                        client.onJoinGame(playerList.getSelectedIndex() - 1);
                }
            }
        });
        abmeldenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.onLobbyWindowLogout();
            }
        });
        playerList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (playerList.getSelectedIndex() > 0)
                    client.setPrivateMessagePartner(playerList.getSelectedIndex() - 1);
                else
                    client.setPrivateMessagePartner(-1);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void resetState() {
        playerList.setListData(new Vector());

        messagePane.setText("Hallo!\n");
        messageTextField.setText("");
        messageTextField.requestFocus();
        statusLabel.setText("");
        setTitle("M4 Lobby");
    }

    public void addLineToMessagePane(String message) {
        messagePane.setText(messagePane.getText() + message + "\n");
        messagePane.setCaretPosition(messagePane.getText().length() - 1);
    }

    @SuppressWarnings("unchecked")
    public void updatePlayerList(String[] list) {
        String nlist[] = new String[list.length + 1];
        nlist[0] = "An alle senden";
        for(int i = 0; i < list.length; i++)
            nlist[i+1] = list[i];

        if (playerList.getSelectedIndex() > 0) {
            String selName = (String) playerList.getSelectedValue();
            playerList.setListData(nlist);
            for (int i = 0; i < list.length; i++) {
                if (list[i].equals(selName)) {
                    playerList.setSelectedIndex(i+1);
                    return;
                }
            }
            playerList.setSelectedIndex(0);
        } else {
            playerList.setListData(nlist);
            playerList.setSelectedIndex(0);
        }
    }

    public void updateStatus(String status) {
        statusLabel.setText(status);
    }

    public void updatePlayerName(String name) {
        setTitle("M4 Lobby - " + name);
    }
}
