/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threed;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Random;
import static threed.ThreeD.offset;
import static threed.Vertex.coeff;
import static threed.Vertex.zAdd;
/**
 *
 * @author leijurv
 */
public class Mesh {
    static final Mesh cube=new Mesh();
    static final Mesh tri=new Mesh();
    static final Mesh plane=new Mesh();
    Vertex[][] faces=new Vertex[0][0];
    Vertex[] light={new Vertex(.62,-2.5,0),new Vertex(-1,-2.5,0)};
    Vertex cam=new Vertex(0,0,0);
    public static void init(){
        Vertex[][] cub={
            {new Vertex(-0.5,-0.5,-0.5),new Vertex(-0.5,0.5,-0.5),new Vertex(0.5,0.5,-0.5),new Vertex(0.5,-0.5,-0.5)},
            {new Vertex(-0.5,-0.5,-0.5),new Vertex(0.5,-0.5,-0.5),new Vertex(0.5,-0.5,0.5),new Vertex(-0.5,-0.5,0.5)},
            {new Vertex(-0.5,-0.5,-0.5),new Vertex(-0.5,-0.5,0.5),new Vertex(-0.5,0.5,0.5),new Vertex(-0.5,0.5,-0.5)},
            {new Vertex(0.5,0.5,0.5),new Vertex(0.5,0.5,-0.5),new Vertex(0.5,-0.5,-0.5),new Vertex(0.5,-0.5,0.5)},
            {new Vertex(0.5,0.5,0.5),new Vertex(0.5,0.5,-0.5),new Vertex(-0.5,0.5,-0.5),new Vertex(-0.5,0.5,0.5)},
            {new Vertex(0.5,0.5,0.5),new Vertex(-0.5,0.5,0.5),new Vertex(-0.5,-0.5,0.5),new Vertex(0.5,-0.5,0.5)}};
        Vertex[][] cub2=new Vertex[cub.length*2][3];
        for (int i=0; i<cub.length; i++){
            if (i==3){
                Vertex v=cub[i][0];
                cub[i][0]=cub[i][2];
                cub[i][2]=v;
            }
            cub2[i*2]=new Vertex[]{cub[i][1],cub[i][0],cub[i][2]};
            cub2[i*2+1]=new Vertex[]{cub[i][3],cub[i][2],cub[i][0]};
        }
        cube.faces=cub2;
        Vertex[][] r={{new Vertex(-0.5,-0.5,0),new Vertex(0,0,0.5),new Vertex(-0.5,0.5,0)},{new Vertex(-0.5,-0.5,0),new Vertex(0,0,0.5),new Vertex(0.5,-0.5,0)},{new Vertex(0.5,0.5,0),new Vertex(0,0,0.5),new Vertex(-0.5,0.5,0)},{new Vertex(0.5,0.5,0),new Vertex(0,0,0.5),new Vertex(0.5,-0.5,0)},
        {new Vertex(-0.5,-0.5,0),new Vertex(0.5,-0.5,0),new Vertex(0.5,0.5,0)},
        {new Vertex(-0.5,0.5,0),new Vertex(-0.5,-0.5,0),new Vertex(0.5,0.5,0)}
        };
        swit(r[0],0,1);
        swit(r[3],0,1);
        tri.faces=r;
        plane.faces=new Vertex[][]{{new Vertex(10,0,10),new Vertex(10,0,-10),new Vertex(-10,0,10)},{new Vertex(-10,0,10),new Vertex(10,0,-10),new Vertex(-10,0,-10)}};
    }
    public static void swit(Vertex[] a,int ind,int ind1){
        Vertex v=a[ind];
        a[ind]=a[ind1];
        a[ind1]=v;
    }
    public static Vertex[] copy(Vertex[] a){
        Vertex[] res=new Vertex[a.length];
        for (int i=0; i<res.length; i++){
            res[i]=new Vertex(a[i]);
        }
        return res;
    }
    public Mesh(String s){
        try{
            faces=new Vertex[0][3];
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream=new FileInputStream(s);
            try ( // Get the object of DataInputStream
                    DataInputStream in=new DataInputStream(fstream)) {
                BufferedReader br=new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine=br.readLine())!=null){
                    Vertex[] x=new Vertex[3];
                    x[0]=new Vertex(strLine);
                    x[1]=new Vertex(br.readLine());
                    x[2]=new Vertex(br.readLine());
                    addFace(x);
                }
                //Close the input stream
            }
        } catch (Exception e){//Catch exception if any
            System.err.println("Error: "+e.getMessage());
        }
    }
    public Mesh(Mesh[] m){
        faces=m[0].faces;
        for (int i=1; i<m.length; i++){
            for (Vertex[] face : m[i].faces){
                addFace(face);
            }
        }
        light=copy(m[0].light);
        cam=new Vertex(m[0].cam);
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
        for (Vertex[] adf : faces){
            int[][] transform=transform(adf);
            if (transform!=null){
                g.drawLine(transform[0][0],transform[1][0],transform[0][1],transform[1][1]);
                g.drawLine(transform[0][1],transform[1][1],transform[0][2],transform[1][2]);
                g.drawLine(transform[0][0],transform[1][0],transform[0][2],transform[1][2]);
            }
        }
    }
    public void render(Graphics g){
        if (type==3){
            renderWireframe(g);
            return;
        }
        g.drawImage(renderFull(),0,0,null);
    }
    static int type=0;
    public BufferedImage renderFull(){
        BufferedImage result=new BufferedImage(1000,700,BufferedImage.TYPE_INT_ARGB);
        
        Graphics g=result.getGraphics();
        g.setColor(new Color(238,238,238,0));
        g.fillRect(0,0,1000,700);
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
            Vertex norm=norm1(faces[i]);
            //Vertex lig=new Vertex(light);
            //lig.
            
            double[] res=light(average(faces[i]),norm);
            g.setColor(grayScale((int)res[0]));
            if(type==2){
                g.setColor(new Color(r.nextInt(256),r.nextInt(256),r.nextInt(256)));
            }
            //g.drawLine(i,i,i,i);
            //g.setColor(new Color(r.nextFloat(),r.nextFloat(),r.nextFloat()));
            if (norm.z<0){
                int[][] transform=transform(faces[i]);
                
                
                if (type==1){
                BufferedImage temp=new BufferedImage(1000,700,BufferedImage.TYPE_INT_RGB);
                Graphics gg=temp.getGraphics();
                gg.setColor(Color.WHITE);
                gg.fillPolygon(transform[0],transform[1],transform[0].length);
                g.setColor(Color.RED);
                int minX=200+offset;
                int maxX=450+offset;
                int minY=100;
                int maxY=400;
                //g.drawRect(minX,minY,maxX-minX,maxY-minY);
                for (int x=minX; x<maxX; x++){
                    for (int y=minY; y<maxY; y++){
                        if (temp.getRGB(x,y)!=-16777216){
                            double e=(double)(x-offset)+0.01;
                            double f=(double)y+0.01;
                            e=(e-300)/(double)coeff;
                            f=(f-300)/(double)coeff;
                            
                            double d=det(norm,faces[i][0]);
                            double bot=2*(norm.x*e+norm.y*f);
                            
                            /*
                            double dett=d*d-4*norm.x*norm.z*e-4*norm.y*norm.z*f;
                            double det=Math.sqrt(dett);
                            double resX=(d*e+e*det)/bot;
                            double resY=(d*f+f*det)/bot;
                            double resZ=(d-det)/(2*norm.z);*/
                            
                            
                            
                            double dett=d*d-4*f*norm.y*norm.z-4*e*norm.z*norm.x+2*d*norm.z*zAdd+norm.z*norm.z*zAdd*zAdd;
                            double det=Math.sqrt(dett);
                            double resX=(d*e+e*norm.z*zAdd+e*det)/bot;
                            double resY=(d*f+f*norm.z*zAdd+f*det)/bot;
                            double resZ=(d-norm.z*zAdd-det)/(2*norm.z);
                            
                            
                            //System.out.println(resX*(resZ+zAdd)+","+e+","+resY*(resZ+zAdd)+","+f);
                            //System.out.println(det(norm,faces[i][0])+","+det(norm,faces[i][1])+","+det(norm,faces[i][2])+","+det(norm,new Vertex(resX,resY,resZ)));
                            //System.out.println(resX+","+resY+","+resZ+","+det);
                            //Color C=(light((int)res[0]));
                            Color C=Color.BLUE;
                            
                            if (dett>0){
                                 C=grayScale((int)light(new Vertex(resX,resY,resZ),norm)[0]);
                                
                            }else{
                                //System.out.println("IS CAT");
                            }
                            result.setRGB(x,y,C.getRGB());
                            //g.drawLine(x,y,x,y);
                            /*
                            if ((x+y)%1175==0){
                                g.setColor(Color.BLUE);
                                g.drawString(minAng==0?"0":(minAng+""),x,y);
                            }*/
                        }
                }}
                }else{
                    g.fillPolygon(transform[0],transform[1],transform[0].length);
                }
                
                    
                
            }
            Vertex cent=average(faces[i]);
            Vertex ad=sub(cent,norm);
            int[] a=cent.transform();
            int[] b=ad.transform();
            
            g.setColor(Color.BLACK);
            g.fillRect(a[0]-2,a[1]-2,4,4);
           // g.drawLine(a[0],a[1],b[0],b[1]);
            if (norm.z<0){
                g.setColor(Color.BLUE);
               // g.drawString(res[1]==0?"0":((180D/Math.PI*res[1])+"").substring(0,4),a[0],a[1]);
            }
        }
        for (Vertex Lig : light){
        int[] A=Lig.transform();
        g.setColor(Color.BLUE);
        g.fillRect(A[0]-2,A[1]-2,4,4);
        }
        return result;
    }
    public double[] light(Vertex point, Vertex norm){
        double minAngPhong=Math.PI;
        double minAngLambert=Math.PI;
        int totalLambert=0;
            int maxCol=0;
            for (int i=0; i<light.length; i++){
            Vertex incidence=sub(light[i],point);
            
            double angLambert=angle(incidence,sub(new Vertex(0,0,0),norm));//Angle between normal and incidence
            
            Vertex oppo=reflect(norm,incidence);
            double angPhong=angle(new Vertex(0,0,1),oppo);//Angle between reflection and camera
            
            int phong=asdf(angPhong,3);
            
            int lambert=asdf(3,angLambert);
            //int phong=lambert;
            totalLambert+=lambert;
            if (phong>maxCol){
                maxCol=phong;
                minAngPhong=angPhong;
                minAngLambert=angLambert;
            }
            }
            maxCol+=totalLambert;
            if (maxCol>255){
                maxCol=255;
            }
            return new double[] {(double)maxCol,minAngPhong,minAngLambert};
    }
    public static Vertex reflect(Vertex norm, Vertex incidence){
        double a=norm.x;
            double b=norm.y;
            double c=norm.z;
            double bot=a*a+b*b+c*c;
            double[][] mat={{2*a*a/bot-1,2*a*b/bot,2*a*c/bot},{2*a*b/bot,2*b*b/bot-1,2*b*c/bot},{2*a*c/bot,2*c*b/bot,1-2*(a*a+b*b)/bot}};
            Vertex oppo=new Vertex(incidence);
            oppo.mult(mat);
            return oppo;
    }
    public static Color grayScale(int a){
        return new Color(a,a,a);
    }
    public static int asdf(double angPhong, double angLambert){
        if (angLambert>Math.PI/2 ){
            angLambert=Math.PI/2;
        }
        if (angPhong>Math.PI/2){
            angPhong=Math.PI/2;
        }
        return 127-(int) (180D/Math.PI*angLambert*1.4D)+128-(int) (180D/Math.PI*angPhong*1.4D);
    }
    public static Vertex cross(Vertex[] a){
        return new Vertex(a[0].y*a[1].z-a[0].z*a[1].y,a[0].z*a[1].x-a[0].x*a[1].z,a[0].x*a[1].y-a[0].y*a[1].x);
    }
    public static Vertex norm1(Vertex[] a){
        return cross(sub(a));
    }
    public static double det(Vertex norm, Vertex face){
        return (norm.x*face.x+norm.y*face.y+norm.z*face.z);
    }
    public static Vertex sub(Vertex a,Vertex b){
        return new Vertex(a.x-b.x,a.y-b.y,a.z-b.z);
    }
    public static Vertex[] sub(Vertex[] a){
        return new Vertex[]{sub(a[1],a[0]),sub(a[2],a[0])};
    }
    public static double dot(Vertex a,Vertex b){
        return a.x*b.x+a.y*b.y+a.z*b.z;
    }
    public static double angle(Vertex a,Vertex b){
        return Math.acos(dot(a,b)/(a.abs()*b.abs()));
    }
    public static Vertex average(Vertex[] f){
        return new Vertex((f[0].x+f[1].x+f[2].x)/3,(f[0].y+f[1].y+f[2].y)/3,(f[0].z+f[1].z+f[2].z)/3);
    }
    public static Vertex add(Vertex a,Vertex b){
        return new Vertex(a.x+b.x,a.y+b.y,a.z+b.z);
    }
    public static boolean comp(Vertex[] a,Vertex[] b){
        return average(a).z>average(b).z;
    }
    public double[] lightDist(double x, double y){
        double minDist=10000;
        double ind=-1;
        for (int i=0; i<light.length; i++){
            Vertex Lig=light[i];
        int[] A=Lig.transform();
        double X=A[0];
        double Y=A[1];
        System.out.println(X+","+Y+","+x+","+y);
        double dist=Math.sqrt((Y-y)*(Y-y)+(X-x)*(X-x));
        if (dist<minDist){
            ind=i;
            minDist=dist;
        }
        }
        return new double[] {minDist,ind};
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
        light=copy(m.light);
        cam=new Vertex(m.cam);
    }
    private static int[][] transform(Vertex[] v){
        int[][] a={v[0].transform(),v[1].transform(),v[2].transform()};
        if (a[0]==null||a[1]==null||a[2]==null){
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
        for (Vertex v : light){
            v.transform(t);
        }
        cam.transform(t);
        return this;
    }
}
