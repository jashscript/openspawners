package me.jashscript.openspawners;

import lombok.SneakyThrows;
import me.jashscript.openspawners.data.Persistance;
import me.jashscript.openspawners.gui.EntityDropsGui;
import me.jashscript.openspawners.gui.EntityTypeGui;
import me.jashscript.openspawners.gui.OpenSpawnerGui;
import me.jashscript.openspawners.listeners.OSListener;
import me.jashscript.openspawners.models.BlockLocation;
import me.jashscript.openspawners.models.OpenSpawnerBlock;
import me.jashscript.openspawners.models.SpawnerType;
import me.jashscript.openspawners.services.CommandService;
import me.jashscript.openspawners.utils.LogUtils;
import me.jashscript.openspawners.utils.TextUtils;
import net.milkbowl.vault.economy.Economy;
import net.sourcewriters.minecraft.versiontools.entity.Hologram;
import net.sourcewriters.minecraft.versiontools.entity.handler.EntityManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;


public final class OSMain extends JavaPlugin {

    public static LogUtils log;
    public static FileConfiguration config;
    public static HashMap<String, SpawnerType> spawnerTypes;
    public static HashMap<BlockLocation, OpenSpawnerBlock> openSpawnerBlocks;
    public static HashMap<Chunk, ArrayList<OpenSpawnerBlock>> OSBChunkIdentification;
    public static HashMap<BlockLocation, Hologram> holograms;
    public static EntityManager entityManager;

    private PluginManager pluginManager;
    public static Economy econ;

    @SneakyThrows
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.config = this.getConfig();

        if (!setupEconomy() ) {
            log.error("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        entityManager = new EntityManager();
        openSpawnerBlocks = new HashMap<>();
        OSBChunkIdentification = Persistance.getChunks();
        holograms = Persistance.getHolograms();
        spawnerTypes = new HashMap<>();
        log = new LogUtils("&l&aOpenSpawners", Bukkit.getLogger(), config.getBoolean("debugmode"));
        getOpenSpawnerBlocks();
        getSpawnerTypes();



        log.debug("Debug Mode ON.");
        log.info("Enabling plugin.");

        this.pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new OSListener(this), this);
        pluginManager.registerEvents(new EntityTypeGui.invListener(), this);
        pluginManager.registerEvents(new EntityDropsGui.invListener(), this);
        pluginManager.registerEvents(new OpenSpawnerGui.invListener(), this);

        Bukkit.getPluginCommand("openspawners").setExecutor(new CommandService.MainCommand());
        Bukkit.getPluginCommand("giveos").setExecutor(new CommandService.GiveCommand());


        Persistance.saveTXT();

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                Persistance.saveHolograms(holograms);
                Persistance.saveChunks(OSBChunkIdentification);
                Persistance.saveOpenSpawnerBlocks(openSpawnerBlocks);
                Persistance.saveSpawnerTypes(spawnerTypes);
            }
        }, 1200L, 1200L);

    }

    public static void playerMessage(Player player, String input) {
        log.playerMessage(player, input);
    }

    public static void playerError(Player player, String input) {
        log.playerError(player, input);
    }


    @SneakyThrows
    @Override
    public void onDisable() {
        Persistance.saveHolograms(holograms);
        Persistance.saveChunks(OSBChunkIdentification);
        Persistance.saveOpenSpawnerBlocks(openSpawnerBlocks);
        Persistance.saveSpawnerTypes(spawnerTypes);
        log.info("Disabling plugin.");
    }


    public static void saveSpawnerTypes(){
        ArrayList<SpawnerType> list = new ArrayList<>();
        spawnerTypes.forEach((s, spawnerType) -> {
            list.add(spawnerType);
        });
        Persistance.saveSpawnerTypes(list);
    }

    private static void getOpenSpawnerBlocks(){
        for (OpenSpawnerBlock openSpawnerBlock : Persistance.getOpenSpawnerBlocks()) {
            openSpawnerBlocks.put(openSpawnerBlock.getBlockLocation(), openSpawnerBlock);
        }
    }

    private static void getSpawnerTypes(){
        for (SpawnerType spawnerType : Persistance.getSpawnerTypes()) {
            spawnerTypes.put(spawnerType.getType(), spawnerType);
        }
    }


    public void reload() {
        this.config = this.getConfig();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

}
