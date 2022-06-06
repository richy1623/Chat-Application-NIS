package Objects.NetworkMessages;

import java.io.FileInputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
//import java.security.spec.KeySpec;
import java.security.spec.KeySpec;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
//import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.SecretKeySpec;

import java.util.Arrays;
import java.util.Base64;
import java.util.zip.*;

public class Encryption {
    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    private static final int HASH_ITERATIONS = 128;
    private static final String salt = "12345678";

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

    public static Cipher getAESCipher(SecretKey key)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aesCipher.init(Cipher.ENCRYPT_MODE, key);
        return aesCipher;
    }

    // Asymmetric encryption algorithm

    public static byte[] encryptionRSA(byte[] messageArray, Key key) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(messageArray);
    }

    // Asymmetric decryption algorithm

    public static byte[] decryptionRSA(byte[] messageArray, Key key) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(messageArray);
    }

    public static String compressZip(String str) throws java.io.UnsupportedEncodingException {
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

    public static String decompressZip(String zip)
            throws java.io.UnsupportedEncodingException, java.util.zip.DataFormatException {

        // Decompress the bytes
        byte[] input = zip.getBytes();
        Inflater decompresser = new Inflater();
        decompresser.setInput(input);
        byte[] result = new byte[100];
        int length = decompresser.inflate(result);

        // System.out.println(resultLength);
        decompresser.end();
        // Decode the bytes into a String
        String outputString = new String(result, 0, length);
        return outputString;

    }

    public static void testZipAndConcat()
            throws java.io.UnsupportedEncodingException, java.util.zip.DataFormatException {
        String test = "aaaaa";
        String compress = compressZip(test);
        System.out.println("compressed: " + compress + "\t" + compress.length());
        String decompress = decompressZip(compress);
        System.out.println("decompressed: " + decompress + "\t" + decompress.length());
        String concat = concatination(decompress, compress);
        System.out.println("concatination: " + concat);
        String[] deconcat = deconcatination(concat);
        System.out.println("deconcatination: " + deconcat[0] + "\t" + deconcat[1]);
    }

    public static String concatination(String message, String hash) {
        return message + "#%%%#" + hash;
    }

    public static String[] deconcatination(String message) {
        return new String[] { message.substring(0, message.indexOf("#%%%#")),
                message.substring(message.indexOf("#%%%#") + 5) };
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

    public static byte[] passEncrypt(byte[] messageArray, String password) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        return cipher.doFinal(messageArray);
    }

    public static byte[] passcrDecrypt(byte[] messageArray, String password) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secret);
        return cipher.doFinal(messageArray);
    }

    public static void testPassEncrpytion() {
        try {
            KeyStore keyStoreServer = KeyStore.getInstance("PKCS12");
            keyStoreServer.load(new FileInputStream("Resources/server_keystore.p12"), "keyring".toCharArray());
            PrivateKey privateKey = (PrivateKey) keyStoreServer.getKey("serverkeypair", "keyring".toCharArray());
            byte[] str = privateKey.getEncoded();
            byte[] ecr = passEncrypt(str, "password");
            byte[] newstr = passcrDecrypt(ecr, "password");
            System.out.println(Arrays.equals(str, ecr));
            System.out.println(Arrays.equals(str, newstr));
        } catch (Exception e) {
            System.out.println("Unable to load Keys");
            e.printStackTrace();
        }
    }

    }

    public static byte[] passcrDecrypt(byte[] messageArray, String password) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            InvalidKeySpecException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        javax.crypto.spec.PBEKeySpec keySpec = new javax.crypto.spec.PBEKeySpec(password.toCharArray(), messageArray,
                HASH_ITERATIONS, KEY_SIZE);
        SecretKeySpec key = new SecretKeySpec(secretKeyFactory.generateSecret(keySpec).getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/EBC/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(messageArray);

    }

}
