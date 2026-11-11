package com.hackerrank.mediacache.internal;

public final class DoublyLinkedList<K, V> {

    public static final class Node<K, V> {
        public final K key;
        public V value;
        public int frequency = 1;
        public Node<K, V> prev;
        public Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final Node<K, V> head = new Node<>(null, null);
    private final Node<K, V> tail = new Node<>(null, null);

    public DoublyLinkedList() {
        head.next = tail;
        tail.prev = head;
    }

    public Node<K, V> newNode(K key, V value) {
        return new Node<>(key, value);
    }

    public boolean isEmpty() {
        return head.next == tail;
    }

    public void linkAsMru(Node<K, V> node) {
        node.prev = tail.prev;
        node.next = tail;
        tail.prev.next = node;
        tail.prev = node;
    }

    public void unlink(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    public Node<K, V> evictLru() {
        Node<K, V> lru = head.next;
        if (lru == tail) {
            throw new IllegalStateException("cannot evict from empty list");
        }
        unlink(lru);
        lru.prev = null;
        lru.next = null;
        return lru;
    }
}