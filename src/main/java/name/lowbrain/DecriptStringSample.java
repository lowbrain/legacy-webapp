package name.lowbrain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Stream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class DecriptStringSample {

    private final Key publicKey;

    public DecriptStringSample(String publicKeyPath) {
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

    public byte[] decrypt(String encodeText) {
        byte[] decrypted = null;

        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            decrypted = cipher.doFinal(Base64.getDecoder().decode(encodeText));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            throw new RuntimeException(e);
        }

        return decrypted;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        WriteXMLSample xml = new WriteXMLSample("123456");
        System.out.println(xml.toXmlString());

        EncriptStringSample encript = new EncriptStringSample(EncriptStringSample.class.getResource("privateKey.pem").getFile());
        String encodeText = encript.encrypt(xml.toXmlByteArray());
        System.out.println(encodeText);

        DecriptStringSample decript = new DecriptStringSample(DecriptStringSample.class.getResource("publicKey.pem").getFile());
        byte[] decodeByte = decript.decrypt(encodeText);
        System.out.println(new String(decodeByte, "Shift-JIS"));
        System.out.println(new String(decodeByte));
    }
}
