/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package threed;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Random;

/**
 *
 * @author leijurv
 */
public class Mesh {
    static final Mesh cube=new Mesh();
    static final Mesh tri=new Mesh();
    static final Mesh plane=new Mesh();
    Vertex[][] faces;
    public static void init(){
        Vertex[][] cub={{new Vertex(-0.5,-0.5,-0.5),new Vertex(-0.5,0.5,-0.5),new Vertex(0.5,0.5,-0.5),new Vertex(0.5,-0.5,-0.5)},{new Vertex(-0.5,-0.5,-0.5),new Vertex(0.5,-0.5,-0.5),new Vertex(0.5,-0.5,0.5),new Vertex(-0.5,-0.5,0.5)},{new Vertex(-0.5,-0.5,-0.5),new Vertex(-0.5,-0.5,0.5),new Vertex(-0.5,0.5,0.5),new Vertex(-0.5,0.5,-0.5)}       ,    {new Vertex(0.5,0.5,0.5),new Vertex(0.5,0.5,-0.5),new Vertex(0.5,-0.5,-0.5),new Vertex(0.5,-0.5,0.5)}, {new Vertex(0.5,0.5,0.5),new Vertex(0.5,0.5,-0.5),new Vertex(-0.5,0.5,-0.5),new Vertex(-0.5,0.5,0.5)}, {new Vertex(0.5,0.5,0.5),new Vertex(-0.5,0.5,0.5),new Vertex(-0.5,-0.5,0.5),new Vertex(0.5,-0.5,0.5)}};
        Vertex[][] cub2=new Vertex[cub.length*2][3];
    for (int i=0; i<cub.length; i++){
        cub2[i*2]=new Vertex[] {cub[i][0],cub[i][1],cub[i][2]};
        cub2[i*2+1]=new Vertex[] {cub[i][2],cub[i][3],cub[i][0]};
    }
    
        cube.faces=cub2;
        Vertex[][] r={{new Vertex(-0.5,-0.5,0),new Vertex(0,0,0.5),new Vertex(-0.5,0.5,0)},{new Vertex(-0.5,-0.5,0),new Vertex(0,0,0.5),new Vertex(0.5,-0.5,0)},{new Vertex(0.5,0.5,0),new Vertex(0,0,0.5),new Vertex(-0.5,0.5,0)},{new Vertex(0.5,0.5,0),new Vertex(0,0,0.5),new Vertex(0.5,-0.5,0)}};
        tri.faces=r;
        plane.faces=new Vertex[][]{{new Vertex(10,0,10),new Vertex(10,0,-10),new Vertex(-10,0,10)},{new Vertex(-10,0,10),new Vertex(10,0,-10),new Vertex(-10,0,-10)}};
    }
    public Mesh(String s){
         try{
             faces=new Vertex[0][3];
  // Open the file that is the first 
  // command line parameter
  FileInputStream fstream = new FileInputStream(s);
  // Get the object of DataInputStream
  DataInputStream in = new DataInputStream(fstream);
  BufferedReader br = new BufferedReader(new InputStreamReader(in));
  String strLine;
  while ((strLine = br.readLine()) != null)   {
      Vertex[] x=new Vertex[3];
      x[0]=new Vertex(strLine);
      x[1]=new Vertex(br.readLine());
      x[2]=new Vertex(br.readLine());
      addFace(x);
      
      
  }
  //Close the input stream
  in.close();
    }catch (Exception e){//Catch exception if any
  System.err.println("Error: " + e.getMessage());
  }
  
    }
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
            if (transform!=null){
            g.drawLine(transform[0][0],transform[1][0],transform[0][1],transform[1][1]);
            g.drawLine(transform[0][1],transform[1][1],transform[0][2],transform[1][2]);
            g.drawLine(transform[0][0],transform[1][0],transform[0][2],transform[1][2]);
            }
        }
    }
    public void render(Graphics g){
        //renderFull(g);
        g.setColor(Color.BLACK);
        renderWireframe(g);
    }
    public void renderFull(Graphics g){
        for (int i=0; i<faces.length-1; i++){//BUBBLE SORT the faces in order to be rendered
            
            if (comp(faces[i],faces[i+1])){
                Vertex[] v=faces[i];
                faces[i]=faces[i+1];
                faces[i+1]=v;
                i-=2;
                if (i==-2){
                    i++;
                }
            }
        }
        Random r=new Random(5021);
        for (int i=0; i<faces.length; i++){
            g.setColor(new Color(r.nextFloat(),r.nextFloat(),r.nextFloat()));
            int[][] transform=transform(faces[i]);
            g.fillPolygon(transform[0],transform[1],transform[0].length);
        }
    }
    public boolean norm(Vertex[] a){
        return a[0].x*a[1].y-a[0].y*a[1].x<0;
    }
    public boolean norm1(Vertex[] a){
        return norm(new Vertex[] {new Vertex(a[1].x-a[0].x,a[1].y-a[0].y,a[1].z-a[0].z),new Vertex(a[2].x-a[0].x,a[2].y-a[0].y,a[2].z-a[0].z)});
    }
    public boolean comp(Vertex[] a, Vertex[] b){//Should b be rendered before a
        
        if (norm1(a) && !norm1(b)){
            return true;
        }
        if (norm1(b) && !norm1(a)){
            return false;
        }
        return a[0].z+a[1].z+a[2].z>b[0].z+b[1].z+b[2].z;//Todo: Make one automatically win if the normal vector of the other is in the negative z direction. Problem is, now triangles have to be defined in counter-clockwise order for normal vectors to work properly. (True story: I only know about normal vectors from working on the STL exporter in Trumpetr =)
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
    private static int[][] transform(Vertex[] v){
       int[][]  a={v[0].transform(),v[1].transform(),v[2].transform()};
       if (a[0]==null || a[1]==null || a[2]==null){
           return null;
       }
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
        return new Mesh(this).MutTransform(t);
    }
    private Mesh MutTransform(Transform t){
        for (Vertex[] v : faces){
            for (Vertex x : v){
                x.transform(t);
            }
        }
        return this;
    }
}
