package Objects.NetworkMessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class SecureMessage implements Serializable{
    private SealedObject sealedObject;
    private SignedObject decryptedObject;
    private boolean signed, encrypted;
    private byte[] encryptedKey;
    private static boolean verbose = true;

    public SecureMessage(NetworkMessage message, SecretKey sessionkey, PublicKey reciverPublicKey, PrivateKey senderPrivateKey){
        try {
            if(verbose) System.out.println("Encrypting Network Message");
            
            if (senderPrivateKey==null){
                signed=false;
                encrypted = true;
                sealedObject = new SealedObject(compress(message), Encryption.getAESCipher(sessionkey));
                encryptedKey = Encryption.encryptionRSA(sessionkey.getEncoded(), reciverPublicKey);
                if (verbose) System.out.println(">Message is not being signed");
                if (verbose) System.out.println(">Encrypting using recivers Public Key");
            }else{
                signed=true;
                SignedObject signedObject = new SignedObject(message, senderPrivateKey, Signature.getInstance("SHA256withRSA"));
                if (verbose) System.out.println(">Signed using own Private Key");
                byte[] compressed = compress(signedObject);
                if (reciverPublicKey==null){
                    encrypted = false;
                    sealedObject = new SealedObject(compressed, Encryption.getAESCipher(sessionkey));
                    encryptedKey = sessionkey.getEncoded();
                }else{
                    encrypted = true;
                    sealedObject = new SealedObject(compressed, Encryption.getAESCipher(sessionkey));
                    encryptedKey = Encryption.encryptionRSA(sessionkey.getEncoded(), reciverPublicKey);
                    if (verbose)System.out.println(">Encrypting using recivers Public Key");
                }
            }
            if (verbose) System.out.println("^^^\n");
        } catch (InvalidKeyException | NoSuchAlgorithmException 
                | IOException | SignatureException | IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException e) {
            e.printStackTrace();
        } 
    }

    public NetworkMessage decrypt(PrivateKey privateKey){
        try{
            if(verbose) System.out.println("Decrypting Network Message");
            byte[] decryptedKey = encryptedKey;
            if (encrypted){
                decryptedKey = Encryption.decryptionRSA(encryptedKey, privateKey);
                if (verbose) System.out.println(">Decrypting using recivers Private Key");
            }
            SecretKey key = (SecretKey) new SecretKeySpec(decryptedKey, "AES");
            if (signed){
                decryptedObject = decompressSignedObject((byte[]) sealedObject.getObject(key));
                return (NetworkMessage) decryptedObject.getObject();
            }else{
                return decompressUnsignedObject((byte[]) sealedObject.getObject(key));
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public boolean validate(PublicKey publicKey){
        try {
            if(verbose) System.out.println("Validating Message Signature");
            if (!signed){
                return false;
            }
            Signature signature = Signature.getInstance("SHA256withRSA");
            if (verbose) System.out.println(">Validating using senders Public Key");
            boolean valid = decryptedObject.verify(publicKey, signature);
            if (verbose) System.out.println(valid ? ">>Valid digital signature" : ">>Invalid digial signature");
            return valid;
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
            return false;
        }
    }

    private byte[] compress(Object o){
        if (verbose) System.out.println(">>Compressing Object");
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
            ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
            objectOut.writeObject(o);
            objectOut.close();
            gzipOut.close();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private SignedObject decompressSignedObject(byte[] bytes){
        if (verbose) System.out.println(">>Deompressing Object");
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            GZIPInputStream gzipIn = new GZIPInputStream(bais);
            ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
            SignedObject o = (SignedObject) objectIn.readObject();
            objectIn.close();
            gzipIn.close();
            if (verbose) System.out.println("^^^");
            return o;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    private NetworkMessage decompressUnsignedObject(byte[] bytes){
        if (verbose) System.out.println(">>Deompressing Object");
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            GZIPInputStream gzipIn = new GZIPInputStream(bais);
            ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
            NetworkMessage o = (NetworkMessage) objectIn.readObject();
            objectIn.close();
            gzipIn.close();
            if (verbose) System.out.println("^^^\n");
            return o;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
