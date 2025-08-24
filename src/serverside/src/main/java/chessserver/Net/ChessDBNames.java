package chessserver.Net;

/**
 * Contains constants for table and column names used in the chess database schema.
 */
public class ChessDBNames {

    // --------------------
    // users table
    // --------------------

    /** Table name: users */
    public static final String usersTable = "users";

    /** Column: UUID (binary(16)) - Unique identifier for the user. */
    public static final String usersUUID = "UUID";

    /** Column: Name (varchar(64)) - Display name of the user. */
    public static final String usersName = "Name";

    /** Column: Email (varchar(255)) - Email address of the user. */
    public static final String usersEmail = "Email";

    /** Column: elo (int) - The player's ELO rating. */
    public static final String usersElo = "elo";

    /** Column: PasswordHash (char(60)) - Hashed password of the user. */
    public static final String usersPassword = "PasswordHash";

    /** Column: UserInfo (json) - Additional profile information for the user. */
    public static final String usersUserInfo = "UserInfo";

    /** Column: UserPreferences (json) - Preferences/settings associated with the user. */
    public static final String usersUserPreferences = "UserPreferences";

    /** Column: CreatedAt (datetime) - Timestamp when the user was created. */
    public static final String usersCreatedAt = "CreatedAt";

    // --------------------
    // friendships table
    // --------------------

    /** Table name: friendships */
    public static final String friendshipsTable = "friendships";

    /** Column: UserAUUID (binary(16)) - UUID of one user in the friendship (part of composite key). */
    public static final String friendAUUID = "UserAUUID";

    /** Column: UserBUUID (binary(16)) - UUID of the other user in the friendship (part of composite key). */
    public static final String friendBUUID = "UserBUUID";

    /** Column: FriendedAt (datetime) - Timestamp when the users became friends. */
    public static final String friendsAt = "FriendedAt";

    // --------------------
    // friend_requests table
    // --------------------

    /** Table name: friend_requests */
    public static final String friendRequestsTable = "friend_requests";

    /** Column: FromUUID (binary(16)) - UUID of the user who sent the friend request. */
    public static final String fromUUID = "FromUUID";

    /** Column: ToUUID (binary(16)) - UUID of the user who received the friend request. */
    public static final String toUUID = "ToUUID";

    /** Column: SentAt (datetime) - Timestamp when the friend request was sent. */
    public static final String sentAt = "SentAt";

    // --------------------
    // chess_games table
    // --------------------

    /** Table name: chess_games */
    public static final String chessGamesTable = "chess_games";

    /** Column: game_uuid (binary(16)) - Unique identifier for the chess game. */
    public static final String gameUUID = "game_uuid";

    /** Column: game_name (varchar(255)) - Optional name/label for the game. */
    public static final String gameName = "game_name";

    /** Column: pgn (text) - PGN-formatted representation of the game moves. */
    public static final String gamePGN = "pgn";

    /** Column: white_player (json) - JSON representation of the white player (user info). */
    public static final String whitePlayer = "white_player";

    /** Column: black_player (json) - JSON representation of the black player (user info). */
    public static final String blackPlayer = "black_player";

    /** Column: white_player_uuid (binary(16)) - UUID of the white player, nullable. */
    public static final String whitePlayerUUID = "white_player_uuid";

    /** Column: black_player_uuid (binary(16)) - UUID of the black player, nullable. */
    public static final String blackPlayerUUID = "black_player_uuid";

    /** Column: boolean (Tinyint(1)) flag representing whether the game is local (player vs bot) or online (player vs player) */
    public static final String localGame = "local_game";

    /** Column: when game put into database (Timestamp) */
    public static final String gameCreatedAt = "CreatedAt";


    // --------------------
    // refresh_tokens table
    // --------------------

    /** Table name: refresh_tokens */
    public static final String refreshTokensTable = "refresh_tokens";

    /** Column: token (char(36)) - Refresh token string (UUID format). */
    public static final String refreshToken = "token";

    /** Column: UUID (binary(16)) - UUID of the user this token is associated with. */
    public static final String refreshTokenUUID = "UUID";

    /** Column: expires_at (timestamp) - Expiration timestamp for the refresh token. */
    public static final String refreshTokenExpiresAt = "expires_at";

    /** Column: revoked (tinyint(1)) - Whether the token has been revoked (0 = false, 1 = true). */
    public static final String refreshTokenRevoked = "revoked";

    /** Column: created_at (timestamp) - When the refresh token was created. */
    public static final String refreshTokenCreatedAt = "created_at";

    /** Column: Device_ID (char(36)) - Identifier for the device associated with the refresh token. */
    public static final String refreshTokenDeviceID = "Device_ID";
}
