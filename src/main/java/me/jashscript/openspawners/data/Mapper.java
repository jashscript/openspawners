package me.jashscript.openspawners.data;

import com.syntaxphoenix.syntaxapi.nbt.NbtCompound;
import com.syntaxphoenix.syntaxapi.nbt.NbtList;
import com.syntaxphoenix.syntaxapi.nbt.NbtNamedTag;
import com.syntaxphoenix.syntaxapi.nbt.NbtString;
import me.jashscript.openspawners.OSMain;
import me.jashscript.openspawners.models.BlockLocation;
import me.jashscript.openspawners.models.Drop;
import me.jashscript.openspawners.models.OpenSpawner;
import me.jashscript.openspawners.models.OpenSpawnerBlock;
import me.jashscript.openspawners.utils.ItemUtils;
import me.jashscript.openspawners.utils.TextUtils;
import net.sourcewriters.minecraft.versiontools.entity.Hologram;
import net.sourcewriters.minecraft.versiontools.reflection.BukkitConversion;
import net.sourcewriters.minecraft.versiontools.reflection.VersionControl;
import net.sourcewriters.minecraft.versiontools.utils.bukkit.Players;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class Mapper {

    private static final BukkitConversion<?> bukkitConversion = VersionControl.get().getBukkitConversion();
    private static OSMain plugin = JavaPlugin.getPlugin(OSMain.class);

    public static class SpawnerType {

        public static NbtNamedTag toNbt(me.jashscript.openspawners.models.SpawnerType spawnerType) {
            NbtCompound root = new NbtCompound();
            root.set("type", spawnerType.getType());
            NbtCompound drops = new NbtCompound();
            int i = 0;
            for (Drop d : spawnerType.getDrops()) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.set("itemstack", bukkitConversion.itemToCompound(d.getItemstack()));
                nbtCompound.set("amount", d.getAmount());
                nbtCompound.set("chance", d.getChance());
                drops.set(String.valueOf(i), nbtCompound);
                i++;
            }
            root.set("drops", drops);
            i = 0;
            NbtCompound costs = new NbtCompound();
            for (Integer cost : spawnerType.getCosts()) {
                costs.set(String.valueOf(i), cost);
                i++;
            }
            root.set("costs", costs);
            return new NbtNamedTag("spawnerType", root);
        }

        public static me.jashscript.openspawners.models.SpawnerType fromNbt(NbtNamedTag nbtNamedTag) {
            me.jashscript.openspawners.models.SpawnerType spawnerType = new me.jashscript.openspawners.models.SpawnerType();
            NbtCompound nbtCompound = (NbtCompound) nbtNamedTag.getTag();
            spawnerType.setType(nbtCompound.getString("type"));

            NbtCompound drops = nbtCompound.getCompound("drops");
            ArrayList<Drop> droplist = new ArrayList<>();
            int index = 0;
            while (drops.hasKey(String.valueOf(index))) {
                Drop drop = new Drop();
                NbtCompound dropCompound = drops.getCompound(String.valueOf(index));
                drop.setAmount(dropCompound.getInt("amount"));
                drop.setChance(dropCompound.getInt("chance"));
                drop.setItemstack(bukkitConversion.itemFromCompound(dropCompound.getCompound("itemstack")));
                droplist.add(drop);
                index++;
            }
            spawnerType.setDrops(droplist);

            ArrayList<Integer> costlist = new ArrayList<>();
            NbtCompound costs = nbtCompound.getCompound("costs");

            index = 0;
            while (costs.hasKey(String.valueOf(index))) {
                costlist.add(costs.getInt(String.valueOf(index)));
                index++;
            }

            spawnerType.setCosts(costlist);

            return spawnerType;
        }

    }

    public static class OpenSpawnerBlock {

        public static NbtNamedTag toNbt(ArrayList<me.jashscript.openspawners.models.OpenSpawnerBlock> openSpawnerBlocks) {
            NbtCompound root = new NbtCompound();
            for (int i = 0; i < openSpawnerBlocks.size(); i++) {
                NbtCompound openSpawnerBlockCompound = new NbtCompound();
                me.jashscript.openspawners.models.OpenSpawnerBlock openSpawnerBlock = openSpawnerBlocks.get(i);
                openSpawnerBlockCompound.set("type", openSpawnerBlock.getType());
                openSpawnerBlockCompound.set("tier", openSpawnerBlock.getTier());
                openSpawnerBlockCompound.set("amount", openSpawnerBlock.getAmount());
                openSpawnerBlockCompound.set("mobAI", openSpawnerBlock.isMobAI());
                openSpawnerBlockCompound.set("owner", String.valueOf(openSpawnerBlock.getOwner()));
                NbtCompound location = new NbtCompound();
                BlockLocation blockLocation = openSpawnerBlock.getBlockLocation();
                location.set("world", blockLocation.getWorld());
                location.set("x", blockLocation.getX());
                location.set("y", blockLocation.getY());
                location.set("z", blockLocation.getZ());
                openSpawnerBlockCompound.set("location", location);
                root.set(String.valueOf(i), openSpawnerBlockCompound);
            }
            return new NbtNamedTag("openSpawnerBlocks", root);
        }

        public static NbtCompound toNbt(me.jashscript.openspawners.models.OpenSpawnerBlock openSpawnerBlock) {
            NbtCompound openSpawnerBlockCompound = new NbtCompound();
            openSpawnerBlockCompound.set("type", openSpawnerBlock.getType());
            openSpawnerBlockCompound.set("tier", openSpawnerBlock.getTier());
            openSpawnerBlockCompound.set("amount", openSpawnerBlock.getAmount());
            openSpawnerBlockCompound.set("mobAI", openSpawnerBlock.isMobAI());
            openSpawnerBlockCompound.set("owner", String.valueOf(openSpawnerBlock.getOwner()));
            NbtCompound location = new NbtCompound();
            BlockLocation blockLocation = openSpawnerBlock.getBlockLocation();
            location.set("world", blockLocation.getWorld());
            location.set("x", blockLocation.getX());
            location.set("y", blockLocation.getY());
            location.set("z", blockLocation.getZ());
            openSpawnerBlockCompound.set("location", location);
            return openSpawnerBlockCompound;
        }


        public static ArrayList<me.jashscript.openspawners.models.OpenSpawnerBlock> fromNbt(NbtNamedTag nbtNamedTag) {
            NbtCompound nbtCompound = (NbtCompound) nbtNamedTag.getTag();
            ArrayList<me.jashscript.openspawners.models.OpenSpawnerBlock> output = new ArrayList<>();
            int index = 0;
            while (nbtCompound.hasKey(String.valueOf(index))) {
                NbtCompound openSpawnerBlockCompound = nbtCompound.getCompound(String.valueOf(index));
                me.jashscript.openspawners.models.OpenSpawnerBlock openSpawnerBlock = new me.jashscript.openspawners.models.OpenSpawnerBlock();
                openSpawnerBlock.setType(openSpawnerBlockCompound.getString("type"));
                openSpawnerBlock.setAmount(openSpawnerBlockCompound.getInt("amount"));
                openSpawnerBlock.setTier(openSpawnerBlockCompound.getInt("tier"));
                openSpawnerBlock.setOwner(UUID.fromString(openSpawnerBlockCompound.getString("owner")));
                NbtCompound blockLocationCompound = openSpawnerBlockCompound.getCompound("location");
                BlockLocation blockLocation = new BlockLocation(
                        blockLocationCompound.getString("world"),
                        blockLocationCompound.getInt("x"),
                        blockLocationCompound.getInt("y"),
                        blockLocationCompound.getInt("z")
                );
                openSpawnerBlock.setBlockLocation(blockLocation);
                output.add(openSpawnerBlock);
                index++;
            }
            return output;
        }

        public static me.jashscript.openspawners.models.OpenSpawnerBlock fromNbt(NbtCompound openSpawnerBlockCompound) {
            me.jashscript.openspawners.models.OpenSpawnerBlock openSpawnerBlock = new me.jashscript.openspawners.models.OpenSpawnerBlock();
            openSpawnerBlock.setType(openSpawnerBlockCompound.getString("type"));
            openSpawnerBlock.setAmount(openSpawnerBlockCompound.getInt("amount"));
            openSpawnerBlock.setOwner(UUID.fromString(openSpawnerBlockCompound.getString("owner")));
            openSpawnerBlock.setTier(openSpawnerBlockCompound.getInt("tier"));
            NbtCompound blockLocationCompound = openSpawnerBlockCompound.getCompound("location");
            BlockLocation blockLocation = new BlockLocation(
                    blockLocationCompound.getString("world"),
                    blockLocationCompound.getInt("x"),
                    blockLocationCompound.getInt("y"),
                    blockLocationCompound.getInt("z"));

            openSpawnerBlock.setBlockLocation(blockLocation);

            return openSpawnerBlock;
        }

        public static me.jashscript.openspawners.models.OpenSpawnerBlock fromPlacing(me.jashscript.openspawners.models.OpenSpawner openSpawner
                , BlockLocation blockLocation, Player owner) {
            me.jashscript.openspawners.models.OpenSpawnerBlock openSpawnerBlock = new me.jashscript.openspawners.models.OpenSpawnerBlock();
            openSpawnerBlock.setBlockLocation(blockLocation);
            openSpawnerBlock.setOwner(owner.getUniqueId());
            openSpawnerBlock.setMobAI(true);
            openSpawnerBlock.setType(openSpawner.getType());
            openSpawnerBlock.setTier(openSpawner.getTier());
            openSpawnerBlock.setAmount(openSpawner.getAmount());
            return openSpawnerBlock;
        }


        public static ItemStack toItemStack(me.jashscript.openspawners.models.OpenSpawnerBlock openSpawnerBlock) {
            return Mapper.OpenSpawner.toItemStack(Mapper.OpenSpawner.fromOpenSpawnerBlock(openSpawnerBlock));
        }

    }

    public static class OpenSpawner {

        public static me.jashscript.openspawners.models.OpenSpawner fromItem(ItemStack itemStack) {
            me.jashscript.openspawners.models.OpenSpawner openSpawner = new me.jashscript.openspawners.models.OpenSpawner();
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) {
                plugin.log.error("Error parsing ItemStack to OpenSpawner! That's a really bad thing and probably something gone real bad for it to happen.");
                plugin.log.error("Jash if you are seeing this you are dumb. And if it's not Jash sorry to all devs about the messy code.");
                return null;
            }

            PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();

            try {
                openSpawner.setType(persistentDataContainer.get(new NamespacedKey(plugin, "type"), PersistentDataType.STRING));
                openSpawner.setAmount(persistentDataContainer.get(new NamespacedKey(plugin, "amount"), PersistentDataType.INTEGER));
                openSpawner.setTier(persistentDataContainer.get(new NamespacedKey(plugin, "tier"), PersistentDataType.INTEGER));
            } catch (Exception exception) {
                plugin.log.error("Error parsing ItemStack to OpenSpawner! It should not happen, the ItemStack doesn't have one or more DataContainer fields needed to be a OpenSpawner.");
            }

            return openSpawner;
        }

        public static ItemStack toItemStack(me.jashscript.openspawners.models.OpenSpawner openSpawner) {
            ItemUtils.ItemstackBuilder builder = new ItemUtils.ItemstackBuilder(Material.SPAWNER)
                    .name(TextUtils.colorParse(
                            OSMain.config.getList("item-format")
                                    .get(openSpawner.getTier() - 1)
                                    .toString()).replace("%amount%", String.valueOf(openSpawner.getAmount()))
                            .replace("%type%", TextUtils.entityTypeParse(openSpawner.getType()))
                            .replace("%tier%", String.valueOf(openSpawner.getTier())));

            if (plugin.getConfig().getKeys(false).contains(openSpawner.getType().toLowerCase(Locale.ROOT) + "-lore")) {
                plugin.getConfig().getList(openSpawner.getType().toLowerCase(Locale.ROOT) + "-lore").forEach(o -> {
                    builder.LoreLine((String) o);
                });
            }

            return new ItemUtils.ContainerBuilder(builder.build(), plugin)
                    .addString("type", openSpawner.getType())
                    .addInt("amount", openSpawner.getAmount())
                    .addInt("tier", openSpawner.getTier()).build();
        }

        public static me.jashscript.openspawners.models.OpenSpawner fromOpenSpawnerBlock(me.jashscript.openspawners.models.OpenSpawnerBlock openSpawnerBlock) {
            me.jashscript.openspawners.models.OpenSpawner output = new me.jashscript.openspawners.models.OpenSpawner();
            output.setType(openSpawnerBlock.getType());
            output.setAmount(openSpawnerBlock.getAmount());
            output.setTier(openSpawnerBlock.getTier());
            return output;
        }

    }

    public static class Holograms {

        public static NbtNamedTag toNbt(HashMap<BlockLocation, Hologram> map) {
            NbtCompound root = new NbtCompound();
            map.forEach((blockLocation, hologram) -> {
                NbtCompound hologramCompound = new NbtCompound();
                Location location = hologram.getLocation();
                NbtList nbtList = new NbtList();
                int index = 0;
                while (hologram.containsLine(index)) {
                    nbtList.add(index, new NbtString(hologram.getLines()[index]));
                    index++;
                }
                hologramCompound.set("lore", nbtList);
                root.set(blockLocation.toString(), hologramCompound);
            });
            return new NbtNamedTag("holograms", root);
        }

        public static HashMap<BlockLocation, Hologram> fromNbt(NbtNamedTag nbtNamedTag) {
            HashMap<BlockLocation, Hologram> map = new HashMap<>();
            NbtCompound compound = (NbtCompound) nbtNamedTag.getTag();
            if (compound.getKeys().size() > 0) {
                compound.getKeys().forEach(s -> {
                    BlockLocation blockLocation = BlockLocation.fromString(s);
                    Hologram hologram = OSMain.entityManager.create(Hologram.BUILDER);
                    NbtCompound hologramCompoud = (NbtCompound) compound.get(s);
                    Location location = getCenter(blockLocation.toLocation()).add(new Vector(0,0.25,0));
                    hologram.teleport(location);
                    hologramCompoud.getList("lore").forEach(o -> {
                        hologram.addLine(((NbtString) o).toString().replace("\"", ""));
                    });
                    hologram.spawn();
                    hologram.show(Players.getOnline());
                    map.put(blockLocation, hologram);
                });
            }

            return map;
        }

    }

    public static Location getCenter(Location loc) {
        return new Location(loc.getWorld(),
                getRelativeCoord(loc.getBlockX()),
                getRelativeCoord(loc.getBlockY()),
                getRelativeCoord(loc.getBlockZ()));
    }

    private static double getRelativeCoord(int i) {
//        double d = i;
//        d = d < 0 ? d - .5 : d + .5;
        double d = i;
        return i + 0.5;
    }

    public static class OBChunks {

        public static HashMap<Chunk, ArrayList<me.jashscript.openspawners.models.OpenSpawnerBlock>> fromNbt(NbtNamedTag nbtNamedTag) {
            NbtCompound nbtCompound = (NbtCompound) nbtNamedTag.getTag();
            HashMap<Chunk, ArrayList<me.jashscript.openspawners.models.OpenSpawnerBlock>> map = new HashMap<>();

            nbtCompound.getKeys().forEach(s -> {
                BlockLocation blockLocation = BlockLocation.fromString(s);
                ArrayList<me.jashscript.openspawners.models.OpenSpawnerBlock> list = new ArrayList<>();
                nbtCompound.getCompound(s).forEach((o, t) -> {
                    list.add(Mapper.OpenSpawnerBlock.fromNbt((NbtCompound) t));
                });
                map.put(blockLocation.toLocation().getChunk(), list);
            });
            return map;
        }

        public static NbtNamedTag toNbt(HashMap<Chunk, ArrayList<me.jashscript.openspawners.models.OpenSpawnerBlock>> map) {
            NbtCompound root = new NbtCompound();
            map.forEach((chunk, openSpawnerBlocks) -> {
                BlockLocation blockLocation = new BlockLocation(chunk.getBlock(0, 0, 0).getLocation());
                String loc = blockLocation.getWorld() + "," + blockLocation.getX() +
                        "," + blockLocation.getY() + "," + blockLocation.getZ();
                NbtCompound list = new NbtCompound();
                int index = 0;
                for (me.jashscript.openspawners.models.OpenSpawnerBlock openSpawnerBlock : openSpawnerBlocks) {
                    list.set(String.valueOf(index), Mapper.OpenSpawnerBlock.toNbt(openSpawnerBlock));
                    index++;
                }
                root.set(loc, list);
            });

            return new NbtNamedTag("chunks", root);
        }

    }


}
