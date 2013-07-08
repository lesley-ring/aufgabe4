package uni.prakinf.m4.server.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ServerManagerWindow extends JFrame {
    private ServerManager manager;
    private JTextPane logPanel;
    private JPanel panel1;
    private JButton startButton;
    private JButton stopButton;
    private JButton passResetButton;

    public ServerManagerWindow(ServerManager manager_) {
        this.manager = manager_;

        setContentPane(panel1);
        setPreferredSize(new Dimension(800, 480));
        pack();

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manager.startServer();
            }
        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manager.stopServer();
            }
        });
        passResetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manager.resetPasswords();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                manager.stopServer();
                System.exit(0);
            }
        });
    }

    public JTextPane getLogPane() {
        return logPanel;
    }
}
