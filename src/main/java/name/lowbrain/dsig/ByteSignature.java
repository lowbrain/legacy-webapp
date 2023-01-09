package name.lowbrain.dsig;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class ByteSignature {

    private final byte[] data;

    public ByteSignature(byte[] data) {
        this.data = data;
    }

    public byte[] sign(PrivateKey key) {
        byte[] result = null;

        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initSign(key);
            sign.update(data);
            result = sign.sign();
        } catch(NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public boolean verify(PublicKey key, byte[] sign) {
        boolean result = false;

        try {
            Signature verify = Signature.getInstance("SHA256withRSA");
            verify.initVerify(key);
            verify.update(data);
            result = verify.verify(sign);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
    
}
