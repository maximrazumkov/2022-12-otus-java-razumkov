package ru.otus.cache.impl;

import static ru.otus.cache.impl.MyCache.CacheListenerAction.GET;
import static ru.otus.cache.impl.MyCache.CacheListenerAction.PUT;
import static ru.otus.cache.impl.MyCache.CacheListenerAction.REMOVE;

import java.util.Set;
import java.util.WeakHashMap;
import lombok.RequiredArgsConstructor;
import ru.otus.cache.HwCache;
import ru.otus.cache.HwListener;

@RequiredArgsConstructor
class MyCache<K, V> implements HwCache<K, V> {
//Надо реализовать эти методы

    private final Set<HwListener<K, V>> listenerSet;
    private final WeakHashMap<K, V> cache;

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        notifyListeners(key, value, PUT);
    }

    @Override
    public void remove(K key) {
        V value = cache.remove(key);
        notifyListeners(key, value, REMOVE);
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        notifyListeners(key, value, GET);
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listenerSet.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listenerSet.remove(listener);
    }

    private void notifyListeners(K key, V value, CacheListenerAction cacheListenerAction) {
        listenerSet.forEach(listener -> listener.notify(key, value, cacheListenerAction.name()));
    }

    enum CacheListenerAction {
        PUT,
        GET,
        REMOVE
    }
}
