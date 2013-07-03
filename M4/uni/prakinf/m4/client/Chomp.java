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


public class Chomp implements ActionListener,IClient {
	private Chompfeld chompfeld;
	Frame frame = new Frame("Chomp");
	private int m = 15;
	private int n = 30;
	Chompspieler cs1 = new Chompspieler();
	Chompspieler cs2 = new Chompspieler();
	int aktSpieler;
	String text;
	boolean spielenmitComputer;
	int x,y;
	boolean[][] spielfeld = new boolean[m][n];
	private MenuItem exit,start,close;
	private Zustand zustand;
	
	public static void main(String [] args) {
		ServerConnection serverConnection = new ServerConnection();
		Chomp chomp = new Chomp();
		serverConnection.setClient(chomp);
		chomp.startTest();
	}
	
	void startTest(){
		frame.addWindowListener(new WindowAdapter() {
			 
            @Override
            public void windowClosing(WindowEvent arg0) {
                System.exit(0);
            }
        });
		frame.setBackground(Color.white);
		if (n<=40 & m<=40) frame.setSize(25*n+30,25*m+30);
		else{
			if(n<=50 & m<=50) frame.setSize(20*n+30,20*m+30);
			else{
				if(n<=66 & m<=66) frame.setSize(15*n+30,15*m+30);
				else{
					if(n<=100 & m<=100) frame.setSize(10*n+30,10*m+30);
					else{
						if(n<=200 & m<=200) frame.setSize(5*n+30,5*m+30);
						else frame.setSize(n+30,m+30);
					}
				}
			}
		}
		MenuBar menuBar = new MenuBar();
		Menu menu = new Menu("Menu");
		exit = new MenuItem("Abbruch");
		start = new MenuItem("Spielen");
		close = new MenuItem("Schliessen");
		menu.add(exit);
		menu.add(start);
		menu.add(close);
		menuBar.add(menu);
		frame.setMenuBar(menuBar);
		frame.setVisible(true);
		zustand = Zustand.KEIN_SPIELER;
		exit.addActionListener(this);
		start.addActionListener(this);
		close.addActionListener(this);
	}
	
	private void spielStart() {
		chompfeld = new Chompfeld();
		frame.add("Center",chompfeld.setChompfeld(m,n));
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
	
	public boolean spielenMitComptuer(){
		int antwort = JOptionPane.showConfirmDialog(frame, "Moechten Sie gegen den Computer spielen?", "Narichten", JOptionPane.YES_NO_OPTION, 
                JOptionPane.INFORMATION_MESSAGE);
        if (antwort == JOptionPane.OK_OPTION){
        	//spielZustand = 1;
        	spielenmitComputer = true;
        	cs2.name = "Computer";
        	cs2.art = "Computer";
        	x = new Random().nextInt(m-1)+1;
    		y = new Random().nextInt(n-1)+1;
        }
        else{
        	
        	//spielZustand = 1;
        	spielenmitComputer = false;
        }
        return spielenmitComputer;
	}
	
	public void spielen(){
		if(spielenmitComputer){
			if(aktSpieler == 2){
				zustand = Zustand.WARTEN;
				text = "Bitte warten Sie.";
				messageDialog(text);
				computerspielen();
			}
			else{
				zustand = Zustand.ZUG;
				text = "Bitte spielen Sie.";
				messageDialog(text);
			}
		}
		else{
			if(aktSpieler == 1){
				zustand = Zustand.ZUG;
				text = "Spieler1 "+cs1.name+" spielt.";
				messageDialog(text);
			}
			else{
				zustand = Zustand.ZUG;
				text = "Spieler2 "+cs2.name+" spielt.";
				messageDialog(text);
			}
			}
	}
	
	public void buttonListener(){
		for(int i=0;i<m;i++){
			for(int j=0;j<n;j++){
				chompfeld.B[i][j].addActionListener(this);
			}
		}
	}
	
	public void messageDialog(String text){ 
		Label label = new Label();
		label.setText((text));
        JOptionPane.showMessageDialog(frame, label); 
    } 
	
	public void confirmDialog(String text){
        int antwort = JOptionPane.showConfirmDialog(frame, text, "Narichten", JOptionPane.YES_NO_OPTION, 
                JOptionPane.INFORMATION_MESSAGE);
        if (antwort == JOptionPane.OK_OPTION) { 
        	frame.removeAll();
        	startTest();
        } else { 
            for(int i=0;i<m;i++){
            	for(int j=0;j<n;j++){
            		chompfeld.B[i][j].setEnabled(false);
            		
            	}
            }
        }
	}
	
	public void confirmAbbruch(){
        int antwort = JOptionPane.showConfirmDialog(frame, "Sind Sie sicher, abzubrechen?", "Meldung", JOptionPane.YES_NO_OPTION, 
                JOptionPane.INFORMATION_MESSAGE);
        if (antwort == JOptionPane.OK_OPTION) {
        	zustand = Zustand.ABBRUCH;
        	if(aktSpieler == 1) text = "Spieler1 "+cs1.name+" hat abgebruchen!";
			else text = cs2.name+" hat abgebruchen!";
        	messageDialog(text);
        	System.exit(0);
        }
        else spielen();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
			if(e.getSource()== close){
				if(zustand == Zustand.KEIN_SPIELER) System.exit(0);
				else confirmAbbruch();
			}
			if(e.getSource()== start){
				spielStart();
			}
			else{
				if(e.getSource() == this.chompfeld.B[0][0]){
					zustand = Zustand.GEWONNEN;
					if(aktSpieler == 1) text = "Spieler2 "+cs2.name+" hat gewonnen! Moechten Sie neu anfangen?";
					else text = "Spieler1 "+cs1.name+" hat gewonnen! Moechten Sie neu anfangen?";
					confirmDialog(text);
				}
				else{
					if(e.getSource() == this.chompfeld.B[0][1]){
						if(chompfeld.B[1][0].getBackground()==Color.white){
							zustand = Zustand.GEWONNEN;
							if(aktSpieler == 1){
								if(spielenmitComputer) text = "Sie haben gewonnen! Moechten Sie neu anfangen?";
								else text = "Spieler1 "+cs1.name+" hat gewonnen! Moechten Sie neu anfangen?";
							}
							else{
								text = "Spieler2 "+cs2.name+" hat gewonnen! Moechten Sie neu anfangen?";
							}
							confirmDialog(text);
						}
						else{
							spielfeld[0][1] = false;
							for(int k=0;k<m;k++){
								for(int l=1;l<n;l++){
									chompfeld.B[k][l].setBackground(Color.white);
									chompfeld.B[k][l].setEnabled(false);
								}
							}
							wechsleSpieler();
						}
						
					}
					else{
						if(e.getSource() == this.chompfeld.B[1][0]){
							if(chompfeld.B[0][1].getBackground()==Color.white){
								zustand = Zustand.GEWONNEN;
								if(aktSpieler == 1){
									if(spielenmitComputer) text = "Sie haben gewonnen! Moechten Sie neu anfangen?";
									else text = "Spieler1 "+cs1.name+" hat gewonnen! Moechten Sie neu anfangen?";
								}
								else text = "Spieler2 "+cs2.name+" hat gewonnen! Moechten Sie neu anfangen?";
								confirmDialog(text);
							}
							else{
								spielfeld[1][0] = false;
								for(int k=1;k<m;k++){
									for(int l=0;l<n;l++){
										chompfeld.B[k][l].setBackground(Color.white);
										chompfeld.B[k][l].setEnabled(false);
									}
								}
								wechsleSpieler();
							}
						}
						else{
							if(e.getSource()== exit){
								confirmAbbruch();				
							}
							else{
							for(int i=0;i<m;i++){
								for(int j=0;j<n;j++){
									if(e.getSource() == this.chompfeld.B[i][j]){
										spielfeld[i][j] = false;
										for(int k=i;k<m;k++){
											for(int l=j;l<n;l++){
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
	}

	public void computerspielen() {
		 if (chompfeld.B[x][y].getBackground() == Color.white) {
			 int a = new Random().nextInt(2);
			 if(a==0){
				 do{
					 x=x/2;
				 }while (x >= 1 & chompfeld.B[x][y].getBackground() == Color.white);
			 }
			 else{
				 do{
					 y=y/2;
				 }while (y >= 1 & chompfeld.B[x][y].getBackground() == Color.white);
			 }
		}
		if(x==0 & y==0){
			if(chompfeld.B[1][0].getBackground()!=Color.white | chompfeld.B[0][1].getBackground()!=Color.white){
				if(chompfeld.B[1][0].getBackground()!=Color.white & chompfeld.B[0][1].getBackground()!=Color.white){
					int b = new Random().nextInt(2);
					if(b==0) x = 1;
					else y = 1;
				}
				else{
					if(chompfeld.B[1][0].getBackground()!=Color.white & chompfeld.B[0][1].getBackground()==Color.white) x = 1;
					else{
						if(chompfeld.B[1][0].getBackground()==Color.white & chompfeld.B[0][1].getBackground()!=Color.white) y = 1;
					}
				}
			}
			else{
				zustand = Zustand.GEWONNEN;
				text = "Sie haben gewonnen! Moechten Sie neu anfangen?";
				confirmDialog(text);
			}
			
		}
		else{
			if(x==0){
				if(y==1){
					if(chompfeld.B[1][0].getBackground()==Color.white){
						zustand = Zustand.VERLOREN;
						text = "Sie haben verloren! Moechten Sie neu anfangen?";
						confirmDialog(text);
					}
					else{
						for(int k=0;k<m;k++){
							for(int l=2;l<n;l++){
								chompfeld.B[k][l].setBackground(Color.white);
								chompfeld.B[k][l].setEnabled(false);
							}
						}
						wechsleSpieler();
					}
				}
				else{
					if((chompfeld.B[0][1].getBackground()!=Color.white & chompfeld.B[1][0].getBackground()==Color.white)|(chompfeld.B[0][1].getBackground()==Color.white & chompfeld.B[1][0].getBackground()!=Color.white)){
						zustand = Zustand.VERLOREN;
						text = "Sie haben verloren! Moechten Sie neu anfangen?";
						confirmDialog(text);
					}
					else{
						if(chompfeld.B[0][1].getBackground()!=Color.white & chompfeld.B[1][0].getBackground()!=Color.white){
							for(int k=2;k<m;k++){
								for(int l=0;l<n;l++){
									chompfeld.B[k][l].setBackground(Color.white);
									chompfeld.B[k][l].setEnabled(false);
								}
							}
							wechsleSpieler();
						}
						else{
							zustand = Zustand.GEWONNEN;
							text = "Sie haben gewonnen! Moechten Sie neu anfangen?";
							confirmDialog(text);
						}
					}
					
				}
				
			}
			else{
				if(y==0){
					if(x==1){
						if(chompfeld.B[0][1].getBackground()==Color.white){
							zustand = Zustand.VERLOREN;
							text = "Sie haben verloren! Moechten Sie neu anfangen?";
							confirmDialog(text);
						}
						else{
							for(int k=2;k<m;k++){
								for(int l=0;l<n;l++){
									chompfeld.B[k][l].setBackground(Color.white);
									chompfeld.B[k][l].setEnabled(false);
								}
							}
							wechsleSpieler();
						}
					}
					else{
						if((chompfeld.B[0][1].getBackground()!=Color.white & chompfeld.B[1][0].getBackground()==Color.white)|(chompfeld.B[0][1].getBackground()==Color.white & chompfeld.B[1][0].getBackground()!=Color.white)){
							zustand = Zustand.VERLOREN;
							text = "Sie haben verloren! Moechten Sie neu anfangen?";
							confirmDialog(text);
						}
						else{
							if(chompfeld.B[0][1].getBackground()!=Color.white & chompfeld.B[1][0].getBackground()!=Color.white){
								for(int k=0;k<m;k++){
									for(int l=2;l<n;l++){
										chompfeld.B[k][l].setBackground(Color.white);
										chompfeld.B[k][l].setEnabled(false);
									}
								}
								wechsleSpieler();
							}
							else{
								zustand = Zustand.GEWONNEN;
								text = "Sie haben gewonnen! Moechten Sie neu anfangen?";
								confirmDialog(text);
							}
						}
					}
					
				}
				else{
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
	
	public void wechsleSpieler(){
        if(aktSpieler==1){
        	aktSpieler=2;
        	spielen();
        }
        else{
        	aktSpieler=1;
        	spielen();
        }
    }

	@Override
	public void verbindungsFehler() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void neuerZustandChomp(Zustand zustand, boolean[][] spielfeld,
			String gegenspieler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void neuerZustandVierGewinnt(Zustand zustand,
			VierGewinntStein[][] spielfeld, String gegenspieler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void spielerListe(String[] name, Spiel[] spiel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nachricht(String name, String nachricht) {
		// TODO Auto-generated method stub
		
	}

}
