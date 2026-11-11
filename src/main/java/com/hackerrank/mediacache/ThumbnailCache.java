package com.hackerrank.mediacache;

import java.util.HashMap;
import java.util.Map;

import com.hackerrank.mediacache.api.MediaCache;
import com.hackerrank.mediacache.internal.DoublyLinkedList;
import com.hackerrank.mediacache.internal.DoublyLinkedList.Node;
import com.hackerrank.mediacache.model.ThumbnailCacheMetrics;

public class ThumbnailCache<K, V> implements MediaCache<K, V> {

    private final Map<K, Node<K, V>> entries = new HashMap<>();
    private final Map<Integer, DoublyLinkedList<K, V>> frequencyBuckets = new HashMap<>();
    private final int maxSlots;
    private int minFrequency;
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

        increaseFrequency(node);
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
            increaseFrequency(existingNode);
            return;
        }

        if (entries.size() == maxSlots) {
            DoublyLinkedList<K, V> leastFrequentBucket = frequencyBuckets.get(minFrequency);
            Node<K, V> evictedNode = leastFrequentBucket.evictLru();
            if (leastFrequentBucket.isEmpty()) {
                frequencyBuckets.remove(minFrequency);
            }
            entries.remove(evictedNode.key);
            numberOfEvictions++;
        }

        DoublyLinkedList<K, V> newBucket = frequencyBuckets.computeIfAbsent(
                1, frequency -> new DoublyLinkedList<>());
        Node<K, V> newNode = newBucket.newNode(assetId, payload);
        newBucket.linkAsMru(newNode);
        entries.put(assetId, newNode);
        minFrequency = 1;
    }

    private void increaseFrequency(Node<K, V> node) {
        int oldFrequency = node.frequency;
        DoublyLinkedList<K, V> oldBucket = frequencyBuckets.get(oldFrequency);
        oldBucket.unlink(node);
        if (oldBucket.isEmpty()) {
            frequencyBuckets.remove(oldFrequency);
            if (oldFrequency == minFrequency) {
                minFrequency++;
            }
        }

        node.frequency++;
        frequencyBuckets.computeIfAbsent(node.frequency, frequency -> new DoublyLinkedList<>())
                .linkAsMru(node);
    }

    @Override
    public ThumbnailCacheMetrics getMetrics() {
        return new ThumbnailCacheMetrics(numberOfHits, numberOfMisses, numberOfEvictions);
    }
}