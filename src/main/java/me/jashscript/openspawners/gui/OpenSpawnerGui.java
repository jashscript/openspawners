package me.jashscript.openspawners.gui;

import me.jashscript.openspawners.OSMain;
import me.jashscript.openspawners.models.BlockLocation;
import me.jashscript.openspawners.models.OpenSpawnerBlock;
import me.jashscript.openspawners.models.SpawnerType;
import me.jashscript.openspawners.utils.ItemUtils;
import me.jashscript.openspawners.utils.TextUtils;
import net.sourcewriters.minecraft.versiontools.entity.Hologram;
import net.sourcewriters.minecraft.versiontools.utils.bukkit.Players;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.IOException;

public class OpenSpawnerGui implements InventoryHolder {

    BlockLocation blockLocation;
    String entityType;
    int amount;
    int tier;

    public OpenSpawnerGui(BlockLocation blockLocation, String entityType, int amount, int tier) {
        this.blockLocation = blockLocation;
        this.entityType = entityType;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }


    public static void open(Player player, BlockLocation blockLocation, String entityType) {
        OpenSpawnerBlock openSpawnerBlock = OSMain.openSpawnerBlocks.get(blockLocation);
        SpawnerType spawnerType = OSMain.spawnerTypes.get(entityType);

        Inventory inventory = Bukkit.getServer().createInventory(new OpenSpawnerGui(blockLocation, entityType
                        , openSpawnerBlock.getAmount(), openSpawnerBlock.getTier()), 27,
                TextUtils.colorParse(
                        OSMain.config.getList("item-format")
                                .get(openSpawnerBlock.getTier() - 1)
                                .toString()).replace("%amount%", String.valueOf(openSpawnerBlock.getTier()))
                        .replace("%type%", TextUtils.entityTypeParse(entityType))
                        .replace("%tier%", String.valueOf(openSpawnerBlock.getTier())));

        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, grayPane());
        }


        inventory.setItem(11, info());
        inventory.setItem(13, spawner(spawnerType, openSpawnerBlock));
        inventory.setItem(15, upgrade(spawnerType, openSpawnerBlock));

        player.openInventory(inventory);

    }


    private static ItemStack grayPane() {
        return new ItemUtils.ItemstackBuilder(Material.GRAY_STAINED_GLASS_PANE).name("").build();
    }

    private static ItemStack info() {
        return new ItemUtils.ItemstackBuilder(Material.NETHER_STAR).name("&eInfo")
                .LoreLine("&7Spawners stack per chunk and")
                .LoreLine("&7can be upgraded using money.")
                .LoreLine("&7Each tier causes the mob to drop")
                .LoreLine("&7different loot!").build();
    }

    private static ItemStack spawner(SpawnerType spawnerType, OpenSpawnerBlock openSpawnerBlock) {
        return new ItemUtils.ItemstackBuilder(Material.SPAWNER).name("&eSpawner")
                .LoreLine("&7Stack Size: &a" + String.valueOf(openSpawnerBlock.getAmount()))
                .LoreLine("&7Tier: &a" + String.valueOf(openSpawnerBlock.getTier()))
                .LoreLine("&7Drop: &a" + TextUtils.parseItemStack(spawnerType.getDrops().get(openSpawnerBlock.getTier() - 1).getItemstack()))
                .build();
    }


    private static ItemStack upgrade(SpawnerType spawnerType, OpenSpawnerBlock openSpawnerBlock) {
        return new ItemUtils.ItemstackBuilder(Material.EMERALD).name("&eUpgrade Spawner")
                .LoreLine("&7Click here to upgrade your spawners!")
                .LoreLine("&7Cost: &a" +
                        (Integer.valueOf(openSpawnerBlock.getTier()) == 5 ?
                                "Already max!" : spawnerType.getCosts().get(openSpawnerBlock.getTier() - 1)))
                .build();
    }

    public static class invListener implements Listener {

        @EventHandler
        public void drag(InventoryDragEvent event) {
            if (!(event.getInventory().getHolder() instanceof OpenSpawnerGui)) return;
            event.setCancelled(true);
        }


        @EventHandler
        public void click(InventoryClickEvent e) throws IOException {
            if (!(e.getInventory().getHolder() instanceof OpenSpawnerGui)) return;
            int rawslot = e.getRawSlot();
            Player player = (Player) e.getWhoClicked();
            if (rawslot < 27) e.setCancelled(true);

            String entitytype = ((OpenSpawnerGui) e.getInventory().getHolder()).entityType;
            BlockLocation blockLocation = ((OpenSpawnerGui) e.getInventory().getHolder()).blockLocation;
            OpenSpawnerBlock openSpawnerBlock = OSMain.openSpawnerBlocks.get(blockLocation);
            int tier = openSpawnerBlock.getTier();

            if (rawslot == 15) {
                if(tier>=5) return;
                SpawnerType spawnerType = OSMain.spawnerTypes.get(entitytype);

                int price = spawnerType.getCosts().get(tier);

                int balance = (int) OSMain.econ.getBalance(player);

                player.closeInventory();

                if (price > balance) {
                    player.sendMessage(TextUtils.colorParse("&l&cYou don't have enough money to upgrade."));
                    return;
                }

                OSMain.econ.withdrawPlayer(player, price);
                openSpawnerBlock.setTier(openSpawnerBlock.getTier() + 1);

                Hologram h = OSMain.holograms.get(openSpawnerBlock.getBlockLocation());
                h.remove();

                Hologram hologram = OSMain.entityManager.create(Hologram.BUILDER);
                hologram.teleport(getCenter(openSpawnerBlock.getBlockLocation().toLocation()).add(new Vector(0, 0.25, 0)));
                hologram.addLine(TextUtils.colorParse(
                        OSMain.config.getList("item-format")
                                .get(openSpawnerBlock.getTier() - 1)
                                .toString()).replace("%amount%", String.valueOf(openSpawnerBlock.getAmount()))
                        .replace("%type%", TextUtils.entityTypeParse(openSpawnerBlock.getType()))
                        .replace("%tier%", String.valueOf(openSpawnerBlock.getTier())));
                hologram.spawn();
                hologram.show(Players.getOnline());
                OSMain.holograms.put(openSpawnerBlock.getBlockLocation(), hologram);

                player.sendMessage(TextUtils.colorParse("&l&aSpawner upgraded!"));

            }
        }

        public Location getCenter(Location loc) {
            return new Location(loc.getWorld(),
                    getRelativeCoord(loc.getBlockX()),
                    getRelativeCoord(loc.getBlockY()),
                    getRelativeCoord(loc.getBlockZ()));
        }

        private double getRelativeCoord(int i) {
//        double d = i;
//        d = d < 0 ? d - .5 : d + .5;
            double d = i;
            return i + 0.5;
        }


    }
}
