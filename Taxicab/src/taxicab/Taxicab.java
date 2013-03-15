/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxicab;

import java.util.HashMap;

/**
 *
 * @author leijurv
 */
public class Taxicab {

    /**
     * @param args the command line arguments
     */
    
    public static class Point{
        int x;
        int y;
        public Point(int X, int Y){
            x=X;
            y=Y;
        }
        public int dist0(Point b){
            if (x==b.x || y==b.y){
                return 1;
            }
            if (y>b.y){
                return b.dist0(this);
            }
            if (x>b.x){
                return (new Point(-x,y).dist0(b));
            }
            return (new Point(x+1,y)).dist0(b)+(new Point(x,y+1)).dist0(b);
        }
        public int dist_2(Point b){
            return dist_2(b.x-x,b.y-y);
        }
        private static int dist_2(int dx, int dy){
            if (dx==0 || dy==0){
                return 1;
            }
            if (dx<0){
                return dist_1(0-dx,dy);
            }
            if (dy<0){
                return dist_1(dx,0-dy);
            }
            if (dx==dy){
                return factorial(dx+1);
            }
            if (dx>dy){
                return dist_2(dy,dx);
            }
            if (dx+1==dy){
                if (dy%2==0){
                    return factorial(dy+1)/(factorial(dy+1-(dy+2)/2)*factorial((dy+2)/2));
                }
               return factorial(dy+1)/(factorial(dy+1-(dy+1)/2)*factorial((dy+1)/2));
                
                //row: dy+1
                  //      col: (dy+1)/2
            }
            
            return dist_2(dx+1,dy-1);
        }
        static HashMap<Integer,Integer> factMap=new HashMap<Integer,Integer>();
        public static int factorial(int i){
            if (factMap.get(i)==null){
                if (i==0){
                    factMap.put(0,1);
                    return 1;
                }else{
                    int x=i*factorial(i-1);
                    factMap.put(i,x);
                    return x;
                }
                
            }
            return factMap.get(i);
        }
        private static int dist_1(int dx, int dy){
            if (dx==0 || dy==0){
                return 1;
            }
            if (dx<0){
                return dist_1(0-dx,dy);
            }
            if (dy<0){
                return dist_1(dx,0-dy);
            }
            return dist_1(dx-1,dy)+dist_1(dx,dy-1);
        }
        public int dist1(Point b){
            return dist_1(x-b.x,y-b.y);
        }
        public void dist2(Point b){
            
        }
    }
    
    public static void main(String[] args) {
        System.out.println((new Point(0,0)).dist_2(new Point(3,3)));
    }
}
