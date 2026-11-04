package com.hackerrank.mediacache.factory;

import com.hackerrank.mediacache.ThumbnailCache;
import com.hackerrank.mediacache.api.MediaCache;

public class ThumbnailCacheFactories<K, V> {
  public static <K, V> MediaCache<K, V> newThumbnailCache(int i) {
      return new ThumbnailCache<>(i);
  }
}
