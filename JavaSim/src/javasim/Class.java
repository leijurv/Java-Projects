package javasim;
/**
 * A simulated class
 * 
 * @author leif
 */
public class Class {
    /**
     * The global variables in this class.
     */
    Variable[] vars=new Variable[0];
    /**
     * The functions in the class. Static and non-static.
     */
    Function[] functions=new Function[0];
    public Class(){
        
    }
    public Class(ClassParser cp){
        if (cp.parsed){
            cp.parse();
        }
        functions=cp.result.functions;
        vars=cp.result.vars;
    }
    /**
     * Calls a function of a given name.
     * 
     * Calls a function of a given name. If multiple functions have that name, it runs all of them. If no function exists with that name, it does nothing.
     * @param functionName 
     */
    public void runFunction(String functionName){
        for (int i=0; i<functions.length; i++){
            if (functions[i].Name.equals(functionName)){
                functions[i].run();
            }
        }
    }
    /**
     * Whether a function exists in this class.
     * @param functionName The function name to be checked
     * @return Whether the function exists
     */
    public boolean FunctionExists(String functionName){
        for (int i=0; i<functions.length; i++){
            if (functions[i].Name.equals(functionName)){
                return true;
            }
        }
        return false;
    }
    /**
     * Adds a function to the list.
     * 
     * @param f The function to be added. 
     */
    public void addFunction(Function f){
        Function[] ne=new Function[functions.length+1];
        System.arraycopy(functions, 0, ne, 0, functions.length);
        ne[ne.length-1]=f;
        functions=ne;
    }
    /**
     * Gives the index in the array of the variable with the given name.
     * 
     * Searches through the class variables, and when it finds one, it returns its index in the array.
     * @param name The name to be searched
     * @return The index of the variable (-1 if it doesn't exist)
     */
    public int varIndex(String name){
        for (int i=0; i<vars.length; i++){
            if (vars[i] != null){
                if (vars[i].name.equals(name)){
                return i;
            }
            }
            
        }
        return -1;
    }
        
}
