package fraction;
public class Fraction{
        int Num;
        int Den;
        public Fraction(int a, int b){
            Num=a;
            Den=b;
        }
        public Fraction(int a){
            this(a,1);
        }
        public Fraction(String a){
            String[] b=a.split("/");
            Num=Integer.parseInt(b[0]);
            Den=b.length==2?Integer.parseInt(b[1]):1;
        }
        public Fraction add(Fraction f){
            if (f.equalsZero()){
                return new Fraction(Num,Den);
            }
            return new Fraction(Num*f.Den+Den*f.Num,Den*f.Den);
        }
        public Fraction subtract(Fraction f){
            return add(f.neg());
        }
        public Fraction neg(){
            return new Fraction(-Num,Den);
        }
        public Fraction inv(){
            return new Fraction(Den,Num);
        }
        public Fraction multiply(Fraction f){
            return new Fraction(Num*f.Num,Den*f.Den);
        }
        public boolean equalsOne(){
            return Num==Den;
        }
        public boolean equalsZero(){
            return Num==0 || Den==0;
        }
        public int toInt(){
            return Num/Den;
        }
        public Fraction simplify(){
            if (Num==0){
                return new Fraction(0,1);
            }
            if (Num<0 && Den<0){
                return new Fraction(-Num,-Den);
            }
            if (Num<0){
                Fraction f=(new Fraction(-Num,Den)).simplify();
                return new Fraction(-f.Num,f.Den);
            }
            if (Den<0){
                Fraction f=(new Fraction(Num,-Den)).simplify();
                return new Fraction(f.Num,-f.Den);
            }
            
            
            if (Num==1 || Den==1){
                return new Fraction(Num,Den);
            }
            
            for (int i=2; i<=Num && i<=Den; i++){
                if (Num%i==0 && Den%i==0){
                    return (new Fraction(Num/i,Den/i)).simplify();
                }
            }
            return new Fraction(Num,Den);
        }
        public Fraction divide(Fraction f){
            return new Fraction(Num*f.Den,Den*f.Num);
        }
        public String toString(){
            Fraction f=simplify();
            Num=f.Num;
            Den=f.Den;
            if (Den==1){
                return Integer.toString(Num);
            }
            if (Den==-1){
                return Integer.toString(0-Num);
            }
            if (equalsOne()){
                return "1";
            }
            if (Num==0){
                return "0";
            }
            return Num+"/"+Den;
        }
        public Fraction pow(int i){
            if (i==0){
                return new Fraction(1);
            }
            return multiply(pow(i-1)).simplify();
        }
        public int compareTo(Object o){
            if (o instanceof Fraction){
                Fraction f=(Fraction)o;
                if ((double)Num/(double)Den>(double)(f.Num)*(double)(f.Den)){
                    return 1;
                }
                if ((double)Num/(double)Den<(double)(f.Num)*(double)(f.Den)){
                    return -1;
                }
            }
            return 0;
        }
        public int getNum(){
            return Num;
        }
        public int getDen(){
            return Den;
        }
    }