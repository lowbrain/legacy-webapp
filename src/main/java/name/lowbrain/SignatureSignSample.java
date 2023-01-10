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
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Stream;

public class SignatureSignSample {

    private final PrivateKey privateKey;

    public SignatureSignSample(String privateKeyPath) {
        Path path = FileSystems.getDefault().getPath(privateKeyPath);
        byte[] keyByte = null;

        // 秘密鍵のPEMファイル読み込み
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

        // 秘密鍵生成
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyByte);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.privateKey = keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public String sign(byte[] message) {
        String base64Sign = null;

        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initSign(this.privateKey);
            sign.update(message);
            base64Sign = Base64.getUrlEncoder().encodeToString(sign.sign());
        } catch(NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }

        return base64Sign;
    }

}
