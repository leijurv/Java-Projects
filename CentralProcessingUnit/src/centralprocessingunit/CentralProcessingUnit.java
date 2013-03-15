/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package centralprocessingunit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author leif
 */
public class CentralProcessingUnit {
    byte[] ram;
    public CentralProcessingUnit(){
        ram=new byte[1048576];
        freeChain.add(new FreeMem(heap,ram.length-heap));
    }
    int heap=0;
    ArrayList<FreeMem> freeChain=new ArrayList<FreeMem>(1);
    public int alloc(int size){
        int p=get(size);
        int pos=freeChain.get(p).start;
        freeChain.get(p).start+=size;
        if (freeChain.get(p).end==freeChain.get(p).start){
            freeChain.remove(p);
        }
        return pos;
    }
    public void free(int start, int end){
        FreeMem f=new FreeMem(start,end);
        if (freeChain.size()==0){
            freeChain.add(f);
            return;
        }
        if (freeChain.size()==1){
            freeChain.add(start<freeChain.get(0).start?0:1,f);
            return;
        }
        if (freeChain.get(0).start>start){
            freeChain.add(0,f);
            return;
        }
        for (int i=1; i<freeChain.size(); i++){
            if (freeChain.get(i).start>start && freeChain.get(i-1).start<start){
                freeChain.add(i,f);
                return;
            }
        }
    }
    private int get(int size){
        for (int i=0; i<freeChain.size(); i++){
            if (freeChain.get(i).end-freeChain.get(i).start>=size){
                return i;
            }
        }
        return -1;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CentralProcessingUnit cpu=new CentralProcessingUnit();
        System.out.println(cpu.alloc(2));
        System.out.println(cpu.alloc(2));
        cpu.free(0,2);
        System.out.println(cpu.alloc(3));
        
        System.out.println(cpu.freeChain);
    }
}
