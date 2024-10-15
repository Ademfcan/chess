package chessengine.Crypto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nd4j.shade.protobuf.compiler.PluginProtos;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.SecureRandom;

public class KeyManager {
    private final static Logger logger = LogManager.getLogger("Key_Manager");
    final static String keyLocation = System.getenv("APPDATA") + "/Chess/Conf/keyStore.jks";

    final static String alias = "userAESKey";
    final static char[] keyPassword = "chessiscool".toCharArray();

    static final String ALGORITHM = "AES";
    static final String TRANSFORMATION = "AES";
    static SecretKey tryloadSecretKey(){

        // Load the keystore from file
        try (FileInputStream fis = new FileInputStream(keyLocation)) {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(fis, keyPassword);
            // Retrieve the secret key
            KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(keyPassword);
            KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias, protectionParam);
            return entry.getSecretKey();
        }
        catch (FileNotFoundException exception){
            try {
                return generateSecretKey();
            }
            catch (Exception generalException){
                logger.error("Key generation exception!",generalException);
            }
        }
        catch (IOException ioException){
            logger.error("Unable to access keystore",ioException);
        }
        catch (Exception e){
            logger.error("Keystore access error",e);
        }
        return null;

    }

    private static SecretKey generateSecretKey() throws Exception {
        // first generate key
        // Generate an AES key
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(256, new SecureRandom());  // 256-bit key
        SecretKey secretKey = keyGen.generateKey();


        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        keyStore.load(null, keyPassword);  // Create a new keystore

        // Store the secret key
        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
        KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(keyPassword);
        keyStore.setEntry(alias, secretKeyEntry, protectionParam);

        // Save the keystore to file
        try (FileOutputStream fos = new FileOutputStream(keyLocation)) {
            keyStore.store(fos, keyPassword);
        }

        return secretKey;
    }

}
