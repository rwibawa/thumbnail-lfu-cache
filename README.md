# Thumbnail LFU Cache

Run all commands from this directory (project root).

## Task

Create a simple in-memory thumbnail cache using Java. It should store data (`payload`) against an `assetId`. When the cache is full, it should remove the item with the fewest accesses. If multiple items have the same frequency, remove the least recently used one among them.

## Rules

### CAPACITY

- The cache can hold only up to `maxSlots` items.
- If `maxSlots` is less than 1, construction is invalid and must throw `IllegalArgumentException`.

### GET

- `get(assetId)` returns payload if `assetId` exists; otherwise returns `null`.
- If found, increase the item's access frequency.
- If not found, frequency and recency order do not change.
- If `assetId` is `null`, throw `NullPointerException`.

### PUT

- `put(assetId, payload)` adds a new item or updates an existing item.
- If item exists, update payload and increase its access frequency.
- If it is a new item and cache is full, remove the least frequently used item first. Use least-recently-used order to break frequency ties.
- If `assetId` or `payload` is `null`, throw `NullPointerException`.

### METRICS

Track these counters since cache creation:

- `hits`: a `get` successfully finds an item.
- `misses`: a `get` does not find an item.
- `evictions`: an old item is removed to make space for a new item.

Invalid operations must not change metrics.

If multiple operations happen, update frequency, tie-breaking recency, and metrics in the same order as operations execute.

The first valid operation starts from an empty cache: no entries, metrics `(0, 0, 0)`.

## What You Should See In Terminal

### Running the application (demo)

Use the same command as in the IDE “Run” menu or:

```bash
mvn clean package -DskipTests && java -jar target/thumbnail-media-cache-1.0.jar
```

That runs `Main`, which builds a cache with `maxSlots = 2`, performs a few `put` / `get` calls, then prints metrics.

#### Current starter state (what you see now)

The starter code does not construct the cache yet, so the program exits immediately with an `AssertionError` and a stack trace similar to:

```
Exception in thread "main" java.lang.AssertionError: TODO: implement constructor
        at com.hackerrank.mediacache.ThumbnailCache.<init>(ThumbnailCache.java:9)
        at com.hackerrank.mediacache.factory.ThumbnailCacheFactories.newThumbnailCache(ThumbnailCacheFactories.java:12)
        at com.hackerrank.mediacache.Main.main(Main.java:9)
```

#### Expected after you complete the exercise

The demo calls `get(101)` twice and `get(202)` once before inserting `303`. When `ThumbnailCache` is correct, `202` is evicted even though it was accessed more recently, because its frequency is lower. You should see four lines of normal output:

```
thumb-101-bytes
null
thumb-303-bytes
ThumbnailCacheMetrics{hits=5, misses=1, evictions=1}
```

The final lookups hit `101` and `303` and miss on evicted `202`; the three setup lookups also count as hits.

### Running tests

With the starter code, `mvn clean test` reports all tests failing (for example `Tests run: 14, Failures: 14, Errors: 0`). After a correct solution you should see `Failures: 0`, `Errors: 0`, and `BUILD SUCCESS`.

## Performance Expectations

- Expected O(1) average time for `get` and `put`.
- Keep memory bounded by `maxSlots`.
- Keep frequency updates and recency-based tie-breaking efficient.
