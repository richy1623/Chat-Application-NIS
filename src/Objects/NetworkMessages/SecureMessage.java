package Objects.NetworkMessages;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class SecureMessage implements Serializable{
    private SignedObject signedObject;
    private byte[] encryptedKey;

    public SecureMessage(NetworkMessage message, SecretKey key, PublicKey serverKey, PrivateKey privateKey){
        try {
            SealedObject messageObject = new SealedObject(message, Encryption.getAESCipher(key));
            //System.out.println(publicKey.getAlgorithm());
            encryptedKey = Encryption.encryptionRSA(key.getEncoded(), serverKey);
            //System.out.println(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
            signedObject = new SignedObject(messageObject, privateKey, Signature.getInstance("SHA256withRSA"));


        } catch (InvalidKeyException | NoSuchAlgorithmException 
                | IOException | SignatureException | IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException e) {
            e.printStackTrace();
        } 
    }

    public NetworkMessage decrypt(PrivateKey privateKey){
        try{
            SealedObject sealed = (SealedObject) signedObject.getObject();
            byte[] decryptedKey = Encryption.decryptionRSA(encryptedKey, privateKey);
            SecretKey key = (SecretKey) new SecretKeySpec(decryptedKey, "AES");
            return (NetworkMessage) sealed.getObject(key);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public boolean validate(PublicKey publicKey){
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            return signedObject.verify(publicKey, signature);
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
            return false;
        }
    }
}
