package chessserver.Net;

import java.util.function.Consumer;

public class ResponseHandler <T extends Payload> {
    private final Consumer<T> onResponse;

    public ResponseHandler(Consumer<T> onResponse) {
        this.onResponse = onResponse;

    }

    public void handleResponse(T response) {
        onResponse.accept(response);
    }
}

