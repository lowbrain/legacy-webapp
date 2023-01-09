package name.lowbrain.dsig;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Stream;

public final class KeyManager {

    private static KeyManager manager = new KeyManager();

    private final PrivateKey privateKey;

    private final PublicKey publicKey;

    private KeyManager() {
        String privateKeyPath = KeyManager.class.getResource("privateKey.pem").getFile();
        privateKey = (PrivateKey) genKey(privateKeyPath, true);

        String publickeyPath = KeyManager.class.getResource("publicKey.pem").getFile();
        publicKey = (PublicKey) genKey(publickeyPath, false);
    }
    
    public static PrivateKey getPrivateKey() {
        return manager.privateKey;
    }

    public static PublicKey getPublicKey() {
        return manager.publicKey;
    }

    private Key genKey(String keyPath, boolean isPrivate) {
        Key key = null;

        Path path = FileSystems.getDefault().getPath(keyPath);
        byte[] keyByte = readKeyFile(path);

        try {
            if (isPrivate) {
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyByte);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                key = keyFactory.generatePrivate(keySpec);
            } else {
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyByte);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                key = keyFactory.generatePublic(keySpec);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        return key;
    }

    private static byte[] readKeyFile(Path path) {
        byte[] keyByte = null;

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

        return keyByte;
    }

}
