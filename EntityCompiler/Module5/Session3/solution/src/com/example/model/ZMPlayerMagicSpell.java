package com.example.model;

import java.util.UUID;

// Represents a magic spell in a players inventory of spells.
public class ZMPlayerMagicSpell
{
    private UUID id;

    // Number of casts a player has left for this magic spell.
    private int remainingCasts;

      // A player that has one of these magic spells.
    private UUID playerId;

      // The magic spell the player has.
    private UUID magicSpellId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID value) {
        this.id = value;
    }

    public int getRemainingCasts() {
        return remainingCasts;
    }

    public void setRemainingCasts(int value) {
        this.remainingCasts = value;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID value) {
        this.playerId = value;
    }

    public UUID getMagicSpellId() {
        return magicSpellId;
    }

    public void setMagicSpellId(UUID value) {
        this.magicSpellId = value;
    }

// =====preserve===== start-customClassCode =====
// Add custom methods here
// =====preserve===== end-customClassCode =====
}
