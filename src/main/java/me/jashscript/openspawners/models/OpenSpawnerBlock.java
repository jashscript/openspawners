package me.jashscript.openspawners.models;

import lombok.Data;

import java.util.UUID;

@Data
public class OpenSpawnerBlock {

    private String type;
    private int tier;
    private int amount;
    private boolean mobAI;

    private UUID owner;
    private BlockLocation blockLocation;

}