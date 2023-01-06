package name.lowbrain;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Stream;

public class SignatureVerifySample {

    private final PublicKey publicKey;

    public SignatureVerifySample(String publicKeyPath) {
        Path path = FileSystems.getDefault().getPath(publicKeyPath);
        byte[] keyByte = null;

        // 公開鍵のPEMファイル読み込み
        try (BufferedReader keyReader = Files.newBufferedReader(path, StandardCharsets.US_ASCII)) {
            StringBuilder keyString = new StringBuilder();
            Stream<String> lines = keyReader.lines();
            lines.forEach(line -> {
                if (line.indexOf("-----BEGIN") < 0 && line.indexOf("-----END") < 0) {
                    keyString.append(line);
                }
            });
            keyByte = Base64.getDecoder().decode(keyString.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 公開鍵生成
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyByte);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verify(byte[] message, String base64Sign) {
        boolean result = false;

        try {
            Signature verify = Signature.getInstance("SHA256withRSA");
            verify.initVerify(publicKey);
            verify.update(message);
            result = verify.verify(Base64.getDecoder().decode(base64Sign));
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public static void main(String[] args) {
        WriteXMLSample xml = new WriteXMLSample("ユーザID");
        System.out.println(xml.toXmlString());

        SignatureSignSample sign = new SignatureSignSample(SignatureSignSample.class.getResource("privateKey.pem").getFile());
        String base64Sign = sign.sign(xml.toXmlByteArray());
        System.out.println(base64Sign);

        SignatureVerifySample verify = new SignatureVerifySample(SignatureVerifySample.class.getResource("publicKey.pem").getFile());
        System.out.println(verify.verify(xml.toXmlByteArray(), base64Sign));
    }

}
