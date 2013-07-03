package uni.prakinf.m4.client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class Spiele implements IClient,ActionListener {
    private String[] names;
    private Spiel[] spiels;
    private MenuItem[] menuItems;
    private int m,n;
    private String name;    //Frage: Sollt eigener Name auch von Server bekommen?
    private Spiel spiel;
    private Zustand zustand;
    private Chomp chomp;
    //private VierGewinnt vierGewinnt;
    ServerConnection serverConnection;
    Frame frame = new Frame("Spiel");
    public Spiele() {
        serverConnection = new ServerConnection();
        serverConnection.setClient(this);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent arg0) {
                System.exit(0);
            }
        });
        frame.setBackground(Color.white);
        frame.setSize(400, 400);
        spielerListe(names, spiels); // Es fehlt noch die Parameter, die von Server bekommen sollen.
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Spiele spiele = new Spiele();
        spiele.spielen();

    }

    private void spielen() {
        if(spiel==Spiel.CHOMP){
            if(chomp.zustand == 1){
                neuerZustandChomp(Zustand.ZUG,chomp.x,chomp.y,chomp.gegenspieler.name);
            }
            if(chomp.zustand == 2){
                // Koordinaten gx, gy sollen von Server bekommen.
                chomp.gegenspielerspielen(gx,gy);
                neuerZustandChomp(Zustand.WARTEN,chomp.x,chomp.y,chomp.gegenspieler.name);
            }
            if(chomp.zustand == 3){
                neuerZustandChomp(Zustand.GEWONNEN,chomp.x,chomp.y,chomp.gegenspieler.name);
            }
            if(chomp.zustand == 4){
                neuerZustandChomp(Zustand.VERLOREN,chomp.x,chomp.y,chomp.gegenspieler.name);
            }
            if(chomp.zustand == 5){
                neuerZustandChomp(Zustand.ABBRUCH,chomp.x,chomp.y,chomp.gegenspieler.name);
            }
        }
    }

    @Override
    public void verbindungsFehler() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void neuerZustandChomp(Zustand zustand, int x, int y, String gegenspieler) {
        zustand = this.zustand;
        if(zustand == Zustand.ZUG){
            // Die Koordinaten x, y sollen durch Server zum Gegenspieler liefern.
            zustand = Zustand.WARTEN;
            chomp.zustand = 2;
        }
        if(zustand == Zustand.WARTEN){
            zustand = Zustand.ZUG;
            chomp.zustand = 1;
        }
        if(zustand == Zustand.GEWONNEN){
            // Die Koordinaten x, y sollen durch Server zum Gegenspieler liefern.
            nachricht(name,"hat gewonnen!");
            chomp.zustand = 0;
        }
        if(zustand == Zustand.VERLOREN){
            // Die Koordinaten x, y sollen durch Server zum Gegenspieler liefern.
            nachricht(name,"hat verloren!");
            chomp.zustand = 0;
        }
        if(zustand == Zustand.ABBRUCH){
            nachricht(name,"hat abgebrochen!");
        }
    }

    @Override
    public void neuerZustandVierGewinnt(Zustand zustand, VierGewinntStein[][] spielfeld, String gegenspieler) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void spielerListe(String[] name, Spiel[] spiel) {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("SpielerListe");
        for (int i = 0;i<name.length;i++){
            if(spiel[i]==Spiel.CHOMP) menuItems[i]=new MenuItem(name[i]+", Chomp");
            if(spiel[i]==Spiel.VIER_GEWINNT) menuItems[i]=new MenuItem(name[i]+", VierGewinnt");
            if(spiel[i]==Spiel.KEINS) menuItems[i]=new MenuItem(name[i]+", Keins");
            menu.add(menuItems[i]);
            menuItems[i].addActionListener(this);
        }
        frame.setMenuBar(menuBar);
    }

    @Override
    public void nachricht(String name, String nachricht) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0;i<names.length;i++){
            if(e.getSource()==menuItems[i]){
                if(serverConnection.mitspielen(names[i],spiels[i])){
                    if(spiels[i]==Spiel.CHOMP){
                        if(serverConnection.neuesSpiel(Spiel.CHOMP,m,n)){  //Frage: Woher kommen die Parameter m,n?
                            spiel = Spiel.CHOMP;
                            chomp = new Chomp();
                            chomp.m = m;
                            chomp.n = n;
                            chomp.zustand = 0;
                            chomp.gegenspieler.name = names[i];
                            chomp.startChomp();
                        }
                    }
                /*else if(spiel[i]==Spiel.VIER_GEWINNT){
                    if(serverConnection.neuesSpiel(Spiel.VIER_GEWINNT,m,n)){
                        spiel = Spiel.VIER_GEWINNT;
                        VierGewinnt vierGewinnt = new VierGewinnt();
                        vierGewinnt.m = m;
                        vierGewinnt.n = n;
                        vierGewinnt.gegenspieler.name = name[i];
                        vierGewinnt.startvierGewinnt();
                    }
                }*/
                }
            }
        }
    }
}
