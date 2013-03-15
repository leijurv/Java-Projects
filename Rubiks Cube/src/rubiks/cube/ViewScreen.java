/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rubiks.cube;

import java.awt.Graphics;
import javax.swing.JComponent;

/**
 *
 * @author leif
 */
public class ViewScreen extends JComponent{
    public void paintComponent(Graphics g){
        synchronized(RubiksCube.openSet){
            g.drawString("OpenSet length: "+RubiksCube.openSet.size(),10,25);
        }
        synchronized(RubiksCube.map){
            g.drawString("Map length: "+RubiksCube.map.size(),10,40);
        }
        synchronized((Object)RubiksCube.currentMoves){
            g.drawString("Current moves: "+RubiksCube.currentMoves,10,55);
        }
    }
}
