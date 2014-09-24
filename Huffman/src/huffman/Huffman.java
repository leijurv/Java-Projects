/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffman;

import java.awt.Graphics;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author leijurv
 */
public class Huffman {
    public static void GOOEY(Node n){
        //System.out.println(n.child1.child1);
        //n.Fin("");
        
        JFrame frame=new JFrame("adf");
        frame.setSize(2000,2000);
        JComponent M=new JComponent(){
            public void paintComponent(Graphics g){
                n.draw(g,500,100);
            }
        };
        frame.setContentPane(M);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        FileInputStream Aa=new FileInputStream("/Users/leijurv/Desktop/derp6.log");
        
        //FileInputStream Aa=new FileInputStream("/Users/leijurv/Documents/adsf.txt");
        //FileInputStream Aa=new FileInputStream("/Users/leijurv/Downloads/hand1.png");
        byte[] x=new byte[Aa.available()];
        System.out.println(Aa.read(x)+","+x.length);
        Aa.close();
        //x[0]=(byte) (0-x[0]);
        //new Random().nextBytes(x);
        Node n=write(x);
        System.out.println("Done writing");
        System.out.println(total/8);
        System.out.println(x.length);
        byte[] r=read(n,x.length);
        System.out.println("Done reading");
        //System.out.println(r.length);
        for (int i=0; i<r.length || i<x.length; i++){
            if (r[i]!=x[i]){
                System.out.println(r[i-1]+","+x[i-1]+"no"+r[i]+","+x[i]);
            }
        }
        System.out.println("Verified");
        //Node n=new Node(new DataInputStream(new FileInputStream("/Users/leijurv/Documents/asdf1.txt")));
        GOOEY(n);
    }
    
    public static byte[] read(Node n,int size) throws Exception{
        FileInputStream a=new FileInputStream("/Users/leijurv/Documents/asdf1.txt");
        n=new Node(new DataInputStream(a));
        byte[] x=new byte[a.available()-4];
        a.read(x);
        int num=new DataInputStream(a).readInt();
        System.out.println(num);
        Node temp=n;
        int pos=0;
        byte[] res=new byte[size];
        for (int i=0; i<x.length; i++){
            boolean[] b=readd(x[i],i==x.length-1?num:8);
            
            for (int nn=0; nn<b.length; nn++){
                boolean A=b[nn];
            temp=A?temp.child2:temp.child1;
            if (temp.isLeaf){
                res[pos++]=temp.cont;
                //System.out.print((char)temp.cont);
                temp=n;
            }
            
            }
        }
        return res;
    }
    public static boolean[] readd(byte b,int num){
        boolean[] result=new boolean[num];
        for (int i=0; i<num; i++){
            result[(num-1)-i]=(1&b)!=0;
            b=(byte) (b>>1);
        }
        return result;
    }
    public static Node ana2(byte[] x){
        ArrayList<Node> a=analysis(x);
        sort(a);
        System.out.println(a);
        while(a.size()>1){
            Node A=lowest(a);
            Node B=lowest(a);
            a.add(0,new Node(A,B));
        }
        Node nn=a.get(0);
        return nn;
    }
    public static Node ana(byte[] x){
        ArrayList<Node> a=analysis(x);
        ArrayList<Node> qu=new ArrayList<Node>();
        sort(a);
        while(a.size()>1 || qu.size()>1){
            Node A=lowest(a,qu);
            Node B=lowest(a,qu);
            qu.add(qu.size(),new Node(A,B));
            //System.out.println(a+"    "+qu);
        }
        Node nn=qu.get(0);
        return nn;
    }
    public static Node write(byte[] x) throws Exception{
        DataOutputStream Ab=new DataOutputStream(new FileOutputStream("/Users/leijurv/Documents/asdf1.txt"));
        long t1=System.currentTimeMillis();
        Node nn=ana(x);
        System.out.println("asdf"+(System.currentTimeMillis()-t1));
        boolean[][] ar=new boolean[256][];
        nn.Fin("",ar,Ab);
        
        
        byte cache=0;
        int num=0;
        for (int i=0; i<x.length; i++){
            /*
            if (i%100000==0){
                System.out.println((float)i/(float)x.length);
            }*/
            byte ab=x[i];
            boolean[] an=ar[128+(int)ab];
            for (int n=0; n<an.length; n++){
                cache=(byte) (cache<<1);
                if (an[n]){
                    cache=(byte) (cache|((byte)1));
                }
                num++;
                if (num==8){
                    //System.out.println(cache);
                    num=0;
                    Ab.writeByte(cache);
                }
            }
        }
        Ab.writeByte(cache);
        Ab.writeInt(num);
        Ab.close();
        return nn;
    }
    public static byte[] readBool(Node n, int size) throws Exception{
        DataInputStream a=new DataInputStream(new FileInputStream("/Users/leijurv/Documents/asdf1.txt"));
        Node temp=n;
        int pos=0;
        byte[] res=new byte[size];
        while(a.available()>0){
            boolean A=a.readBoolean();
            temp=A?temp.child2:temp.child1;
            if (temp.isLeaf){
                res[pos++]=temp.cont;
                temp=n;
            }
        }
        return res;
    }
    public static Node writeBool(byte[] x) throws Exception{
        Node nn=ana(x);
        
        DataOutputStream Ab=new DataOutputStream(new FileOutputStream("/Users/leijurv/Documents/asdf1.txt"));
        boolean[][] ar=new boolean[256][];
        nn.Fin("",ar,Ab);
        System.out.println("MEOWED");
        for (int i=0; i<x.length; i++){
            byte ab=x[i];
            boolean[] an=ar[128+(int)ab];
            for (int n=0; n<an.length; n++){
                Ab.writeBoolean(an[n]);
            }
        }
        Ab.close();
        return nn;
    }
    public static Node lowest(ArrayList<Node> a, ArrayList<Node> b){
        if (a.isEmpty()){
            return b.remove(0);
        }
        if (b.isEmpty()){
            return a.remove(0);
        }
        if (b.get(0).value<a.get(0).value){
            return b.remove(0);
        }
        return a.remove(0);
    }
    public static ArrayList<Node> analysis(byte[] m){
        long t1=System.currentTimeMillis();
        ArrayList<Byte> chars=new ArrayList<Byte>();
        for (int i=0; i<m.length; i++){
            if (!chars.contains(m[i])){
                chars.add(m[i]);
            }
        }
        long t2=System.currentTimeMillis();
        int[] counts=new int[chars.size()];
        for (int i=0; i<m.length; i++){
            counts[chars.indexOf(m[i])]++;
        }
        long t3=System.currentTimeMillis();
        //System.out.println(counts[255]);
        ArrayList<Node> result=new ArrayList<Node>();
        for (int i=0; i<chars.size(); i++){
            result.add(new Node(chars.get(i),counts[i]));
        }
        long t4=System.currentTimeMillis();
        System.out.println(t2-t1);
        System.out.println(t3-t2);
        System.out.println(t4-t3);
        return result;
    }
    static int total;
    public static class Node{
        byte cont;
        int value;
        boolean isLeaf;
        Node child1;
        Node child2;
        Node parent;
        public Node(DataInputStream a){
            try{
                isLeaf=a.readBoolean();
                if (isLeaf){
                    cont=a.readByte();
                }else{
                    child1=new Node(a);
                    child2=new Node(a);
                }
            } catch (IOException ex){
            }
            
        }
        public Node(byte V,int Va){
            cont=V;
            value=Va;
            isLeaf=true;
            child1=null;
            child2=null;
            parent=null;
        }
        public Node(Node a, Node b){
            isLeaf=false;
            child1=a;
            child2=b;
            value=a.value+b.value;
            a.parent=this;
            b.parent=this;
        }
        public String toString(){
            return isLeaf?value+":{"+cont+"}":value+":{"+child1+","+child2+"}";
        }
        public void Fin(String sf,boolean[][] a,DataOutputStream ab){
            try{
                ab.writeBoolean(isLeaf);
            } catch (IOException ex){
            }
            if (isLeaf){
                try{
                ab.writeByte(cont);
            } catch (IOException ex){
            }
                a[128+(int)cont]=toB(sf);
                //System.out.println(sf+":"+cont+":"+value);
                total+=sf.length()*value;
                return;
            }
            child1.Fin(sf+"0",a,ab);
            child2.Fin(sf+"1",a,ab);
        }
       
        public int getWidth(){
            if (isLeaf){
                return 20;
            }
            return child1.getWidth()+child2.getWidth();
        }
        public int getPos(int centX){
            if (isLeaf){
                return centX;
            }
            return centX-getWidth()/2+child1.getWidth();
        }
        public void draw(Graphics g, int x, int y){
            if (isLeaf){
                g.drawString(cont+"",x,y);
                return;
            }
            int w1=child1.getWidth();
            int w2=child2.getWidth();
            int mx=getPos(x);
            int x1=child1.getPos(mx-w1/2);
            int x2=child2.getPos(mx+w2/2);
            g.drawString(value+"",mx,y);
            g.drawLine(mx,y,x1,y+15);
            g.drawLine(mx,y,x2,y+15);
            child1.draw(g,mx-w1/2,y+15);
            child2.draw(g,mx+w2/2,y+15);
        }
    }
    public static boolean[] toB(String a){
        boolean[] b=new boolean[a.length()];
        for (int i=0; i<b.length; i++){
            b[i]=a.charAt(i)=='1';
        }
        return b;
    }
    public static void print(boolean[] a){
        for (int i=0; i<a.length; i++){
            if (a[i]){
                System.out.print("1");
            }else{
                System.out.print("0");
            }
        }
        System.out.println();
    }
    public static Node lowest(ArrayList<Node> a){
        int min=Integer.MAX_VALUE;
        int ind=-1;
        for (int i=0; i<a.size(); i++){
            if (a.get(i).value<min){
                min=a.get(i).value;
                ind=i;
            }
        }
        return a.remove(ind);
    }
      
    
    public static void sort(ArrayList<Node> a){
        //boolean going=true;
        //while(going){
            for (int i=0; i<a.size()-1; i++){
                if (a.get(i).value>a.get(i+1).value){
                    Node n=a.get(i);
                    a.set(i,a.get(i+1));
                    a.set(i+1,n);
                    i--;
                    if (i!=-1){
                        i--;
                    }
                }else{
                   // if (i==a.size()-2){
                    //    going=false;
                   // }
                }
            }
        //}
    }
}
