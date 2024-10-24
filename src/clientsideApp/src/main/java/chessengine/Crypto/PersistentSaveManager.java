package chessengine.Crypto;

import chessengine.ChessRepresentations.ChessGame;
import chessengine.Misc.ChessConstants;
import chessserver.FrontendClient;
import chessserver.UserInfo;
import chessserver.UserPreferences;
import org.nd4j.shade.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class PersistentSaveManager {
    private final static String appdataPathGameSaves = System.getenv("APPDATA") + "/Chess/save.txt";
    private final static String appdataPathUserpreferences = System.getenv("APPDATA") + "/Chess/userPreferences.txt";
    private final static String appdataPathUserPass = System.getenv("APPDATA") + "/Chess/q378hf8f98qf.txt";
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
            ChessConstants.mainLogger.debug("No save data, creating a folder as: " + System.getenv("APPDATA") + "/Chess)");
            File directory = new File(appdataFolder);
            directory.mkdirs();


        } catch (SecurityException e) {
            ChessConstants.mainLogger.error("No permission to create a file! Path: " + appdataPathUserpreferences);
        } catch (Exception e) {
            ChessConstants.mainLogger.error("Error reading user pref from AppData: " + e.getMessage());
        }
        return null;
    }

    public static void writeUserprefToSave(UserPreferences pref) {
        try {
            FileWriter writer = new FileWriter(appdataPathUserpreferences, false);
            writer.write(objectmapper.writeValueAsString(pref));
            writer.close();
        } catch (FileNotFoundException e) {
            ChessConstants.mainLogger.debug("No save data, creating a folder as: " + System.getenv("APPDATA") + "/Chess)");
            File directory = new File(appdataFolder);
            directory.mkdirs();


        } catch (SecurityException e) {
            ChessConstants.mainLogger.error("No permission to create a file! Path: " + appdataPathUserpreferences);
        } catch (Exception e) {
            ChessConstants.mainLogger.error("Error writing user info to AppData: " + e.getMessage());
        }
    }

    public static FrontendClient readUserInfoFromAppData() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(appdataPathUserPass));
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
            if (sb.toString().isEmpty()) {
                return null;
            } else {
                UserInfo info = objectmapper.readValue(sb.toString(), UserInfo.class);
                return new FrontendClient(info);
            }

        } catch (FileNotFoundException e) {
            ChessConstants.mainLogger.debug("No save data, creating a folder as: " + System.getenv("APPDATA") + "/Chess)");
            File directory = new File(appdataFolder);
            directory.mkdirs();


        } catch (SecurityException e) {
            ChessConstants.mainLogger.error("No permission to create a file! Path: " + appdataPathUserPass);
        } catch (Exception e) {
            ChessConstants.mainLogger.error("Error reading user info from AppData: " + e.getMessage());
        }
        return null;
    }

    public static void writeUserToAppData(UserInfo info) {
        try {
            FileWriter writer = new FileWriter(appdataPathUserPass, false);
            writer.write(objectmapper.writeValueAsString(info));
            writer.close();
        } catch (FileNotFoundException e) {
            ChessConstants.mainLogger.debug("No save data, creating a folder as: " + System.getenv("APPDATA") + "/Chess)");
            File directory = new File(appdataFolder);
            directory.mkdirs();


        } catch (SecurityException e) {
            ChessConstants.mainLogger.error("No permission to create a file! Path: " + appdataPathUserPass);
        } catch (Exception e) {
            ChessConstants.mainLogger.error("Error writing user pref to AppData: " + e.getMessage());
        }
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
            ChessConstants.mainLogger.debug("No save data, creating a folder as: " + System.getenv("APPDATA") + "/Chess)");
            File directory = new File(appdataFolder);
            directory.mkdirs();


        } catch (SecurityException e) {
            ChessConstants.mainLogger.error("No permission to create a file! Path: " + appdataPathGameSaves);
        } catch (Exception e) {
            ChessConstants.mainLogger.error("Error reading game from AppData: " + e.getMessage());
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
                    ChessConstants.mainLogger.debug("Removing line" + line);

                }
            }
            reader.close();

            // then put all the games we want back in the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(appdataPathGameSaves, false));
            writer.write(stringBuilder.toString());
            writer.close();

        } catch (Exception e) {
            ChessConstants.mainLogger.error("Error removing game from AppData: " + e.getMessage());
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

            ChessConstants.mainLogger.debug("Content written to the file successfully. Location: " + appdataPathGameSaves);
        } catch (IOException e) {
            ChessConstants.mainLogger.error("Error removing game from AppData: " + e.getMessage());
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

            ChessConstants.mainLogger.debug("Content appended to the file successfully. Location: " + appdataPathGameSaves);
        } catch (IOException e) {
            ChessConstants.mainLogger.error("Error appending game to AppData: " + e.getMessage());
        }
    }
}
