package ksaito.encrypt;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Encrypt {
    private static final String key = "asdfghjklzxcvbnm";
    private SecretKeySpec secretKeySpec;
    private Cipher cipher;

    // 暗号化・復号処理が同時に呼び出されたときに、Cipher#initを実行しないようにするためのロック
    // 実際に同時にCipher#initした時にどうなるかは未確認。良くないことが起きそう気がするのでロックしている
    // cipherを暗号化用／復号用でインスタンスを分けるのもアリかも
    private final Object lockObj = new Object();

    public void init() throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
        this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    }

    /**
     * 暗号化する
     *
     * @param str 暗号化する文字列
     * @return 暗号化した文字列
     */
    public String encrypt(String str) throws IOException, InvalidKeyException {
        synchronized (this.lockObj) {
            this.setEncryptMode();
            byte[] encodedBytes;
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                // 最初に初期ベクトルを出力
                outputStream.write(this.cipher.getIV());
                // ByteArrayOutputStream#toByteArray実行前に、CipherOutputStreamを先にcloseしないと復号化に失敗する
                try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, this.cipher)) {
                    cipherOutputStream.write(str.getBytes());
                }
                encodedBytes = outputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }

            byte[] base64Bytes = Base64.getEncoder().encode(encodedBytes);
            return new String(base64Bytes);
        }
    }


    /**
     * 復号する
     *
     * @param str 復号する文字列
     * @return 復号した文字列
     */
    public String decrypt(String str) throws IOException, InvalidAlgorithmParameterException, InvalidKeyException {
        synchronized (this.lockObj) {
            byte[] base64Bytes = Base64.getDecoder().decode(str);
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(base64Bytes)) {
                byte[] iv = getIVForDecode(inputStream);
                this.setDecryptMode(iv);
                return this.decryptCipherStream(inputStream);
            } catch (IOException | InvalidAlgorithmParameterException | InvalidKeyException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }


    /**
     * 暗号化モードに設定する
     */
    private void setEncryptMode() throws InvalidKeyException {
        try {
            this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKeySpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw e;
        }
    }
    /**
     * 復号モードに設定する
     */
    private void setDecryptMode(byte[] iv) throws InvalidAlgorithmParameterException, InvalidKeyException {
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        try {
            this.cipher.init(Cipher.DECRYPT_MODE, this.secretKeySpec, ivParameterSpec);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 暗号化されたストリームを復号する
     *
     * @param inputStream ストリーム
     * @return 復号されたデータ
     */
    private String decryptCipherStream(ByteArrayInputStream inputStream) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (CipherInputStream cipherInputStream = new CipherInputStream(inputStream, this.cipher)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = cipherInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
            }
            return outputStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 暗号化されたデータから初期ベクトルを取得する。
     *
     * @param inputStream 暗号化データのストリーム
     * @return 初期ベクトル
     */
    private static byte[] getIVForDecode(ByteArrayInputStream inputStream) {
        byte[] iv = new byte[16];
        for (int i = 0; i < iv.length; ++i) {
            iv[i] = (byte)inputStream.read();
        }
        return iv;
    }
}
