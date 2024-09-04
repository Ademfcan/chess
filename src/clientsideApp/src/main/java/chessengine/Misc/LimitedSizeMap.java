package chessengine.Misc;

import java.util.LinkedHashMap;
import java.util.Map;

public class LimitedSizeMap<K, V> extends LinkedHashMap<K, V> {
    private static final long serialVersionUID = 1L;
    private final int maxEntries;

    public LimitedSizeMap(int maxEntries) {
        super(maxEntries + 1, 1.0f, true); // Pass 'true' for access order
        this.maxEntries = maxEntries;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxEntries;
    }
}
