/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package threed;

/**
 *
 * @author leijurv
 */
public class Vertex {
    double x;
    double y;
    double z;
    public Vertex(double X, double Y, double Z){
        x=X;
        y=Y;
        z=Z;
    }
    public Vertex(Vertex v){
        this(v.x,v.y,v.z);
    }
    public Vertex(String s){
        String[] r=s.split(" ");
        x=Double.parseDouble(r[0]);
        y=Double.parseDouble(r[1]);
        z=Double.parseDouble(r[2]);
    }
    public int[] transform(){
        
        if (z<=-2){
            return null;
        }
        
        if (x/z>2){
            //return null;
        }
        System.out.println(2+z);
        return new int[] {(int)(300+10*(x*(2+z)))+ThreeD.offset,(int)(300+10*(y*(2+z)))};
    }
    public String toString(){
        return x+","+y+","+z;
    }
    public void transform(Transform t){
        x*=t.Sx;
        y*=t.Sy;
        z*=t.Sz;
        mult(new double[][] {{Math.cos(t.Ry),0,Math.sin(t.Ry)},{0,1,0},{-Math.sin(t.Ry),0,Math.cos(t.Ry)}});
        mult(new double[][] {{1,0,0},{0,Math.cos(t.Rx),-Math.sin(t.Rx)},{0,Math.sin(t.Rx),Math.cos(t.Rx)}});
        mult(new double[][] {{Math.cos(t.Rz),-Math.sin(t.Rz),0},{Math.sin(t.Rz),Math.cos(t.Rz),0},{0,0,1}});
        x+=t.Dx;
        y+=t.Dy;
        z+=t.Dz;
        
    }
    public void mult(double[][] mat){
        double[] res=new double[3];
        for (int i=0; i<3; i++){
            res[i]=mat[0][i]*x+mat[1][i]*y+mat[2][i]*z;
        }
        x=res[0];
        y=res[1];
        z=res[2];
    }
}
