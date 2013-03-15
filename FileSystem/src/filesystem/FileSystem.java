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
public class FileSystem {

    /**
     * @param args the command line arguments
     */
    Directory root = new Directory();
    String path = "/";
    Directory current = root;

    public FileSystem() {
    }

    public void cd(String c) {
        Directory d = current.getFromPath(c,true);
        if (d != null) {
            current = d;
        }
        path = current.path();
    }

    public void mkdir(String name) {
        current.addFolder(name);
    }

    public void mkfile(String name) {
        current.addFile(name);
    }

    public void mv(String name, String place) {
        Directory D=current.getFromPath(name,false);
        File F=current.getFileFromPath(name,false);
        boolean di=true;
        if (D==null){
            if (F==null){
                System.out.println("doesn't exist");
                return;
            }else{
                di=false;
            }
        }
        if (di) {
            D.parent = current.getFromPath(place,true);
        } else {
            F.parent = current.getFromPath(place,true);
        }
        root.updateAll();
    }

    public void ls() {
        for (Directory d : current.contents) {
            System.out.print(d.name + " ");
        }
        for (File d : current.files) {
            System.out.print(d.name + " ");
        }
        System.out.println();
    }
    public void rm(String file){
        File f=current.getFileFromPath(file,true);
        f.parent=null;
        root.updateAll();
    }
    public static void parse(FileSystem f, String command) {
        String x = command;
        String n = x.split(" ")[0];
        if (n.equals("cd")) {
            f.cd(x.split(" ")[1]);
            return;
        }
        if (n.equals("ls")) {
            f.ls();
            return;
        }
        if (n.equals("mkdir")) {
            f.mkdir(x.split(" ")[1]);
            return;
        }
        if (n.equals("mkfile")) {
            f.mkfile(x.split(" ")[1]);
            return;
        }
        if (n.equals("mv")) {
            f.mv(x.split(" ")[1], x.split(" ")[2]);
            return;
        }
        if (n.equals("rm")) {
            f.rm(x.split(" ")[1]);
            return;
        }
        System.out.println("Command not found");
        
    }

    public static void main(String[] args) {
        FileSystem f = new FileSystem();
        Scanner scan = new Scanner(System.in);
        boolean running=true;
        while (running) {
            System.out.print(f.path + "  " + ">");
            String x = scan.nextLine();
            if (x.equals("exit")) {
                running=false;
            }
            parse(f,x);

        }
    }
}
