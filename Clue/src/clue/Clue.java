/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clue;

/**
 *
 * @author leif
 */
public class Clue {

    /**
     * @param args the command line arguments
     */
    static final int possibilities=24;
    static int[] Khas=new int[0];
    static int[] KNhas=new int[0];
    static int[] Ehas=new int[0];
    static int[] ENhas=new int[0];
    static int[] Shas=new int[0];
    static int[] SNhas=new int[0];
    static int[][] SmHas=new int[0][0];
    static int[][] KmHas=new int[0][0];
    static int[][] EmHas=new int[0][0];
    static final int[] meHas={};//Remember to change this!!!
    static int[] NoneHave=new int[0];
    static int[] have=new int[0];
    public static void main(String[] args) {
        
        Everything();
        Everything();
        Everything();
        Everything();
        Everything();
        print();
    }
    public static void Archive1(){
        parse("S:2,8,14");
        parse("K-5");
        parse("E+9");
        parse("K-9");
        parse("K-18");
        parse("S-5");
        parse("S-9");
        parse("S-18");
        parse("S:4,9,14");
        parse("K+14");
        parse("S-12");
    }
    public static void Archive2(){
        //Leif suspect 4,8,21
        parse("K+4");
        //Karla suspect 3,9,14
        parse("S:3,9,14");
        //Steve suspect 2,9,13
        parse("E-2,9,13");
        //I show Dad 9
        //Erika suspect 1,10,16
        parse("K-1,10,16");
        parse("S:1,10,16");
        //Leif suspect 5,8,21
        parse("K-5,8,21");
        parse("S-5,8,21");
        parse("E+8");
        //Karla suspect 6,7,16
        parse("S:6,7,16");
        //Steve suspect 4,12,21
        parse("E-4,12,21");
        //I show dad 21
        //Erika suspect 3,7,21
        //I show erika 21
        //Mom suspect 3,7,16
        parse("S:3,7,16");
        //Dad suspect 5,12,16
        parse("E:5,12,16");
        //Erika suspect 
        //Leif Suspect 5,7,13
        //parse("K-5,7,13");
        //parse("S-5,7,13");
        //parse("E-5,7,13");
    }
    public static void Archive3(){
        parse("S:6,11,23");
        parse("E:2,12,17");
        parse("S-5,15,24");
        parse("K:5,15,24");//Something went wrong here. I think it was 16 instead of 15.
        parse("K+1");
        parse("E:3,8,24");
        parse("S:3,14,17");
        parse("K:1,10,17");
        parse("E:3,11,24");
        parse("K+23");
        parse("E:2,15,16");
        parse("E+24");//CHEATING
        parse("K-6,12,19");
        parse("E+19");
        parse("E:2,10,22");
        parse("S:6,13,23");
        parse("S-2,13,21");
        parse("K+14");
        parse("S-3,14,19");
        parse("K-3,9,18");
        parse("E-3,9,18");
        parse("S+9");
        parse("E-1,15,20");
        parse("S-1,15,20");
        parse("S-3,13,18");
        parse("K:3,13,18");
        parse("S:3,15,23");
        parse("K-5,7,19");
    }
    public static void Everything(){
        InferHave();
        InferNotHave();
        calcHave();
        calcNoneHave();
    }
    public static void InferNotHave(){
        for (int i=0; i<meHas.length; i++){
            if (!contains(KNhas,meHas[i])){
                KNhas=add(meHas[i],KNhas);
            }
            if (!contains(ENhas,meHas[i])){
                ENhas=add(meHas[i],ENhas);
            }
            if (!contains(SNhas,meHas[i])){
                SNhas=add(meHas[i],SNhas);
            }
        } 
        for (int i=0; i<Khas.length; i++){
            if (!contains(ENhas,Khas[i])){
                ENhas=add(Khas[i],ENhas);
            }
            if (!contains(SNhas,Khas[i])){
                SNhas=add(Khas[i],SNhas);
            }
        }
        for (int i=0; i<Shas.length; i++){
            if (!contains(Shas,Shas[i])){
                ENhas=add(meHas[i],ENhas);
            }
            if (!contains(KNhas,Shas[i])){
                KNhas=add(Shas[i],KNhas);
            }
        }
        for (int i=0; i<Ehas.length; i++){
            if (!contains(KNhas,Ehas[i])){
                KNhas=add(Ehas[i],KNhas);
            }
            if (!contains(SNhas,Ehas[i])){
                SNhas=add(Ehas[i],SNhas);
            }
        }
    }
    
    public static void print(){
        System.out.println("√ in X column means someone has that, O in X column means no-one has that.");
        System.out.println("√ in any other column means that that person has that, O means they don't");
        System.out.println("Blank means not known");
        System.out.println("     X    M    K    S    E");
        char Has='√';
        char NoHas='O';
        for (int i=1; i<=possibilities; i++){
            System.out.print(i);
            System.out.print("   ");
            if (i<10){
                System.out.print(" ");
            }
            if (contains(have,i)){
                System.out.print(Has);
            }else{
                if (contains(NoneHave,i)){
                    System.out.print(NoHas);
                }else{
                    System.out.print(" ");
                }
            }
            System.out.print("    ");
            if (contains(meHas,i)){
                System.out.print(Has);
            }else{
                if(!contains(meHas,i)){
                    System.out.print(NoHas);
                }else{
                    System.out.print(" ");
                }
                
            }
            System.out.print("    ");
            if (contains(Khas,i)){
                System.out.print(Has);
            }else{
                if(contains(KNhas,i)){
                    System.out.print(NoHas);
                }else{
                    System.out.print(" ");
                }
                
            }
            System.out.print("    ");
            if (contains(Shas,i)){
                System.out.print(Has);
            }else{
                if(contains(SNhas,i)){
                    System.out.print(NoHas);
                }else{
                    System.out.print(" ");
                }
                
            }
            System.out.print("    ");
            if (contains(Ehas,i)){
                System.out.print(Has);
            }else{
                if(contains(ENhas,i)){
                    System.out.print(NoHas);
                }else{
                    System.out.print(" ");
                }
                
            }
            System.out.println();
        }
        for (int i=0; i<EmHas.length; i++){
            System.out.print("E:");
            for (int n=0; n<EmHas[i].length; n++){
                System.out.print(EmHas[i][n]+" ");
            }
            System.out.println();
        }
        for (int i=0; i<SmHas.length; i++){
            System.out.print("S:");
            for (int n=0; n<SmHas[i].length; n++){
                System.out.print(SmHas[i][n]+" ");
            }
            System.out.println();
        }
        for (int i=0; i<KmHas.length; i++){
            System.out.print("K:");
            for (int n=0; n<KmHas[i].length; n++){
                System.out.print(KmHas[i][n]+" ");
            }
            System.out.println();
        }
    }
    public static void calcHave(){
        have=meHas;
        for (int i=0; i<Shas.length; i++){
            have=add(Shas[i],have);
        }
        for (int i=0; i<Khas.length; i++){
            have=add(Khas[i],have);
        }
        for (int i=0; i<Ehas.length; i++){
            have=add(Ehas[i],have);
        }
    }
    public static void calcNoneHave(){
        for (int i=1; i<=possibilities; i++){
            if (contains(SNhas,i) && contains(ENhas,i) && contains(KNhas,i) && !contains(meHas,i)){
                NoneHave=add(i,NoneHave);
            }
        }
    }
    public static void InferHave(){
        InferHaveE();
        InferHaveE();
        InferHaveE();
        InferHaveS();
        InferHaveS();
        InferHaveS();
        InferHaveK();
        InferHaveK();
        InferHaveK();
        for (int i=0; i<EmHas.length; i++){
            if (EmHas[i].length==1){
                if (!contains(Ehas,EmHas[i][0])){
                    Ehas=add(EmHas[i][0],Ehas);
                }
                
            }
        }
        for (int i=0; i<SmHas.length; i++){
            if (SmHas[i].length==1){
                if (!contains(Shas,SmHas[i][0])){
                    Shas=add(SmHas[i][0],Shas);
                }
                
            }
        }
        for (int i=0; i<KmHas.length; i++){
            if (KmHas[i].length==1){
                if (!contains(Khas,KmHas[i][0])){
                    Khas=add(KmHas[i][0],Khas);
                }
                
            }
        }
    }
    public static void InferHaveE(){
        for (int i=0; i<ENhas.length; i++){
            for (int n=0; n<EmHas.length; n++){
                if (contains(EmHas[n],ENhas[i])){
                    int[] r=remove(EmHas[n],ENhas[i]);
                    EmHas[n]=r;
                }
            }
        }
    }
    public static void InferHaveS(){
        for (int i=0; i<SNhas.length; i++){
            for (int n=0; n<SmHas.length; n++){
                if (contains(SmHas[n],SNhas[i])){
                    int[] r=remove(SmHas[n],SNhas[i]);
                    SmHas[n]=r;
                }
            }
        }
    }
    public static void InferHaveK(){
        for (int i=0; i<KNhas.length; i++){
            for (int n=0; n<KmHas.length; n++){
                if (contains(KmHas[n],KNhas[i])){
                    int[] r=remove(KmHas[n],KNhas[i]);
                    KmHas[n]=r;
                }
            }
        }
    }
    public static int[] remove(int[] a, int b){
        int[] result=new int[0];
        for (int i=0; i<a.length; i++){
            if (a[i]!=b){
                result=add(a[i],result);
            }
        }
        return result;
    }
    public static boolean contains(int[] a, int b){
        for (int i=0; i<a.length; i++){
            if (a[i]==b){
                return true;
            }
        }
        return false;
    }
    public static void parse(String a){
        char person=a.charAt(0);
        if (a.contains(":")){
            int[] t={Integer.parseInt(a.split(":")[1].split(",")[0]),Integer.parseInt(a.split(":")[1].split(",")[1]),Integer.parseInt(a.split(":")[1].split(",")[2])};
            switch(person){
                case 'E':
                    EmHas=addhas(t,EmHas);
                break;
                case 'S':
                    SmHas=addhas(t,SmHas);
                break;
                case 'K':
                    KmHas=addhas(t,KmHas);
                break;
            }
        }
        if (a.contains("+")){
            switch(person){
                case 'E':
                    Ehas=add(Integer.parseInt(a.substring(2,a.length())),Ehas);
                break;
                case 'S':
                    Shas=add(Integer.parseInt(a.substring(2,a.length())),Shas);
                break;
                case 'K':
                    Khas=add(Integer.parseInt(a.substring(2,a.length())),Khas);
                break;
            }
        }
        if (a.contains("-")){
            int[] t={Integer.parseInt(a.split("-")[1].split(",")[0]),Integer.parseInt(a.split("-")[1].split(",")[1]),Integer.parseInt(a.split("-")[1].split(",")[2])};
            switch(person){
                case 'E':
                    ENhas=add(t[0],ENhas);
                    ENhas=add(t[1],ENhas);
                    ENhas=add(t[2],ENhas);
                break;
                case 'S':
                    SNhas=add(t[0],SNhas);
                    SNhas=add(t[1],SNhas);
                    SNhas=add(t[2],SNhas);
                break;
                case 'K':
                    KNhas=add(t[0],KNhas);
                    KNhas=add(t[1],KNhas);
                    KNhas=add(t[2],KNhas);
                break;
            }
        }
    }
    public static int[][] addhas(int[] a, int[][] b){
        int[][] res=new int[b.length+1][0];
        System.arraycopy(b, 0, res, 0, b.length);
        res[b.length]=a;
        return res;
    }
    public static int[] add(int a, int[] b){
        int[] res=new int[b.length+1];
        System.arraycopy(b, 0, res, 0, b.length);
        res[b.length]=a;
        return res;
    }
}