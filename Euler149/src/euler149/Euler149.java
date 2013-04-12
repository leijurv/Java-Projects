package euler149;
public class Euler149 {
    static long[][] matrix=new long[2000][2000];
    public static long get(int j){
        return matrix[j/2000][j%2000];
    }
    public static void generate(){
        for (int i=0; i<2000; i++){
            for (int j=0; j<2000; j++){
                long n=i*2000+j+1;
                if (n>55){
                    matrix[i][j]=(get((int)n-25)+get((int)n-56)+1000000)%1000000-500000;
                }else{
                    matrix[i][j]=(100003-200003*n+300007*n*n*n)%1000000-500000;
                }
            }
        }
    }
    public static long max(long[] x){
        long max_total=0;
        long max_temp=0;
        for (long a : x){
            max_temp = Math.max(max_temp + a, 0);
            max_total = Math.max(max_total, max_temp);
        }
        return max_total;
    }
    public static void main(String[] args) {
        generate();
        long max_sum=0;
        //Horixontal
        for (int i=0; i<matrix.length; i++){
            max_sum=Math.max(max_sum,max(matrix[i]));
        }
        //Vertical
        for (int i=0; i<matrix.length; i++){
            long[] x=new long[2000];
            for (int j=0; j<matrix.length; j++){
                x[j]=matrix[j][i];
            }
            max_sum=Math.max(max_sum,max(x));
        }
        System.out.println(max_sum);
        for (int i=0; i<matrix.length; i++){
            for (int j=0; j<matrix.length; j++){
                String s=Long.toString(matrix[i][j]);
                while(s.length()<8){
                    s=s+" ";
                }
                System.out.print(s);
            }
            System.out.println();
        }
    }
}
