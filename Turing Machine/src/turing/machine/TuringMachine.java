/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package turing.machine;

/**
 *
 * @author leijurv
 */
public class TuringMachine {

    /**
     * @param args the command line arguments
     */
    String finalState;
    String[] states;
    int state=0;
    String blankSymbol="0";
    String[] tape={blankSymbol};
    String[] symbols;
    int tapePosition=0;
    String[][] tuples;
    public TuringMachine(String[] Symbols, int BlankSymbolIndex, String[] States, int StartingStateIndex, int EndingStateIndex, String[] Tape, String[][] Five_Tuples){
        symbols=Symbols;
        blankSymbol=symbols[BlankSymbolIndex];
        states=States;
        state=StartingStateIndex;
        finalState=states[EndingStateIndex];
        tape=Tape;
        if (tape.length==0){
            tape=new String[1];
            tape[0]=blankSymbol;
        }
        tuples=Five_Tuples;
    }
    public void print(){
        System.out.println(states[state]);
        System.out.println(tapePosition);
        for (int i=0; i<tape.length-1; i++){
            System.out.print(tape[i]);
            System.out.print(" ");
        }
        System.out.println(tape[tape.length-1]);
    }
    public boolean tupleMatchesCurrentState(String[] tuple){
        if (!tape[tapePosition].equals(tuple[1])){
            return false;
        }
        if (!states[state].equals(tuple[0])){
            return false;
        }
        return true;
    }
    public void Do(){
        while(!states[state].equals(finalState)){
            doNext();
            print();
            System.out.println();
        }
    }
    public void doNext(){
        for (int i=0; i<tuples.length; i++){
            if (tupleMatchesCurrentState(tuples[i])){
                tape[tapePosition]=tuples[i][2];
                for (int n=0; n<states.length; n++){
                    if (states[n].equals(tuples[i][4])){
                        state=n;
                    }
                }
                if (tuples[i][3].equals("R")){
                    if (tapePosition==tape.length-1){
                        String[] q=new String[tape.length+1];
                        System.arraycopy(tape, 0, q, 0, q.length-1);
                        q[q.length-1]=blankSymbol;
                        tape=q;
                    }
                    tapePosition++;
                }
                if (tuples[i][3].equals("L")){
                    if (tapePosition==0){
                        String[] q=new String[tape.length+1];
                        System.arraycopy(tape, 0, q, 1, tape.length);
                        q[0]=blankSymbol;
                        tape=q;
                    }else{
                        tapePosition--;
                    }
                }
                return;
            }
        }
    }
    public TuringMachine(){}
    public static void main(String[] args) {
        String[][] Tuples0={{"A","0","1","R","B"},{"A","1","1","L","C"},{"B","0","1","L","A"},{"B","1","1","R","B"},{"C","0","1","L","B"},{"C","1","1","R","HALT"}};
        String[] States0={"A","B","C","HALT"};
        String[] Symbols0={"0","1"};
        String[] tape0={"1"};
        TuringMachine busyBeaverOne=new TuringMachine(Symbols0,0,States0,0,3,tape0,Tuples0);
        busyBeaverOne.Do();
    }
}
