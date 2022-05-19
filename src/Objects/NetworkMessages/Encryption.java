package Objects.NetworkMessages;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;

    private Encryption() {

    }


    // Generation of 2040 bit key pair using the RSA algorithm

    public static KeyPair generate() throws NoSuchAlgorithmException {

        final KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        final SecureRandom random = new SecureRandom();
        generator.initialize(KEY_SIZE, random);
        return generator.generateKeyPair();
        
    }

    // Generation of a 128-bit ASE session key 

    public static SecretKey sessionKey() throws NoSuchAlgorithmException {

        byte[] keyBytes = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(keyBytes);
        return new SecretKeySpec(keyBytes, "AES");
        
    }


    public static byte[] RSAEncryption(byte[] messageArray, Key key) throws NoSuchAlgorithmException,
        NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

            Cipher cipher = Cipher.getInstance("RSA/EBC/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(messageArray);
        }



 
    public static byte[] RSADencryption(byte[] messageArray, Key key) throws NoSuchAlgorithmException,
        NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

            Cipher cipher = Cipher.getInstance("RSA/EBC/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(messageArray);
        }

    
   



}




    