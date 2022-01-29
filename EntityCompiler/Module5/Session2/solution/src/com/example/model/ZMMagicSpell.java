package com.example.model;

import java.util.UUID;

// Gives you the ability to do magical things.
public class ZMMagicSpell
{
    private UUID id;

    // Name of the magic spell.
    private String name;

    // The amount of energy it requires to cast it.
    private int castEnergy;

    // How much it costs to buy.
    private long price;

    // How much it weighs. This affects how many spells a player can carry.
    private int weight;

    public UUID getId() {
        return id;
    }

    public void setId(UUID value) {
        this.id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public int getCastEnergy() {
        return castEnergy;
    }

    public void setCastEnergy(int value) {
        this.castEnergy = value;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long value) {
        this.price = value;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int value) {
        this.weight = value;
    }
}
