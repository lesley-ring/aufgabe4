package uni.prakinf.m4.client;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

import javax.swing.JOptionPane;


public class Chomp implements ActionListener {
    private Chompfeld chompfeld;
    Frame frame = new Frame("Chomp");
    int m;
    int n;
    //Chompspieler cs1 = new Chompspieler();
    //Chompspieler cs2 = new Chompspieler();
    Chompspieler gegenspieler = new Chompspieler();
    int aktSpieler;
    String text;
    boolean spielenmitComputer;
    boolean localspiel;
    int x, y;
    boolean[][] spielfeld = new boolean[m][n];
    private MenuItem exit, start, close;
    int zustand; // 0 für Spiel fängt nicht an; 1 für ZUG; 2 für WARTEN; 3 für GEWONNEN; 4 für VERLOREN; 5 für ABBRUCH

    void startChomp() {
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent arg0) {
                System.exit(0);
            }
        });
        frame.setBackground(Color.white);
        if (n <= 40 & m <= 40) frame.setSize(25 * n + 30, 25 * m + 30);
        else {
            if (n <= 50 & m <= 50) frame.setSize(20 * n + 30, 20 * m + 30);
            else {
                if (n <= 66 & m <= 66) frame.setSize(15 * n + 30, 15 * m + 30);
                else {
                    if (n <= 100 & m <= 100) frame.setSize(10 * n + 30, 10 * m + 30);
                    else {
                        if (n <= 200 & m <= 200) frame.setSize(5 * n + 30, 5 * m + 30);
                        else frame.setSize(n + 30, m + 30);
                    }
                }
            }
        }
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Menu");
        exit = new MenuItem("Abbruch");
        start = new MenuItem("Spielen local");
        close = new MenuItem("Schliessen");
        menu.add(exit);
        menu.add(start);
        menu.add(close);
        menuBar.add(menu);
        frame.setMenuBar(menuBar);
        frame.setResizable(false);
        frame.setVisible(true);
        exit.addActionListener(this);
        start.addActionListener(this);
        close.addActionListener(this);
    }

    private void spielStart() {
        chompfeld = new Chompfeld();
        frame.add("Center", chompfeld.setChompfeld(m, n));
        frame.add("North", new Label(" "));
        frame.add("South", new Label(" "));
        frame.add("West", new Label(" "));
        frame.add("East", new Label(" "));
        frame.setVisible(true);
        aktSpieler = new Random().nextInt(2) + 1;
        buttonListener();
        spielenMitComptuer();
        spielen();
    }

    public boolean spielenMitComptuer() {
        int antwort = JOptionPane.showConfirmDialog(frame, "Moechten Sie gegen den Computer spielen?", "Narichten", JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
        if (antwort == JOptionPane.OK_OPTION) {
            spielenmitComputer = true;
            //cs2.name = "Computer";
            //cs2.art = "Computer";
            x = new Random().nextInt(m - 1) + 1;
            y = new Random().nextInt(n - 1) + 1;
        } else {
            spielenmitComputer = false;
        }
        return spielenmitComputer;
    }

    public void spielen() {
        if(localspiel){
            if (spielenmitComputer) {
                if (aktSpieler == 2) {
                    text = "Bitte warten Sie.";
                    messageDialog(text);
                    computerspielen();
                } else {
                    text = "Bitte spielen Sie.";
                    messageDialog(text);
                }
            } else {
                if (aktSpieler == 1) {
                    text = "Spieler1 spielt.";
                    messageDialog(text);
                } else {
                    text = "Spieler2 spielt.";
                    messageDialog(text);
                }
            }
        }
        else{
            if (zustand == 2) {
                text = "Bitte warten Sie.";
                messageDialog(text);
            } else {
                text = "Bitte spielen Sie.";
                messageDialog(text);
            }
        }
    }

    public void gegenspielerspielen(int gx, int gy){
                    spielfeld[gx][gy] = false;
                    for (int k = gx; k < m; k++) {
                        for (int l = gy; l < n; l++) {
                            chompfeld.B[k][l].setBackground(Color.white);
                            chompfeld.B[k][l].setEnabled(false);
                        }
                    }
    }

    public void buttonListener() {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                chompfeld.B[i][j].addActionListener(this);
            }
        }
    }

    public void messageDialog(String text) {
        Label label = new Label();
        label.setText((text));
        JOptionPane.showMessageDialog(frame, label);
    }

    public void confirmDialog(String text) {
        int antwort = JOptionPane.showConfirmDialog(frame, text, "Narichten", JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
        if (antwort == JOptionPane.OK_OPTION) {
            frame.removeAll();
            startChomp();
        } else {
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    chompfeld.B[i][j].setEnabled(false);

                }
            }
        }
    }

    public void confirmAbbruch() {
        int antwort = JOptionPane.showConfirmDialog(frame, "Sind Sie sicher, abzubrechen?", "Meldung", JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
        if (antwort == JOptionPane.OK_OPTION) {
            if(localspiel){
                if (aktSpieler == 1) text = "Spieler1 hat abgebrochen!";
                else text = "Spieler2 hat abgebrochen!";
                messageDialog(text);
                System.exit(0);
            }
            else{
                zustand = 5;
                System.exit(0);
            }

        } else spielen();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == close) {
            if (zustand == 0) System.exit(0);
            else confirmAbbruch();
        }
        if (e.getSource() == start) {
            localspiel = true;
            spielStart();
        } else {
            if(localspiel){
                if (e.getSource() == this.chompfeld.B[0][0]) {
                    if (aktSpieler == 1) text = "Spieler2 hat gewonnen! Moechten Sie neu anfangen?";
                    else text = "Spieler1 hat gewonnen! Moechten Sie neu anfangen?";
                    confirmDialog(text);
                } else {
                    if (e.getSource() == this.chompfeld.B[0][1]) {
                        if (chompfeld.B[1][0].getBackground() == Color.white) {
                            if (aktSpieler == 1) {
                                if (spielenmitComputer) text = "Sie haben gewonnen! Moechten Sie neu anfangen?";
                                else text = "Spieler1 hat gewonnen! Moechten Sie neu anfangen?";
                            } else {
                                text = "Spieler2 hat gewonnen! Moechten Sie neu anfangen?";
                            }
                            confirmDialog(text);
                        } else {
                            spielfeld[0][1] = false;
                            for (int k = 0; k < m; k++) {
                                for (int l = 1; l < n; l++) {
                                    chompfeld.B[k][l].setBackground(Color.white);
                                    chompfeld.B[k][l].setEnabled(false);
                                }
                            }
                            wechsleSpieler();
                        }

                    } else {
                        if (e.getSource() == this.chompfeld.B[1][0]) {
                            if (chompfeld.B[0][1].getBackground() == Color.white) {
                                if (aktSpieler == 1) {
                                    if (spielenmitComputer) text = "Sie haben gewonnen! Moechten Sie neu anfangen?";
                                    else text = "Spieler1 hat gewonnen! Moechten Sie neu anfangen?";
                                } else text = "Spieler2 hat gewonnen! Moechten Sie neu anfangen?";
                                confirmDialog(text);
                            } else {
                                spielfeld[1][0] = false;
                                for (int k = 1; k < m; k++) {
                                    for (int l = 0; l < n; l++) {
                                        chompfeld.B[k][l].setBackground(Color.white);
                                        chompfeld.B[k][l].setEnabled(false);
                                    }
                                }
                                wechsleSpieler();
                            }
                        } else {
                            if (e.getSource() == exit) {
                                confirmAbbruch();
                            } else {
                                for (int i = 0; i < m; i++) {
                                    for (int j = 0; j < n; j++) {
                                        if (e.getSource() == this.chompfeld.B[i][j]) {
                                            spielfeld[i][j] = false;
                                            for (int k = i; k < m; k++) {
                                                for (int l = j; l < n; l++) {
                                                    chompfeld.B[k][l].setBackground(Color.white);
                                                    chompfeld.B[k][l].setEnabled(false);
                                                }
                                            }

                                        }
                                    }
                                }
                                wechsleSpieler();
                            }
                        }
                    }
                }
            }
            else{
                if(zustand ==1){
                    if (e.getSource() == this.chompfeld.B[0][0]) {
                        zustand = 4;
                        text = "Sie haben verloren! Moechten Sie neu anfangen?";
                        confirmDialog(text);
                    } else {
                        if (e.getSource() == this.chompfeld.B[0][1]) {
                            if (chompfeld.B[1][0].getBackground() == Color.white) {
                                zustand = 3;
                                text = "Sie haben gewonnen! Moechten Sie neu anfangen?";
                                confirmDialog(text);
                            } else {
                                spielfeld[0][1] = false;
                                x = 0;
                                y = 1;
                                for (int k = 0; k < m; k++) {
                                    for (int l = 1; l < n; l++) {
                                        chompfeld.B[k][l].setBackground(Color.white);
                                        chompfeld.B[k][l].setEnabled(false);
                                    }
                                }
                            }
                        } else {
                            if (e.getSource() == this.chompfeld.B[1][0]) {
                                if (chompfeld.B[0][1].getBackground() == Color.white) {
                                    zustand = 3;
                                    text = "Sie haben gewonnen! Moechten Sie neu anfangen?";
                                    confirmDialog(text);
                                } else {
                                    spielfeld[1][0] = false;
                                    x = 1;
                                    y = 0;
                                    for (int k = 1; k < m; k++) {
                                        for (int l = 0; l < n; l++) {
                                            chompfeld.B[k][l].setBackground(Color.white);
                                            chompfeld.B[k][l].setEnabled(false);
                                        }
                                    }
                                    zustand = 2;
                                }
                            } else {
                                if (e.getSource() == exit) {
                                    confirmAbbruch();
                                } else {
                                    for (int i = 0; i < m; i++) {
                                        for (int j = 0; j < n; j++) {
                                            if (e.getSource() == this.chompfeld.B[i][j]) {
                                                spielfeld[i][j] = false;
                                                x = i;
                                                y = j;
                                                for (int k = i; k < m; k++) {
                                                    for (int l = j; l < n; l++) {
                                                        chompfeld.B[k][l].setBackground(Color.white);
                                                        chompfeld.B[k][l].setEnabled(false);
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }

        }
    }

    public void computerspielen() {
        if (chompfeld.B[x][y].getBackground() == Color.white) {
            int a = new Random().nextInt(2);
            if (a == 0) {
                do {
                    x = x / 2;
                } while (x >= 1 & chompfeld.B[x][y].getBackground() == Color.white);
            } else {
                do {
                    y = y / 2;
                } while (y >= 1 & chompfeld.B[x][y].getBackground() == Color.white);
            }
        }
        if (x == 0 & y == 0) {
            if (chompfeld.B[1][0].getBackground() != Color.white | chompfeld.B[0][1].getBackground() != Color.white) {
                if (chompfeld.B[1][0].getBackground() != Color.white & chompfeld.B[0][1].getBackground() != Color.white) {
                    int b = new Random().nextInt(2);
                    if (b == 0) x = 1;
                    else y = 1;
                } else {
                    if (chompfeld.B[1][0].getBackground() != Color.white & chompfeld.B[0][1].getBackground() == Color.white)
                        x = 1;
                    else {
                        if (chompfeld.B[1][0].getBackground() == Color.white & chompfeld.B[0][1].getBackground() != Color.white)
                            y = 1;
                    }
                }
            } else {
                text = "Sie haben gewonnen! Moechten Sie neu anfangen?";
                confirmDialog(text);
            }

        } else {
            if (x == 0) {
                if (y == 1) {
                    if (chompfeld.B[1][0].getBackground() == Color.white) {
                        text = "Sie haben verloren! Moechten Sie neu anfangen?";
                        confirmDialog(text);
                    } else {
                        for (int k = 0; k < m; k++) {
                            for (int l = 2; l < n; l++) {
                                chompfeld.B[k][l].setBackground(Color.white);
                                chompfeld.B[k][l].setEnabled(false);
                            }
                        }
                        wechsleSpieler();
                    }
                } else {
                    if ((chompfeld.B[0][1].getBackground() != Color.white & chompfeld.B[1][0].getBackground() == Color.white) | (chompfeld.B[0][1].getBackground() == Color.white & chompfeld.B[1][0].getBackground() != Color.white)) {
                        text = "Sie haben verloren! Moechten Sie neu anfangen?";
                        confirmDialog(text);
                    } else {
                        if (chompfeld.B[0][1].getBackground() != Color.white & chompfeld.B[1][0].getBackground() != Color.white) {
                            for (int k = 2; k < m; k++) {
                                for (int l = 0; l < n; l++) {
                                    chompfeld.B[k][l].setBackground(Color.white);
                                    chompfeld.B[k][l].setEnabled(false);
                                }
                            }
                            wechsleSpieler();
                        } else {
                            text = "Sie haben gewonnen! Moechten Sie neu anfangen?";
                            confirmDialog(text);
                        }
                    }

                }

            } else {
                if (y == 0) {
                    if (x == 1) {
                        if (chompfeld.B[0][1].getBackground() == Color.white) {
                            text = "Sie haben verloren! Moechten Sie neu anfangen?";
                            confirmDialog(text);
                        } else {
                            for (int k = 2; k < m; k++) {
                                for (int l = 0; l < n; l++) {
                                    chompfeld.B[k][l].setBackground(Color.white);
                                    chompfeld.B[k][l].setEnabled(false);
                                }
                            }
                            wechsleSpieler();
                        }
                    } else {
                        if ((chompfeld.B[0][1].getBackground() != Color.white & chompfeld.B[1][0].getBackground() == Color.white) | (chompfeld.B[0][1].getBackground() == Color.white & chompfeld.B[1][0].getBackground() != Color.white)) {
                            text = "Sie haben verloren! Moechten Sie neu anfangen?";
                            confirmDialog(text);
                        } else {
                            if (chompfeld.B[0][1].getBackground() != Color.white & chompfeld.B[1][0].getBackground() != Color.white) {
                                for (int k = 0; k < m; k++) {
                                    for (int l = 2; l < n; l++) {
                                        chompfeld.B[k][l].setBackground(Color.white);
                                        chompfeld.B[k][l].setEnabled(false);
                                    }
                                }
                                wechsleSpieler();
                            } else {
                                text = "Sie haben gewonnen! Moechten Sie neu anfangen?";
                                confirmDialog(text);
                            }
                        }
                    }

                } else {
                    for (int k = x; k < m; k++) {
                        for (int l = y; l < n; l++) {
                            chompfeld.B[k][l].setBackground(Color.white);
                            chompfeld.B[k][l].setEnabled(false);
                        }
                    }
                    wechsleSpieler();
                }
            }
        }

    }

    public void wechsleSpieler() {
        if (aktSpieler == 1) {
            aktSpieler = 2;
            spielen();
        } else {
            aktSpieler = 1;
            spielen();
        }
    }
}
