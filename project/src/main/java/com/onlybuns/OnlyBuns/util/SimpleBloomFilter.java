package com.onlybuns.OnlyBuns.util;
import java.util.BitSet;

public class SimpleBloomFilter {
    private final BitSet bitSet;
    private final int size; // Size of the bit array
    private final int numHashFunctions; // Number of hash functions

    public SimpleBloomFilter(int size, int numHashFunctions) {
        this.size = size;
        this.numHashFunctions = numHashFunctions;
        this.bitSet = new BitSet(size);
    }

    // Hash function to generate an index in the bit array
    private int hash(String value, int seed) {
        int hash = 0;
        for (char c : value.toCharArray()) {
            hash = (hash * seed) + c;
        }
        return Math.abs(hash) % size;
    }

    // Add an item to the Bloom filter
    public void add(String value) {
        for (int i = 0; i < numHashFunctions; i++) {
            int index = hash(value, i + 1); // Use different seeds for each hash
            bitSet.set(index);
        }
    }

    // Check if an item is in the Bloom filter
    public boolean mightContain(String value) {
        for (int i = 0; i < numHashFunctions; i++) {
            int index = hash(value, i + 1); // Use different seeds for each hash
            if (!bitSet.get(index)) {
                return false; // If any hash position is not set, it is definitely not in the filter
            }
        }
        return true; // It might be in the filter
    }
}
