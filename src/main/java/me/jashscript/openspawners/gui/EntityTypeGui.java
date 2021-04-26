package me.jashscript.openspawners.gui;

import me.jashscript.openspawners.OSMain;
import me.jashscript.openspawners.data.Persistance;
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
import java.util.Locale;

public class EntityTypeGui implements InventoryHolder {

    public String entitytype;

    public EntityTypeGui(String entitytype) {
        this.entitytype = entitytype;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public static void open(Player player, String entityType) {
        Inventory inventory = Bukkit.getServer().createInventory(new EntityTypeGui(entityType), 27, "Editing " + TextUtils.entityTypeParse(entityType) + "Spawner Type");
        ItemStack costs = new ItemUtils.ItemstackBuilder(Material.EMERALD).name("&l&fUpgrade Costs").build();
        ItemStack drops = new ItemUtils.ItemstackBuilder(Material.BONE).name("&l&fDrops").build();

        inventory.setItem(11, costs);
        inventory.setItem(15, drops);
        player.openInventory(inventory);
    }

    public static class invListener implements Listener {

        @EventHandler
        public void drag(InventoryDragEvent event){
            if(!(event.getInventory().getHolder() instanceof EntityTypeGui)) return;
            event.setCancelled(true);
        }


        @EventHandler
        public void click(InventoryClickEvent e) throws IOException {
            if(!(e.getInventory().getHolder() instanceof EntityTypeGui)) return;
            int rawslot = e.getRawSlot();
            Player player = (Player)e.getWhoClicked();
            if(rawslot<27) e.setCancelled(true);

            String entitytype = ((EntityTypeGui)e.getInventory().getHolder()).entitytype;

            if(rawslot==11){
                player.closeInventory();
                addCosts(player, entitytype);
            } else if(rawslot==15){
                EntityDropsGui.open(player, entitytype.toUpperCase(Locale.ROOT));
            }
        }




        public void addCosts(Player player, String entitytype){
            OSMain plugin = JavaPlugin.getPlugin(OSMain.class);
            PlayerChatInput.PlayerChatInputBuilder<String> builder =
                    new PlayerChatInput.PlayerChatInputBuilder<>(plugin, player);


            builder.isValidInput((p, str) -> {
                try {
                    return str.split(" ").length==4;
                } catch (Exception exception) {
                    return false;
                }
            }).setValue((p, str) -> str).onInvalidInput((p, str) -> {
                p.sendMessage("Invalid input.");
                return true;
            }).sendValueMessage(TextUtils.colorParse("&l&aWrite in chat the different costs for tier 2-5, separeted by spaces. Example: 100 500 1200 2000 (tier 1 don't need price). Type cancel to cancel."))
                    .toCancel("cancel").defaultValue("")
                    .onFinish((p, value) -> {
                        String[] args = value.split(" ");
                        SpawnerType spawnerType;
                        try {
                            spawnerType = Persistance.getSpawnerType(entitytype);
                        } catch (IOException exception) {
                            OSMain.log.playerError(player, "Something went wrong with the spawner type.");
                            return;
                        }

                        ArrayList<Integer> costs = new ArrayList<>();
                        try {
                            costs.add(Integer.valueOf(args[0]));
                            costs.add(Integer.valueOf(args[1]));
                            costs.add(Integer.valueOf(args[2]));
                            costs.add(Integer.valueOf(args[3]));
                        } catch (Exception exception){
                            p.sendMessage(TextUtils.colorParse("&cWrong formatting! Use numbers as the costs please."));
                            return;
                        }

                        spawnerType.setCosts(costs);
                        OSMain.spawnerTypes.put(entitytype, spawnerType);
                        OSMain.saveSpawnerTypes();
                        EntityTypeGui.open(player, entitytype);

                    }).build().start();
        }
    }




}
