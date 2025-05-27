package edu.sdccd.cisc191.subsystems;

import edu.sdccd.cisc191.game.GalacticShip;
import edu.sdccd.cisc191.game.Planet;
import edu.sdccd.cisc191.game.Player;
import edu.sdccd.cisc191.game.PlayerInventory;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


    /*
     * Features added:
     * Random events (enemy battle, resources, healing zone)
     * Enemy combat integrated (triggers CombatSystem)
     * UI messages for immersion
     * Ensures the player has a ship before exploring (Prevents crashes)
     */

// Handles planet exploration with random events
public class ExplorationSystem {
    private final Lock lock = new ReentrantLock();
    private final Random random = new Random();
    private final CombatSystem combatSystem = new CombatSystem();

    /*
     * Explores a planet and triggers a random event
     * @param player The player exploring
     * @param planet The planet is being explored.
     */
    public void explorePlanet(Player player, Planet planet, PlayerInventory inventory) {
        lock.lock();
        try {
            System.out.println(player.getName() + " is exploring " + planet.getName());

            if (player.getFleet().isEmpty()) {
                System.out.println("No ships available to explore! Build a ship first.");
                return;
            }

            // Get Player's first ship for interactions
            GalacticShip playerShip = player.getFleet().get(0);

            // Generate a random event
            int event = random.nextInt(3);

            switch (event) {
                case 0 -> {
                    // Enemy Encounter
                    System.out.println("Enemy detected on " + planet.getName() + "!");
                    // Create an AI enemy ship
                    GalacticShip enemyShip = new GalacticShip("Alien Raider", 150, 30);
                    combatSystem.engageCombatAsync(playerShip, enemyShip, result -> {
                        System.out.println(result);

                        if (playerShip.isDestroyed()) {
                            synchronized (player.getFleet()) {
                                player.getFleet().remove(playerShip);
                                System.out.println("Your ship was destroyed!");
                            }
                        }
                    });
                }
                case 1 -> {
                    // Resource Discovery
                    System.out.println("You found resources on " + planet.getName() + "! +10 fuel, +10 minerals.");
                    inventory.addResource("Fuel",10);
                    inventory.addResource("Minerals",10);
                    System.out.println("New Totals → Fuel: " + inventory.getResourceAmount("Fuel") + ", Minerals: " + inventory.getResourceAmount ("Minerals"));
                }
                case 2 -> {
                    // Safe Zone Healing
                    System.out.println("Safe zone detected! Your ships regain +20 HP.");
                    playerShip.takeDamage(+20); // Heals the ship
                }
            }

        } finally {
            lock.unlock();
        }
    }
    public void shutdown() {
        combatSystem.shutdown();
    }
}