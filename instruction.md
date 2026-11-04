# Java: Thumbnail LRU Cache
## Task:
Create a simple in-memory thumbnail cache using Java. It should store data (payload) against an asset id. When the cache is full, it should remove the item that hasn’t been used for the longest time.
 
## Rules
* **CAPACITY**: The cache can hold only up to maxSlots items. If maxSlots is less than 1, it’s not valid and should throw an IllegalArgumentException.
* **GET**: get(assetId) should return the data if the asset id exists. If it doesn’t exist, return null. If the item is found, mark it as recently used. If not found, don’t change anything. If assetId is null, throw a NullPointerException.
* **PUT**: put(assetId, payload) should add a new item or update an existing one. If the item already exists, update its data and mark it as recently used. If it’s a new item and the cache is full, remove the least recently used item first, then add the new one. If assetId or payload is null, throw a NullPointerException.
* **METRICS**: Track three counters since cache creation:
  * hits: when a get call successfully finds the item
  * misses: when a get call does not find the item
  * evictions: each time an old item is removed to make space for a new one
  * Invalid operations should not change these numbers.
  * If multiple operations happen, update usage order and metrics exactly in the same order as the operations occur.
  * The first valid operation begins with an empty cache, having no entries and all metrics set to zero.

Navigate through the existing codebase and complete the cache behavior in ThumbnailCache. You may also update any required supporting classes under src/main/java to ensure everything works correctly and all tests pass.
 
## Performance Expectations:
- Maintain bounded memory based on `maxSlots.`
- Keep recency updates efficient
 
## Note:
Use `README.md` for technical details, package-level guidance, and exact test commands.
```java
package com.hackerrank.mediacache;

import com.hackerrank.mediacache.api.MediaCache;
import com.hackerrank.mediacache.factory.ThumbnailCacheFactories;

public class Main {

public static void main(String[] args) {
        MediaCache<Integer, String> cache = ThumbnailCacheFactories.newThumbnailCache(2);
        cache.put(101, "thumb-101-bytes");
        cache.put(202, "thumb-202-bytes");
        System.out.println(cache.get(101));
        cache.put(303, "thumb-303-bytes");
        System.out.println(cache.get(202));
        System.out.println(cache.get(303));
        System.out.println(cache.getMetrics());
    }
}
```