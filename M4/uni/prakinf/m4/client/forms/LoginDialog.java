package uni.prakinf.m4.client.forms;

import uni.prakinf.m4.client.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginDialog extends JFrame {
    private Client client;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField serverTextField;
    private JTextField benutzernameTextField;
    private JPasswordField passwortPasswordField;

    public LoginDialog(Client client_) {
        this.client = client_;
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("M4 Login");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.onLoginDialogExit();
            }
        });

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.onLoginDialogOKClick(serverTextField.getText(), benutzernameTextField.getText(), String.valueOf(passwortPasswordField.getPassword()));
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.onLoginDialogExit();
            }
        });
    }

    public void resetEntries() {
        serverTextField.setText("localhost");
        benutzernameTextField.setText("");
        passwortPasswordField.setText("");
        invalidate();
    }
}
