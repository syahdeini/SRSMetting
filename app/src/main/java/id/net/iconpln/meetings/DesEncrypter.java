package id.net.iconpln.meetings;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class DesEncrypter {
    Cipher ecipher;
    Cipher dcipher;

    DesEncrypter(SecretKey key) {
        try {
            ecipher = Cipher.getInstance("DES");
            dcipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);

        }catch (Exception e)
        {
            System.out.print(e);
        }
    }

    public byte[] encrypt(String str) {
        try {
            // Encode the string into bytes using utf-8
            System.out.println("s264 Started");
            byte[] r64 = stringToR64(str);
            System.out.println("s264 worked");
            // Encrypt
            byte[] enc = ecipher.doFinal(r64);

            // Encode bytes to base64 to get a string
            //return new sun.misc.BASE64Encoder().encode(enc);
            return enc;
        }catch (Exception e)
        {
            System.out.print(e);
        }
        return null;
    }

    public String decrypt(byte[] ciphertext) {
        try {

            // Decrypt
            byte[] plainbytes = dcipher.doFinal(ciphertext);

            // Decode using utf-8
            return r64ToString(plainbytes);
        }catch (Exception e)
        {
            System.out.print(e);
        }
        return null;
    }
    public static byte[] stringToR64(String thekey) {
        byte[] ans = new byte[16];
        thekey = thekey.toLowerCase();

        // Go through all 16 characters.
        for (int i=0; i<16; i++) {
            byte val = (byte)(thekey.charAt(i));

            // We need to assign value separately if it is a digit or a letter.
            if ((byte)'0' <= val && val <= (byte)'9')
                val = (byte) (val - (byte)'0');
            else
                val = (byte) (val - (byte)'a' + (byte)10);

            ans[i] = val;
        }

        return ans;
    }
    public static String r64ToString(byte[] thebytes) {
        String ans = null;

        // Go through all 16 characters.
        for (int i=0; i<16; i++) {
            byte val = thebytes[i];

            // We need to assign value separately if it is a digit or a letter.
            if ((byte)0 <= val && val <= (byte)25)//Lowercase
                val = (byte) (val + (byte)'a');
            else if ((byte)26 <= val && val <= (byte)50)//Uppercase
                val = (byte) (val + (byte)'A' - (byte)25);

            // Peel off the binary bits as before...
            ans += (char)val;
        }

        return ans;
    }
}