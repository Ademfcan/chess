package chessengine;

import chessserver.FrontendClient;
import chessserver.UserInfo;
import chessserver.UserPreferences;
import org.nd4j.shade.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PersistentSaveManager {
    private final static String appdataPathGameSaves = System.getenv("APPDATA") + "/Chess/save.txt";
    private final static String appdataPathUserpreferences = System.getenv("APPDATA") + "/Chess/userPreferences.txt";
    private final static String appdataPathUserPass = System.getenv("APPDATA") + "/Chess/q378hf8f98qf.txt";
    private final static String appdataFolder = System.getenv("APPDATA") + "/Chess";

    private final static ObjectMapper objectmapper = new ObjectMapper();
    public static UserPreferences readUserprefFromSave(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(appdataPathUserpreferences));
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while (line != null){
                sb.append(line);
                line = reader.readLine();
            }
            if(sb.toString().isEmpty()){
                return null;
            }
            else{
                return objectmapper.readValue(sb.toString(), UserPreferences.class);
            }
        }
        catch (FileNotFoundException e){
            ChessConstants.mainLogger.debug("No save data, creating a folder as: " + System.getenv("APPDATA") + "/Chess)");
            File directory = new File(appdataFolder);
            directory.mkdirs();


        }
        catch (SecurityException e){
            ChessConstants.mainLogger.error("No permission to create a file! Path: " + appdataPathUserpreferences);
        }
        catch (Exception e){
            ChessConstants.mainLogger.error("Error reading user pref from AppData: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void writeUserprefToSave(UserPreferences pref){
        try {
            FileWriter writer = new FileWriter(appdataPathUserpreferences,false);
            writer.write(objectmapper.writeValueAsString(pref));
            writer.close();
        }
        catch (FileNotFoundException e){
            ChessConstants.mainLogger.debug("No save data, creating a folder as: " + System.getenv("APPDATA") + "/Chess)");
            File directory = new File(appdataFolder);
            directory.mkdirs();


        }
        catch (SecurityException e){
            ChessConstants.mainLogger.error("No permission to create a file! Path: " + appdataPathUserpreferences);
        }
        catch (Exception e){
            ChessConstants.mainLogger.error("Error writing user info to AppData: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static FrontendClient readUserFromAppData(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(appdataPathUserPass));
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while (line != null){
                sb.append(line);
                line = reader.readLine();
            }
            if(sb.toString().isEmpty()){
                return null;
            }
            else{
                UserInfo info = objectmapper.readValue(sb.toString(), UserInfo.class);
                return new FrontendClient(info);
            }

        }
        catch (FileNotFoundException e){
            ChessConstants.mainLogger.debug("No save data, creating a folder as: " + System.getenv("APPDATA") + "/Chess)");
            File directory = new File(appdataFolder);
            directory.mkdirs();


        }
        catch (SecurityException e){
            ChessConstants.mainLogger.error("No permission to create a file! Path: " + appdataPathUserPass);
        }
        catch (Exception e){
            ChessConstants.mainLogger.error("Error reading user info from AppData: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void writeUserToAppData(UserInfo info){
        try {
            FileWriter writer = new FileWriter(appdataPathUserPass,false);
            writer.write(objectmapper.writeValueAsString(info));
            writer.close();
        }
        catch (FileNotFoundException e){
            ChessConstants.mainLogger.debug("No save data, creating a folder as: " + System.getenv("APPDATA") + "/Chess)");
            File directory = new File(appdataFolder);
            directory.mkdirs();


        }
        catch (SecurityException e){
            ChessConstants.mainLogger.error("No permission to create a file! Path: " + appdataPathUserPass);
        }
        catch (Exception e){
            ChessConstants.mainLogger.error("Error writing user pref to AppData: " + e.getMessage());
            e.printStackTrace();
        }
    }



    // game save related

    public static List<ChessGame> readFromAppData(){
        List<ChessGame> games = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(appdataPathGameSaves));
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the line contains the data to remove
                String[] split = line.split(",");
                Arrays.stream(split).forEach(s -> s = s.trim());
                // index 0 = hashcode 1 = name 2 = player1 name 3 = player2 name 4 = player1 elo 5 = player2 elo 6 = player1pfp, 7 = player2pfp, 8 = game pgn 9 = isvsComputer
                games.add(new ChessGame(split[8],split[1],split[2],split[3],Integer.parseInt(split[4]),Integer.parseInt(split[5]),split[6],split[7],Boolean.parseBoolean(split[9]),split[0]));
            }
            reader.close();
        }
        catch (FileNotFoundException e){
            ChessConstants.mainLogger.debug("No save data, creating a folder as: " + System.getenv("APPDATA") + "/Chess)");
            File directory = new File(appdataFolder);
            directory.mkdirs();


        }
        catch (SecurityException e){
            ChessConstants.mainLogger.error("No permission to create a file! Path: " + appdataPathGameSaves);
        }
        catch (Exception e){
            ChessConstants.mainLogger.error("Error reading game from AppData: " + e.getMessage());
            e.printStackTrace();
        }
        return games;

    }

    public static void removeGameFromData(String gameHash){
        try {
            // read in all games that are not the one we want to remove
            BufferedReader reader = new BufferedReader(new FileReader(appdataPathGameSaves));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the line contains the data to remove
                String hash = line.split(",")[0];
                System.out.println(hash);
                System.out.println(gameHash);
                if (!hash.trim().equals(gameHash)) {
                    // Append the line to the StringBuilder if it doesn't contain the data to remove
                    stringBuilder.append(line).append("\n");
                }
                else{
                    ChessConstants.mainLogger.debug("Removing line" + line);

                }
            }
            reader.close();

            // then put all the games we want back in the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(appdataPathGameSaves, false));
            writer.write(stringBuilder.toString());
            writer.close();

        }
        catch (Exception e){
            ChessConstants.mainLogger.error("Error removing game from AppData: " + e.getMessage());
        }
    }

    public static void writeToAppData( List<ChessGame> content,boolean isAppend){

        try {
            // Open the file in append mode
            BufferedWriter writer = new BufferedWriter(new FileWriter(appdataPathGameSaves, isAppend));
            for(ChessGame game : content){
                writer.write(game.hashCode() + "," + game.getGameName() + "," + game.getPlayer1name() + "," + game.getPlayer2name() + "," + game.getPlayer1Elo() + "," + game.getPlayer2Elo() + "," + game.getPlayer1PfpUrl() + "," + game.getPlayer2PfpUrl() + "," + game.gameToPgn() + "," + game.isVsComputer() + "\n");
            }
            // Write content to the file

            // Close the writer
            writer.close();

            ChessConstants.mainLogger.debug("Content written to the file successfully. Location: " + appdataPathGameSaves);
        } catch (IOException e) {
            ChessConstants.mainLogger.error("Error removing game from AppData: " + e.getMessage());
        }
    }

    public static void appendGameToAppData(ChessGame game){
        try {
            // Open the file in append mode
            BufferedWriter writer = new BufferedWriter(new FileWriter(appdataPathGameSaves, true));
            writer.write(game.hashCode() + "," + game.getGameName() + "," + game.getPlayer1name() + "," + game.getPlayer2name() + "," + game.getPlayer1Elo() + "," + game.getPlayer2Elo() + "," + game.getPlayer1PfpUrl() + "," + game.getPlayer2PfpUrl() + "," + game.gameToPgn() + "," + game.isVsComputer() + "\n");
            // Write content to the file

            // Close the writer
            writer.close();

            ChessConstants.mainLogger.debug("Content appended to the file successfully. Location: " + appdataPathGameSaves);
        } catch (IOException e) {
            ChessConstants.mainLogger.error("Error appending game to AppData: " + e.getMessage());
        }
    }
}
