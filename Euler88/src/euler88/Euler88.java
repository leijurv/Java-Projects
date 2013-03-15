/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package euler88;
import java.util.*;
/**
 *
 * @author leif
 */
public class Euler88 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final int SIZE = 12000;
final int RANGE = 24000; //P.S. RANGE can be smaller, down to 12,200

int[] k = new int[SIZE + 1];
List<Set<Integer>> nums = new ArrayList<Set<Integer>>(RANGE + 1);
for (int i = 0; i <= RANGE; i++) nums.add(new HashSet<Integer>());

//dynamically calculate all the k's
for (int i = 2; i <= RANGE / 2; i++) {
	nums.get(i).add(-i + 1);

	for (int num : nums.get(i)) {
		int current = i + i;
		int new_num = num - 1;

		for (int j = 2; j <= i && current <= RANGE; j++) {
			nums.get(current).add(new_num);
			int pk = current + new_num;
			if (pk <= SIZE && (current < k[pk] || k[pk] == 0)) k[pk] = current;
			new_num--;
			current += i;
		}
	}
}

//show the answer
boolean success = true;
for (int i = 2; i < k.length; i++) if (k[i] == 0) success = false;
if (success) {
        ArrayList<Integer> N=new ArrayList<Integer>();
        for (int i : k){
            if (!N.contains(i)){
                N.add(i);
            }
        }
        int[] aa=new int[N.size()];
        int ii=0;
        for (Object o : N.toArray()){
            aa[ii]=(Integer)o;
            ii++;
        }
        k=aa;
	int sum = 0;
	for (int i : k) sum += i;
	System.out.println(sum);
}
    }
}
