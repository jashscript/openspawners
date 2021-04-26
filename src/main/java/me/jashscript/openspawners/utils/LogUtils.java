package me.jashscript.openspawners.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class LogUtils {

    private final String prefix;
    private Logger logger;
    private boolean debugmode;

    public LogUtils(String prefix, Logger logger, boolean debugmode){
        this.prefix = TextUtils.colorParse(prefix);
        this.logger = logger;
        this.debugmode = debugmode;
    }

    public void debug(String input){
        if(!debugmode) return;

        logger.info(new TextUtils().textBuilder().green().t(" [DEBUG] ")
                .t(this.prefix).green().t(": ").t(TextUtils.colorParse(input)).build());
    }

    public void info(String input){
        if(!debugmode) return;

        logger.info(TextUtils.textBuilder().yellow().t(" [INFO] ")
                .t(this.prefix).yellow().t(": ").t(TextUtils.colorParse(input)).build());
    }

    public String ingameInfo(String input){
        return TextUtils.textBuilder()
                .t(this.prefix).t(": ").yellow().t(TextUtils.colorParse(input)).build();
    }

    public String ingameError(String input){
        return TextUtils.textBuilder()
                .t(this.prefix).t(": ").red().t(TextUtils.colorParse(input)).build();
    }

    public void error(String input){
        if(!debugmode) return;

        logger.info(TextUtils.textBuilder().red().t("[ERROR]")
                .t(this.prefix).red().t(": ").t(TextUtils.colorParse(input)).build());
    }

    public void playerMessage(Player player, String input){
        player.sendMessage(TextUtils.textBuilder()
                .t(this.prefix).t(": ").yellow().t(TextUtils.colorParse(input)).build());
    }

    public void playerError(Player player, String input){
        player.sendMessage(TextUtils.textBuilder()
                .t(this.prefix).t(": ").red().t(TextUtils.colorParse(input)).build());
    }


}
