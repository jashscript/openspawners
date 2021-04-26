package me.jashscript.openspawners.models;

import lombok.Data;

import java.util.ArrayList;

@Data
public class SpawnerType {

    private String type;
    private ArrayList<Drop> drops = new ArrayList<>();
    private ArrayList<Integer> costs = new ArrayList<>();

}

