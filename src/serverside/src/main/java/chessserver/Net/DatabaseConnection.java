package chessserver.Net;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DatabaseConnection {
    public final static HikariDataSource dataSource;

    static {
        dataSource = initDbConnection();
        Runtime.getRuntime().addShutdownHook(new Thread(dataSource::close));
    }

    private static HikariDataSource initDbConnection(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/chessDB");
        config.setUsername("root");
        config.setPassword("chess");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("autoReconnect", "true");
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(10));
        config.setIdleTimeout(TimeUnit.SECONDS.toMillis(30));
        config.setMaxLifetime(TimeUnit.HOURS.toMillis(1));
        config.setMaximumPoolSize(30);
        config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(10)); // logs if a connection is held for >10 seconds

        return new HikariDataSource(config);
    }

    @FunctionalInterface
    public interface SqlErroringConsumer<T> {
        void accept(T t) throws SQLException;
    }

    @FunctionalInterface
    public interface SqlErroringFunction<T, R> {
        R apply(T t) throws SQLException;
    }

    public static int executeUpdate(String query, Object... queryArgs) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement stmt = conn.prepareStatement(query);
            for (int i = 0; i < queryArgs.length; i++) {
                stmt.setObject(i + 1, queryArgs[i]);
            }

            int ret = stmt.executeUpdate();

            stmt.close();
            return ret;
        }
    }

    public static void executeQuery(String query, SqlErroringConsumer<ResultSet> onResult, Object... queryArgs) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            for (int i = 0; i < queryArgs.length; i++) {
                stmt.setObject(i + 1, queryArgs[i]);
            }

            ResultSet rs = stmt.executeQuery();
            onResult.accept(rs);
            rs.close();
            stmt.close();
        }
    }

    public static <R> R executeQueryReturn(String query, SqlErroringFunction<ResultSet, R> processor, Object... args) throws SQLException{
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 0; i < args.length; i++) {
                stmt.setObject(i + 1, args[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                return processor.apply(rs);
            }
        }
    }

    public static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());  // 8 bytes
        buffer.putLong(uuid.getLeastSignificantBits()); // 8 bytes
        return buffer.array(); // total 16 bytes
    }

    public static UUID bytesToUUID(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long mostSigBits = buffer.getLong();
        long leastSigBits = buffer.getLong();
        return new UUID(mostSigBits, leastSigBits);
    }

}
