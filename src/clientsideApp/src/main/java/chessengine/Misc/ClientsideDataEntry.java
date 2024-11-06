package chessengine.Misc;

import chessserver.DatabaseEntry;

public class ClientsideDataEntry {
    private boolean isCurrentlyOnline;
    private DatabaseEntry databaseEntry;

    public ClientsideDataEntry(boolean isCurrentlyOnline, DatabaseEntry databaseEntry) {
        this.isCurrentlyOnline = isCurrentlyOnline;
        this.databaseEntry = databaseEntry;
    }

    public boolean isCurrentlyOnline() {
        return isCurrentlyOnline;
    }

    public void setCurrentlyOnline(boolean currentlyOnline) {
        isCurrentlyOnline = currentlyOnline;
    }

    public DatabaseEntry getDatabaseEntry() {
        return databaseEntry;
    }

    public void setDatabaseEntry(DatabaseEntry databaseEntry) {
        this.databaseEntry = databaseEntry;
    }

}
