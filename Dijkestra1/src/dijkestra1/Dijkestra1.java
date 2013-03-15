/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dijkestra1;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author leif
 */
public class Dijkestra1 {

    /**
     * @param args the command line arguments
     */
    public static int[] add(int[] a, int b){
        int[] c=new int[a.length+1];
        for (int i=0; i<a.length; i++){
            c[i]=a[i];
        }
        c[a.length]=b;
        return c;
    }
    public static int[][] add(int[][] a){
        int[][] c=new int[a.length+1][0];
        for (int i=0; i<a.length; i++){
            c[i]=a[i];
        }
        c[a.length]=new int[0];
        return c;
    }
    public static int[][] readfile(String filename){
        int[][] result=new int[0][0];
        try{
            FileInputStream fstream = new FileInputStream(filename);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            int i=0;
            while ((strLine = br.readLine()) != null)   {
                String[] a=strLine.split(",");
                result=add(result);
                for (String x : a){
                    result[i]=add(result[i],Integer.parseInt(x));
                }
                i++;
            }
            in.close();
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return result;
    }
    static int[][] matrix=readfile("/matrix.txt");
    static Point[][] Matrix=new Point[80][80];
    static{
        for (int i=0; i<80; i++){
            for (int n=0; n<80; n++){
                Matrix[i][n]=new Point(i,n);
            }
        }
    }
    public static void main(String[] args) {
        int best=Integer.MAX_VALUE;
        //for (int y=0; y<80; y++){
            for (int n=0; n<80; n++){
                int dist=solve(Matrix[0][n],0);
                if (dist<best){
                    best=dist;
                }
                System.out.println(":"+n+":"+best);
                for (int i=0; i<80; i++){
            for (int q=0; q<80; q++){
                Matrix[i][q]=new Point(i,q);
            }
       // }
            }
        }
        System.out.println(best);
    }
    public static int solve(Point source,int endy){
        List<Point> Q=new ArrayList<Point>();
        for (int i=0; i<80; i++){
            for (int n=0; n<80; n++){
                Q.add(Matrix[i][n]);
            }
        }/*
        for (int i=0; i<80; i++){
            Q.get(i).dist=0;
            Q.get(i).visited=true;
        }*/
        Q.get(source.y).dist=0;
        Q.get(source.y).visited=true;
        
        
        while (!Q.isEmpty()){
            int smallest=Integer.MAX_VALUE;
            int index=0;
            for (int i=0; i<Q.size(); i++){
                if (Q.get(i).visited){
                    if (Q.get(i).dist<smallest){
                        smallest=Q.get(i).dist;
                        index=i;
                    }
                }
            }
            //System.out.println(sintmallest);
            
            Point y=Q.get(index);
            if (y.x==79){
                return y.dist+matrix[0][source.y];
            }
            Q.remove(index);
            List<Point> N=y.N();
            for (Point n:N){
                int alt=y.dist+matrix[n.x][n.y];
                if (!n.visited){
                    n.dist=alt;
                    n.visited=true;
                    n.prev=y;
                }else{
                if (alt<n.dist){
                    n.dist=alt;
                    n.visited=true;
                    n.prev=y;
                }
                }
            }
        }
        int res=Matrix[79][endy].dist;
        return res+matrix[0][source.y];
    }
}
