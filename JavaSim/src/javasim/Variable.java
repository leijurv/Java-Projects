package javasim;
/**
 * A variable
 * 
 * @author leif
 */
public class Variable {
    /**
     * Whether the variable is static
     */
    boolean Static=false;
    /**
     * The name of the variable
     */
    String name="";
    /**
     * The content of the variable, as a string.
     */
    String string_content="";
    /**
     * The datatype.
     */
    String type="";
    /**
     * The array allowed datatypes.  (This shouldn't be final)
     */
    static final String[] datatypes={"int","String"};
    public Variable(String Name, String Content, String Type, boolean STatic){
        string_content=Content;
        if (Type.equals("String") && Content.startsWith(JavaSim.quot) && Content.endsWith(JavaSim.quot)){
            string_content=Content.substring(1,Content.length()-1);
        }
        name=Name;
        Static=STatic;
        type=Type;
        if (!validName(name) || !validDatatype(type)){
            //ERROR
        }
    }
    /**
     * Checks if a String is a valid datatype
     * @param Datatype The datatype to be checked.
     * @return A boolean true if it is a valid datatype
     */
    public static boolean validDatatype(String Datatype){
        for (int i=0; i<datatypes.length; i++){
            if (datatypes[i].equals(Datatype)){
                return true;
            }
        }
        return false;
    }
    /**
     * Checks if a String is a valid name
     * @param Name The name to be checked
     * @return A boolean true if it is a valid name.
     */
    public static boolean validName(String Name){
        //Valid
        for (int i=0; i<Name.length(); i++){
            if (!valid(Name.substring(i,i+1))){
                return false;
            }
        }
        return true;
    }
    protected static boolean valid(String a){
        String[] chars={"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
        for (int i=0; i<chars.length; i++){
            if (chars[i].equals(a)){
                return true;
            }
        }
        return false;
    }
}
