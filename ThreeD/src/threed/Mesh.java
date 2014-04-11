/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package threed;

import java.awt.Graphics;

/**
 *
 * @author leijurv
 */
public class Mesh {
    Vertex[][] faces;
    public void render(Graphics g){
        for (Vertex[] tri : faces){
            int[][] transform=transform(tri);
            g.drawLine(transform[0][0],transform[0][1],transform[1][0],transform[1][1]);
            g.drawLine(transform[1][0],transform[1][1],transform[2][0],transform[2][1]);
            g.drawLine(transform[0][0],transform[0][1],transform[2][0],transform[2][1]);
        }
    }
    public Mesh(){
        
    }
    public Mesh(Mesh m){
        faces=new Vertex[m.faces.length][3];
        for (int i=0; i<faces.length; i++){
            for (int j=0; j<3; j++){
                faces[i][j]=new Vertex(m.faces[i][j]);
            }
        }
    }
    public static int[][] transform(Vertex[] v){
        return new int[][] {v[0].transform(),v[1].transform(),v[2].transform()};
    }
    public String toString(){
        String s="";
        for (Vertex[] tri : faces){
            s=s+"("+tri[0]+":"+tri[1]+":"+tri[2]+")\n";
        }
        return s;
    }
    public Mesh transform(Transform t){
        Mesh m=new Mesh(this);
        m.Mtransform(t);
        return m;
    }
    private void Mtransform(Transform t){
        for (Vertex[] v : faces){
            for (Vertex x : v){
                x.transform(t);
            }
        }
    }
}
