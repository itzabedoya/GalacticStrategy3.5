package edu.sdccd.cisc191.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameBoard implements Serializable{
    private static final long serialVersionUID = 1L;

    private int[][] planets; // Represents planets in the galaxy (0 = empty, other values = planet IDs)
    private int[][] resourceCosts; // Represents resource cost to traverse each cell
    private final int rows = 5;
    private final int cols = 5;

    public GameBoard() {
        planets = new int[rows][cols];
        resourceCosts = new int[rows][cols];
        initializeBoard();
    }

    /**
     * Initializes the game board with default values.
     * Planets are set to 0 (empty), and resource costs are set to default values.
     */
    public void initializeBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                planets[i][j] = 0; // No planet in this cell
                resourceCosts[i][j] = 1; // Default traversal cost
            }
        }
    }

    /**
     * Places a planet at the specified location.
     *
     * @param row      The row of the cell.
     * @param col      The column of the cell.
     * @param planetId The ID of the planet to place (e.g., 1, 2, 3...).
     */
    public void placePlanet(int row, int col, int planetId) {
        if (isValidCoordinate(row,col)) {
            planets[row][col] = planetId;
        }
    }

    public void resetPlanet(int row, int col) {
        if(isValidCoordinate(row,col)) {
            planets[row][col] = 0;
        }
    }
    /**
     * Sets the resource cost for traversing a specific cell.
     *
     * @param row  The row of the cell.
     * @param col  The column of the cell.
     * @param cost The resource cost to traverse this cell.
     */
    public void setResourceCost(int row, int col, int cost) {
        if(isValidCoordinate(row, col)){
            resourceCosts[row][col] = cost;
        }
    }

    /**
     * Gets the ID of the planet at a specific location.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return The ID of the planet (or 0 if no planet is present).
     */
    public int getPlanetId(int row, int col) {
        return isValidCoordinate(row,col) ? planets[row][col] : -1;
    }

    /**
     * Gets the resource cost to traverse a specific cell.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return The resource cost to traverse this cell.
     */
    public int getResourceCost(int row, int col) {
        return isValidCoordinate(row,col) ? resourceCosts[row][col]: -1;
    }

    /**
     * Gets the entire planets array.
     *
     * @return A 2D array representing all planets on the board.
     */
    public int[][] getPlanets() {
        return planets;
    }

    /**
     * Gets the entire resource costs array.
     *
     * @return A 2D array representing all resource costs on the board.
     */
    public int[][] getResourceCosts() {
        return resourceCosts;
    }

    /**
     * Prints a visual representation of the game board with planets and costs.
     */
    public void displayBoard() {
        System.out.println("Planets:");
        for (int [] row : planets ) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
        System.out.println("\nResource Costs:");
        for (int[] row : resourceCosts) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    public boolean isValidCoordinate(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public List<int[]> getAllPlanets() {
        List<int[]> List = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (planets[i][j] != 0) {
                    List.add(new int[]{i, j, planets[i][j]});
                }
            }
        }
        return List;
    }

    public int [][] getBoardSnapshot(){
        int [][] copy = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(planets[i], 0, copy[i], 0, cols);
        }
        return copy;
    }

    public int getCellValue(int i, int j) {
        // Ensure i and j are within bounds
        if (i < 0 || i >= rows || j < 0 || j >= cols) {
            throw new IllegalArgumentException("Invalid cell coordinates");
        }

        // Pack the planet ID and resource cost into a single integer
        // Use the lower 16 bits for the planet ID and the upper 16 bits for the resource cost
        return (resourceCosts[i][j] << 16) | (planets[i][j] & 0xFFFF);
    }

    public int setCellValue(int i, int i1, int i2) {
        // Ensure i and j are within bounds
        i1 = getCellValue(i, i1);
        i2 = getCellValue(i, i2);
        int j = 0;
        if (i < 0 || i >= rows || j < 0 || j >= cols) {
            throw new IllegalArgumentException("Invalid cell coordinates");
        }
        if (i1 < 0 || i1 >= rows || i2 < 0 || i2 >= cols) {
            throw new IllegalArgumentException("Invalid cell coordinates");
        }
        if (i2 < 0 || i2 >= rows || i1 >= cols) {
            throw new IllegalArgumentException("Invalid cell coordinates");
        }

        // Pack the planet ID and resource cost into a single integer
        // Use the lower 16 bits for the planet ID and the upper 16 bits for the resource cost
        return (resourceCosts[i][j] << 16) | (planets[i][j] & 0xFFFF);
    }

    public int[][] getBoard() {
        int[][] board = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = planets[i][j];
            }
        } return board;
    }

    // ===== Module 2: 2D Array Operations on Planets =====

    public int getPlanetAtIndex(int row, int col) {
        return isValidCoordinate(row, col) ? planets[row][col] : -1;
    }

    public void setPlanetAtIndex(int row, int col, int planetId) {
        if (isValidCoordinate(row, col)) {
            planets[row][col] = planetId;
        }
    }

    public int[] findPlanetIndex(int planetId) {
        for (int i = 0; i < planets.length; i++) {
            for (int j = 0; j < planets[i].length; j++) {
                if (planets[i][j] == planetId) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1}; // Not found
    }

    public void deletePlanetAtIndex(int row, int col) {
        if (isValidCoordinate(row, col)) {
            planets[row][col] = 0;
        }
    }

    public void expandBoard(int newRows, int newCols) {
        int[][] newPlanets = new int[newRows][newCols];
        int[][] newCosts = new int[newRows][newCols];
        for (int i = 0; i < planets.length; i++) {
            for (int j = 0; j < planets[i].length; j++) {
                newPlanets[i][j] = planets[i][j];
                newCosts[i][j] = resourceCosts[i][j];
            }
        }
        this.planets = newPlanets;
        this.resourceCosts = newCosts;
    }

    public void shrinkBoard(int newRows, int newCols) {
        int[][] newPlanets = new int[newRows][newCols];
        int[][] newCosts = new int[newRows][newCols];
        for (int i = 0; i < newRows && i < planets.length; i++) {
            for (int j = 0; j < newCols && j < planets[i].length; j++) {
                newPlanets[i][j] = planets[i][j];
                newCosts[i][j] = resourceCosts[i][j];
            }
        }
        this.planets = newPlanets;
        this.resourceCosts = newCosts;
    }

    public void printPlanetsAndCosts() {
        System.out.println("Planets:");
        for (int[] row : planets) {
            for (int p : row) {
                System.out.print(p + "\t");
            }
            System.out.println();
        }
        System.out.println("Resource Costs:");
        for (int[] row : resourceCosts) {
            for (int c : row) {
                System.out.print(c + "\t");
            }
            System.out.println();
        }
    }
}

