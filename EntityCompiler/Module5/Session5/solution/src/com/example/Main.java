package com.example;

import com.example.model.ZMPlayer;
import com.example.dto.ZDPlayerDto;
import com.example.controller.ZAPlayerController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Main {
    private Map<UUID, ZMPlayer> playerById = new HashMap<>();

    public static void main(String[] args) {
        Main main = new Main();
        main.setupData();
    }

    private void setupData() {
        // Player
        ZDPlayerDto player1dto = new ZDPlayerDto();
        player1dto.setFirstName("This is too long"); // should violate constraint
        player1dto.setLastName("This is too just long"); // should violate constraint
        player1dto.setExperiencePoints(1000);
        player1dto.setHealth(10);
        player1dto.setCoins(250000);
        player1dto.setLevel(3);
        player1dto.setLevel(13); // should violate constraint

        // pass this to the API code, expect exception
        ZAPlayerController controller = new ZAPlayerController();
        try {
            ZDPlayerDto createdPlayerDto = controller.createPlayer(player1dto);
        } catch (ValidationException e) {
            System.out.println("Expected validation error: " + e.getMessage());
        }
        player1dto.setFirstName("Lance");
        try {
            ZDPlayerDto createdPlayerDto = controller.createPlayer(player1dto);
        } catch (ValidationException e) {
            System.out.println("Expected validation error: " + e.getMessage());
        }
        player1dto.setLastName("Skyrunner");
        try {
            ZDPlayerDto createdPlayerDto = controller.createPlayer(player1dto);
        } catch (ValidationException e) {
            System.out.println("Expected validation error: " + e.getMessage());
        }
        player1dto.setLevel(2);
        ZDPlayerDto createdPlayerDto;
        try {
            createdPlayerDto = controller.createPlayer(player1dto);
        } catch (ValidationException e) {
            System.err.println("UNEXPECTED validation error: " + e.getMessage());
            return;
        }
        System.out.println("Good to go, no more constraint issues!");

        // Model
        ZMPlayer player1 = ZMPlayer.fromDTO(createdPlayerDto);

        player1.setFirstName("This is too long");
        if (!player1.getFirstName().equals("Lance")) {
            System.err.println("First name constraint not working!");
        }

        player1.setLastName("This is too just long");
        if (!player1.getLastName().equals("Skyrunner")) {
            System.err.println("Last name constraint not working!");
        }
        player1.setExperiencePoints(1000);
        player1.setHealth(10);
        player1.setCoins(250000);
        player1.setLevel(3);
        player1.setLevel(13); // should be ignored
        if (player1.getLevel() != 3) {
            System.err.println("Level constraint not working!");
        }
        playerById.put(player1.getId(), player1);
    }
}
