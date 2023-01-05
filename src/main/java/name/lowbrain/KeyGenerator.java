package name.lowbrain;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        // 鍵の生成
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        keyGenerator.initialize(2048);
        KeyPair keyPair = keyGenerator.generateKeyPair();

        // 暗号鍵をPEM形式で出力
        String privatePem = generatePem(keyPair.getPrivate());
        writeFile("src/main/resources/name/lowbrain/privateKey.pem", privatePem);
        System.out.println(privatePem);

        // 複合鍵をPEM形式で出力
        String publicPem = generatePem(keyPair.getPublic());
        writeFile("src/main/resources/name/lowbrain/publicKey.pem", publicPem);
        System.out.println(publicPem);
    }

    private static String generatePem(Key key) {
        StringBuilder pem = new StringBuilder();
        String keyName = null;

        if (key instanceof PrivateKey) {
            keyName = "RSA PRIVATE KEY";
        } else {
            keyName = "PUBLIC KEY";
        }

        String base64 = Base64.getEncoder().encodeToString(key.getEncoded());
        pem.append("-----BEGIN " + keyName + "-----\r\n");
        char[] c = base64.toCharArray();
        for (int i = 0; i < c.length; i++) {
            pem.append(c[i]);
            if ((i + 1) % 64 == 0) pem.append("\r\n");
        }
        pem.append("\r\n-----END " + keyName + "-----");

        return pem.toString();
    }

    private static void writeFile(String filePath, String pemString) {
        Path path = FileSystems.getDefault().getPath(filePath);
        try (BufferedWriter keyWriter = Files.newBufferedWriter(path, StandardCharsets.US_ASCII)){
            keyWriter.write(pemString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}