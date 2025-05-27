package edu.sdccd.cisc191.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Represents a player in the Galactic Strategy game.
 * Each player has a name and a fleet of GalacticShips.
 */
public class Player implements Serializable{
    private static final long serialVersionUID = 1L;

    private String name;
    private List<GalacticShip> fleet;

    /**
     * Constructs a Player with the specified name and initializes an empty fleet.
     *
     * @param name The name of the player.
     */
    public Player(String name) {
        this.name = name;
        this.fleet = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<GalacticShip> getFleet() {
        return new ArrayList<>(fleet);
    }

    /**
     * Adds a GalacticShip to the player's fleet.
     *
     * @param ship The GalacticShip to add.
     */
    public void addShip(GalacticShip ship) {
        if (ship != null && !fleet.contains(ship)) {
            fleet.add(ship);
        }
    }

    public void removeShip(GalacticShip ship) {
        fleet.remove(ship);
    }

    public GalacticShip findShipByName(String shipName) {
        for (GalacticShip ship : fleet) {
            if ((ship.getName().equalsIgnoreCase(shipName))) {
                return ship;
            }
        }
        return null;
    }

    /**
     * Returns the total health of all ships in the fleet.
     *
     * @return The total health of the fleet.
     */
    public int getTotalFleetHealth() {
        int totalHealth = 0;
        for (GalacticShip ship : fleet) {
            totalHealth += ship.getHealth();
        }
        return totalHealth;
    }

    public boolean allShipsDestroyed() {
        for (GalacticShip ship : fleet) {
            if (!ship.isDestroyed()) {
                return false;
            }
        }
        return true;
    }

    public int getActiveShipCount() {
        int count = 0;
        for (GalacticShip ship : fleet) {
            if (!ship.isDestroyed()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String toString() {
        return String.format("Player{name='%s', fleetSize=%d}", name, fleet.size());
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return Objects.equals(name, player.name) &&
                Objects.equals(fleet, player.fleet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fleet);
    }
}