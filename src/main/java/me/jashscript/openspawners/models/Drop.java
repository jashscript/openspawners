package me.jashscript.openspawners.models;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public class Drop {
    private ItemStack itemstack;
    private int amount;
    private int chance;

    public Drop() {
    }
}
