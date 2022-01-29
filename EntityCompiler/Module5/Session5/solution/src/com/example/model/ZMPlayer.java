package com.example.model;

import java.util.UUID;
import com.example.dto.ZDPlayerDto;

// A player in the game.
public class ZMPlayer
{
    private UUID id;

    // First name of the player.
    private String firstName;

    // Last name of the player.
    private String lastName;

    // Experience Points
    private long experiencePoints;

    // Level achieved.
    private int level;

    // Amount of health remaining.
    private int health;

    // Amount of energy remaining used to cast magic spells.
    private int magicEnergy;

    // Number of coins the player has.
    private long coins;

    public UUID getId() {
        return id;
    }

    public void setId(UUID value) {
        this.id = value;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        // Make sure first name is the correct length.
        if (!(firstName.length() >= 1 && firstName.length() <= 15)) {
            return;
        }
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        // Make sure last name is the correct length.
        if (!(lastName.length() >= 3 && lastName.length() <= 20)) {
            return;
        }
        this.lastName = lastName;
    }

    public long getExperiencePoints() {
        return experiencePoints;
    }

    public void setExperiencePoints(long experiencePoints) {
        this.experiencePoints = experiencePoints;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (!(level <= 12)) {
            return;
        }
        this.level = level;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMagicEnergy() {
        return magicEnergy;
    }

    public void setMagicEnergy(int magicEnergy) {
        this.magicEnergy = magicEnergy;
    }

    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }

    // Create instance from a DTO object
    public static ZMPlayer fromDTO(ZDPlayerDto dtoObject) {
        ZMPlayer modelObject = new ZMPlayer();
        modelObject.id = dtoObject.getId();
        modelObject.firstName = dtoObject.getFirstName();
        modelObject.lastName = dtoObject.getLastName();
        modelObject.experiencePoints = dtoObject.getExperiencePoints();
        modelObject.level = dtoObject.getLevel();
        modelObject.health = dtoObject.getHealth();
        modelObject.magicEnergy = dtoObject.getMagicEnergy();
        modelObject.coins = dtoObject.getCoins();
        return modelObject;
    }
}
