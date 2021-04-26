package me.jashscript.openspawners.listeners;

import me.jashscript.openspawners.OSMain;
import me.jashscript.openspawners.data.Mapper;
import me.jashscript.openspawners.gui.OpenSpawnerGui;
import me.jashscript.openspawners.models.*;
import me.jashscript.openspawners.utils.ItemUtils;
import me.jashscript.openspawners.utils.TextUtils;
import net.sourcewriters.minecraft.versiontools.entity.Hologram;
import net.sourcewriters.minecraft.versiontools.entity.handler.CustomEntity;
import net.sourcewriters.minecraft.versiontools.entity.handler.DefaultEntityType;
import net.sourcewriters.minecraft.versiontools.utils.bukkit.Players;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class OSListener implements Listener {

    static int index = 0;

    private OSMain plugin;

    public OSListener(OSMain plugin) {
        this.plugin = plugin;
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void placeBlock(BlockPlaceEvent e) {
        if (e.isCancelled()) return;
        ItemStack itemStack = e.getItemInHand();
        if (!ItemUtils.isOpenSpawner(itemStack)) return;

        ArrayList<String> worldWhitelist = (ArrayList<String>) plugin.getConfig().getList("world-whitelist");

        Block block = e.getBlock();
        Location location = block.getLocation();
        String world = location.getWorld().getName();

        if(!worldWhitelist.contains(world)) {
            e.setCancelled(true);
            return;
        }

        BlockLocation blockLocation = new BlockLocation(location);
        Player player = e.getPlayer();
        OpenSpawner openSpawner = Mapper.OpenSpawner.fromItem(itemStack);
        OpenSpawnerBlock openSpawnerBlock = Mapper.OpenSpawnerBlock.fromPlacing(openSpawner, blockLocation, player);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            CreatureSpawner creatureSpawner;
            try {
                creatureSpawner = (CreatureSpawner) block.getState();
            } catch (Exception exception) {
                System.out.println("ERROR CREATURESPAWNER PLEASE TELL JASH");
                return;
            }
            creatureSpawner.setSpawnedType(EntityType.valueOf(openSpawner.getType()));
            creatureSpawner.setSpawnCount(openSpawner.getAmount() * 4);
            creatureSpawner.update();
            OSMain.openSpawnerBlocks.put(blockLocation, openSpawnerBlock);
            Chunk chunk = block.getChunk();
            if (OSMain.OSBChunkIdentification.containsKey(chunk)) {

                ArrayList<OpenSpawnerBlock> list = OSMain.OSBChunkIdentification.get(chunk);
                AtomicBoolean a = new AtomicBoolean(false);
                list.forEach(OSB -> {
                    if (OSB.getType().equals(openSpawnerBlock.getType()) && OSB.getTier() == openSpawnerBlock.getTier()) {
                        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                            location.getBlock().setType(Material.AIR);
                        }, 1L);
                        OSB.setAmount(OSB.getAmount() + openSpawnerBlock.getAmount());

                        CreatureSpawner CS;
                        try {
                            CS = (CreatureSpawner) OSB.getBlockLocation().toLocation().getBlock().getState();
                        } catch (Exception exception) {
                            System.out.println("ERROR CREATURESPAWNER PLEASE TELL JASH");
                            return;
                        }
                        CS.setSpawnCount(OSB.getAmount() * 4);
                        CS.update();

                        Hologram h = OSMain.holograms.get(OSB.getBlockLocation());
                        h.remove();

                        Hologram hologram = OSMain.entityManager.create(Hologram.BUILDER);
                        hologram.teleport(getCenter(OSB.getBlockLocation().toLocation()).add(new Vector(0, 0.25, 0)));
                        hologram.addLine(TextUtils.colorParse(
                                OSMain.config.getList("item-format")
                                        .get(OSB.getTier() - 1)
                                        .toString()).replace("%amount%", String.valueOf(OSB.getAmount()))
                                .replace("%type%", TextUtils.entityTypeParse(OSB.getType()))
                                .replace("%tier%", String.valueOf(OSB.getTier())));
                        hologram.spawn();
                        hologram.show(Players.getOnline());
                        OSMain.holograms.put(OSB.getBlockLocation(), hologram);
                        a.set(true);
                    }
                });
                if (a.get()) return;
                list.add(openSpawnerBlock);
                Hologram hologram = OSMain.entityManager.create(Hologram.BUILDER);
                hologram.teleport(getCenter(location).add(new Vector(0, 0.25, 0)));
                hologram.addLine(TextUtils.colorParse(
                        OSMain.config.getList("item-format")
                                .get(openSpawner.getTier() - 1)
                                .toString()).replace("%amount%", String.valueOf(openSpawner.getAmount()))
                        .replace("%type%", TextUtils.entityTypeParse(openSpawner.getType()))
                        .replace("%tier%", String.valueOf(openSpawner.getTier())));
                hologram.spawn();
                hologram.show(Players.getOnline());
                OSMain.holograms.put(openSpawnerBlock.getBlockLocation(), hologram);
            } else {
                ArrayList<OpenSpawnerBlock> newList = new ArrayList<>();
                newList.add(openSpawnerBlock);
                OSMain.OSBChunkIdentification.put(chunk, newList);
                Hologram hologram = OSMain.entityManager.create(Hologram.BUILDER);
                hologram.teleport(getCenter(location).add(new Vector(0, 0.25, 0)));
                hologram.addLine(TextUtils.colorParse(
                        OSMain.config.getList("item-format")
                                .get(openSpawner.getTier() - 1)
                                .toString()).replace("%amount%", String.valueOf(openSpawner.getAmount()))
                        .replace("%type%", TextUtils.entityTypeParse(openSpawner.getType()))
                        .replace("%tier%", String.valueOf(openSpawner.getTier())));
                hologram.spawn();
                hologram.show(Players.getOnline());
                OSMain.holograms.put(openSpawnerBlock.getBlockLocation(), hologram);
            }
        }, 1L);
    }

    @EventHandler
    public void interact(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        Block block = e.getClickedBlock();
        if (block.getType() != Material.SPAWNER) return;
        Location location = block.getLocation();
        BlockLocation blockLocation = new BlockLocation(location);
        if (!OSMain.openSpawnerBlocks.containsKey(blockLocation)) return;
        OpenSpawnerBlock openSpawnerBlock = OSMain.openSpawnerBlocks.get(blockLocation);
        Player player = e.getPlayer();
        if (!openSpawnerBlock.getOwner().equals(player.getUniqueId()) && !player.isOp()) return;

        SpawnerType spawnerType = OSMain.spawnerTypes.get(openSpawnerBlock.getType());

        OpenSpawnerGui.open(player, blockLocation, openSpawnerBlock.getType());

    }

    @EventHandler
    public void breakBlock(BlockBreakEvent e) {
        if (e.isCancelled()) return;
        Block block = e.getBlock();
        Location location = block.getLocation();
        BlockLocation blockLocation = new BlockLocation(location);
        if (!OSMain.openSpawnerBlocks.containsKey(blockLocation)) return;
        OpenSpawnerBlock openSpawnerBlock = OSMain.openSpawnerBlocks.get(blockLocation);
        if (plugin.getConfig().getBoolean("only-owner-can-break")) {
            Player player = e.getPlayer();
            if (openSpawnerBlock.getOwner() != player.getUniqueId()) {
                e.setCancelled(true);
                plugin.log.playerMessage(player, "You can't break another person's spawner!");
            }
        }
        if (e.getPlayer().getInventory().getItemInMainHand().getType().toString().toLowerCase(Locale.ROOT).contains("pickaxe")) {
            if (plugin.getConfig().getBoolean("need-silktouch")) {
                if (e.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
                    location.getWorld().dropItemNaturally(location, Mapper.OpenSpawnerBlock.toItemStack(openSpawnerBlock));
                }
            } else {
                location.getWorld().dropItemNaturally(location, Mapper.OpenSpawnerBlock.toItemStack(openSpawnerBlock));
            }
        }
        Hologram x = OSMain.holograms.get(openSpawnerBlock.getBlockLocation());
        OSMain.holograms.remove(openSpawnerBlock.getBlockLocation());
        x.remove();
        OSMain.openSpawnerBlocks.remove(blockLocation);
        if (OSMain.OSBChunkIdentification.containsKey(getCenter(location).getChunk())) {
            ArrayList<OpenSpawnerBlock> OSBList = OSMain.OSBChunkIdentification.get(getCenter(location).getChunk());
            OSBList.remove(openSpawnerBlock);
        }
    }


    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        for (CustomEntity customEntity : OSMain.entityManager.getAll()) {
            if (!(customEntity.getType() == DefaultEntityType.HOLOGRAM)) continue;
            Hologram hologram = (Hologram) customEntity;
            hologram.hide(e.getPlayer());
            hologram.show(e.getPlayer());
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

    @EventHandler
    public void spawn(SpawnerSpawnEvent e) {
        Block block = e.getSpawner().getBlock();
        BlockLocation blockLocation = new BlockLocation(block.getLocation());
        if (!OSMain.openSpawnerBlocks.containsKey(blockLocation)) {
            e.setCancelled(true);
            return;
        }

        OpenSpawnerBlock openSpawnerBlock = OSMain.openSpawnerBlocks.get(blockLocation);

        Entity entity = e.getEntity();
        EntityType entityType = entity.getType();
        for (Entity nearbyEntity : entity.getNearbyEntities(20, 20, 20)) {
            if (!(entityType == nearbyEntity.getType())) continue;

            PersistentDataContainer persistentDataContainer = nearbyEntity.getPersistentDataContainer();

            if (!persistentDataContainer.has(new NamespacedKey(plugin, "openspawner"), PersistentDataType.STRING))
                continue;

            String loc = persistentDataContainer.get(new NamespacedKey(plugin, "openspawner"), PersistentDataType.STRING);

            if (!(loc.equals(blockLocation.toString()))) continue;

            int tier = persistentDataContainer.get(new NamespacedKey(plugin, "tier"), PersistentDataType.INTEGER);

            if (!(openSpawnerBlock.getTier() == tier)) continue;

            int amount = persistentDataContainer.get(new NamespacedKey(plugin, "amount"), PersistentDataType.INTEGER);

            if (amount == plugin.getConfig().getInt("max-mob-stack")) continue;

            e.setCancelled(true);
            persistentDataContainer.set(new NamespacedKey(plugin, "amount"), PersistentDataType.INTEGER, amount + 1);
            updateStackedEntity(nearbyEntity);

            return;
        }

        PersistentDataContainer entityContainer = entity.getPersistentDataContainer();

        entityContainer.set(new NamespacedKey(plugin, "openspawner"), PersistentDataType.STRING, blockLocation.toString());
        entityContainer.set(new NamespacedKey(plugin, "tier"), PersistentDataType.INTEGER, openSpawnerBlock.getTier());
        entityContainer.set(new NamespacedKey(plugin, "amount"), PersistentDataType.INTEGER, openSpawnerBlock.getAmount());
        updateStackedEntity(entity);
    }

    @EventHandler
    public void onVanillaDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Mob)) return;
        Mob mob = (Mob) e.getEntity();

        PersistentDataContainer persistentDataContainer = mob.getPersistentDataContainer();
        if (!persistentDataContainer.has(new NamespacedKey(plugin, "openspawner"), PersistentDataType.STRING)) return;

        e.getDrops().clear();

    }

    @EventHandler
    public void onDeath(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Mob)) return;
        Mob mob = (Mob) e.getEntity();

        if(!((mob.getHealth()- e.getDamage()) <=0)) return;

        PersistentDataContainer persistentDataContainer = mob.getPersistentDataContainer();

        if (!persistentDataContainer.has(new NamespacedKey(plugin, "openspawner"), PersistentDataType.STRING)) return;

        int tier = persistentDataContainer.get(new NamespacedKey(plugin, "tier"), PersistentDataType.INTEGER);

        int amount = persistentDataContainer.get(new NamespacedKey(plugin, "amount"), PersistentDataType.INTEGER);

        ArrayList<Drop> drops = OSMain.spawnerTypes.get(mob.getType().name()).getDrops();

        Drop drop = drops.get(tier-1);

        EntityDamageEvent.DamageCause dc = e.getCause();
        Player killer = mob.getKiller();

        if(dc.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) && killer != null){
            if(amount>1){
                e.setCancelled(true);
                persistentDataContainer.set(new NamespacedKey(plugin, "amount"), PersistentDataType.INTEGER, amount-1);
                updateStackedEntity(mob);
                mob.setHealth(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
                int rand = new Random().nextInt(100);
                if(rand<=drop.getChance()){
                    Location location = mob.getLocation();
                    ItemStack itemStack = drop.getItemstack();
                    itemStack.setAmount(drop.getAmount());
                    location.getWorld().dropItemNaturally(location, itemStack);
                }
            } else {
                int rand = new Random().nextInt(100);
                if(rand<=drop.getChance()){
                    Location location = mob.getLocation();
                    ItemStack itemStack = drop.getItemstack();
                    itemStack.setAmount(drop.getAmount());
                    location.getWorld().dropItemNaturally(location, itemStack);
                }
            }
        } else {
            if(plugin.getConfig().getBoolean("non-player-kill-stack")) {
                for (int i = 0; i < amount; i++) {
                    int rand = new Random().nextInt(100);
                    if (rand <= drop.getChance()) {
                        Location location = mob.getLocation();
                        ItemStack itemStack = drop.getItemstack();
                        itemStack.setAmount(drop.getAmount());
                        location.getWorld().dropItemNaturally(location, itemStack);
                    }
                }
            } else {
                if(amount>1){
                    e.setCancelled(true);
                    persistentDataContainer.set(new NamespacedKey(plugin, "amount"), PersistentDataType.INTEGER, amount-1);
                    updateStackedEntity(mob);
                    mob.setHealth(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
                    int rand = new Random().nextInt(100);
                    if(rand<=drop.getChance()){
                        Location location = mob.getLocation();
                        ItemStack itemStack = drop.getItemstack();
                        itemStack.setAmount(drop.getAmount());
                        location.getWorld().dropItemNaturally(location, itemStack);
                    }
                } else {
                    int rand = new Random().nextInt(100);
                    if (rand <= drop.getChance()) {
                        Location location = mob.getLocation();
                        ItemStack itemStack = drop.getItemstack();
                        itemStack.setAmount(drop.getAmount());
                        location.getWorld().dropItemNaturally(location, itemStack);
                    }
                }
            }
        }

    }


    private void updateStackedEntity(Entity entity) {
        Mob mob = (Mob) entity;
        PersistentDataContainer persistentDataContainer = mob.getPersistentDataContainer();
        int tier = persistentDataContainer.get(new NamespacedKey(plugin, "tier"), PersistentDataType.INTEGER);
        int amount = persistentDataContainer.get(new NamespacedKey(plugin, "amount"), PersistentDataType.INTEGER);
        String type = mob.getType().toString();

        mob.setCustomName(TextUtils.colorParse(
                OSMain.config.getList("mob-format")
                        .get(tier - 1)
                        .toString()).replace("%amount%", String.valueOf(amount))
                .replace("%type%", TextUtils.entityTypeParse(type))
                .replace("%tier%", String.valueOf(tier)));
        mob.setCustomNameVisible(true);

    }


}
