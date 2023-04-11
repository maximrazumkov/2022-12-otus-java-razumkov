package ru.otus.cache.impl;

import java.util.HashSet;
import java.util.WeakHashMap;
import ru.otus.cache.HwCache;

public class MyCacheFactory {
    private MyCacheFactory() {}

    public static <K, V> HwCache<K, V> create() {
        return new MyCache<>(new HashSet<>(), new WeakHashMap<>());
    }
}
