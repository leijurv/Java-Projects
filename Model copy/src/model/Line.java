package model;

import java.awt.*;

public class Line extends Component{

int x1,y1,x2,y2;
int x,y,w,h;

public Line(int a,int b, int squareSize){
	setBackground(Color.blue);
	x1=((a%8)*squareSize)+(squareSize/2);
	y1=((a/8)*squareSize)+(squareSize/2);
	x2=((b%8)*squareSize)+(squareSize/2);
	y2=((b/8)*squareSize)+(squareSize/2);

	if(x1<x2){
		x=x1;w=x2-x1;
	}else{
		x=x2;w=x1-x2;
	}

	if(y1<y2){
		y=y1;h=y2-y1;
	}else{
		y=y2;h=y1-y2;
	}
	setBounds(x,y,w,h);
}

public void paint(Graphics g){
	g.setColor(getBackground());
	g.drawLine(x1-x,y1-y,x2-x,y2-y);
}

}