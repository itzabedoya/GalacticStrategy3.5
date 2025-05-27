package edu.sdccd.cisc191.game;

import edu.sdccd.cisc191.subsystems.ExplorationSystem;
import edu.sdccd.cisc191.subsystems.ResourceManagement;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Main Game Class (Integrates JavaFX, Shipyard System, and Exploration System)
public class Game extends Application {
    private Shipyard shipyard;
    private ExplorationSystem explorationSystem;
    private ResourceManagement resourceManagement;
    private Player player;
    private PlayerInventory inventory;

    private ListView<String> fleetListView;
    private Label statusLabel;
    private TextArea gameLog;
    private ComboBox<String> planetSelector;
    private Label resourceLabel;

    private GameState gameState;

    public enum GameState {
        MENU, PLAYING, PAUSED, GAME_OVER
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize game components
        player = new Player("Captain");
        shipyard = new Shipyard(ship -> player.addShip(ship));
        explorationSystem = new ExplorationSystem();
        resourceManagement = new ResourceManagement();
        inventory = new PlayerInventory();

        gameState = GameState.MENU;

        // Create UI elements
        statusLabel = new Label("Welcome to Galactic Strategy! (Press ENTER to start)");
        fleetListView = new ListView<>();
        shipyard.buildShip("Fighter");
        updateFleetDisplay();

        gameLog = new TextArea();
        gameLog.setEditable(false);
        gameLog.setPrefHeight(150);

        resourceLabel = new Label("Resources:\n" + inventory.displayResources());

        // Shipyard UI Buttons
        Button buildFighterBtn = new Button("Build Fighter (10 Minerals, 5 Energy)");
        Button buildCruiserBtn = new Button("Build Cruiser (15 Minerals, 7 Energy)");
        Button buildBattleshipBtn = new Button("Build Battleship (20 Minerals, 10 Energy)");
        Button upgradeShipBtn = new Button("Upgrade Selected Ship");

        // Resource Gathering Button
        Button gatherDilithiumBtn = new Button("Gather Dilithium");
        gatherDilithiumBtn.setOnAction(e -> gatherDilithium());

        // Exploration UI
        Button exploreBtn = new Button("Explore Planet");
        planetSelector = new ComboBox<>();
        planetSelector.getItems().addAll("Mars", "Jupiter", "Neptune", "Alpha Centauri", "Andromeda");
        planetSelector.setValue("Mars");

        // Assign button actions
        buildFighterBtn.setOnAction(e -> buildShip("Fighter", 10, 5));
        buildCruiserBtn.setOnAction(e -> buildShip("Cruiser", 15, 7));
        buildBattleshipBtn.setOnAction(e -> buildShip("Battleship", 20, 10));
        upgradeShipBtn.setOnAction(e -> upgradeSelectedShip());
        exploreBtn.setOnAction(e -> exploreSelectedPlanet());


        // Layout for the UI
        VBox layout = new VBox(10);
        layout.getChildren().addAll(
                statusLabel, fleetListView, buildFighterBtn, buildCruiserBtn,
                buildBattleshipBtn, upgradeShipBtn, gatherDilithiumBtn,
                planetSelector, exploreBtn, gameLog, resourceLabel
        );

        Scene scene = new Scene(layout,500, 600);

        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));

        primaryStage.setTitle("Galactic Strategy");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Game loop
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGame();
            }
        };
        gameLoop.start();
    }

    // Corrected this method:
    private boolean canUseResources(int mineralsCost, int energyCost) {
        return inventory.getResourceAmount("Minerals") >= mineralsCost &&
                inventory.getResourceAmount("Energy") >= energyCost;
    }

    private void buildShip(String shipType, int mineralsCost, int energyCost) {
        if (inventory == null || !canUseResources(mineralsCost, energyCost)) {
            statusLabel.setText("Not enough resources to build " + shipType);
            return;
        }

        // Deduct resources once (previous code mistakenly called useResource twice)
        boolean mineralsUsed = inventory.useResource("Minerals", mineralsCost);
        boolean energyUsed = inventory.useResource("Energy", energyCost);

        if (!mineralsUsed || !energyUsed) {
            statusLabel.setText("Resource usage error building " + shipType);
            return;
        }

        statusLabel.setText("Building " + shipType + "...");

        shipyard.buildShip(shipType);

        // Build the ship and update UI after delay - fixed parentheses and method syntax
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> Platform.runLater(this::updateFleetDisplay), 2, TimeUnit.SECONDS);
        executor.shutdown();

        // Update resource display immediately after resource deduction
        resourceLabel.setText("Resources:\n" + inventory.displayResources());
    }

    private void handleKeyPress(KeyCode key) {
        switch (key) {
            case ENTER:
                if (gameState == GameState.MENU) gameState = GameState.PLAYING;
                break;
            case P:
                if (gameState == GameState.PLAYING) gameState = GameState.PAUSED;
                else if (gameState == GameState.PAUSED) gameState = GameState.PLAYING;
                break;
            case ESCAPE:
                gameState = GameState.GAME_OVER;
                break;
        }
    }

    private void updateGame() {
        switch (gameState) {
            case MENU:
                statusLabel.setText("In Menu... Press 'Enter' to play");
                break;
            case PLAYING:
                statusLabel.setText("Game running... Manage Fleet & Explore");
                break;
            case PAUSED:
                statusLabel.setText("Game Paused. Press 'P' to resume!");
                break;
            case GAME_OVER:
                statusLabel.setText("Game Over! Press 'ESC' to exit");
                break;
        }
    }

    private void upgradeSelectedShip() {
        String selectedShip = fleetListView.getSelectionModel().getSelectedItem();
        if (selectedShip == null || selectedShip.isEmpty()) {
            statusLabel.setText("Select a ship to upgrade!");
            return;
        }

        String shipName = selectedShip.split(" \\| ")[0]; // Extract name before upgrade
        shipyard.upgradeShip(shipName);
        updateFleetDisplay();
        statusLabel.setText(selectedShip + " upgraded!");
    }

    private void updateFleetDisplay() {
        fleetListView.getItems().clear();
        if (shipyard == null || shipyard.getPlayerFleet() == null) return;
        for (GalacticShip ship : shipyard.getPlayerFleet()) {
            fleetListView.getItems().add(ship.getName() + " | HP: " + ship.getHealth() + " | ATK: " + ship.getAttackPower());
        }
    }

    private void gatherDilithium() {
        resourceManagement.gatherResources(player, "Dilithium", inventory);
        resourceLabel.setText("Resources:\n" + inventory.displayResources());
    }

    private void exploreSelectedPlanet() {
        String planetName = planetSelector.getValue();
        Planet planet = new Planet(planetName);

        gameLog.appendText("Exploring " + planetName + "...\n");
        explorationSystem.explorePlanet(player, planet, inventory);

        updateFleetDisplay();
        resourceLabel.setText("Resources:\n" + inventory.displayResources());
    }

    @Override
    public void stop() {
        shipyard.shutdown();
    }
}
