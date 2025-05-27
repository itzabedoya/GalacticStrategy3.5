package edu.sdccd.cisc191.game;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import javafx.application.Platform;
import edu.sdccd.cisc191.subsystems.CustomLinkedList;

    /*
     * Shipyard class for managing spaceship constructions and upgrades
     * Uses I/O streams for saving.loading shipyard state
     * Uses ExecutorService for concurrent ship building
     */

     /*
      * Features added:
      * Ship Construction: Player can build ships asynchronously (multithreading)
      * Ship Upgrades: Upgrades ships, increasing their stats
      * Save and Load: Fleet persists between game sessions using file I/O (OOS)
      * Concurrency Handling: Uses ExecutorService for shipbuilding
      * Interactive Testing: main method allows quick testing of shipyard features
      */

public class Shipyard {
    private final Map<String, GalacticShip> availableShips;
    private final CustomLinkedList<GalacticShip> playerFleet;
    private final ExecutorService shipBuilderPool;
    private final String saveFile = "GalacticStrategy3/src/main/resources/galactic_game_state_csv"; // Stores the list of ships
    private Consumer<GalacticShip> onShipBuilt; //Callback from UI

    // Constructs a Shipyard with predefined ship options
    public Shipyard(Consumer<GalacticShip> onShipBuilt) {
        this.availableShips = new HashMap<>();
        this.playerFleet = new CustomLinkedList<>();
        this.shipBuilderPool = Executors.newFixedThreadPool(2); // Allows 2 ships to be built at a time
        this.onShipBuilt = onShipBuilt;

        initializeShipyard();
        loadShipyardState();
        }

    // Initializes default ship types available in the shipyard
    private void initializeShipyard() {
        availableShips.put("Fighter", new GalacticShip("Fighter", 100, 20));
        availableShips.put("Cruiser", new GalacticShip("Cruiser", 200, 40));
        availableShips.put("Battleship", new GalacticShip("Battleship", 300, 60));
    }


    // Displays available ships and their stats
    public void displayAvailableShips() {
        System.out.println("Available Ships:");
        for (Map.Entry<String, GalacticShip> entry: availableShips.entrySet()) {
            GalacticShip ship = entry.getValue();
            System.out.println("- " + ship.getName() + " | Health: " + ship.getHealth() + " | Attack: " + ship.getAttackPower());
        }
    }

    /* Asynchronously builds a new spaceship for the player
     * @param shipType The type of ship to construct
     */
    public void buildShip(String shipType) {
        if (!availableShips.containsKey(shipType)) {
            System.out.println("Invalid ship type.");
            return;
        }

        System.out.println("Building " + shipType + "...");

        Callable<GalacticShip> task = () -> {
            try {
                Thread.sleep(2000);
                GalacticShip newShip = new GalacticShip(shipType, availableShips.get(shipType).getHealth(), availableShips.get(shipType).getAttackPower());
                synchronized (playerFleet) {
                    playerFleet.add(newShip);
                }
                saveShipyardState();
                return newShip;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Build interrupted for " + shipType, e);
            }
        };
        Future<GalacticShip> future = shipBuilderPool.submit(task);

        shipBuilderPool.submit(() -> {
            try {
                // Simulate shipbuilding time
                GalacticShip builtShip = future.get();
                System.out.println("Build complete! " + builtShip.getName());
                if (onShipBuilt != null) {
                    Platform.runLater(() -> onShipBuilt.accept(builtShip));
                }
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error building ship: " + e.getMessage());
            }
        });
    }

    /*
     * Upgrades a player's ship by increasing its health and attack power
     * @param shipName The name of the ship to upgrade
     */

    public void upgradeShip(String shipName) {
        synchronized (playerFleet) {
            for (GalacticShip ship : playerFleet) {
                if (ship.getName().equalsIgnoreCase(shipName)) {
                    ship.takeDamage(-50); // Increases health by 50
                    System.out.println(shipName + " upgraded! New Health: " + ship.getHealth());
                    saveShipyardState(); // Save fleet upgrades
                    return;
                }
            }
        }
        System.out.println("Ship not found in your fleet.");
    }

    // Displays the player's current fleet
    public void displayPlayerFleet() {
        System.out.println("\nYour Fleet: ");
        synchronized (playerFleet) {
            if (playerFleet.isEmpty()) {
                System.out.println("No ships in fleet.");
            } else {
                for (GalacticShip ship : playerFleet) {
                    System.out.println("- " + ship.getName() + " | Health: " + ship.getHealth() + " | Attack: " + ship.getAttackPower());
                }
            }
        }
    }

    /*
     * Retrieves the player's fleet
     * @return List of GalacticShips in the player's fleet
     */
    public List<GalacticShip> getPlayerFleet() {
        return playerFleet.toList();
    }

    // Saves the player's fleet to a file using ObjectOutputStream
    private void saveShipyardState() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(saveFile))) {
            out.writeObject(playerFleet.toList());
            System.out.println("Shipyard state saved.");
        } catch (IOException e) {
            System.err.println("Error saving shipyard state: " + e.getMessage());
        }
    }

    // Load the player's fleet from a file using ObjectInputStream
    private void loadShipyardState() {
        File file = new File(saveFile);
        if (!file.exists()) return; // No save file yet

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(saveFile))) {
            List<GalacticShip> loadedFleet = (List<GalacticShip>) in.readObject();
            synchronized (playerFleet) {
                playerFleet.clear();
                for (GalacticShip ship : loadedFleet) {
                    playerFleet.add(ship);
                }
            }
            System.out.println("Shipyard state loaded.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading shipyard states: " + e.getMessage());
        }
    }

    // Shuts down the ExecutorService for clean-up
    public void shutdown() {
        shipBuilderPool.shutdown();
    }

    // Test the Shipyard functionality
    public static void main(String[] args) {
        Shipyard shipyard = new Shipyard(ship -> {
            System.out.println("Built ship: " + ship.getName());
        });

        shipyard.displayAvailableShips();
        shipyard.buildShip("Fighter");
        shipyard.buildShip("Cruiser");

        try {
            Thread.sleep(5000); // Wait for ships to be built
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        shipyard.displayPlayerFleet();
        shipyard.upgradeShip("Fighter");
        shipyard.displayPlayerFleet();

        shipyard.shutdown();
    }

}
