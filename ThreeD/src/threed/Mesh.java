/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package threed;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 *
 * @author leijurv
 */
public class Mesh {
    Vertex[][] faces;
    public Mesh(Mesh[] m){
        faces=m[0].faces;
        for (int i=1; i<m.length; i++){
            for (int j=0; j<m[i].faces.length; j++){
                addFace(m[i].faces[j]);
            }
        }
    }
    public void addFace(Vertex[] v){
        Vertex[][] N=new Vertex[faces.length+1][3];
        for (int i=0; i<faces.length; i++){
            N[i]=faces[i];
        }
        N[faces.length]=v;
        faces=N;
    }
    public void renderWireframe(Graphics g){
        for (Vertex[] tri : faces){
            int[][] transform=transform(tri);
            g.drawLine(transform[0][0],transform[1][0],transform[0][1],transform[1][1]);
            g.drawLine(transform[0][1],transform[1][1],transform[0][2],transform[1][2]);
            g.drawLine(transform[0][0],transform[1][0],transform[0][2],transform[1][2]);
        }
    }
    public void render(Graphics g){
        renderFull(g);
    }
    public void renderFull(Graphics g){
        for (int i=0; i<faces.length-1; i++){
            if (comp(faces[i],faces[i+1])){
                Vertex[] v=faces[i];
                faces[i]=faces[i+1];
                faces[i+1]=v;
                i-=2;
                if (i==-2){
                    i++;
                }
                System.out.println(i);
            }
        }
        Random r=new Random(5021);
        for (int i=0; i<faces.length; i++){
            g.setColor(new Color(r.nextFloat(),r.nextFloat(),r.nextFloat()));
            int[][] transform=transform(faces[i]);
            g.fillPolygon(transform[0],transform[1],transform[0].length);
        }
    }
    public boolean comp(Vertex[] a, Vertex[] b){
        return a[0].z+a[1].z+a[2].z<b[0].z+b[1].z+b[2].z;
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
       int[][]  a={v[0].transform(),v[1].transform(),v[2].transform()};
       int[][] b=new int[a[0].length][a.length];
       for (int i=0; i<a.length; i++){
           for (int j=0; j<a[0].length; j++){
               b[j][i]=a[i][j];
           }
       }
       return b;
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
