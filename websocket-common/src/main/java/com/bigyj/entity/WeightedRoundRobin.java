package com.bigyj.entity;

import java.util.concurrent.atomic.AtomicLong;

public class WeightedRoundRobin {
    private int weight;
    private AtomicLong current = new AtomicLong(0);
    private long lastUpdate;

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
        current.set(0);
    }

    public long increaseCurrent() {
        return current.addAndGet(weight);
    }

    public void sel(int total) {
        current.addAndGet(-1 * total);
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
