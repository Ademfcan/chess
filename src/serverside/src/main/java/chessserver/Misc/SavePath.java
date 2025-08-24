package chessserver.Misc;

import java.nio.file.Path;

public class SavePath {
    /**
     * Returns the path to the save directory for the application.
     * The path is constructed as:
     * <p>
     * <b>{user.home}/.chess-app/{subPaths...}</b
     *
     * @param subPaths Optional sub-paths to append to the save directory.
     * @return The Path object representing the save directory.
     */
    public static Path getSavePath(String... subPaths) {
        String userHome = System.getProperty("user.home");

        // Create a new array with ".chess" + subPaths
        String[] fullPaths = new String[subPaths.length + 1];
        fullPaths[0] = ".chess-app";
        System.arraycopy(subPaths, 0, fullPaths, 1, subPaths.length);

        return Path.of(userHome, fullPaths);
    }
}
