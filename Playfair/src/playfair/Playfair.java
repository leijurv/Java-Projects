/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package playfair;

import java.util.Scanner;

/**
 *
 * @author leif
 */
public class Playfair {

    public static class Table{
        String[][] table=new String[5][5];
        public Table(String key){
            table=construct(key);
        }
        public static String[][] construct(String k){
            String temp=k.toUpperCase()+"ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            temp=temp.replace("J","I");
            String r="";
            for (int i=0; i<temp.length(); i++){
                boolean already=false;
                String c=temp.substring(i,i+1);
                for (int n=0; n<i; n++){
                    if (temp.substring(n,n+1).equals(c)){
                        already=true;
                        
                    }else{
                        continue;
                    }
                }
                if (!already){
                    r=r+c;
                }
            }
            String[][] result=new String[5][5];
                    
            for (int i=0; i<5; i++){
                for (int n=0; n<5; n++){
                    result[i][n]=r.substring(n+(i*5),1+n+(i*5));
                }
                
                
            }
            
            return result;
        }
        public void print(){
            for (int i=0; i<table.length; i++){
                for (int n=0; n<table[i].length; n++){
                    System.out.print(table[i][n]);
                }
                System.out.println();
            }
        }
        public String decodeDiagraph(String d){
            String a=d.substring(0,1);
            String b=d.substring(1,2);
            return rota(a,b);
        }
        public String encodeDiagraph(String d){
            String a=d.substring(0,1);
            String b=d.substring(1,2);
            return rot(a,b);
        }
        public String encode(String f){
            String d="";
            for (int i=0; i<f.length()-1; i++){
                d=d+f.substring(i,i+1);
                if (f.substring(i,i+1).equals(f.substring(i+1,i+2))){
                    d=d+"x";
                }
            }
            d=d+f.substring(f.length()-1,f.length());
            if (d.length()%2==1){
                d=d+"A";
            }
            String r="";
            for (int i=0; i<d.length(); i=i+2){
                String a=d.substring(i,i+2);
                r=r+encodeDiagraph(a);
            }
            return r;
        }
        public String decode(String d){
            String r="";
        for (int i=0; i<d.length(); i=i+2){
                String a=d.substring(i,i+2);
                r=r+decodeDiagraph(a);
            }
            return r;
        }
        public static int[] rect(int a, int b, int c, int d){
        return new int[] {a,d,c,b};
    }
        public static int[] enc(int a, int b, int c, int d){
            if (b==d){
             return new int[]   {(a+1)%5,b,(c+1)%5,d}; 
            }
            if (a==c){
                return new int[] {a,(b+1)%5,c,(d+1)%5};
            }
            return rect(a,b,c,d);
        }
        public String[] commonGroupOfFour(String a){
            String[] b=new String[a.length()/2-1];
            String[] e=new String[0];
            int[] ocr=new int[0];
            String[] cc=new String[0];
            String[] common=new String[0];
            for (int i=0; i<b.length; i++){
                String c=a.substring(i*2,(i+2)*2);
                b[i]=c;
                boolean d=false;
                for (int n=0; n<cc.length; n++){
                    if (cc[n].equals(c)){
                        ocr[n]++;
                        d=true;
                    }
                }
                if (!d){
                    cc=add(cc,c);
                    ocr=add(ocr,1);
                }
                if (ocr.length!=cc.length){
                    throw new RuntimeException("MAJOR ERROR");
                }
            }
             int[] alr=new int[0];
             
            for (int n=0; n<cc.length; n++){
               
                
                int ci=1;
                for (int i=0; i<cc.length; i++){
                    boolean w=true;
                    for (int q=0; q<alr.length; q++){
                        if (i==alr[q]){
                            w=false;
                        }
                    }
                    if (ocr[i]>=ocr[ci] && w){
                        ci=i;
                    }
                }
                alr=add(alr,ci);
                common=add(common,cc[ci]+":"+ocr[ci]+":"+m.decode(cc[ci])/*+":"+ci*/);
            }
            
            return common;
        }
        public String[] commonGroupOfTwo(String a){
            String[] b=new String[a.length()/2];
            String[] e=new String[0];
            int[] ocr=new int[0];
            String[] cc=new String[0];
            String[] common=new String[0];
            for (int i=0; i<b.length; i++){
                String c=a.substring(i*2,(i+1)*2);
                b[i]=c;
                boolean d=false;
                for (int n=0; n<cc.length; n++){
                    if (cc[n].equals(c)){
                        ocr[n]++;
                        d=true;
                    }
                }
                if (!d){
                    cc=add(cc,c);
                    ocr=add(ocr,1);
                }
                if (ocr.length!=cc.length){
                    throw new RuntimeException("MAJOR ERROR");
                }
            }
             int[] alr=new int[0];
             int lst=0;
             for (int i=0; i<ocr.length; i++){
                 if (ocr[i]<ocr[lst]){
                     lst=i;
                 }
             }
            for (int n=0; n<cc.length; n++){
               
                
                int ci=lst;
                for (int i=0; i<cc.length; i++){
                    boolean w=true;
                    for (int q=0; q<alr.length; q++){
                        if (i==alr[q]){
                            w=false;
                        }
                    }
                    if (ocr[i]>=ocr[ci] && w){
                        ci=i;
                    }
                }
                alr=add(alr,ci);
                common=add(common,cc[ci]+":"+ocr[ci]+":"+m.decode(cc[ci])/*+":"+ci*/);
            }
            
            return common;
        }
        public String[] add(String[] a, String b){
            String[] c=new String[a.length+1];
            for (int d=0; d<a.length; d++){
                c[d]=a[d];
            }
            c[a.length]=b;
            return c;
        }
        public int[] add(int[] a, int b){
            int[] c=new int[a.length+1];
            for (int d=0; d<a.length; d++){
                c[d]=a[d];
            }
            c[a.length]=b;
            return c;
        }
        public static int[] dec(int a, int b, int c, int d){
            if (b==d){
                int[] q={(a==0)?4:a-1,b,((c==0)?4:c-1),d};
             return q;
            }
            if (a==c){
                
                int[] q={a,(b==0)?4:b-1,c,((d==0)?4:d-1)};
                return q;
            }
            return rect(a,b,c,d);
        }
        public String rota(String a, String b){
            int[] c=find(a,b);
            int[] d=dec(c[0],c[1],c[2],c[3]);
            return table[d[0]][d[1]]+table[d[2]][d[3]];
        }
        public String rot(String a, String b){
            int[] c=find(a,b);
            int[] d=enc(c[0],c[1],c[2],c[3]);
            return table[d[0]][d[1]]+table[d[2]][d[3]];
        }
    public  int[] find(String a, String b){
        int[] c=new int[4];
        for (int i=0; i<5; i++){
            for (int n=0; n<5; n++){
                String d=table[i][n];
                if (d.equals(a)){
                    c[0]=i;
                    c[1]=n;
                }
                if (d.equals(b)){
                    c[2]=i;
                    c[3]=n;
                }
            }
        }
        return c;
    }
    public static int alphaIndex(String a){
        String b="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i=0; i<b.length(); i++){
            if (a.equals(b.substring(i,i+1))){
                return i;
            }
        }
        return -1;
    }
    
    }
    
    public static void ed(){
        Scanner scan=new Scanner(System.in);
        System.out.print("Playfair encoder/decoder. Key (lowercase text, no spaces)? >");
        
        Table main=new Table(scan.nextLine());
        main.print();
        System.out.print("Text? (lowercase, no spaces)>");
        String a=scan.nextLine();
        System.out.println("Encoded: "+main.encode(a));
        System.out.println("Decoded: "+main.decode(a));
    }
    public static String removeSpaces(String b){
        String c="";
        for (int i=0; i<b.length(); i++){
            if (b.charAt(i)!=' '){
                c=c+b.substring(i,i+1);
            }
        }
        return c;
    }
    static Table m=new Table("KEVINHECTORBRIANCLOUGHHHHHHHHHDMFSP");
    public static void main(String[] args) {
        for (String[] S : m.table){
            for (String SS : S){
                System.out.print(SS);
            }
            System.out.println();
        }
        m.table=new String[][] {{"K","E","V","I","N"},{"H","C","T","O","R"},{"B","A","L","U","G"},{"D","M","F","S","P"},{"Q","W","X","Y","Z"}};
        
        String y="OC OY FO LB VN PI AS AK OP VY GE SK OV MU FG UW ML NO OE DR NC FO RS OC VM TU YE RP FO LB VN PI AS AK OP VI VK YE OC NK OC CA RI CV VL TS OC OY TR FD VC VO OU EG KP VO OY VK TH ZS CV MB TW TR HP NK LR CU EG MS LN VL ZS CA NS CK OP OR MZ CK IZ US LC CV FD LV OR TH ZS CL EG UX MI FO LB IM VI VK IU AY VU UF VW VC CB OV OV PF RH CA CS FG EO LC KM OC GE UM OH UE BR LX RH EM HP BM PL TV OE DR NC FO RS GI ST HO GI LC VA IO AM VZ IR RL NI IW US GE WS RH CA UG IM FO RS KV ZM GC LB CG DR NK CV CP YU XL OK FY FO LB VC CK DO KU UH AV OC OC LC IU SY CR GU FH BE VK RO IC SV PF TU QU MK IG PE CE MG CG PG GM OQ US YE FV GF HR AL AU QO LE VK RO EO KM UQ IR XC CB CV MA OD CL AN OY NK BM VS MV CN VR OE DR NC GE SK YS YS LU UX NK GE GM ZG RS ON LC VA GE BG LB IM OR DP RO CK IN AN KV CN FO LB CE UM NK PT VK TC GE FH OK PD UL XS UE OP CL AN OY NK VK BU OY OD OR SN XL CK MG LV CV GR MN OP OY OF OC VK OC VK VW OF CL AN YE FV UA VN RP NC WM IP OR DG LO SH IM OC NM LC CV GR MN OP OY HX AI FO OU EP GC HK";
        String b="TH IS ST AG EI SN UM BE RS IX AN DI TI SA PL AY FA IR CI PH ER ST OP TH EF OL WI NG ST AG EI SN UM BE RS EV EN WI TH IN TH EC ON TE XT OF TH IS CO MP ET IT IO NA ND IT IS EN CR YP TE DA CX CO RD IN GT OA NA DF GV XT YP EC IP HE RS TO PW HE NY OU AT TE MP TX TO CR YP TA NA LY SE ST AG ES EV EN YO UW IL LS EX ET HA TI TI SM OR EC OM PL IC AT ED TH AN AS TR AI GH TF OR WA RD AD FG VX CI PH ER ST OP UN FO RT UN AT EL YI CA NX NO TG IV EY OU AN YM OR EC LU ES ST OP NE WP AR AG RA PH IN TE RM SO FT HI SX ST AG ET HE SH ib bo LE TH TH AT YO US HO UL DT AK EN OT EO FI SM OL YB DE NU MN EW PA RA GR AP HY OU WI LX LP RO BA BL YH AV EN OT IC ED BY NO WT HA TE AC HS TA GE IS IN AD IF FE RE NT CI PH ER AN DI SU SU AL LY IN AN AP PR OP RI AT EL AN GU AG ES TO PS OT HE VI GE NE RE ST AG EW AS IN FR EN CH AN DT HI SP LA YF AI RS TA GE IS IN EN GL IS HS TO PI FT HE PA TX TE RN PE RS IS TS TH EN TH EN EX TS TA GE WI LX LB EI NG ER MA NS TO PB UT DO ES TH EP AT TE RN PE RS IS TQ UE ST IO NM AR KQ";
        String a=removeSpaces(y);
        String c=removeSpaces(b);
        String ab=removeSpaces(a);
        String[] q=m.commonGroupOfFour(a);
        for (int i=0; i<q.length; i++){
            System.out.println(q[i]);
        }
        String[] w=m.commonGroupOfTwo(a);
        for (int i=0; i<w.length; i++){
            System.out.println(w[i]);
        }
        System.out.println(c);
        System.out.println(ab);
    }
    
}
