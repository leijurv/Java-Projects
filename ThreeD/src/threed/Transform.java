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
public class Transform {
    double Dx;
    double Dy;
    double Dz;
    double Rx;
    double Ry;
    double Rz;
    public Transform(double DX,double DY,double DZ){
        Dx=DX;
        Dy=DY;
        Dz=DZ;
    }
    public Transform(double DX,double DY,double DZ,double RX,double RY,double RZ){
        this(DX,DY,DZ);
        Rx=RX;
        Ry=RY;
        Rz=RZ;
    }
}
