package chessengine.Net;

import chessengine.Crypto.DeviceId;
import chessengine.Crypto.TokenStore;
import chessserver.JWTUtil;
import chessserver.Net.Message;
import chessserver.Net.MessageConfig;
import chessserver.Net.MessageHandler;
import chessserver.Net.MessageTypes.UserMessageTypes;
import chessserver.Net.PayloadTypes.UserMessagePayloadTypes;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class JWTManager {
    private static final Logger logger = LogManager.getLogger(JWTManager.class);

    private final AtomicBoolean isRefreshingAccessToken = new AtomicBoolean(false);
    private final MessageHandler<?> sender;
    private final Runnable logoutEscalation;

    private final Object tokenLock = new Object(); // Lock object for synchronization
    private CompletableFuture<String> accessToken;

    public JWTManager(MessageHandler<?> sender, Runnable logoutEscalation) {
        this.sender = sender;
        this.logoutEscalation = logoutEscalation;
        this.accessToken = requestAccessToken(true);
    }

    public void escalateToLogout() {
        logoutEscalation.run();
    }

    public boolean isLoggedIn() {
        return TokenStore.getRefreshToken() != null;
    }

    public CompletableFuture<String> requestAccessToken(boolean forceRefresh) {
        if (!isLoggedIn()) {
            logger.warn("Not logged in, cannot request access token", new Throwable("Not logged in"));
            return CompletableFuture.completedFuture(null);
        }

        synchronized (tokenLock) {
            if (!forceRefresh && accessToken != null && accessToken.isDone()) {
                try {
                    String token = accessToken.get();
                    if (token != null && !JWTUtil.isExpired(token)) {
                        logger.debug("Returning existing valid access token");
                        return accessToken;
                    } else {
                        logger.debug("Access token expired or null, refreshing");
                    }
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error retrieving access token", e);
                    // Allow fallback to token refresh
                }
            }

            if (isRefreshingAccessToken.get()) {
                logger.debug("Already refreshing access token, returning existing future");
                return accessToken;
            }

            logger.debug("Starting token refresh process");
            isRefreshingAccessToken.set(true);
            accessToken = new CompletableFuture<>();

            if (isLoggedIn()) {
                sender.sendMessage(
                        new MessageConfig(
                                new Message(
                                        UserMessageTypes.ClientRequest.GETJWTTOKEN,
                                        new UserMessagePayloadTypes.RefreshAcessTokenPayload(
                                                TokenStore.getRefreshToken(),
                                                DeviceId.DEVICE_ID
                                        )
                                )).onDataResponse((UserMessagePayloadTypes.JWTAcessTokenPayload jwtToken) -> {
                                    synchronized (tokenLock) {
                                        if (jwtToken.JwtToken() != null) {
                                            accessToken.complete(jwtToken.JwtToken());
                                        } else {
                                            accessToken.complete(null); // So callers don't hang
                                            escalateToLogout();
                                        }
                                        isRefreshingAccessToken.set(false);
                                    }
                                }).onStatusResponse((status) -> {
                                    if(status.response().isErrorStatus()) {
                                        logger.info(status);
                                        accessToken.complete(null);
                                        isRefreshingAccessToken.set(false);
                                    }
                                })
                        );
            } else {
                logger.warn("Not logged in during token refresh");
                accessToken.complete(null);
                isRefreshingAccessToken.set(false);
            }
        }

            return accessToken;

    }
}
