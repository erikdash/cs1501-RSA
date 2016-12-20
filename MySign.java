import java.io.*;
import java.security.MessageDigest;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.math.BigInteger;
public class MySign
{
    public static void main(String[] args)
    {
        if(args.length != 2)
        {
            System.err.println("Invalid number of command line arguments");
            System.exit(0);
        }
        
        String flag = args[0];
        Path newFile = Paths.get(args[1]);
        if(flag.equalsIgnoreCase("s"))
        {
            sign(newFile);
        }
        else if(flag.equalsIgnoreCase("v"))
        {
            verify(newFile);
        }
        else
        {
            System.err.println("Invalid command line arguments (need 's' or 'v' as flag)");
            System.exit(0);
        }
    }
    
    public static void sign(Path f)
    {
        try
        {
            byte[] data = Files.readAllBytes(f);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data);
            byte[] digest = md.digest();
            BigInteger result = new BigInteger(1, digest);
            System.out.println("Signature: " + result.toString());
            
            FileInputStream fis = new FileInputStream("privkey.rsa");
            ObjectInputStream ois = new ObjectInputStream(fis);
            BigInteger D = (BigInteger)ois.readObject();
            BigInteger N = (BigInteger)ois.readObject();
            ois.close();           
            BigInteger decrypted = result.modPow(D, N);   
            System.out.println("'Decrypted' signature: " + decrypted.toString());
            
            FileOutputStream fos = new FileOutputStream(f + ".signed");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.writeObject(decrypted);
            oos.close();
        }
        catch(Exception e)
        {
            System.out.println("error reading from file");
            System.out.println(e.toString());
        }
    }
    
    public static void verify(Path f)
    {
        try
        {
            FileInputStream fis = new FileInputStream(f + "");
            ObjectInputStream ois = new ObjectInputStream(fis);
            byte[] origData = (byte[])ois.readObject();
            BigInteger origDecrypt = (BigInteger)ois.readObject();
            ois.close();
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(origData);
            byte[] newDigest = md.digest();
            BigInteger newResult = new BigInteger(1, newDigest);
            System.out.println("newResult: " + newResult.toString());
            
            FileInputStream fis2 = new FileInputStream("pubkey.rsa");
            ObjectInputStream ois2 = new ObjectInputStream(fis2);
            BigInteger E = (BigInteger)ois2.readObject();
            BigInteger N = (BigInteger)ois2.readObject();
            ois2.close();
            
            BigInteger origSig = origDecrypt.modPow(E, N);
            System.out.println("Signature ('decrypted'): " + origDecrypt.toString());
            System.out.println("Signature ('encrypted'): " + origSig.toString());//should match newResult
            int result = origSig.compareTo(newResult);
            if(result == 0)
            {
                System.out.println("Valid signature");
            }
            else
            {
                System.out.println("Invalid signature");
            }
        }
        catch(Exception e)
        {
            System.out.println("error reading from file");
            System.out.println(e.toString());
        }
    }
}
