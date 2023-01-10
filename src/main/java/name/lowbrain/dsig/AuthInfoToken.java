package name.lowbrain.dsig;

import java.util.Base64;

public class AuthInfoToken {

    private final byte[] data;

    private final byte[] sign;

    public AuthInfoToken(String base64Token) {
        String[] tokens = base64Token.split("\\.");
        this.data = Base64.getUrlDecoder().decode(tokens[0]);
        this.sign = Base64.getUrlDecoder().decode(tokens[1]);
    }

    private AuthInfoToken(byte[] data, byte[] sign) {
        this.data = data; 
        this.sign = sign;
    }

    public String toBase64Token() {
        String token1 = Base64.getUrlEncoder().encodeToString(data);
        String token2 = Base64.getUrlEncoder().encodeToString(sign);
        return token1 + "." + token2;
    }

    public AuthInfoXml getAuthInfoXml() {
        return AuthInfoXml.parse(data);
    }

    public AuthInfoXml getValidatedAuthInfoXml() {
        if (!validateSignature()) throw new RuntimeException(); 
        return getAuthInfoXml();
    }

    public static AuthInfoToken newInstance(AuthInfoXml authInfo) {
        byte[] byteData = authInfo.getByte();
        ByteSignature byteSignature = new ByteSignature(byteData);
        byte[] byteSign = byteSignature.sign(KeyManager.getPrivateKey());

        return new AuthInfoToken(byteData, byteSign);
    }

    private boolean validateSignature() {
        ByteSignature byteSignature = new ByteSignature(data);
        return byteSignature.verify(KeyManager.getPublicKey(), sign);
    }

    public static void main(String[] args) {
        AuthInfoXml xml = new AuthInfoXml("ユーザID");
        System.out.println(xml.toXmlString());

        AuthInfoToken outToken = AuthInfoToken.newInstance(xml);
        String out = outToken.toBase64Token();
        System.out.println(out);

        AuthInfoToken inToken = new AuthInfoToken(out);
        AuthInfoXml inXml = inToken.getValidatedAuthInfoXml();
        System.out.println(inXml.toXmlString());
    }
}
