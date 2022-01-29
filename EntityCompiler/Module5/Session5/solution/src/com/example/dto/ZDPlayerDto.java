package com.example.dto;

import java.util.UUID;
import com.example.model.ZMPlayer;
import java.util.List;
import java.util.ArrayList;

// A player in the game.
public class ZDPlayerDto
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
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
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
    // Create instance from a Model object
    public static ZDPlayerDto fromModel(ZMPlayer modelObject) {
        ZDPlayerDto dtoObject = new ZDPlayerDto();
        dtoObject.id = modelObject.getId();
        dtoObject.firstName = modelObject.getFirstName();
        dtoObject.lastName = modelObject.getLastName();
        dtoObject.experiencePoints = modelObject.getExperiencePoints();
        dtoObject.level = modelObject.getLevel();
        dtoObject.health = modelObject.getHealth();
        dtoObject.magicEnergy = modelObject.getMagicEnergy();
        dtoObject.coins = modelObject.getCoins();
        return dtoObject;
    }
    public List<String> firstNameConstraintsViolated() {
        List<String> violations = new ArrayList<>();
        if (!(firstName.length() >= 1 && firstName.length() <= 15)) {
            violations.add("correctLength");
        }
        return violations;
    }
    public List<String> lastNameConstraintsViolated() {
        List<String> violations = new ArrayList<>();
        if (!(lastName.length() >= 3 && lastName.length() <= 20)) {
            violations.add("correctLength");
        }
        return violations;
    }
    public List<String> levelConstraintsViolated() {
        List<String> violations = new ArrayList<>();
        if (!(level <= 12)) {
            violations.add("levelValue");
        }
        return violations;
    }
}
