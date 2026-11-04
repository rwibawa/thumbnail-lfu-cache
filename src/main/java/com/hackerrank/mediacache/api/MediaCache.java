package com.hackerrank.mediacache.api;

import com.hackerrank.mediacache.model.ThumbnailCacheMetrics;

public interface MediaCache<K, V> {

    V get(K assetId);

    void put(K assetId, V payload);

    ThumbnailCacheMetrics getMetrics();
}