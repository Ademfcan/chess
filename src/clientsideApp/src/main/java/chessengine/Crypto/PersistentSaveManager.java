package chessengine.Crypto;

import chessengine.ChessRepresentations.ChessGame;
import chessengine.Misc.ChessConstants;
import chessserver.FrontendClient;
import chessserver.UserInfo;
import chessserver.UserPreferences;
import org.apache.commons.math3.analysis.function.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nd4j.shade.jackson.databind.ObjectMapper;

import javax.crypto.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class PersistentSaveManager {
    private final static Logger logger = LogManager.getLogger("Persistent_Save_Manager");
    private final static String appdataPathGameSaves = System.getenv("APPDATA") + "/Chess/save.txt";
    private final static String appdataPathUserpreferences = System.getenv("APPDATA") + "/Chess/userPreferences.txt";
    private final static String appdataPathUserPass = System.getenv("APPDATA") + "/Chess/Conf/C1H2E3S4S4U5S4E3R6.dat";
    private final static String appdataFolder = System.getenv("APPDATA") + "/Chess";

    private final static ObjectMapper objectmapper = new ObjectMapper();

    public static UserPreferences readUserprefFromSave() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(appdataPathUserpreferences));
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
            if (sb.toString().isEmpty()) {
                return null;
            } else {
                return objectmapper.readValue(sb.toString(), UserPreferences.class);
            }
        } catch (FileNotFoundException e) {
            logger.debug("No save data, creating a folder as: " + System.getenv("APPDATA") + "/Chess)");
            File directory = new File(appdataFolder);
            directory.mkdirs();


        } catch (SecurityException e) {
            logger.error("No permission to create a file! Path: " + appdataPathUserpreferences);
        } catch (Exception e) {
            logger.error("Error reading user pref from AppData: " + e.getMessage());
        }
        return null;
    }

    public static void writeUserprefToSave(UserPreferences pref) {
        try {
            FileWriter writer = new FileWriter(appdataPathUserpreferences, false);
            writer.write(objectmapper.writeValueAsString(pref));
            writer.close();
        } catch (FileNotFoundException e) {
            logger.debug("No save data, creating a folder as: " + System.getenv("APPDATA") + "/Chess)");
            File directory = new File(appdataFolder);
            directory.mkdirs();


        } catch (SecurityException e) {
            logger.error("No permission to create a file! Path: " + appdataPathUserpreferences);
        } catch (Exception e) {
            logger.error("Error writing user info to AppData: " + e.getMessage());
        }
    }

    public static FrontendClient readUserInfoFromAppData() {
        SecretKey key = KeyManager.tryLoadSecretKey();
        if (key == null) {
            logger.error("Secret key could not be loaded.");
            return null;
        }

        String info = readFromEncrypted(key, appdataPathUserPass);
        if (info != null) {
            try {
                return new FrontendClient(objectmapper.readValue(info, UserInfo.class));
            } catch (Exception e) {
                logger.error("Error mapping user info to FrontendClient object", e);
            }
        }
        return null;
    }

    public static void writeUserToAppData(UserInfo info) {
        SecretKey key = KeyManager.tryLoadSecretKey();
        if (key == null) {
            logger.error("Secret key could not be loaded.");
            return;
        }

        try {
            encryptToFile(objectmapper.writeValueAsString(info), key, appdataPathUserPass);
        } catch (Exception e) {
            logger.error("Error writing user info to AppData", e);
        }
    }

    private static void encryptToFile(String data, SecretKey secretKey, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            logger.debug("Created new file for encryption: " + filePath);
        }

        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            try (FileOutputStream fos = new FileOutputStream(file);
                 CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {
                cos.write(data.getBytes());
            } catch (IOException e) {
                logger.error("IO error during file encryption at: " + filePath, e);
            }
        } catch (Exception e) {
            logger.error("Encryption error in encryptToFile", e);
        }
    }

    private static String readFromEncrypted(SecretKey secretKey, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            logger.debug("No encrypted file found at: " + filePath);
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            try (FileInputStream fis = new FileInputStream(file);
                 CipherInputStream cis = new CipherInputStream(fis, cipher)) {
                return new String(cis.readAllBytes());
            } catch (IOException e) {
                logger.error("IO error during file decryption at: " + filePath, e);
            }
        } catch (Exception e) {
            logger.error("Decryption error in readFromEncrypted", e);
        }
        return null;
    }



    // game save related

    public static List<ChessGame> readGamesFromAppData() {
        List<ChessGame> games = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(appdataPathGameSaves));
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the line contains the data to remove
                games.add(CryptoUtils.gameFromSaveString(line));
            }
            reader.close();
        } catch (FileNotFoundException e) {
            logger.debug("No save data, creating a folder as: " + System.getenv("APPDATA") + "/Chess)");
            File directory = new File(appdataFolder);
            directory.mkdirs();


        } catch (SecurityException e) {
            logger.error("No permission to create a file! Path: " + appdataPathGameSaves);
        } catch (Exception e) {
            logger.error("Error reading game from AppData: " + e.getMessage());
            e.printStackTrace();
        }
        return games;

    }

    public static void removeGameFromData(String gameHash) {
        try {
            // read in all games that are not the one we want to remove
            BufferedReader reader = new BufferedReader(new FileReader(appdataPathGameSaves));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the line contains the data to remove
                String hash = line.split(",")[0];
                if (!hash.trim().equals(gameHash)) {
                    // Append the line to the StringBuilder if it doesn't contain the data to remove
                    stringBuilder.append(line).append("\n");
                } else {
                    logger.debug("Removing line" + line);

                }
            }
            reader.close();

            // then put all the games we want back in the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(appdataPathGameSaves, false));
            writer.write(stringBuilder.toString());
            writer.close();

        } catch (Exception e) {
            logger.error("Error removing game from AppData: " + e.getMessage());
        }
    }

    public static void writeToAppData(List<ChessGame> content, boolean isAppend) {

        try {
            // Open the file in append mode
            BufferedWriter writer = new BufferedWriter(new FileWriter(appdataPathGameSaves, isAppend));
            for (ChessGame game : content) {
                writer.write(CryptoUtils.chessGameToSaveString(game));
            }
            // Write content to the file

            // Close the writer
            writer.close();

            logger.debug("Content written to the file successfully. Location: " + appdataPathGameSaves);
        } catch (IOException e) {
            logger.error("Error removing game from AppData: " + e.getMessage());
        }
    }

    public static void appendGameToAppData(ChessGame game) {
        try {
            // Open the file in append mode
            BufferedWriter writer = new BufferedWriter(new FileWriter(appdataPathGameSaves, true));
            writer.write(CryptoUtils.chessGameToSaveString(game) + "\n");
            // Write content to the file

            // Close the writer
            writer.close();

            logger.debug("Content appended to the file successfully. Location: " + appdataPathGameSaves);
        } catch (IOException e) {
            logger.error("Error appending game to AppData: " + e.getMessage());
        }
    }
}
