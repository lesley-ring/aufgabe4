package uni.prakinf.m4.client.forms;


import uni.prakinf.m4.client.Client;
import uni.prakinf.m4.client.IClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameWindow extends JFrame {
    private Client client;
    private IClient.Spiel spiel;
    private JLabel gameStatus;
    private JPanel gamePanel;
    private JPanel panel1;
    private int lastX, lastY;
    private JButton[][] buttons;

    public GameWindow(Client client_) {
        this.client = client_;
        lastX = 0;
        lastY = 0;
        buttons = new JButton[0][0];

        setContentPane(panel1);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.leaveGame();
            }
        });
    }

    public void updateWindow(boolean enabled, String status, IClient.VierGewinntStein[][] spielfeld) {
        loadGame(IClient.Spiel.VIER_GEWINNT);
        loadButtons(spielfeld.length, spielfeld[0].length + 1);
        for (int x = 0; x < lastX; x++) {
            for (int y = 0; y < (lastY - 1); y++) {
                switch (spielfeld[x][y]) {
                    case GEGNER:
                        buttons[x][y + 1].setBackground(Color.RED);
                        buttons[x][y + 1].setText(" ");
                        break;
                    case SPIELER:
                        buttons[x][y + 1].setBackground(Color.YELLOW);
                        buttons[x][y + 1].setText(" ");
                        break;
                    case LEER:
                        buttons[x][y + 1].setBackground(Color.BLACK);
                        buttons[x][y + 1].setText(" ");
                        break;
                }
            }
            buttons[x][0].setText("v");
            buttons[x][0].setBackground(Color.lightGray);
        }
        updateWindow(enabled, status);
    }

    public void updateWindow(boolean enabled, String status, boolean[][] spielfeld) {
        loadGame(IClient.Spiel.CHOMP);
        loadButtons(spielfeld.length, spielfeld[0].length);

        for (int x = 0; x < lastX; x++)
            for (int y = 0; y < lastY; y++) {
                buttons[x][y].setBackground(spielfeld[x][y] ? new Color(139, 69, 19) : Color.lightGray);
                buttons[x][y].setText(" ");
            }

        updateWindow(enabled, status);
    }

    public void updateWindow(boolean enabled, String status) {
        gameStatus.setText(status);
        for (int x = 0; x < lastX; x++)
            for (int y = 0; y < lastY; y++) {
                buttons[x][y].setEnabled(enabled);
            }

        pack();
        invalidate();

    }

    private void loadButtons(int sizex, int sizey) {
        if (sizex != lastX || sizey != lastY) {
            gamePanel.removeAll();
            buttons = new JButton[sizex][sizey];
            gamePanel.setLayout(new GridBagLayout());
            for (int x = 0; x < sizex; x++) {
                for (int y = 0; y < sizey; y++) {
                    buttons[x][y] = new JButton("?");
                    GridBagConstraints c = new GridBagConstraints();
                    c.gridx = x;
                    c.gridy = y;
                    gamePanel.add(buttons[x][y], c);

                    final int fx = x;
                    final int fy = y;
                    buttons[x][y].addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            client.buttonClicked(fx, fy);
                        }
                    });
                }
            }
            lastX = sizex;
            lastY = sizey;
            pack();
        }
    }

    private void loadGame(IClient.Spiel spiel) {
        if (this.spiel != spiel) {
            //...

            switch (spiel) {
                case CHOMP:
                    setTitle("Chomp");
                    break;
                case VIER_GEWINNT:
                    setTitle("Vier Gewinnt");
                    break;
            }
            this.spiel = spiel;
        }

    }

    public void resetState() {
        gamePanel.removeAll();
        gameStatus.setText("Warten...");
        lastX = 0;
        lastY = 0;
    }
}
