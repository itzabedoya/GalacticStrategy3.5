package edu.sdccd.cisc191.game;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Manages player's inventory of resources
public class PlayerInventory implements Serializable {
    private final Map<String, Resource> resources;
    private final Lock lock = new ReentrantLock();

    public PlayerInventory() {
        resources = new HashMap<>();
        resources.put("Dilithium", new Resource("Dilithium"));
        resources.put("Fuel", new Resource("Fuel"));
        resources.put("Minerals", new Resource("Minerals"));
        resources.put("Energy", new Resource("Energy"));
        resources.put("Credits", new Resource("Credits"));
    }

    public void addResource(String type, int amount) {
        lock.lock();
        try {
            resources.computeIfAbsent(type, Resource::new).addAmount(amount);
        } finally {
            lock.unlock();
        }
    }

    public boolean useResource(String type, int amount) {
        lock.lock();
        try {
            return resources.containsKey(type) && resources.get(type).useAmount(amount);
        } finally {
            lock.unlock();
        }
    }

    public int getResourceAmount(String type) {
        lock.lock();
        try {
            return resources.getOrDefault(type, new Resource(type)).getAmount();
        } finally {
            lock.unlock();
        }
    }

    public String displayResources() {
        lock.lock();
        try {
            StringBuilder sb = new StringBuilder("=== Player Resources ===\n");
            for (Resource r : resources.values()) {
                sb.append(r.toString()).append("\n");
            }
            return sb.toString();
        } finally {
            lock.unlock();
        }
    }

    public void refineDilithium() {
        lock.lock();
        try {
            int dilithium = getResourceAmount("Dilithium");
            if (dilithium >= 10) {
                useResource("Dilithium", 10);
                addResource("Fuel", 5);
                addResource("Energy", 5);
                addResource("Minerals", 5);
                System.out.println("Refined 10 Dilithium into 5 each of Fuel, Energy, and Minerals!");
            } else {
                System.out.println("Not enough Dilithium to refine.");
            }
        } finally {
            lock.unlock();
        }
    }
}
