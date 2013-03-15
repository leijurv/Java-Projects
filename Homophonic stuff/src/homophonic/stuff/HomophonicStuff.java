/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package homophonic.stuff;

/**
 *
 * @author leijurv
 */
public class HomophonicStuff {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String jack="I DVMUF LFEEF SOQ YQV SQTUI WFXFM YQVFJXF EFQUQ JFPTUF M XISSFLQTUQ M RPQEUM UMTUI YFSSFIXM KFJFXFM LQ TIEUVF EQTEF SOQ LQX VFWMTQTUQ TIT KIJXFMUQ TQJMV XQEYQVFQTHM LFVQUVI MX EIX LQX WITLI EQTHG JQTUQ SITEFLQVGUQ XG KIEUVG EQWQTHG DGUF TIT DIEUQ G KFKQV SIWQ AVPUF WG YQV EQJPFV KFVUPUQ QSGTIESQTHG XF WFQF SIWYGJTF DQSFI EF GJPUF SIT RPQEUG IVGHFIT YFSSFIXC CX SCWWFTI SOQ C YQTC YIESFC XF CKVQF VFUQTPUF Q KIXUC TIEUVC YIYYC TQX WCUUFTI LQF VQWF DCSQWWI CXF CX DIXXQ KIXI EQWYVQ CSRPFEUCTLI LCX XCUI WCTSFTI UPUUQ XQ EUQXXQ JFC LQ XCXUVI YIXI KQLQC XCTIUUQ Q X TIEUVI UCTUI ACEEI SOQ TIT EPVJQC DPIV LQXWCVFT EPIXI SFTRPQ KIXUQ VCSSQEI Q UCTUI SCEEI XI XPWQ QVZ LF EIUUI LZ XZ XPTZ YIF SOQ TUVZUF QVZKZW TQ XZXUI YZEEIRPZTLI TZYYZVKQ PTZ WITUZJTZ AVPTZ YQV XZ LFEUZTHZ Q YZVKQWF ZXUZ UZTUI RPZTUI KQLPUZ TIT ZKQZ ZXSPTZ TIF SF ZXXQJVNWWI Q UIEUI UIVTI FT YFNTUI SOQ LQ XN TIKN UQVVN PT UPVAI TNSRPQ Q YQVSIEEQ LQX XQJTI FX YVFWI SNTUI UVQ KIXUQ FX DQ JFVBV SIT UPUUQ XBSRPQ B XB RPBVUB XQKBV XB YIYYB FT EPEI QXB YVIVB FVQ FT JFP SIWBXUVPF YFBSRPQFTDFT SOQ X WBV DP EIYVB TIF VFSOFPEI  XB YBVIXB FT SILFSQ Q QRPBUIV";
        String r="";
        String[] es={"M","G","C","Z","N","B"};
        String[] singleletters={"I"};
        for (int i=0; i<jack.length(); i++){
            System.out.println(i);
            if (jack.substring(i,i+1).equals(" ")){
                r=r+" ";
            }else{
                if (i>0&&i!=jack.length()-2){
                    if (jack.substring(i-1,i).equals(" ") && jack.substring(i+1,i+2).equals(" ")){
                        boolean alr=false;
                        for (int n=0; n<singleletters.length; n++){
                            if (singleletters[n].equals(jack.substring(i,i+1))){
                                alr=true;
                            }
                        }
                        String[] t=new String[singleletters.length+1];
                        for (int n=0; n<singleletters.length; n++){
                            t[n]=singleletters[n];
                        }
                        for (int n=0; n<es.length; n++){
                            if (es[n].equals(jack.substring(i,i+1))){
                                alr=true;
                            }
                        }
                        t[singleletters.length]=jack.substring(i,i+1);
                        if (!alr){
                            singleletters=t;
                        }
                    }
                }
                boolean k=false;
                for (int n=0; n<es.length; n++){
                    if (es[n].equals(jack.substring(i,i+1))){
                        r=r+"I";
                        k=true;
                    }
                }
                
                if (jack.substring(i,i+1).equals("Q")){//This replaces K in ciphertext with O. 
                    r=r+"E";
                    k=true;
                }
                if (jack.substring(i,i+1).equals("R")){//This replaces K in ciphertext with O. 
                    r=r+"Q";
                    k=true;
                }if (jack.substring(i,i+1).equals("P")){//This replaces K in ciphertext with O. 
                    r=r+"U";
                    k=true;
                }if (jack.substring(i,i+1).equals("M")){//This replaces K in ciphertext with O. 
                    r=r+"A";
                    k=true;
                }if (jack.substring(i,i+1).equals("U")){//This replaces K in ciphertext with O. 
                    r=r+"T";
                    k=true;
                }if (jack.substring(i,i+1).equals("I")){//This replaces K in ciphertext with O. 
                    r=r+"O";
                    k=true;
                }if (jack.substring(i,i+1).equals("V")){//This replaces K in ciphertext with O. 
                    r=r+"R";
                    k=true;
                }
                if (jack.substring(i,i+1).equals("E")){//This replaces K in ciphertext with O. 
                    //r=r+"S";
                    //k=true;
                }
                if (!k){
                    r=r+"-";
                }
            }
        }
        System.out.println(r);
        String[] a=r.split(" ");
        for (int i=0; i<a.length; i++){
            if (a[i].indexOf("-")==-1){
                System.out.println(a[i]);
            }}
        for (int i=0; i<singleletters.length; i++){
            System.out.println(singleletters[i]);
        }
    }
}
