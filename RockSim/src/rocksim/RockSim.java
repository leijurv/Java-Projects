/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rocksim;

/**
 *
 * @author leijurv
 */
public class RockSim {

    /**
     * @param args the command line arguments
     */
    final static int points=2;
    static double[] times=new double[points+1];
    static double[] newtons=new double[points+1];
    
    static double mass=1;
    
    static final double gravity=9.8;
    
    static double[] acceleration=new double[points+1];
    static double[] velocity=new double[points+1];
    static double[] position=new double[points+1];
    
    static double burnout_altitude=0;
    static double burnout_velocity=0;
    static double apogee=0;
    
    static double start_altitude=0;
    public static void main(String[] args) {
        times[0]=0;
        newtons[0]=9.8;
        times[1]=1;
        newtons[1]=10.8;
        times[2]=1.5;
        newtons[2]=11.8;
        for (int i=0; i<times.length; i++){
            acceleration[i]=(newtons[i]/mass)-gravity;
        }
        velocity[0]=0;
        for (int i=1; i<velocity.length; i++){
            velocity[i]=velocity[i-1]+((times[i]-times[i-1])*(acceleration[i-1]+((acceleration[i]-acceleration[i-1])/2)));
        }
        position[0]=0;
        for (int i=1; i<position.length; i++){
            position[i]=position[i-1]+((times[i]-times[i-1])*(velocity[i-1]+((velocity[i]-velocity[i-1])/2)));
        }
        burnout_altitude=position[position.length-1]+start_altitude;
        burnout_velocity=velocity[velocity.length-1];
        apogee=burnout_altitude+((burnout_velocity*burnout_velocity)/(2*gravity));
        System.out.println(apogee);
    }
}
