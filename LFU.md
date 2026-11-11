Implementing a Least Frequently Used (LFU) cache with a standard Singly Linked List degrades performance to O(N). Unlike LRU, which only tracks recency, LFU must maintain items sorted by their actual access frequency counts.
However, you can achieve perfect O(1) time complexity for LFU by using a combination of HashMaps and a Singly Linked List of Doubly Linked Lists (known as the An-Hwang-Kuo algorithm), or by leveraging Java's built-in collections.
Here is how LFU caches work, why linear lists fail them, and the correct data structures to use.

Why a Standard Singly or Doubly Linked List Fails LFU

If you maintain a single flat list of items ordered by frequency:
1. When an item's frequency increases from 5 to 6, it can no longer stay where it is.
2. It must be moved ahead of all items that still have a frequency of 5.
3. To find its new correct position, you must traverse the list linearly to find where the "frequency 5" block ends. This requires O(N) time, regardless of whether the list is singly or doubly linked.

The Standard O(1) LFU Architecture

To achieve O(1) performance, an LFU cache splits the tracking into two dimensions: Frequency and Recency (to break ties when two items have the same frequency).
The architecture relies on two HashMaps and a collection of Doubly Linked Lists:
```
[Frequency Map]
  Freq 1  -------->  [ Doubly Linked List: Node A <-> Node B ]  (Tie-breaker: LRU order)
  Freq 2  -------->  [ Doubly Linked List: Node C ]
  Freq 5  -------->  [ Doubly Linked List: Node D <-> Node E ]

[Cache Map]
  Key "A" -------->  Pointer to Node A
  Key "B" -------->  Pointer to Node B
```

How the O(1) Mechanism Works:

• The Cache Map: Maps the key directly to its Node for instant O(1) retrieval.
• The Frequency Map: Maps a specific frequency number (e.g., 1, 2, 5) to a dedicated Doubly Linked List containing all nodes with that exact frequency.
• When an item is accessed: You look it up in O(1). You remove it from its current frequency list in O(1) (which requires a Doubly Linked List so it can decouple itself instantly). You then increment its frequency counter and append it to the tail of the next frequency list in O(1).
• When evicting: You look up the minFrequency list, and remove the oldest node from the head of that specific list in O(1) time.

Complete Java Implementation: O(1) LFU Cache

This implementation utilizes a customized internal doubly linked list group combined with LinkedHashSet (which acts as an O(1) doubly linked list bucket underneath) to keep the code clean and production-ready.
```
java
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class LFUCache {

    public static void main(String[] args) {
        LFUCache cache = new LFUCache(3);
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        
        System.out.println(cache.get(1)); // 1
        System.out.println(cache.get(1)); // 1
        System.out.println(cache.get(3)); // 3
        // System.out.println(cache.get(2));
        System.out.println();

        cache.put(4, 4);
        System.out.println(cache.get(1)); // 1
        System.out.println(cache.get(2)); // -1
        System.out.println(cache.get(3)); // 3
        System.out.println(cache.get(4)); // 4
        System.out.println();
    }

    
    // Internal class representing cached data
    private static class CacheNode {
        int key;
        int value;
        int frequency;

        CacheNode(int key, int value) {
            this.key = key;
            this.value = value;
            this.frequency = 1; // Initial frequency is always 1
        }
    }

    private final int capacity;
    private int minFrequency;
    
    // Quick lookup for cache nodes
    private final Map<Integer, CacheNode> cacheMap;
    
    // Maps a frequency count to an ordered set of nodes sharing that frequency
    // LinkedHashSet acts as a doubly linked list providing O(1) insertion/deletion
    private final Map<Integer, LinkedHashSet<CacheNode>> frequencyMap;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.minFrequency = 0;
        this.cacheMap = new HashMap<>();
        this.frequencyMap = new HashMap<>();
    }

    public int get(int key) {
        CacheNode node = cacheMap.get(key);
        if (node == null || capacity == 0) {
            return -1;
        }
        updateFrequency(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (capacity <= 0) return;

        // If key already exists, update value and bump frequency
        if (cacheMap.containsKey(key)) {
            CacheNode node = cacheMap.get(key);
            node.value = value;
            updateFrequency(node);
            return;
        }

        // If cache is full, evict the least frequently used item
        if (cacheMap.size() >= capacity) {
            evictLFUItem();
        }

        // Add brand new node
        CacheNode newNode = new CacheNode(key, value);
        cacheMap.put(key, newNode);
        
        // New nodes always start at frequency 1
        minFrequency = 1; 
        frequencyMap.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(newNode);
    }

    private void updateFrequency(CacheNode node) {
        int oldFreq = node.frequency;
        int newFreq = oldFreq + 1;
        node.frequency = newFreq;

        // Remove node from its current frequency tier list
        LinkedHashSet<CacheNode> oldList = frequencyMap.get(oldFreq);
        oldList.remove(node);

        // If the current minFrequency list becomes empty, update global minFrequency
        if (oldFreq == minFrequency && oldList.isEmpty()) {
            minFrequency++;
        }

        // Move node into its upgraded frequency tier list
        frequencyMap.computeIfAbsent(newFreq, k -> new LinkedHashSet<>()).add(node);
    }

    private void evictLFUItem() {
        // Retrieve the bucket of nodes with the absolute lowest frequency
        LinkedHashSet<CacheNode> minFreqList = frequencyMap.get(minFrequency);
        
        // The first item in the LinkedHashSet is the oldest (LRU tie-breaker)
        CacheNode deadNode = minFreqList.iterator().next();
        
        // Remove from both tracking structures
        minFreqList.remove(deadNode);
        cacheMap.remove(deadNode.key);
    }
}
```
Use code with caution.

Quick Summary of Cache Differences

• LRU (Least Recently Used): Cares about time. Moves items to the head on access. Easily built using a single HashMap + single Doubly Linked List in O(1).
• LFU (Least Frequently Used): Cares about popularity count. Requires grouping nodes by frequency tiers. Built using two HashMaps + a cluster of Doubly Linked Lists to maintain O(1) execution.
Would you like to explore how to add time-to-live (TTL) expiration to this LFU cache, or are you preparing for a system design discussion where you need to analyze cache stampede or eviction strategies? Let me know where to take it next!
