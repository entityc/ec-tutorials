package com.example;

import com.example.model.ZMPlayer;
import com.example.model.ZMPlayerMagicSpell;
import com.example.model.ZMMagicSpell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Main {
    private Map<UUID, ZMPlayer> playerById = new HashMap<>();
    private Map<UUID, ZMMagicSpell> magicSpellById = new HashMap<>();
    private List<ZMPlayerMagicSpell> playerMagicSpells = new ArrayList<>();

    public static void main(String[] args) {
        Main main = new Main();
        main.setupData();
        main.printPlayerMagicSpells();
    }

    private void setupData() {
        ZMPlayer player1 = new ZMPlayer();
        player1.setId(UUID.randomUUID());
        player1.setFirstName("Lance");
        player1.setLastName("Skyrunner");
        player1.setExperiencePoints(1000);
        player1.setHealth(10);
        player1.setCoins(250000);
        player1.setLevel(3);
        playerById.put(player1.getId(), player1);

        ZMMagicSpell transport = new ZMMagicSpell();
        transport.setId(UUID.randomUUID());
        transport.setName("Transport");
        transport.setCastEnergy(100);
        transport.setPrice(120000);
        transport.setWeight(87);
        magicSpellById.put(transport.getId(), transport);

        ZMPlayerMagicSpell player1Transport = new ZMPlayerMagicSpell();
        player1Transport.setId(UUID.randomUUID());
        player1Transport.setPlayerId(player1.getId());
        player1Transport.setMagicSpellId(transport.getId());
        player1Transport.setRemainingCasts(2);
        playerMagicSpells.add(player1Transport);
    }

    private void printPlayerMagicSpells() {
        for (ZMPlayerMagicSpell playerMagicSpell : playerMagicSpells) {
            ZMPlayer player = playerById.get(playerMagicSpell.getPlayerId());
            ZMMagicSpell magicSpell = magicSpellById.get(playerMagicSpell.getMagicSpellId());
            String spellPlural = playerMagicSpell.getRemainingCasts() == 1 ? "spell" : "spells";
            System.out.println(player.getFirstName() + " has " + playerMagicSpell.getRemainingCasts() + " " + magicSpell.getName() + " magic " + spellPlural);
        }
    }
}
