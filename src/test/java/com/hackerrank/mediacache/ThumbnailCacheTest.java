package com.hackerrank.mediacache;

import junit.framework.TestCase;

import com.hackerrank.mediacache.model.ThumbnailCacheMetrics;

public class ThumbnailCacheTest extends TestCase {

    public void testRejectsNonPositiveCapacity() {
        assertIllegalArgument(() -> new ThumbnailCache<Integer, String>(0));
        assertIllegalArgument(() -> new ThumbnailCache<Integer, String>(-1));
    }

    public void testStartsEmptyAndCountsMisses() {
        ThumbnailCache<Integer, String> cache = new ThumbnailCache<>(2);

        assertMetrics(cache, 0, 0, 0);
        assertNull(cache.get(101));
        assertMetrics(cache, 0, 1, 0);
    }

    public void testGetCountsHitsAndReturnsPayload() {
        ThumbnailCache<Integer, String> cache = new ThumbnailCache<>(2);
        cache.put(101, "thumbnail-101");

        assertEquals("thumbnail-101", cache.get(101));
        assertNull(cache.get(202));
        assertMetrics(cache, 1, 1, 0);
    }

    public void testEvictsLeastFrequentlyUsedEntry() {
        ThumbnailCache<Integer, String> cache = new ThumbnailCache<>(2);
        cache.put(101, "thumbnail-101");
        cache.put(202, "thumbnail-202");
        cache.get(101);
        cache.get(101);
        cache.get(202);

        cache.put(303, "thumbnail-303");

        assertEquals("thumbnail-101", cache.get(101));
        assertNull(cache.get(202));
        assertEquals("thumbnail-303", cache.get(303));
        assertMetrics(cache, 5, 1, 1);
    }

    public void testEvictsLeastRecentlyUsedEntryWhenFrequenciesTie() {
        ThumbnailCache<Integer, String> cache = new ThumbnailCache<>(2);
        cache.put(101, "thumbnail-101");
        cache.put(202, "thumbnail-202");
        cache.get(101);
        cache.get(202);

        cache.put(303, "thumbnail-303");

        assertNull(cache.get(101));
        assertEquals("thumbnail-202", cache.get(202));
        assertEquals("thumbnail-303", cache.get(303));
        assertMetrics(cache, 4, 1, 1);
    }

    public void testUpdatingEntryChangesPayloadAndIncreasesFrequency() {
        ThumbnailCache<Integer, String> cache = new ThumbnailCache<>(2);
        cache.put(101, "old-thumbnail");
        cache.put(202, "old-thumbnail-202");
        cache.get(101);
        cache.put(202, "new-thumbnail-202");
        cache.put(303, "thumbnail-303");

        assertNull(cache.get(101));
        assertEquals("new-thumbnail-202", cache.get(202));
        assertEquals("thumbnail-303", cache.get(303));
        assertMetrics(cache, 3, 1, 1);
    }

    public void testCapacityOneEvictsPreviousEntry() {
        ThumbnailCache<Integer, String> cache = new ThumbnailCache<>(1);
        cache.put(101, "thumbnail-101");
        cache.put(202, "thumbnail-202");

        assertNull(cache.get(101));
        assertEquals("thumbnail-202", cache.get(202));
        assertMetrics(cache, 1, 1, 1);
    }

    public void testMissDoesNotChangeFrequencyOrEvictionOrder() {
        ThumbnailCache<Integer, String> cache = new ThumbnailCache<>(2);
        cache.put(101, "thumbnail-101");
        cache.put(202, "thumbnail-202");
        cache.get(101);
        cache.get(303);

        cache.put(303, "thumbnail-303");

        assertEquals("thumbnail-101", cache.get(101));
        assertNull(cache.get(202));
        assertMetrics(cache, 2, 2, 1);
    }

    public void testNullArgumentsThrowWithoutChangingMetrics() {
        ThumbnailCache<Integer, String> cache = new ThumbnailCache<>(2);
        cache.put(101, "thumbnail-101");
        ThumbnailCacheMetrics beforeInvalidCalls = cache.getMetrics();

        assertNullPointer(() -> cache.get(null));
        assertNullPointer(() -> cache.put(null, "thumbnail"));
        assertNullPointer(() -> cache.put(202, null));

        assertEquals(beforeInvalidCalls, cache.getMetrics());
    }

    private void assertMetrics(ThumbnailCache<Integer, String> cache,
            long hits, long misses, long evictions) {
        ThumbnailCacheMetrics metrics = cache.getMetrics();
        assertEquals(hits, metrics.getHits());
        assertEquals(misses, metrics.getMisses());
        assertEquals(evictions, metrics.getEvictions());
    }

    private void assertIllegalArgument(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        fail("Expected IllegalArgumentException");
    }

    private void assertNullPointer(Runnable action) {
        try {
            action.run();
        } catch (NullPointerException expected) {
            return;
        }
        fail("Expected NullPointerException");
    }
}
