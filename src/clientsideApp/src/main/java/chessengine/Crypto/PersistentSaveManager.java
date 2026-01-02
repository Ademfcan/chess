package chessengine.Crypto;

import chessengine.App;
import chessserver.ChessRepresentations.GameInfo;
import chessserver.Misc.ChessConstants;
import chessserver.Misc.SavePath;
import chessserver.User.UserInfo;
import chessserver.User.UserPreferences;
import chessserver.User.UserWGames;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.concurrent.locks.ReentrantReadWriteLock;



public class PersistentSaveManager {
    private final static Logger logger = LogManager.getLogger("Persistent_Save_Manager");
    private final static ObjectMapper objectmapper = new ObjectMapper();


    private final static Path gameSavesS = SavePath.getSavePath("data", "ChessGamesS.json");
    private final static Path gameSavesU = SavePath.getSavePath("data", "ChessGamesU.json");
    private final static Path userInfo = SavePath.getSavePath("data", "UserInfo.json");
    private final static Path userPreferences = SavePath.getSavePath("data", "UserPref.json");

    public static FileTracker<UserPreferences> userPreferenceTracker = new FileTracker<>(userPreferences.toFile(), 5, UserPreferences.getDefaultPreferences(), new TypeReference<UserPreferences>() {});
    public static FileTracker<UserInfo> userInfoTracker = new FileTracker<>(userInfo.toFile(), 5, ChessConstants.defaultClient.info(), new TypeReference<UserInfo>() {});
    public static FileTracker<List<GameInfo>> gameTracker = new FileTracker<>(gameSavesS.toFile(), 5, new ArrayList<>(), new TypeReference<List<GameInfo>>() {});
    public static FileTracker<List<GameInfo>> unsavedGameTracker = new FileTracker<>(gameSavesU.toFile(), 5, new ArrayList<>(), new TypeReference<List<GameInfo>>() {});

    public static List<GameInfo> getAllGames(){
        return Stream.concat(gameTracker.getTracked().stream(), unsavedGameTracker.getTracked().stream()).collect(Collectors.toList());
    }

    public static void removeGameFromData(UUID gameID) {
        List<GameInfo> savedGames = gameTracker.getTracked();
        savedGames.removeIf(game -> game.gameUUID().equals(gameID));
    }

    public static void updateAll(UserWGames userWGames) {
        userPreferenceTracker.updateTracked(userWGames.user().preferences());
        userInfoTracker.updateTracked(userWGames.user().userInfo());
        gameTracker.updateTracked(userWGames.games());
        unsavedGameTracker.resetTracked(); // since all coming from server, none will be unsaved
    }



    public static class FileTracker<T> {
        private final TypeReference<T> tTypeReference;
        private final T defaultValue;
        private final File file;

        private T tracked;
        private @Nullable Consumer<T> onPeriodicUpdate;

        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        public FileTracker(File file, int updateTimeM, T defaultValue, TypeReference<T> tTypeReference) {
            this.file = file;
            this.defaultValue = defaultValue;
            this.tTypeReference = tTypeReference;

            try {
                Files.createDirectories(file.getParentFile().toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            T loaded = readValueFromSave(file);

            if(loaded == null){
                loaded = defaultValue;
            }

            this.tracked = loaded;

            System.out.println("Tracked: " + loaded);

            App.scheduledExecutorService.schedule(() -> {
                forceWrite(); // Now thread-safe
                forceUpdate(); // Safe access to tracked inside forceUpdate
            }, updateTimeM, TimeUnit.MINUTES);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                forceWrite();
                forceUpdate();
            }));
        }

        public void setOnPeriodicUpdate(@Nullable Consumer<T> onPeriodicUpdate) {
            this.onPeriodicUpdate = onPeriodicUpdate;
        }

        public T getTracked() {
            lock.readLock().lock();
            try {
                return tracked;
            } finally {
                lock.readLock().unlock();
            }
        }

        public void updateTracked(T newTracked) {
            lock.writeLock().lock();
            try {
                this.tracked = newTracked;
                System.out.println("New Tracked: " + newTracked);
            } finally {
                lock.writeLock().unlock();
            }
        }

        public void resetTracked() {
            lock.writeLock().lock();
            try {
                this.tracked = defaultValue;
            } finally {
                lock.writeLock().unlock();
            }
        }

        public void forceWrite() {
            lock.readLock().lock(); // Read-lock is enough if `tracked` itself is immutable or handled safely
            try {
                writeValueToSave(tracked, file);
            } finally {
                lock.readLock().unlock();
            }
        }

        public void forceUpdate() {
            lock.readLock().lock();
            try {
                if (onPeriodicUpdate != null) {
                    onPeriodicUpdate.accept(tracked);
                }
            } finally {
                lock.readLock().unlock();
            }
        }

        private void writeValueToSave(T pass, File file) {
            try {
                objectmapper.writeValue(file, pass);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (SecurityException e) {
                logger.error("No permission to create a file! Path: " + file);
            } catch (Exception e) {
                logger.error("Error writing to AppData: " + e.getMessage());
            }
        }

        private T readValueFromSave(File file) {
            try {
                return objectmapper.readValue(file, tTypeReference);
            } catch (FileNotFoundException e) {
                logger.debug("No save data");
            } catch (SecurityException e) {
                logger.error("No permission to read the file! Path: " + file, e);
            } catch (Exception e) {
                logger.error("Error reading from AppData: ", e);
            }
            return null;
        }
    }



}
