package debruijn;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class DeBruijn {
	static int size=3;
	static int donesize=(int)Math.pow(2, size);
	static boolean done=false;
	static ArrayList<String> pos=new ArrayList<String>();
	static ArrayList<String> finished=new ArrayList<String>();
	public static void compute(){
            if (pos.size()==0){
                done=true;
                return;
            }
		String c=pos.get(0);
		pos.remove(0);
			if (c.length()>=donesize){
				finished.add(c);
				return;
			}
			if (valid(c+"0")){
				pos.add(c+"0");
			}
			if (valid(c+"1")){
				pos.add(c+"1");
			}
		
	}
	public static boolean validmain(String a){
		ArrayList<String> alr=new ArrayList<String>();
		for(int i=0; i<a.length(); i++){
			String c="";
			if (i>=a.length()-size+1){
				c=a.substring(i,a.length());
				int b=a.length()-i;
				c=c+a.substring(0,size-b);
			}else{
				c=a.substring(i,i+size);
			}
			String b=c;
			if (alr.contains(b)){
				return false;
			}
			alr.add(b);
		}
		return true;
	}
	public static boolean valid(String a){
		if (a.length()>=donesize){
			return validmain(a);
		}
		ArrayList<String> alr=new ArrayList<String>();
		for (int i=0; i<a.length()-size+1; i++){
			String c=a.substring(i,i+size);
			String b=c;
			if (alr.contains(b)){
				return false;
			}
			alr.add(b);
		}
		return true;
	}
	public static void init(){
		String start="1";
		for (int i=0; i<size; i++){
			start="0"+start;
		}
		pos.add(start);
	}
	public static void main(String[] args){
		System.out.print("DeBruijn calculator. Length? >");
		Scanner scan=new Scanner(System.in);
		size=Integer.parseInt(scan.nextLine());
		long t=System.currentTimeMillis();
		donesize=(int)Math.pow(2, size);
		//donesize=17;
		init();
		while(!done){
			compute();
		}
		System.out.println(finished.size()+" found.");
		BigInteger sum=BigInteger.ZERO;
		for (String a:finished){
			String r=a;
			r+=":";
			for(int i=0; i<a.length(); i++){
				String c="";
				if (i>=a.length()-size+1){
					c=a.substring(i,a.length());
					int b=a.length()-i;
					c=c+a.substring(0,size-b);
				}else{
					c=a.substring(i,i+size);
				}
				BigInteger b=new BigInteger(c,2);
				r+=b;
				if (i!=a.length()-1){
					r+=",";
				}
			}
			//sum=sum.add(new BigInteger(a,2));
			System.out.println(r);
			//y.add(r);
		}
		System.out.println(sum);
		//mwrite("/Users/Student/Desktop/out.txt",y);
		System.out.println("BTW, it only generates those ");
		System.out.println(System.currentTimeMillis()-t);
	}
	 public static void mwrite(String name, ArrayList<String> a)
	  {
	  try{
	  // Create file 
	  FileWriter fstream = new FileWriter(name);
	  BufferedWriter out = new BufferedWriter(fstream);
	  for (String n:a){
		  out.write(n);
		  out.write("-");
	  }
	  //Close the output stream
	  out.close();
	  }catch (Exception e){//Catch exception if any
	  System.err.println("Error: " + e.getMessage());
	  }
	  }
}
