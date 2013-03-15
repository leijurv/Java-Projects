/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filesystem;

/**
 *
 * @author leif
 */
public class File {

    String name;
    Directory parent;

    public String path() {
        return parent.path() + name;
    }

    public File(String Name) {
        name = Name;
    }
    
    public void update(){
        if (!parent.files.contains(this)){
            parent.files.add(this);
        }
        
    }
}
