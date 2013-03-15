/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dijkrestra;

/**
 *
 * @author leijurv
 */
public class Dijkrestra{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        long startTime=System.currentTimeMillis();
        System.out.println(startTime);
        int numberOfPoints=6;
        int IdOfGoal=4;
        boolean[][] connections={{},{true/*Point 1 is connected to point 0*/},{true/*Point 2 is connected to point 0*/,true/*Point 2 is connected to point 1*/},{false/*Point 3 is not connected to point 0*/,true/*Point 3 is connected to point 1*/,true/*Point 3 is connected to point 2... you get the idea*/},{false,false,false,true},{true,false,true,false,true}};
        int[][] distances={{7/*Point 0 is 7 away from the first point it's connected to*/,9/*Point 0 is 9 away from the second point it's connected to*/,14/*You get the idea*/},{7,10,15},{9,10,11,2},{15,11,6},{6,9},{14,2,9}};
        //These aren't random: I got then from http://en.wikipedia.org/wiki/File:Dijksta_Anim.gif
        Graph g=construct(connections,distances,IdOfGoal,numberOfPoints);
        g.solve();
        int[] yar=g.getPath();
        System.out.println("Distance: "+g.points[IdOfGoal].dist);
        System.out.print("Path: ");
        for (int i=0; i<yar.length-1; i++){
            System.out.print(yar[i]+",");
        }
        System.out.println(yar[yar.length-1]);
        
    }
    public static Graph construct(boolean[][] t, int[][] w, int c, int a){
        for (int i=0; i<t.length; i++){
            if (t[i].length<6){
                int n=0;
                if (t[i].length==n){
                    if (i==n){
                        t[i]=addend(t[i],false);
                    }else{
                        t[i]=addend(t[i],t[1][i]);
                    }
                }
                n++;
                if (t[i].length==n){
                    if (i==n){
                        t[i]=addend(t[i],false);
                    }else{
                        t[i]=addend(t[i],t[n][i]);
                    }
                    
                }
                n++;
                if (t[i].length==n){
                    if (i==n){
                        t[i]=addend(t[i],false);
                    }else{
                        t[i]=addend(t[i],t[n][i]);
                    }
                    
                }
                n++;
                if (t[i].length==n){
                    if (i==n){
                        t[i]=addend(t[i],false);
                    }else{
                        t[i]=addend(t[i],t[n][i]);
                    }
                    
                }
                n++;
                if (t[i].length==n){
                    if (i==n){
                        t[i]=addend(t[i],false);
                    }else{
                        t[i]=addend(t[i],t[n][i]);
                    }
                    
                }
                n++;
                if (t[i].length==n){
                    if (i==n){
                        t[i]=addend(t[i],false);
                    }else{
                        t[i]=addend(t[i],t[n][i]);
                    }
                    
                }
                
                
            }
        }
        int[][] d=new int[a][a];
        for (int i=0; i<a; i++){
            int b=0;
            for (int n=0; n<a; n++){
                if (t[i][n]){
                    d[i][n]=w[i][b];
                    b++;
                }
            }
        }
        Graph g=new Graph(t,d,4);
        return g;
    }
    public static class Graph{
        Point[] points;
        int dest;
        public int[] getPath(){
            points[0].prev=null;
            int[] r=new int[0];
            int p=dest;
            while(points[p].prev!=null){
                r=add(p,r);
                p=points[p].prev.id;
            }
            r=add(0,r);
            return r;
        }
        public Graph(boolean[][] connected, int[][] dists, int target){
            dest=target;
            points=new Point[connected.length];
            for (int i=0; i<connected.length; i++){
                points[i]=new Point();
                for (int n=0; n<connected[i].length; n++){
                    if (connected[i][n]){
                        points[i].adda(n);
                        points[i].add(dists[i][n]);
                    }
                }
                points[i].id=i;
            }
            for (int i=0; i<points.length; i++){
                for (int n=0; n<points[i].connectedids.length; n++){
                    points[i].add(points[points[i].connectedids[n]]);
                }
            }
        }
        public void solve(){
            Graph g=this;
        Point source=g.points[0];
        source.dist=0;
        source.inf=false;
        source.prev=null;
        Point[] Q=g.points;
        boolean f=true;
        while(Q.length!=0){
            Point u=new Point();
            int a=0;
            for (int i=1; i<Q.length; i++){
                if (Q[i].dist<=u.dist){
                    u=Q[i];
                    a=i;
                }
            }
            if (u.id==dest){
                //DONE
                return;
            }
            if (u.inf && !f){
                throw new RuntimeException("No possible path!");
            }
            if (f){
                a=0;
                u=Q[a];
                f=false;
            }
            Q=remove(Q,a);
            for (int i=0; i<u.connected.length; i++){
                int alt=u.dist+u.dists[i];
                if (alt<u.connected[i].dist){
                    u.connected[i].dist=alt;
                    u.connected[i].prev=u;
                    u.connected[i].inf=false;
                    //decrease key v in q
                }
            }
        }
        
    }
        public int[] add(int p, int[] connected){
            int[] c=new int[connected.length+1];
            for (int i=0; i<connected.length; i++){
                c[i+1]=connected[i];
            }
            c[0]=p;
            return c;
        }
    }
    private static boolean[] addend(boolean[] a, boolean b) {
        boolean[] c = new boolean[a.length + 1];
        System.arraycopy(a, 0, c, 0, a.length);
        c[a.length] = b;
        return c;
    }
    
    public static Point[] remove(Point[] a, int b){
        if (b==0){
            if (a.length==1){
                Point[] r={};
                return r;
            }
            Point[] r=new Point[a.length-1];
            for (int i=1; i<r.length; i++){
                
            }
        }
        Point[] r=new Point[a.length-1];
        System.arraycopy(a, 0, r, 0, b);
        for (int i=b+1; i<a.length; i++){
            r[i-1]=a[i];
        }
        return r;
    }
    public static class Point{
        int id;
        int dist;
        boolean inf;
        Point[] connected=new Point[0];
        int[] connectedids=new int[0];
        int[] dists=new int[0];
        Point prev;
        public Point(){
            dist=30000000;
            inf=true;
        }
        public void add(Point p){
            Point[] c=new Point[connected.length+1];
            System.arraycopy(connected, 0, c, 0, c.length-1);
            c[connected.length]=p;
            connected=c;
        }
        public void adda(int a){
            int[] b=new int[connectedids.length+1];
            System.arraycopy(connectedids, 0, b, 0, connectedids.length);
            b[connectedids.length]=a;
            connectedids=b;
        }
        public void add(int a){
            int[] b=new int[dists.length+1];
            System.arraycopy(dists, 0, b, 0, dists.length);
            b[dists.length]=a;
            dists=b;
        }
    }
}
