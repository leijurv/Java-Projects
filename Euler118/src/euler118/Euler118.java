/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package euler118;

/**
 *
 * @author leijurv
 */
public class Euler118 {

    public static void main(String[] args) {
            new Euler118().Bruteforce();
        }

        int[] perm = new int[] { 1, 2, 3, 4,5};
  
        public void Bruteforce() {

            int count = 0;

            do {
                count += CheckPartitions(0, 0);
            } while (nextPermuation());
                          
            System.out.println(count);
        }

        private boolean nextPermuation() {

            int N = perm.length;
            int i = N - 1;

            while (perm[i - 1] >= perm[i]) {
                i = i - 1;
                if (i == 0)
                    return false;

            }

            int j = N;
            while (perm[j - 1] <= perm[i - 1]) {
                j = j - 1;
            }

            // swap values at position i-1 and j-1
            swap(i - 1, j - 1);

            i++;
            j = N;

            while (i < j) {
                swap(i - 1, j - 1);
                i++;
                j--;
            }

            return true;
        }



        private void swap(int i, int j) {
            int k = perm[i];
            perm[i] = perm[j];
            perm[j] = k;
        }


        private int CheckPartitions(int startIndex, int prev) {
            int count = 0;
            for (int i = startIndex; i < perm.length; i++) {
                
                //form the number x of the digits startIndex -> i
                int number = 0;
                for(int j = startIndex; j <= i; j++){
                    number = number * 10 + perm[j];                    
                }
                
                //We only count ordered sets, so check that the current number is larger than the previous
                if(number < prev) continue;

                //Check that number is prime 
                if(!IsPrime(number)) continue;

                // No more digits so return
                
                if(i == (perm.length-1)) {System.out.println(prev);
                return count + 1;}
                
                count += CheckPartitions(i + 1, number);
            }

            return count;
        }

        public boolean IsPrime(int n) {
            if (n < 2)
                return false;
            if (n < 4)
                return true;
            if (n % 2 == 0)
                return false;
            if (n < 9)
                return true;
            if (n % 3 == 0)
                return false;
            if (n < 25)
                return true;

            int s = (int)Math.sqrt(n);
            for (int i = 5; i <= s; i += 6) {
                if (n % i == 0)
                    return false;
                if (n % (i + 2) == 0)
                    return false;
            }

            return true;
        }


}
