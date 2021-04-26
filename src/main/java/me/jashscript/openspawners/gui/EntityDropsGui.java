package me.jashscript.openspawners.gui;

import me.jashscript.openspawners.OSMain;
import me.jashscript.openspawners.data.Persistance;
import me.jashscript.openspawners.models.Drop;
import me.jashscript.openspawners.models.SpawnerType;
import me.jashscript.openspawners.utils.ItemUtils;
import me.jashscript.openspawners.utils.TextUtils;
import me.nemo_64.spigotutilities.playerinputs.chatinput.PlayerChatInput;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;

public class EntityDropsGui implements InventoryHolder {

    public String entitytype;

    public EntityDropsGui(String entitytype) {
        this.entitytype = entitytype;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }


    public static void open(Player player, String entityType) throws IOException {
        Inventory inventory = Bukkit.getServer().createInventory(new EntityDropsGui(entityType), 27, "Editing " + TextUtils.entityTypeParse(entityType) + " Drops");

        SpawnerType spawnerType = OSMain.spawnerTypes.get(entityType);

        ArrayList<Drop> drops = spawnerType.getDrops();


        if(drops.size() >=1){
            inventory.setItem(11, EntityDropsGui.drop(drops.get(0), 1));
        } else {
            inventory.setItem(11, EntityDropsGui.empty(1));
        }

        if(drops.size() >=2){
            inventory.setItem(12, EntityDropsGui.drop(drops.get(1), 2));
        } else {
            inventory.setItem(12, EntityDropsGui.empty(2));
        }

        if(drops.size() >=3){
            inventory.setItem(13, EntityDropsGui.drop(drops.get(2), 3));
        } else {
            inventory.setItem(13, EntityDropsGui.empty(3));
        }

        if(drops.size() >=4){
            inventory.setItem(14, EntityDropsGui.drop(drops.get(3), 4));
        } else {
            inventory.setItem(14, EntityDropsGui.empty(4));
        }

        if(drops.size() >=5){
            inventory.setItem(15, EntityDropsGui.drop(drops.get(4), 5));
        } else {
            inventory.setItem(15, EntityDropsGui.empty(5));
            inventory.setItem(26, EntityDropsGui.add());
        }
        
        player.openInventory(inventory);
    }


    private static ItemStack empty(int tier){
        return new ItemUtils.ItemstackBuilder(Material.BARRIER).name("&l&fDrop for tier "+tier)
                .LoreLine("")
                .LoreLine("&l&fEMPTY")
                .build();
    }


    private static ItemStack drop(Drop drop, int tier){
        ItemStack itemStack = drop.getItemstack();
        return new ItemUtils.ItemstackBuilder(itemStack.getType()).name("&l&fDrop for tier "+tier)
                .LoreLine("&fAmount: " + drop.getAmount())
                .LoreLine("&fChance: " + drop.getChance())
                .LoreLine("")
                .LoreLine("&l&fCLICK TO EDIT")
                .build();
    }

    private static ItemStack add(){
        return new ItemUtils.ItemstackBuilder(Material.EMERALD_BLOCK).name("&l&fAdd Drop!")
                .LoreLine("")
                .LoreLine("&l&fClick to add a drop.")
                .build();
    }

    public static class invListener implements Listener {

        @EventHandler
        public void drag(InventoryDragEvent event){
            if(!(event.getInventory().getHolder() instanceof EntityDropsGui)) return;
            event.setCancelled(true);
        }


        @EventHandler
        public void click(InventoryClickEvent e) throws IOException {
            if(!(e.getInventory().getHolder() instanceof EntityDropsGui)) return;
            int rawslot = e.getRawSlot();
            Player player = (Player)e.getWhoClicked();
            String entitytype = ((EntityDropsGui)e.getInventory().getHolder()).entitytype;
            if(rawslot<27) e.setCancelled(true);

            int tier = rawslot-10;

            if(rawslot<11 || rawslot>15) {
                if(rawslot==26){
                    player.closeInventory();
                    addDrop(player, entitytype, tier-1);
                }
            } else {
                SpawnerType spawnerType = OSMain.spawnerTypes.get(entitytype);
                ArrayList<Drop> drops = spawnerType.getDrops();
                if(drops.size()>=tier) {
                    player.closeInventory();
                    changeDrop(player, entitytype, rawslot - 10);
                }
            }

        }

        public void addDrop(Player player, String entitytype, int tier) {
            OSMain plugin = JavaPlugin.getPlugin(OSMain.class);
            PlayerChatInput.PlayerChatInputBuilder<Integer> builder =
                    new PlayerChatInput.PlayerChatInputBuilder<>(plugin, player);


            builder.isValidInput((p, str) -> {
                try {
                    return str.split(" ").length==1 && (Integer.valueOf(str)>0 && Integer.valueOf(str) <=100);
                } catch (Exception exception) {
                    return false;
                }
            }).setValue((p, str) -> {
                return Integer.valueOf(str);
            }).onInvalidInput((p, str) -> {
                p.sendMessage("Invalid input.");
                return true;
            }).sendValueMessage(TextUtils.colorParse("&l&aPick the item you want to drop for this tier and then type the chance to drop from 1 to 100"))
                    .toCancel("cancel").defaultValue(100)
                    .onFinish((p, value) -> {
                        SpawnerType spawnerType;
                            spawnerType = OSMain.spawnerTypes.get(entitytype);

                        ArrayList<Drop> drops = spawnerType.getDrops();
                        Drop drop = new Drop();
                        ItemStack itemStack = p.getInventory().getItemInMainHand();
                        int amount = itemStack.getAmount();
                        itemStack.setAmount(1);
                        drop.setItemstack(itemStack);
                        drop.setAmount(amount);
                        drop.setChance(value);
                        drops.add(drop);

                        spawnerType.setDrops(drops);
                        OSMain.spawnerTypes.put(entitytype, spawnerType);
                        OSMain.saveSpawnerTypes();
                        try {
                            EntityDropsGui.open(p, entitytype);
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }

                    }).build().start();
        }

        public void changeDrop(Player player, String entitytype, int tier){
            OSMain plugin = JavaPlugin.getPlugin(OSMain.class);
            PlayerChatInput.PlayerChatInputBuilder<Integer> builder =
                    new PlayerChatInput.PlayerChatInputBuilder<>(plugin, player);


            builder.isValidInput((p, str) -> {
                try {
                    return str.split(" ").length==1 && (Integer.valueOf(str)>0 && Integer.valueOf(str) <=100);
                } catch (Exception exception) {
                    return false;
                }
            }).setValue((p, str) -> {
                return Integer.valueOf(str);
            }).onInvalidInput((p, str) -> {
                p.sendMessage("Invalid input.");
                return true;
            }).sendValueMessage(TextUtils.colorParse("&l&aPick the item you want to drop for this tier and then type the chance to drop from 1 to 100"))
                    .toCancel("cancel").defaultValue(100)
                    .onFinish((p, value) -> {
                        SpawnerType spawnerType;

                            spawnerType = OSMain.spawnerTypes.get(entitytype);


                        ArrayList<Drop> drops = spawnerType.getDrops();
                        Drop drop = new Drop();
                        ItemStack itemStack = p.getInventory().getItemInMainHand();
                        int amount = itemStack.getAmount();
                        itemStack.setAmount(1);
                        drop.setItemstack(itemStack);
                        drop.setAmount(amount);
                        drop.setChance(value);
                        drops.set(tier-1, drop);

                        spawnerType.setDrops(drops);
                        OSMain.spawnerTypes.remove(entitytype);
                        OSMain.spawnerTypes.put(entitytype, spawnerType);
                        OSMain.saveSpawnerTypes();
                        try {
                            EntityDropsGui.open(p, entitytype);
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }

                    }).build().start();
        }

    }

}
