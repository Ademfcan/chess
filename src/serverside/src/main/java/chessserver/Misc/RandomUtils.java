package chessserver.Misc;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
    public static <T> T getRandomElement(List<T> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    public static  <T> T getRandomElement(T[] arr) {
        return arr[ThreadLocalRandom.current().nextInt(arr.length)];
    }
}
