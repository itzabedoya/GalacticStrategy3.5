package edu.sdccd.cisc191.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a spaceship in the Galactic Strategy game.
 * Each ship has a name, health, attack power, and combat abilities, and can engage in combat.
 */
public class GalacticShip implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private int health;
    private final int maxHealth;
    private final int attackPower;
    private final List<CombatAbility> combatAbilities;
    private final Lock lock = new ReentrantLock();

    /**
     * Enum representing different combat abilities a ship can have.
     */
    public enum CombatAbility {
        LASER_CANNON,
        SHIELD_GENERATOR,
        MISSILE_LAUNCHER,
        CLOAKING_DEVICE,
        REPAIR_DRONES
    }

    /**
     * Constructs a GalacticShip with the specified name, health, and attack power.
     *
     * @param name The name of the ship.
     * @param health The initial health of the ship.
     * @param attackPower The attack power of the ship.
     */
    public GalacticShip(String name, int health, int attackPower) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.attackPower = attackPower;
        this.combatAbilities = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        lock.lock();
        try {
            return health;
        } finally {
            lock.unlock();
        }
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getAttackPower() {
        return attackPower;
    }

    /**
     * Reduces the ship's health by the specified damage amount.
     * Health cannot go below zero.
     *
     * @param damage The amount of damage to take.
     */

    public void takeDamage(int damage) {
        if (damage > 0) {
            lock.lock();
            try {
                health -= damage;
                if (health < 0) {
                    health = 0;
                }    // health cannot go below 0
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Repairs the ship by the specified amount, up to max health.
     *
     * @param amount The amount of health to restore.
     */

    public void repair(int amount) {
        if (amount > 0) {
            lock.lock();
            try {
                health += amount;
                if (health > maxHealth) {
                    health = maxHealth;
                }
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Attacks another GalacticShip, causing damage equal to this ship's attack power.
     *
     * @param target The ship to attack.
     */

    public void attack(GalacticShip target) {
        if (target != null && !this.isDestroyed()) {
            target.takeDamage(this.attackPower);
        }
    }

    /**
     * Returns whether this ship is destroyed (health <= 0).
     *
     * @return true if destroyed, false otherwise.
     */

    public boolean isDestroyed() {
        lock.lock();
        try {
            return health <= 0;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Adds a combat ability to the ship.
     *
     * @param ability The combat ability to add.
     */
    public void addCombatAbility(CombatAbility ability) {
        if (ability != null) {
            lock.lock();
            try {
                if (!combatAbilities.contains(ability)) {
                    combatAbilities.add(ability);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Removes a combat ability from the ship.
     *
     * @param ability The combat ability to remove.
     */
    public void removeCombatAbility(CombatAbility ability) {
        lock.lock();
        try {
            combatAbilities.remove(ability);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Checks if the ship has a specific combat ability.
     *
     * @param ability The combat ability to check for.
     * @return true if the ship has the ability, false otherwise.
     */
    public boolean hasCombatAbility(CombatAbility ability) {
        lock.lock();
        try {
            return combatAbilities.contains(ability);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Gets a list of all combat abilities the ship has.
     *
     * @return A list of the ship's combat abilities.
     */
    public List<CombatAbility> getCombatAbilities() {
        lock.lock();
        try {
            return new ArrayList<>(combatAbilities);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return String.format("%s [Health: %d/%d, Attack: %d, Abilities: %s]",
                name, getHealth(), maxHealth, attackPower, getCombatAbilities());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GalacticShip)) return false;
        GalacticShip that = (GalacticShip) o;
        return health == that.getHealth() &&
                maxHealth == that.maxHealth &&
                attackPower == that.attackPower &&
                Objects.equals(name, that.name) &&
                Objects.equals(getCombatAbilities(), that.getCombatAbilities());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, getHealth(), maxHealth, attackPower, getCombatAbilities());
    }
}