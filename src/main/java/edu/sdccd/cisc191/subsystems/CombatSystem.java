package edu.sdccd.cisc191.subsystems;

import edu.sdccd.cisc191.game.GalacticShip;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

    /*
     * Features added:
     * Turn-based attack sequence
     * Attack power variation (randomized damage)
     * UI logs for attacks and destruction
     * Game Over condition if player's ship is destroyed
     */

// Handles combat between two ships in a turn-based system
public class CombatSystem {
    private final Lock lock = new ReentrantLock();
    private final Random random = new Random();
    private final ExecutorService combatExecutor = Executors.newSingleThreadExecutor();

    /*
     * Engages combat between two ships with turn-based mechanics
     * @param playerShip The player's ship
     * @param enemyShip The enemy ship
     * @param onCombatEnd Callback when combat ends (can update UI)
     */

    public void engageCombatAsync(GalacticShip playerShip, GalacticShip enemyShip, Consumer<String> onCombatEnd) {

                combatExecutor.submit(() -> engageCombat(playerShip, enemyShip, onCombatEnd));
    }

    private void engageCombat(GalacticShip playerShip, GalacticShip enemyShip, Consumer<String> onCombatEnd) {
        lock.lock();
        try {
            System.out.println("Combat Started: " + playerShip.getName() + " vs. " + enemyShip.getName());

            while (!playerShip.isDestroyed() && !enemyShip.isDestroyed()) {
                // Player Attacks
                int playerAttack = playerShip.getAttackPower() + random.nextInt(5); // Slight damage variation
                enemyShip.takeDamage(playerAttack);
                System.out.println(playerShip.getName() + " attacks! " + enemyShip.getName() + " takes " + playerAttack + " damage. ");

                if (enemyShip.isDestroyed()) {
                    String message = enemyShip.getName() + " has been destroyed!";
                    System.out.println(message);
                    if(onCombatEnd != null) onCombatEnd.accept(message);
                    break;
                }

                // Enemy Attacks
                int enemyAttack = enemyShip.getAttackPower() + random.nextInt(5);
                playerShip.takeDamage(enemyAttack);
                System.out.println(enemyShip.getName() + " attacks! " + playerShip.getName() + " takes " + enemyAttack + " damage.");

                if (playerShip.isDestroyed()) {
                    String message = playerShip.getName() + " has been destroyed! GAME OVER!";
                    System.out.println(message);
                    if(onCombatEnd != null) onCombatEnd.accept(message);
                    break;
                }

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();

        }
    }
    public void shutdown() {
        combatExecutor.shutdown();
    }
}