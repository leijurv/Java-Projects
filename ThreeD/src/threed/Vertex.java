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
        x=v.x;
        y=v.y;
        z=v.z;
    }
    public int[] transform(){
        double amt=(((double)ThreeD.x)/200)%5;
        return new int[] {(int)(300+200*(x)),(int)(300+200*(y))};
        //return new int[] {(int)(300+200*(x+z*0.2)),(int)(300+200*(y+z*0.8))};
        //return new int[] {(int)(300+200*(x+y*(1-amt))),(int)(300+200*(y*amt+z))};
    }
    public String toString(){
        return x+","+y+","+z;
    }
    public void transform(Transform t){
        x+=t.Dx;
        y+=t.Dy;
        z+=t.Dz;
        mult(new double[][] {{1,0,0},{0,Math.cos(t.Rx),-Math.sin(t.Rx)},{0,Math.sin(t.Rx),Math.cos(t.Rx)}});
        mult(new double[][] {{Math.cos(t.Ry),0,Math.sin(t.Ry)},{0,1,0},{-Math.sin(t.Ry),0,Math.cos(t.Ry)}});
        mult(new double[][] {{Math.cos(t.Rz),-Math.sin(t.Rz),0},{Math.sin(t.Rz),Math.cos(t.Rz),0},{0,0,1}});
        
        
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
