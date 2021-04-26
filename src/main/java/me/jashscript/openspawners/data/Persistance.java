package me.jashscript.openspawners.data;

import com.syntaxphoenix.syntaxapi.nbt.NbtNamedTag;
import com.syntaxphoenix.syntaxapi.nbt.tools.NbtDeserializer;
import com.syntaxphoenix.syntaxapi.nbt.tools.NbtSerializer;
import me.jashscript.openspawners.OSMain;
import me.jashscript.openspawners.models.*;
import net.sourcewriters.minecraft.versiontools.entity.Hologram;
import org.bukkit.Chunk;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Persistance {

    private static OSMain plugin = JavaPlugin.getPlugin(OSMain.class);

    public static void saveOpenSpawnerBlocks(List<OpenSpawnerBlock> openSpawnerBlocks) {
        File file = new File(plugin.getDataFolder() + "/spawners_locations.nbt");
        NbtNamedTag nbtNamedTag = Mapper.OpenSpawnerBlock.toNbt((ArrayList) openSpawnerBlocks);
        try {
            NbtSerializer.COMPRESSED.toFile(nbtNamedTag, file);
        } catch (IOException exception) {
            plugin.log.error("Error saving spawner_locations.nbt file!!!!!");
            return;
        }
        plugin.log.debug("spawner_locations.nbt saved!!!");
    }


    public static List<OpenSpawnerBlock> getOpenSpawnerBlocks() {
        File file = new File(plugin.getDataFolder() + "/spawners_locations.nbt");
        if(!file.exists()){
            ArrayList<OpenSpawnerBlock> list = new ArrayList<>();
            saveOpenSpawnerBlocks(list);
            return list;
        }
        NbtNamedTag nbtNamedTag = null;
        try {
            nbtNamedTag = NbtDeserializer.COMPRESSED.fromFile(file);
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
        return Mapper.OpenSpawnerBlock.fromNbt(nbtNamedTag);
    }

    public static void saveSpawnerTypes(List<SpawnerType> spawnerTypes) {
        spawnerTypes.forEach(spawnerType -> {
            File file = new File(plugin.getDataFolder() + "/spawner_types/" + spawnerType.getType());
            NbtNamedTag nbtNamedTag = Mapper.SpawnerType.toNbt(spawnerType);
            try {
                NbtSerializer.COMPRESSED.toFile(nbtNamedTag, file);
            } catch (IOException exception) {
                plugin.log.error("Error saving " + spawnerType.getType() + " file!!!!!");
                return;
            }
            plugin.log.debug(spawnerType.getType() + " saved!!!");
        });
    }

    public static List<SpawnerType> getSpawnerTypes() {
        ArrayList<SpawnerType> output = new ArrayList<>();
        File dir = new File(plugin.getDataFolder() + "/spawner_types");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File spawnerType : directoryListing) {
                try {
                    output.add(Mapper.SpawnerType.fromNbt(NbtDeserializer.COMPRESSED.fromFile(spawnerType)));
                } catch (IOException exception) {
                    plugin.log.error("Error getting " + spawnerType.getName() + " file!");
                }
            }
            return output;
        }

        dir.mkdir();
        return getSpawnerTypes();
    }

    public static SpawnerType getSpawnerType(String entitytype) throws IOException {
        File file = new File(plugin.getDataFolder() + "/spawner_types/" + entitytype);
        return Mapper.SpawnerType.fromNbt(NbtDeserializer.COMPRESSED.fromFile(file));
    }

    public static void saveHolograms(HashMap<BlockLocation, Hologram> map) throws IOException {
        File file = new File(plugin.getDataFolder() + "/holograms.nbt");
        NbtNamedTag nbtNamedTag = Mapper.Holograms.toNbt(map);
        NbtSerializer.COMPRESSED.toFile(nbtNamedTag, file);
    }

    public static void saveChunks(HashMap<Chunk, ArrayList<OpenSpawnerBlock>> map) throws IOException {
        File file = new File(plugin.getDataFolder() + "/chunks.nbt");
        NbtNamedTag nbtNamedTag = Mapper.OBChunks.toNbt(map);
        NbtSerializer.COMPRESSED.toFile(nbtNamedTag, file);
    }

    public static HashMap<Chunk, ArrayList<OpenSpawnerBlock>> getChunks() throws IOException {
        File file = new File(plugin.getDataFolder() + "/chunks.nbt");
        if(!file.exists()){
            HashMap<Chunk, ArrayList<OpenSpawnerBlock>> map = new HashMap<>();
            saveChunks(map);
            return map;
        }
        NbtNamedTag nbtNamedTag = NbtDeserializer.COMPRESSED.fromFile(file);
        return Mapper.OBChunks.fromNbt(nbtNamedTag);
    }

    public static HashMap<BlockLocation, Hologram> getHolograms() throws IOException {
        File file = new File(plugin.getDataFolder() + "/holograms.nbt");
        if(!file.exists()){
            HashMap<BlockLocation, Hologram> map = new HashMap<>();
            saveHolograms(map);
            return map;
        }
        NbtNamedTag nbtNamedTag = NbtDeserializer.COMPRESSED.fromFile(file);
        return Mapper.Holograms.fromNbt(nbtNamedTag);
    }

    public static void saveTXT() {
        File txtFile = new File(plugin.getDataFolder(), "/mob_types.txt");
        if (!txtFile.exists()) {
            InputStream fis = plugin.getClass().getResourceAsStream("/mob_types.txt");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(txtFile);
                byte[] buf = new byte[1024];
                int i = 0;
                while ((i = fis.read(buf)) != -1) {
                    fos.write(buf, 0, i);
                }
            } catch (Exception e) {
                plugin.log.error("ERROR SAVING TXT");
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (Exception e) {}
            }
        }
    }

    public static void saveOpenSpawnerBlocks(HashMap<BlockLocation, OpenSpawnerBlock> openSpawnerBlocks) {
        ArrayList<OpenSpawnerBlock> list = new ArrayList<>();
        openSpawnerBlocks.forEach((blockLocation, openSpawnerBlock) -> {
            list.add(openSpawnerBlock);
        });

        saveOpenSpawnerBlocks(list);
    }

    public static void saveSpawnerTypes(HashMap<String, SpawnerType> spawnerTypes) {
        ArrayList<SpawnerType> list = new ArrayList<>();
        spawnerTypes.forEach((s, spawnerType) -> {
            list.add(spawnerType);
        });

        saveSpawnerTypes(list);
    }
}
