/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package euler82;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leif
 */
public class Euler82 {

    /**
     * @param args the command line arguments
     */
    public static int minimumPath() throws NumberFormatException, IOException
	{

		int[][] arr1 = new int[80][80], arr2 = new int[80][80], arr3 = new int[80][80];
		File file = new File("/matrix.txt");
		StringBuffer contents = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;
            int rowIndex = 0;
            while ((text = reader.readLine()) != null) {
                String[] strArr = text.split(",");
                for(int i=0;i<strArr.length;i++)
                	arr1[rowIndex][i] = Integer.parseInt(strArr[i].trim());
                rowIndex++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
		for(int i=0;i<80;i++)
		{
			arr2[i][0] = arr1[i][0];
		}
		//from the second column to the second rightmost column
		for(int j=1;j<80-1;j++)
		{
		   for(int i=0;i<80;i++)
		   {
			   int min = Integer.MAX_VALUE;
			   // reach from its upper left path
			   for(int k=0;k<i;k++)
			   {
				   //start from arr2[k][j-1], going right and down
				   arr3[k][j-1] = arr2[k][j-1];
				   arr3[k][j]   = arr3[k][j-1]+arr1[k][j];
				   int l = k+1;
				   while(l<i)
				   {
                                arr3[l][j-1] = arr3[l-1][j-1] + arr1[l][j-1];
				          arr3[l][j]   = Math.min(arr3[l][j-1], arr3[l-1][j])+arr1[l][j];
                                          l++;
                                   }
                                   int temp = Math.min(arr3[i-1][j-1]+arr1[i][j-1], arr3[i-1][j]);
                                    min = Math.min(temp, min);
                           }

                           // reach from its bottom left path
                           for(int k=79;k>i;k--)
			   {
				   //start from arr2[k][j-1], going right and up
				   arr3[k][j-1] = arr2[k][j-1];
				   arr3[k][j]   = arr3[k][j-1]+arr1[k][j];
				   int l = k-1;
				   while(l>i)
				   {
					   arr3[l][j-1] = arr3[l+1][j-1] + arr1[l][j-1];
					   arr3[l][j]   = Math.min(arr3[l][j-1], arr3[l+1][j])+arr1[l][j];
					   l--;
				   }
				   int temp = Math.min(arr3[i+1][j-1]+arr1[i][j-1], arr3[i+1][j]);
				   min = Math.min(temp, min);
			   }

			   //reach from its left'
			   min = Math.min(arr2[i][j-1], min);
			   arr2[i][j] = arr1[i][j] + min;
		   }
		}
		//find min from rightmost column
		int min = Integer.MAX_VALUE;
		for(int i=0;i<80;i++)
		{
			min = Math.min(arr2[i][78]+arr1[i][79], min);
		}
		return min;
	}
    public static void main(String[] args) {
        try {
            System.out.println(minimumPath());
            // TODO code application logic here
        } catch (NumberFormatException ex) {
            Logger.getLogger(Euler82.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Euler82.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
