import java.util.*;
import java.math.*;
import java.io.*;
public class MyKeyGen
{
    //private static BigInteger P;
    //private static BigInteger Q;
    
    public static void main(String[] args) throws IOException
    {
        BigInteger P = pickP();
        System.out.println("P: " + P.toString());
        BigInteger Q = pickQ();
        System.out.println("Q: " + Q.toString());
        BigInteger N = P.multiply(Q);
        System.out.println("N: " + N.toString());
        BigInteger PHIN = phi(N, P, Q);
        System.out.println("PHIN: " + PHIN.toString());
        BigInteger E = pickE(PHIN);
        System.out.println("E: " + E.toString());
        BigInteger D = pickD(PHIN, E);
        System.out.println("D: " + D.toString());
        FileOutputStream pub = new FileOutputStream("pubkey.rsa");
        ObjectOutputStream oos1 = new ObjectOutputStream(pub);
        oos1.writeObject(E);
        oos1.writeObject(N);
        oos1.close();
        FileOutputStream priv = new FileOutputStream("privkey.rsa");
        ObjectOutputStream oos2 = new ObjectOutputStream(priv);
        oos2.writeObject(D);
        oos2.writeObject(N);
        oos2.close();
    }
    
    public static BigInteger pickP()
    {       
        return BigInteger.probablePrime(512, new Random());
    }
    
    public static BigInteger pickQ()
    {
        return BigInteger.probablePrime(512, new Random());
    }
    
    public static BigInteger phi(BigInteger n, BigInteger P, BigInteger Q)
    {
        BigInteger p2 = P.subtract(BigInteger.ONE);
        BigInteger q2 = Q.subtract(BigInteger.ONE);
        return p2.multiply(q2);
    }
    
    public static BigInteger pickE(BigInteger phin)
    {   
        BigInteger e;
        Boolean flag = false;
        do 
        {
            e = new BigInteger(phin.bitLength(), new Random());
            if(e.gcd(phin).equals(BigInteger.ONE) && (e.compareTo(phin) < 0) && (e.compareTo(BigInteger.ONE) > 0)) 
            {
                flag = true;
            }
        } while (flag == false); 
        return e;
    }
    
    public static BigInteger pickD(BigInteger phin, BigInteger e)
    {
        BigInteger d = e.modInverse(phin);
        return d;
    }
}
