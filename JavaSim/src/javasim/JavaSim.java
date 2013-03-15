package javasim;
/**
 * The main class
 * 
 * @author leif
 */
public class JavaSim {

    /**
     * A quotation mark, " 
     */
    static final String quot=Character.toString('"');
    /**
     * The classes. Maybe put this as a static field in Class?
     */
    static Class[] classes=new Class[1];
    public static void main(String[] args) {
        String[] ProjectEuler1={"int a=0","public static void aaa(){","a=a+1","}","public static void main(String[] args){","int aa=0","while(a<1000){","if(a%5==0||a%3==0){","aa=aa+a","}","aaa()","}","System.out.println(aa)","}"};
        String[] d=ProjectEuler1;
        ClassParser cp=new ClassParser(d,"ProjectEulerOne",0);
        cp.parse();
        classes[0]=new Class(cp);
        classes[0].runFunction("main");
        System.out.println(classes[0].vars[0].name);
        System.out.println(classes[0].functions[1].vars[0].name);
    }
    /*
    static int a=0;
    public static void aaa(){
        a=a+1;
    }
    public static void main(String[] args){
        mai(args);
        int aa=0;
        while(a<1000){
            if (a%5==0 || a%3==0){
                aa=aa+a;
            }
            aaa();
        }
        System.out.println(aa);
    }*/
}
