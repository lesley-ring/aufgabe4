package uni.prakinf.m4.client;

import java.awt.Button;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Panel;


@SuppressWarnings("serial")
public class Chompfeld extends Panel {
	Color color;
	int m = 3;
	int n = 6;
	//boolean[][] changeColor =  new boolean[m][n];
	Button[][] B;

	public Panel setChompfeld(int m, int n){
		Panel p = new Panel();
		p.setLayout(new GridLayout(m,n));
		B = new Button[m][n];
		for(int i=0;i<m;i++){
			for(int j=0;j<n;j++){
				B[i][j] = new Button(" ");
				if(i==0 & j==0){
					B[i][j].setEnabled(true);
					B[i][j].setBackground(Color.gray);
				}
				else{
					B[i][j].setEnabled(true);
					B[i][j].setBackground(new Color(200, 200, 200));
				}
				p.add(B[i][j]);
			}
		}
		return p;
		
	}
}
