package chessengine.Crypto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.KeyStore;
import java.security.SecureRandom;

public class KeyManager {
    private static final Logger logger = LogManager.getLogger("Key_Manager");
    private static final String keyLocation = System.getenv("APPDATA") + "/Chess/Conf/keyStore.jks";

    private static final String aesAlias = "userAESKey";
    private static final String passwordAlias = "userPassword";
    private static final char[] keyPassword = "chessiscool".toCharArray();

    private static final String ALGORITHM = "AES";

    public static SecretKey tryLoadSecretKey() {
        // Load the keystore from file
        try {
            File keyFile = new File(keyLocation);
            if (!keyFile.exists()) {
                logger.warn("Keystore file not found: " + keyLocation + ". Creating a new one.");
                return generateSecretKey(true); // Generate a new key if the keystore does not exist
            }

            try (FileInputStream fis = new FileInputStream(keyLocation)) {
                KeyStore keyStore = KeyStore.getInstance("JCEKS");
                keyStore.load(fis, keyPassword);

                // Retrieve the secret key
                KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(keyPassword);
                KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry(aesAlias, protectionParam);
                if (entry != null) {
                    return entry.getSecretKey();
                } else {
                    logger.warn("Secret key not found, generating a new one.");
                    return generateSecretKey(false);
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("Keystore file not found: " + keyLocation, e);
        } catch (IOException e) {
            logger.error("Unable to access keystore: " + keyLocation, e);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while loading the secret key", e);
        }
        return null;
    }

    private static SecretKey generateSecretKey(boolean makeNew) {
        try {
            // Generate an AES key
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256, new SecureRandom());  // 256-bit key
            SecretKey secretKey = keyGen.generateKey();

            // Load or create keystore
            KeyStore keyStore = KeyStore.getInstance("JCEKS");

            File keyFile = new File(keyLocation);
            if (!keyFile.exists()) {
                logger.warn("Keystore file not found, creating one: " + keyLocation);
                keyFile.getParentFile().mkdirs(); // This creates the necessary directories
                keyStore.load(null, keyPassword); // Create a new keystore
                logger.info("New keystore created.");
            } else {
                try (FileInputStream fis = new FileInputStream(keyLocation)) {
                    keyStore.load(fis, keyPassword);
                }
            }

            // Store the secret key in the keystore
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
            KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(keyPassword);
            keyStore.setEntry(aesAlias, secretKeyEntry, protectionParam);

            // Save the updated keystore
            try (FileOutputStream fos = new FileOutputStream(keyLocation)) {
                keyStore.store(fos, keyPassword);
            }

            return secretKey;
        } catch (IOException e) {
            logger.error("I/O error occurred while generating secret key", e);
        } catch (Exception e) {
            logger.error("Error occurred during secret key generation", e);
        }
        return null;
    }

    public static void saveNewPassword(String password) {
        saveNewPassword(password, false);
    }

    private static void saveNewPassword(String password, boolean makeNew) {
        try {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");

            File keyFile = new File(keyLocation);
            if (!keyFile.exists()) {
                logger.warn("Keystore file not found, creating one: " + keyLocation);
                keyFile.getParentFile().mkdirs(); // This creates the necessary directories
                saveNewPassword(password, true); // Create new keystore and save password
                return;
            }

            try (FileInputStream fis = new FileInputStream(keyLocation)) {
                keyStore.load(fis, keyPassword);
            }

            // Convert the string to bytes
            byte[] stringBytes = password.getBytes();

            // Create a SecretKey from the string bytes
            SecretKey secretKey = new SecretKeySpec(stringBytes, 0, stringBytes.length, ALGORITHM);

            // Store the SecretKey in the KeyStore
            keyStore.setEntry(passwordAlias, new KeyStore.SecretKeyEntry(secretKey),
                    new KeyStore.PasswordProtection(keyPassword));

            // Save the KeyStore to a file
            try (FileOutputStream fos = new FileOutputStream(keyLocation)) {
                keyStore.store(fos, keyPassword);
            }
        } catch (FileNotFoundException e) {
            logger.error("Keystore file not found error", e);
        } catch (IOException e) {
            logger.error("I/O error occurred while saving the new password", e);
        } catch (Exception e) {
            logger.error("Error occurred during password saving", e);
        }
    }

    public static String tryLoadCurrentPasswordHash() {
        try {
            File keyFile = new File(keyLocation);
            if (!keyFile.exists()) {
                logger.warn("Keystore file not found: " + keyLocation + ". Creating a new one.");
                generateSecretKey(true); // Create a new keystore
                return null;
            }

            try (FileInputStream fis = new FileInputStream(keyLocation)) {
                KeyStore keyStore = KeyStore.getInstance("JCEKS");
                keyStore.load(fis, keyPassword);

                // Retrieve the secret key
                KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(keyPassword);
                KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry(passwordAlias, protectionParam);
                if (entry != null) {
                    return new String(entry.getSecretKey().getEncoded());
                } else {
                    logger.warn("Password alias not found in the keystore");
                }
            }
        } catch (FileNotFoundException e) {
            logger.debug("Keystore file not found: " + keyLocation);
        } catch (IOException e) {
            logger.error("Unable to access keystore: " + keyLocation, e);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while loading password hash", e);
        }
        return null;
    }
}
