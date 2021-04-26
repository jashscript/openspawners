package me.jashscript.openspawners.services;

import lombok.NoArgsConstructor;
import me.jashscript.openspawners.OSMain;
import me.jashscript.openspawners.data.Mapper;
import me.jashscript.openspawners.data.Persistance;
import me.jashscript.openspawners.gui.EntityTypeGui;
import me.jashscript.openspawners.models.OpenSpawner;
import me.jashscript.openspawners.models.SpawnerType;
import me.jashscript.openspawners.utils.LogUtils;
import me.jashscript.openspawners.utils.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Locale;

public class CommandService {

    private static LogUtils log = OSMain.log;
    private static OSMain plugin = JavaPlugin.getPlugin(OSMain.class);

    @NoArgsConstructor
    public static class MainCommand implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if(!(sender instanceof Player)){
                log.info("This command is meant only for players to use for safety reasons.");
                return true;
            }
            Player player = (Player) sender;
            if(!label.equals("openspawners")) return true;

            if(!player.hasPermission("openspawners"))

            if(args.length==0){
                log.playerMessage(player,
                        "&fUse /openspawners entity_type replacing entity_type for one of the entity_types from mob_types.txt but in lowercase!" +
                                " If you never used to the specific entity_type it will create the new entity_type and then you can use" +
                                " the command again to open the configuration menu!");
                return true;
            }

            if(args.length>1){
                log.playerMessage(player, "&fWrong number of arguments!");
                return true;
            }

            File file = new File(plugin.getDataFolder() + "/spawner_types/"+args[0].toUpperCase(Locale.ROOT));

            if(file.exists()) {
                EntityTypeGui.open(player, args[0].toUpperCase(Locale.ROOT));
                return true;
            }

            try{
                EntityType.valueOf(args[0].toUpperCase(Locale.ROOT));
            } catch(Exception exception){
                log.playerMessage(player,"&fWrong entity type! Use the names from mob_types.txt but in lowercase.");
                return true;
            }

            SpawnerType spawnerType = new SpawnerType();
            spawnerType.setType(args[0].toUpperCase(Locale.ROOT));
            OSMain.spawnerTypes.put(args[0].toUpperCase(Locale.ROOT), spawnerType);
            OSMain.saveSpawnerTypes();

            log.playerMessage(player, "&fSpawner type created! Use /openspawners "+args[0]+" to edit it!");

            return true;
        }
    }
    @NoArgsConstructor
    public static class GiveCommand implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if(sender instanceof Player){
                Player player = (Player) sender;
                if(!player.hasPermission("giveos") && !player.isOp()) return true;
            }

            if(args.length == 0){
                sender.sendMessage(TextUtils.colorParse("&fUsage: /giveos playername spawner_type tier amount"));
                return true;
            }

            if(args.length!=4) {
                sender.sendMessage(TextUtils.colorParse("&cWrong number of parameters!"));
                return true;
            }

            try{
                SpawnerType spawnerType = Persistance.getSpawnerType(args[1].toUpperCase(Locale.ROOT));
                if(spawnerType == null) {
                    sender.sendMessage(TextUtils.colorParse("&cThis spawnertype doesn't exist! Create it first"));
                    return true;
                }

                Player player = plugin.getServer().getPlayer(args[0]);

                if(player == null) {
                    sender.sendMessage(TextUtils.colorParse("&cThis player doesn't exist or is offline!"));
                    return true;
                }

                OpenSpawner openSpawner = new OpenSpawner();
                openSpawner.setType(args[1].toUpperCase(Locale.ROOT));
                openSpawner.setTier(Integer.valueOf(args[2]));
                openSpawner.setAmount(Integer.valueOf(args[3]));

                player.getInventory().addItem(Mapper.OpenSpawner.toItemStack(openSpawner));
                sender.sendMessage(TextUtils.colorParse("&l&aSpawner given with success!"));


            } catch(Exception exception){
                sender.sendMessage(TextUtils.colorParse("&cSomething went wrong in the command! Maybe you haven't created that spawner type yet."));
            }

            return true;
        }
        }
    }

