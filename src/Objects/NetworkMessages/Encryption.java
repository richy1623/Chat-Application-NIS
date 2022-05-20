package Objects.NetworkMessages;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import java.util.Arrays;
import java.util.zip.*;

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


    // Asymmetric encryption algorithm

    public static byte[] encryptionRSA(byte[] messageArray, Key key) throws NoSuchAlgorithmException,
        NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

            Cipher cipher = Cipher.getInstance("RSA/EBC/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(messageArray);
        }



    // Asymmetric decryption algorithm

    public static byte[] decryptionRSA(byte[] messageArray, Key key) throws NoSuchAlgorithmException,
        NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

            Cipher cipher = Cipher.getInstance("RSA/EBC/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(messageArray);
        }





    public static String compressZip(String str) throws java.io.UnsupportedEncodingException{
        // Encode a String into bytes
        byte[] input = str.getBytes();

        // Compress the bytes
        byte[] output = new byte[100];
        Deflater compresser = new Deflater();
        compresser.setInput(input);
        compresser.finish();
        int length = compresser.deflate(output);
        compresser.end();

        return new String(output, 0, length);
        
    }

    public static String decompressZip(String zip) throws java.io.UnsupportedEncodingException, java.util.zip.DataFormatException{
        
        // Decompress the bytes
        byte[] input = zip.getBytes();
        Inflater decompresser = new Inflater();
        decompresser.setInput(input);
        byte[] result = new byte[100];
        int length = decompresser.inflate(result);
        
        //System.out.println(resultLength);
        decompresser.end();
        // Decode the bytes into a String
        String outputString = new String(result, 0, length);
        return outputString;
      
        
    }

    public static void testZipAndConcat() throws java.io.UnsupportedEncodingException, java.util.zip.DataFormatException{
        String test = "aaaaa";
        String compress = compressZip(test);
        System.out.println("compressed: "+compress+"\t"+compress.length());
        String decompress = decompressZip(compress);
        System.out.println("decompressed: "+decompress+"\t"+decompress.length());
        String concat = concatination(decompress, compress);
        System.out.println("concatination: "+concat);
        String[] deconcat = deconcatination(concat);
        System.out.println("deconcatination: "+deconcat[0]+"\t"+deconcat[1]);
    }

    public static String concatination(String message, String hash) {
        return message+"#%%%#"+hash;
    }

    public static String[] deconcatination(String message) {
        return new String[]{message.substring(0, message.indexOf("#%%%#")), message.substring(message.indexOf("#%%%#")+5)};
    }

    // Symmetric decryption algorithm
    public static byte[] encryptionAES(byte[] messageArray, Key key) throws NoSuchAlgorithmException,
        NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

            Cipher cipher = Cipher.getInstance("AES/EBC/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(messageArray);
        }



    // Symmetric decryption algorithm

    public static byte[] decryptionAES(byte[] messageArray, Key key) throws NoSuchAlgorithmException,
        NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

            Cipher cipher = Cipher.getInstance("AES/EBC/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(messageArray);
        }



    
   



}




    