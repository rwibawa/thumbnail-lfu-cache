package com.hackerrank.mediacache.model;

import java.util.Objects;

public final class ThumbnailCacheMetrics {

    private final long hits;
    private final long misses;
    private final long evictions;

    public ThumbnailCacheMetrics(long hits, long misses, long evictions) {
        if (hits < 0 || misses < 0 || evictions < 0) {
            throw new IllegalArgumentException("counters must be non-negative");
        }
        this.hits = hits;
        this.misses = misses;
        this.evictions = evictions;
    }

    public long getHits() {
        return hits;
    }

    public long getMisses() {
        return misses;
    }

    public long getEvictions() {
        return evictions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ThumbnailCacheMetrics that = (ThumbnailCacheMetrics) o;
        return hits == that.hits && misses == that.misses && evictions == that.evictions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hits, misses, evictions);
    }

    @Override
    public String toString() {
        return "ThumbnailCacheMetrics{hits=" + hits + ", misses=" + misses + ", evictions=" + evictions + "}";
    }
}