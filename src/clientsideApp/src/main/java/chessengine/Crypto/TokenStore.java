package chessengine.Crypto;

import chessserver.Misc.SavePath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * The KeyManager class provides functionality for securely managing
 * refresh tokens using a keystore. It supports storing, retrieving,
 * and clearing refresh tokens, as well as generating or loading
 * a secure password for the keystore.
 */
public class TokenStore {
    private static final Logger logger = LogManager.getLogger("RefreshTokenManager");
    private static final Path keyLocation = SavePath.getSavePath(".conf", "keyStore.jks");
    private static final String tokenAlias = "refreshToken";
    private static final char[] keyPassword = loadOrGeneratePassword();
    private static final String ALGORITHM = "AES";


    private static String currentRefreshToken = loadRefreshToken();


    /**
     * Checks if a refresh token is currently stored in the keystore.
     *
     * @return true if a refresh token exists, false otherwise.
     */
    public boolean isLoggedIn() {
        return getRefreshToken() != null;
    }


    /**
     * Clears the refresh token from the keystore, if present.
     * Logs a warning if the keystore does not exist or no token is found.
     */
    public static void clearRefreshToken() {
        try {
            File keyFile = keyLocation.toFile();
            if (!keyFile.exists()) {
                logger.warn("Keystore does not exist; nothing to clear.");
                return;
            }

            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            try (FileInputStream fis = new FileInputStream(keyFile)) {
                keyStore.load(fis, keyPassword);
            }

            if (keyStore.containsAlias(tokenAlias)) {
                keyStore.deleteEntry(tokenAlias);
                try (FileOutputStream fos = new FileOutputStream(keyFile)) {
                    keyStore.store(fos, keyPassword);
                }
                logger.info("Refresh token cleared successfully.");
            } else {
                logger.info("No refresh token found to clear.");
            }

        } catch (Exception e) {
            logger.error("Failed to clear refresh token", e);
            throw new RuntimeException("Unable to clear refresh token", e);
        }
        finally {
            currentRefreshToken = loadRefreshToken();
        }
    }

    /**
     * Sets a new refresh token string into the keystore.
     * If the keystore does not exist, it will be created.
     *
     * @param token The new refresh token to store.
     */
    public static void setRefreshToken(String token) {
        try {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            File keyFile = keyLocation.toFile();

            if (!keyFile.exists()) {
                logger.info("Creating keystore as it does not exist: " + keyLocation);
                keyFile.getParentFile().mkdirs();
                keyStore.load(null, keyPassword);
            } else {
                try (FileInputStream fis = new FileInputStream(keyFile)) {
                    keyStore.load(fis, keyPassword);
                }
            }

            byte[] stringBytes = token.getBytes(StandardCharsets.UTF_8);
            SecretKey secretKey = new SecretKeySpec(stringBytes, 0, stringBytes.length, ALGORITHM);

            keyStore.setEntry(tokenAlias, new KeyStore.SecretKeyEntry(secretKey),
                    new KeyStore.PasswordProtection(keyPassword));

            try (FileOutputStream fos = new FileOutputStream(keyFile)) {
                keyStore.store(fos, keyPassword);
            }

            logger.info("Refresh token stored successfully.");
        } catch (Exception e) {
            logger.error("Failed to store refresh token", e);
            throw new RuntimeException("Unable to store refresh token", e);
        }
        finally {
            currentRefreshToken = loadRefreshToken();
        }
    }

    public static @Nullable String getRefreshToken(){
        return currentRefreshToken;
    }

    /**
     * Reads the current refresh token from the keystore.
     *
     * @return The stored refresh token, or null if not present or on error.
     */
    private static @Nullable String loadRefreshToken() {
        try {
            File keyFile = keyLocation.toFile();
            if (!keyFile.exists()) {
                logger.warn("Keystore does not exist: " + keyLocation);
                return null;
            }

            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            try (FileInputStream fis = new FileInputStream(keyFile)) {
                keyStore.load(fis, keyPassword);
            }

            if (!keyStore.containsAlias(tokenAlias)) {
                logger.info("No refresh token found in keystore.");
                return null;
            }

            KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(keyPassword);
            KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry(tokenAlias, protectionParam);

            return new String(entry.getSecretKey().getEncoded());
        } catch (Exception e) {
            logger.error("Failed to read refresh token", e);
            return null;
        }
    }

    /**
     * Loads an existing keystore password from a file or generates a new one.
     * The password is stored securely in a file for future use.
     *
     * @return A character array containing the keystore password.
     */
    private static char[] loadOrGeneratePassword() {
        Path keyPath = SavePath.getSavePath(".conf", "keystore.key");

        try {
            if (!Files.exists(keyPath)) {
                Files.createDirectories(keyPath.getParent());

                byte[] random = new byte[32];
                new SecureRandom().nextBytes(random);
                String encoded = Base64.getEncoder().encodeToString(random);

                Files.writeString(keyPath, encoded, StandardOpenOption.CREATE_NEW);
                keyPath.toFile().setReadable(true, true);
                keyPath.toFile().setWritable(true, true);
                keyPath.toFile().setExecutable(false, false);
                logger.info("Generated new keystore password.");
            }

            String stored = Files.readString(keyPath);
            return stored.toCharArray();

        } catch (IOException e) {
            logger.error("Could not load or generate keystore password", e);
            throw new RuntimeException(e);
        }
    }
}