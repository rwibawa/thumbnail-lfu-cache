package com.hackerrank.mediacache;

import java.util.HashMap;
import java.util.Map;

import com.hackerrank.mediacache.api.MediaCache;
import com.hackerrank.mediacache.internal.DoublyLinkedList;
import com.hackerrank.mediacache.internal.DoublyLinkedList.Node;
import com.hackerrank.mediacache.model.ThumbnailCacheMetrics;

public class ThumbnailCache<K, V> implements MediaCache<K, V> {

    private final DoublyLinkedList<K, V> queue = new DoublyLinkedList<>();
    private final Map<K, Node<K, V>> entries = new HashMap<>();
    private final int maxSlots;
    private long numberOfHits;
    private long numberOfMisses;
    private long numberOfEvictions;

    public ThumbnailCache(int maxSlots) {
        if (maxSlots < 1) {
            throw new IllegalArgumentException("Invalid Cache capacity... It cannot be less than 1");
        }
        this.maxSlots = maxSlots;
    }

    @Override
    public V get(K assetId) {
        if (assetId == null) {
            throw new NullPointerException("Asset ID cannot be null");
        }

        Node<K, V> node = entries.get(assetId);
        if (node == null) {
            numberOfMisses++;
            return null;
        }

        queue.moveToMru(node);
        numberOfHits++;
        return node.value;
    }

    @Override
    public void put(K assetId, V payload) {
        if (assetId == null || payload == null) {
            throw new NullPointerException("Asset ID/Payload cannot be null");
        }

        Node<K, V> existingNode = entries.get(assetId);
        if (existingNode != null) {
            existingNode.value = payload;
            queue.moveToMru(existingNode);
            return;
        }

        if (entries.size() == maxSlots) {
            Node<K, V> evictedNode = queue.evictLru();
            entries.remove(evictedNode.key);
            numberOfEvictions++;
        }

        Node<K, V> newNode = queue.newNode(assetId, payload);
        queue.linkAsMru(newNode);
        entries.put(assetId, newNode);
    }

    @Override
    public ThumbnailCacheMetrics getMetrics() {
        return new ThumbnailCacheMetrics(numberOfHits, numberOfMisses, numberOfEvictions);
    }
}