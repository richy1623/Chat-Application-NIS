package Objects.NetworkMessages;

import java.io.FileInputStream;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.util.Arrays;
import java.util.zip.*;

public class Encryption {
    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    private static final String salt = "12345678";
    private static final byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private static final IvParameterSpec ivspec = new IvParameterSpec(iv);

    private Encryption() {

    }

    // Generation of 2040 bit key pair using the RSA algorithm

    public static KeyPair generate() throws NoSuchAlgorithmException {

        final KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        //final SecureRandom random = new SecureRandom();
        generator.initialize(KEY_SIZE);
        return generator.generateKeyPair();

    }

    public static PrivateKey generatePrivate(byte[] key) {
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(key);
            return keyFactory.generatePrivate(privateKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static SecretKey generateSecretKey(byte[] key) {
        try {
            // construct a secret key from the given byte array
            return new SecretKeySpec(key, 0, key.length, "AES");
        } catch (Exception e) {
            System.out.println("##Error generating secret key from byte[]");
            e.printStackTrace();
            return null;
        }

    }

    // Generation of a 128-bit ASE session key

    public static SecretKey sessionKey() throws NoSuchAlgorithmException {

        byte[] keyBytes = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(keyBytes);
        return new SecretKeySpec(keyBytes, "AES");

    }

    public static Cipher getAESCipher(SecretKey key) {
        Cipher aesCipher = null;
        try {
            aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            System.out.println("##Error fetching ciper from Secret Key");
            e.printStackTrace();
        }
        return aesCipher;
    }

    // Asymmetric encryption algorithm

    public static byte[] encryptionRSA(byte[] messageArray, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(messageArray);
        }catch (Exception e){
            System.out.println("##Error Encrytping with RSA");
            e.printStackTrace();
            return null;
        }
        
    }

    // Asymmetric decryption algorithm

    public static byte[] decryptionRSA(byte[] messageArray, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(messageArray);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println("##Error Decrypting with RSA");
            e.printStackTrace();
            return null;
        }
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

    // Symmetric encryption algorithm
    public static byte[] encryptionAES(byte[] messageArray, Key key) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
        return cipher.doFinal(messageArray);
    }

    // Symmetric decryption algorithm

    public static byte[] decryptionAES(byte[] messageArray, Key key) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
        return cipher.doFinal(messageArray);
    }

    public static byte[] passEncrypt(byte[] messageArray, String password) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            InvalidKeySpecException, InvalidAlgorithmParameterException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        cipher.init(Cipher.ENCRYPT_MODE, secret, ivspec);
        return cipher.doFinal(messageArray);
    }

    public static byte[] passcrDecrypt(byte[] messageArray, String password) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            InvalidKeySpecException, InvalidAlgorithmParameterException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

                System.out.println("\tEncryption Class:");
                System.out.println("\t The secret key is: " + secret);


        cipher.init(Cipher.DECRYPT_MODE, secret, ivspec);
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

    public static void testAESEncryption() throws Exception {
        byte[] message = "Hello world".getBytes();
        SecretKey key = sessionKey();
        byte[] cipered = encryptionAES(message, key);
        byte[] decrypt = decryptionAES(cipered, key);
        System.out.println(Arrays.equals(message, cipered));
        System.out.println(Arrays.equals(message, decrypt));
    }

}