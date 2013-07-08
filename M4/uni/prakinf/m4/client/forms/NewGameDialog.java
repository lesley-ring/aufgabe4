package uni.prakinf.m4.client.forms;

import uni.prakinf.m4.client.Client;
import uni.prakinf.m4.client.IClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewGameDialog extends JDialog {
    private Client client;

    private JSpinner widthSpinner;
    private JSpinner heightSpinner;
    private JButton vierGewinntButton;
    private JButton chompButton;
    private JPanel panel1;

    public NewGameDialog(Client client_) {
        this.client = client_;
        setContentPane(panel1);
        setTitle("Neues Spiel");
        vierGewinntButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                client.newGame(IClient.Spiel.VIER_GEWINNT, (Integer) widthSpinner.getValue(), (Integer) heightSpinner.getValue());
            }
        });
        chompButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                client.newGame(IClient.Spiel.CHOMP, (Integer) widthSpinner.getValue(), (Integer) heightSpinner.getValue());
            }
        });
    }

    public void resetState() {
        widthSpinner.setValue(7);
        heightSpinner.setValue(6);
    }
}
