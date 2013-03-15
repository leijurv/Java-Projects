/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filesystem;

import java.util.*;

/**
 *
 * @author leif
 */
public class Directory {

    String name;
    Directory parent;
    List<Directory> contents = new ArrayList<Directory>();
    List<File> files = new ArrayList<File>();
    boolean root = false;

    protected Directory(Directory Parent, String Name) {
        name = Name;
        parent = Parent;
    }

    public Directory() {
        root = true;
        parent = null;
        name = "/";
    }

    public String path() {
        if (root) {
            return "/";
        }
        return parent.path() + name + "/";
    }

    public void addFolder(String Name) {
        Directory New = new Directory(this, Name);
        contents.add(New);
    }

    public void addFile(String name) {
        File f = new File(name);
        files.add(f);
        f.parent = this;
    }

    public void update() {
        for (int i = 0; i < contents.size(); i++) {
            if (contents.get(i) == null) {
                contents.remove(i);
                if (i == contents.size()) {
                    break;
                }
            }
            if (contents.get(i).parent == null) {
                contents.remove(i);
            } else {
                if (!contents.get(i).parent.equals(this)) {
                    Directory d = contents.get(i);
                    if (!d.parent.contents.contains(d)) {
                        d.parent.contents.add(d);
                    }
                    contents.remove(i);
                }
                
            }
        }
        for (int i = 0; i < files.size(); i++) {
            while (files.get(i) == null) {
                files.remove(i);
                if (i == files.size()) {
                    break;
                }
            }
            if (files.get(i).parent == null) {
                files.remove(i);
            } else {
                if (!files.get(i).parent.equals(this)) {
                    files.get(i).update();
                    files.remove(i);
                }
            }
        }
    }

    public void updateAll() {
        update();
        for (Directory d : contents) {

            d.updateAll();
        }
        for (File f : files) {
            //f.update();
        }

    }

    public Directory getFromPath(String Path, boolean output) {
        
        if (Path.startsWith("/")) {
            if (root) {
                if (Path.equals("/")) {
                    return this;
                }
                return getFromPath(Path.substring(1, Path.length()), output);
            } else {
                return parent.getFromPath(Path, output);
            }
        }
        String path = Path.split("/")[0];
        
        
        if (Path.indexOf("/") == -1 || Path.indexOf("/") == Path.length() - 1) {
            if (path.equals("..")) {
                return parent;
            }
            if (path.equals(".")){
                return this;
            }
            for (Directory d : contents) {
                if (d.name.equals(path)) {
                    return d;
                }
            }
            for (File d : files) {
                if (d.name.equals(path)) {
                    if (output) {
                        System.out.println("Not a directory");
                    }
                    return null;
                }
            }
            if (output) {
                System.out.println("No such thing");
            }
            return null;
        }
        String next = Path.substring(Path.indexOf("/") + 1, Path.length());
        if (path.equals(".")){
            return getFromPath(next,output);
        }
        if (path.equals("..")){
            return parent.getFromPath(next,output);
        }
        for (Directory d : contents) {
            if (d.name.equals(path)) {
                return d.getFromPath(next, output);
            }

        }
        for (File d : files) {
            if (d.name.equals(path)) {
                if (output) {
                    System.out.println("Not a directory");
                }
                return null;
            }
        }
        if (output) {
            System.out.println("No such thing");
        }
        return null;
    }

    public File getFileFromPath(String Path, boolean output) {
        if (Path.length() == 0) {
            if (output) {
                System.out.println("Not a file");
            }
        }
        if (Path.startsWith("/")) {
            if (root) {
                return getFileFromPath(Path.substring(1, Path.length()), output);
            } else {
                return parent.getFileFromPath(Path, output);
            }
        }
        String path = Path.split("/")[0];

        if (Path.indexOf("/") == -1 || Path.indexOf("/") == Path.length() - 1) {
            if (path.equals("..")) {
                if (output) {
                    System.out.println("Not a file");
                }
                return null;
            }
            for (File d : files) {
                if (d.name.equals(path)) {
                    return d;
                }
            }
            for (Directory d : contents) {
                if (d.name.equals(path)) {
                    if (output) {
                        System.out.println("Not a file");
                    }
                    return null;
                }
            }
            if (output) {
                System.out.println("No such thing");
            }
            return null;
        }
        String next = Path.substring(Path.indexOf("/") + 1, Path.length());
        System.out.println(path+":"+next);
        if (path.equals("..")) {
            return parent.getFileFromPath(next, output);
        }
        if (path.equals(".")){
            return getFileFromPath(next, output);
        }
        for (Directory d : contents) {
            if (d.name.equals(path)) {
                return d.getFileFromPath(next, output);
            }

        }
        for (File f : files) {
            if (f.name.equals(path)) {
                if (output) {
                    System.out.println("Not a directory");
                }
                return null;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return path() + ":" + name;
    }
}
