/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package centralprocessingunit;

/**
 *
 * @author leif
 */
public class FreeMem {
    int start;
    int end;
    public FreeMem(int Start, int End){
        start=Start;
        end=End;
    }
    public String toString(){
        return "("+start+","+end+")";
    }
}
