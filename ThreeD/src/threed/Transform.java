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
    double Sx;
    double Sy;
    double Sz;
    public Transform(double DX,double DY,double DZ){
        this(DX,DY,DZ,0,0,0);
    }
    public Transform(double DX,double DY,double DZ,double RX,double RY,double RZ){
        this(DX,DY,DZ,RX,RY,RZ,1,1,1);
    }
    public Transform(double DX,double DY,double DZ,double RX,double RY,double RZ,double SX,double SY,double SZ){
        Dx=DX;
        Dy=DY;
        Dz=DZ;
        Rx=RX;
        Ry=RY;
        Rz=RZ;
        Sx=SX;
        Sy=SY;
        Sz=SZ;
    }

}
