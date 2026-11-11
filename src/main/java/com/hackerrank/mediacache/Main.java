package com.hackerrank.mediacache;

import com.hackerrank.mediacache.api.MediaCache;
import com.hackerrank.mediacache.factory.ThumbnailCacheFactories;

public class Main {

    public static void main(String[] args) {
        MediaCache<Integer, String> cache = ThumbnailCacheFactories.newThumbnailCache(3);
        cache.put(101, "thumb-101-bytes");
        cache.put(202, "thumb-202-bytes");
        cache.put(303, "thumb-303-bytes");
        cache.get(101);
        cache.get(101);
        cache.get(303);
        cache.put(404, "thumb-404-bytes");
        System.out.println(cache.get(101));
        System.out.println(cache.get(202));
        System.out.println(cache.get(303));
        System.out.println(cache.getMetrics());
    }
}