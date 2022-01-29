package com.example.model;

import java.util.UUID;

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

    public void setFirstName(String value) {
        this.firstName = value;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String value) {
        this.lastName = value;
    }

    public long getExperiencePoints() {
        return experiencePoints;
    }

    public void setExperiencePoints(long value) {
        this.experiencePoints = value;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int value) {
        this.level = value;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int value) {
        this.health = value;
    }

    public int getMagicEnergy() {
        return magicEnergy;
    }

    public void setMagicEnergy(int value) {
        this.magicEnergy = value;
    }

    public long getCoins() {
        return coins;
    }

    public void setCoins(long value) {
        this.coins = value;
    }

// =====preserve===== start-customClassCode =====
    public String getFullName() {
        return firstName + " " + lastName;
    }
// =====preserve===== end-customClassCode =====
}
