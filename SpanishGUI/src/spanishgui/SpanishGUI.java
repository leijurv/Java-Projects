package spanishgui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class SpanishGUI extends JPanel implements KeyListener,MouseListener{
    static boolean titlepage=true;
     public static class Verb{
        String infinitive_form="";
        String conjugated_form="";
        String tense="";
        boolean regular=true;
        public Verb(String verb){
            infinitive_form=verb;
        }
        public void conjugate(String p){
            if (infinitive_form.length()<3){
                return;
            }
            if (irregular(infinitive_form)){
                conjugated_form=c_irregular(p);
                return;
            }
            if (tense.equals("future")){
                conjugated_form=c_future(p);
                return;
            }
            if (infinitive_form.substring(infinitive_form.length()-2,infinitive_form.length()).equals("ir")){
                if (tense.equals("present")){
                    conjugated_form=c_present_ir(p);
                }
                return;
            }
            if (infinitive_form.substring(infinitive_form.length()-2,infinitive_form.length()).equals("ar")){
                if (tense.equals("present")){
                    conjugated_form=c_present_ar(p);
                }
                return;
            }
            if (infinitive_form.substring(infinitive_form.length()-2,infinitive_form.length()).equals("er")){
                if (tense.equals("present")){
                    conjugated_form=c_present_er(p);
                }
               
                return;
            }
            regular=false;
        }
        public void setTense(String p){
            if (p.equalsIgnoreCase("present")){
                tense="present";
            }else{
                tense="future";
            }
        }
        @Override
        public String toString(){
            return conjugated_form;
        }
        public String c_present_ir(String p){
            if (p.equalsIgnoreCase("yo")){
                return infinitive_form.substring(0,infinitive_form.length()-2)+"o";
            }
            if (p.equalsIgnoreCase("tu")){
                return infinitive_form.substring(0,infinitive_form.length()-2)+"es";
            }
            if (p.equalsIgnoreCase("ella") || p.equalsIgnoreCase("el") || p.equalsIgnoreCase("usted")){
                return infinitive_form.substring(0,infinitive_form.length()-2)+"e";
            }
            if (p.equalsIgnoreCase("nosotros")){
                return infinitive_form.substring(0,infinitive_form.length()-2)+"imos";
            }
            if (p.equalsIgnoreCase("vosotros")){
                return infinitive_form.substring(0,infinitive_form.length()-2)+"ís";
            }
            if (p.equalsIgnoreCase("ellas") || p.equalsIgnoreCase("ellos") || p.equalsIgnoreCase("ustedes")){
                return infinitive_form.substring(0,infinitive_form.length()-2)+"en";
            }
            throw new RuntimeException("Error: " + p + " is not a valid conjugated_formjugation method");
        }
        public String c_future(String p){
            if (p.equalsIgnoreCase("yo")){
                return infinitive_form+"é";
            }
            if (p.equalsIgnoreCase("tu")){
                return infinitive_form+"ás";
            }
            if (p.equalsIgnoreCase("ella") || p.equalsIgnoreCase("el") || p.equalsIgnoreCase("usted")){
                return infinitive_form+"á";
            }
            if (p.equalsIgnoreCase("nosotros")){
                return infinitive_form+"emos";
            }
            if (p.equalsIgnoreCase("vosotros")){
                return infinitive_form+"éis";
            }
            if (p.equalsIgnoreCase("ellas") || p.equalsIgnoreCase("ellos") || p.equalsIgnoreCase("ustedes")){
                return infinitive_form+"án";
            }
            throw new RuntimeException("Error: " + p + " is not a valid conjugated_formjugation method");
        }
        public String c_present_ar(String p){
            if (p.equalsIgnoreCase("yo")){
                return infinitive_form.substring(0,infinitive_form.length()-2)+"o";
            }
            if (p.equalsIgnoreCase("tu")){
                return infinitive_form.substring(0,infinitive_form.length()-2)+"as";
            }
            if (p.equalsIgnoreCase("ella") || p.equalsIgnoreCase("el") || p.equalsIgnoreCase("usted")){
                return infinitive_form.substring(0,infinitive_form.length()-2)+"a";
            }
            if (p.equalsIgnoreCase("nosotros")){
                return infinitive_form.substring(0,infinitive_form.length()-2)+"amos";
            }
            if (p.equalsIgnoreCase("vosotros")){
                return infinitive_form.substring(0,infinitive_form.length()-2)+"áis";
            }
            if (p.equalsIgnoreCase("ellas") || p.equalsIgnoreCase("ellos") || p.equalsIgnoreCase("ustedes")){
                return infinitive_form.substring(0,infinitive_form.length()-2)+"an";
            }
            throw new RuntimeException("Error: " + p + " is not a valid conjugated_formjugation method");
        }
        public String c_present_er(String p){
            if (p.equalsIgnoreCase("yo")){
                return infinitive_form.substring(0,infinitive_form.length()-2)+"o";
            }
            if (p.equalsIgnoreCase("tu")){
                return infinitive_form.substring(0,infinitive_form.length()-2)+"es";
            }
            if (p.equalsIgnoreCase("ella") || p.equalsIgnoreCase("el") || p.equalsIgnoreCase("usted")){
                return infinitive_form.substring(0,infinitive_form.length()-2)+"e";
            }
            if (p.equalsIgnoreCase("nosotros")){
                return infinitive_form.substring(0,infinitive_form.length()-2)+"emos";
            }
            if (p.equalsIgnoreCase("vosotros")){
                return infinitive_form.substring(0,infinitive_form.length()-2)+"éis";
            }
            if (p.equalsIgnoreCase("ellas") || p.equalsIgnoreCase("ellos") || p.equalsIgnoreCase("ustedes")){
                return infinitive_form.substring(0,infinitive_form.length()-2)+"en";
            }
            throw new RuntimeException("Error: " + p + " is not a valid conjugated_formjugation method");
        }
        public boolean irregular(String verb){
            if (verb.equalsIgnoreCase("estar")){
                return true;
            }
            if (verb.equalsIgnoreCase("tener")){
                return true;
            }
            if (verb.equalsIgnoreCase("ser")){
                return true;
            }
            return false;
        }
        public String c_irregular(String p){
            if (tense.equals("present")){
                return c_irregular_present(p);
            }
            if (tense.equals("future")){
                return c_irregular_future(p);
            }
            return "";
        }
            
        public String c_irregular_present(String p){
            if (infinitive_form.equalsIgnoreCase("estar")){
                if (p.equalsIgnoreCase("yo")){
                    return "estoy";
                }
                if (p.equalsIgnoreCase("tu")){
                    return "estás";
                }
                if (p.equalsIgnoreCase("ella")){
                    return "está";
                }
                if (p.equalsIgnoreCase("ellos")){
                    return "están";
                }
                return c_present_ar(p);
            }
            if (infinitive_form.equalsIgnoreCase("tener")){
                if (p.equalsIgnoreCase("yo")){
                    return "tengo";
                }
                if (p.equalsIgnoreCase("tu")){
                    return "tienes";
                }
                if (p.equalsIgnoreCase("ella")){
                    return "tiene";
                }
                if (p.equalsIgnoreCase("ellos")){
                    return "tienen";
                }
                return c_present_er(p);
            }
            if (infinitive_form.equalsIgnoreCase("ser")){
                if (p.equalsIgnoreCase("yo")){
                    return "soy";
                }
                if (p.equalsIgnoreCase("tu")){
                    return "eres";
                }
                if (p.equalsIgnoreCase("ella")){
                    return "es";
                }
                if (p.equalsIgnoreCase("nosotros")){
                    return "somos";
                }
                if (p.equalsIgnoreCase("vosotros")){
                    return "sois";
                }
                if (p.equalsIgnoreCase("ellos")){
                    return "son";
                }
            }
            throw new RuntimeException("Internal Error: Irregular verb " + infinitive_form + " recognized as irregular verb, but conjugated is not known.");
        }
        public String c_irregular_future(String p){
            return c_future(p);
        }
    }
    String verbtext="";
    String tense="present";
    boolean alt=false;
    public SpanishGUI(){
        super();
        addKeyListener(this);
        addMouseListener(this);
    }
    @Override
    public void paintComponent(Graphics g){
        g.clearRect(0,0,640,480);
        if (titlepage){
            g.drawString("Spanish Verb conjugator", 320, 240);
            g.drawString("By Leif", 320, 255);
            return;
        }
        g.drawString("Type in the infinitive form of a Spanish verb.", 10, 10);
        g.drawString("By Lurf Jurv",10,200);
        g.drawString("© March 1, 2012",10,215);
        g.drawString("Click here to change the tense", 100, 450);
        g.drawString("Tense: " + tense, 320, 450);
        g.drawString("Verb: " + verbtext,320,10);
        Verb verb=new Verb(verbtext);
        verb.setTense(tense);
        verb.conjugate("yo");
        g.drawString("Yo: " + verb.toString(), 50, 50);
        verb.conjugate("tu");
        g.drawString("Tú: " + verb.toString(), 50, 70);
        verb.conjugate("ella");
        g.drawString("Él, Ella, Usted: "+verb.toString(), 50, 90);
        verb.conjugate("nosotros");
        g.drawString("Nosotros: " + verb.toString(), 400, 50);
        verb.conjugate("vosotros");
        g.drawString("Vosotros: " + verb.toString(), 400, 70);
        verb.conjugate("ellos");
        g.drawString("Ellas, Ellos, Ustedes: " + verb.toString(), 400, 90);
    }
    public void nextTense(){
        if (tense.equals("present")){
            tense="future";
        }else{
            tense="present";
        }
    }
    @Override
    public void keyPressed( KeyEvent key){
        if (alt){
            System.out.println(key.getKeyChar());
            if (key.getKeyChar()=='a'){
                verbtext=verbtext+"á";
            }
            if (key.getKeyChar()=='e'){
                verbtext=verbtext+"é";
            }
            if (key.getKeyChar()=='i'){
                verbtext=verbtext+"í";
            }
            if (key.getKeyChar()=='o'){
                verbtext=verbtext+"ó";
            }
            if (key.getKeyChar()=='u'){
                verbtext=verbtext+"ú";
            }
        }
        if (key.getKeyCode()==18){
            
            alt=true;
            System.out.println("alt pressed:" + alt);
        }
        
        if (key.getKeyCode()==KeyEvent.VK_BACK_SPACE){
            verbtext=verbtext.substring(0,verbtext.length()-1);
        }else{
            verbtext+=key.getKeyChar();
        }
        repaint();
    }
    @Override
    public void keyTyped(KeyEvent key){ }   
    @Override
    public void keyReleased(KeyEvent key){ 
        if (key.getKeyCode()==18){
            
            alt=false;
            System.out.println("alt released: " + alt);
        }
    }
    @Override
    public void mousePressed(MouseEvent e) { 
        
    }
    @Override
    public void mouseReleased(MouseEvent e) { }
    @Override
    public void mouseEntered(MouseEvent e) { }
    @Override
    public void mouseExited(MouseEvent e) { }
    @Override
    public void mouseClicked(MouseEvent e) {
        if (titlepage){
            titlepage=false;
        }
        if (e.getY()>400){
            nextTense();
        }
        repaint();
    }
    
    public static void main(String[] args) {
        JFrame frame=new JFrame("Spanish conjugations");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640,480);
        SpanishGUI panel=new SpanishGUI();
        panel.setFocusable(true);
        frame.setContentPane(panel);
        frame.setVisible(true);
    }
    //© Feb 6, 2012
    //Lurf Jurv
}
