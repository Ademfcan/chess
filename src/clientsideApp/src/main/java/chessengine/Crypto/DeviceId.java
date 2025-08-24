package chessengine.Crypto;

import chessengine.App;
import chessserver.Misc.SavePath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class DeviceId {
    private static final Path DEVICE_ID_PATH = SavePath.getSavePath(".conf", "device.id");

    /**
     * The unique device ID for this client, persisted in a file
     * If the file does not exist, it will be created with a new UUID. Always will be the same ID for the same device.
     */
    public static final String DEVICE_ID = getDeviceId();

    private static String getDeviceId() {
        try {
            if (Files.exists(DEVICE_ID_PATH)) {
                return Files.readString(DEVICE_ID_PATH).trim();
            }

            String id = UUID.randomUUID().toString();
            Files.createDirectories(DEVICE_ID_PATH.getParent());
            Files.writeString(DEVICE_ID_PATH, id, StandardOpenOption.CREATE_NEW);
            return id;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load or generate device ID", e);
        }
    }
}
