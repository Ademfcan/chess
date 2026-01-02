package chessserver.Net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;

public class MessageFlag {
    private static final Logger logger = LogManager.getLogger("MessageFlag");

    public static PayloadWFlag read(ByteBuffer buffer) {
        Flags flag = Flags.valueOf(buffer.get()); // first byte is the flag
        byte[] payload = new byte[buffer.remaining()];
        buffer.get(payload);

        return new PayloadWFlag(flag, payload);
    }

    public static ByteBuffer write(Flags flag, byte[] payload) {
        ByteBuffer buffer = ByteBuffer.allocate(payload.length + 1);
        buffer.put(flag.getFlag());
        buffer.put(payload);
        buffer.flip();
        return buffer;
    }

    public enum Flags{
        MESSAGE((byte) 1),
        STATUS((byte) 2);


        private final byte flag;

        Flags(byte flag) {
            this.flag = flag;
        }

        public byte getFlag() {
            return flag;
        }

        public static Flags valueOf(byte flag) {
            for (Flags f : Flags.values()) {
                if (f.getFlag() == flag) {
                    return f;
                }
            }
            logger.error("Unknown flag value: {}", flag);
            throw new IllegalArgumentException("Unknown flag value: " + flag);
        }
    }

    public record PayloadWFlag(Flags flag, byte[] payload) {}
}


