package me.jashscript.openspawners.utils;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Array;
import java.util.Locale;

public class TextUtils {

    public static String colorParse(String input){
        return ChatColor.translateAlternateColorCodes('&',input);
    }

    public static TextBuilder textBuilder() {
        return new TextUtils.TextBuilder();
    }

    public static String parseItemStack(ItemStack itemStack){
        if(itemStack.hasItemMeta()){
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta.hasDisplayName()){
                return itemMeta.getDisplayName();
            }
        }
        StringBuilder output = new StringBuilder();
        String[] split = itemStack.getType().name().toLowerCase().split("_");
        for(int i = 0; i< split.length; i++){
            if(i!=0) output.append(" ");
            output.append(split[i].substring(0,1).toUpperCase(Locale.ROOT) + split[i].substring(1));
        }
        return output.toString();
    }


    public static String entityTypeParse(String entitytype){
        StringBuilder stringBuilder = new StringBuilder();
        if(entitytype.contains("_")) {
            String[] split = entitytype.split("_");
            stringBuilder.append(split[0].substring(0, 1).toUpperCase(Locale.ROOT) + split[0].substring(1).toLowerCase(Locale.ROOT));
            stringBuilder.append(" ");
            stringBuilder.append(split[1].substring(0, 1).toUpperCase(Locale.ROOT) + split[1].substring(1).toLowerCase(Locale.ROOT));
        } else {
            stringBuilder.append(entitytype.substring(0,1).toUpperCase(Locale.ROOT) + entitytype.substring(1).toLowerCase(Locale.ROOT));
        }
        return stringBuilder.toString();
    }


    public static class TextBuilder {
        private StringBuilder text = new StringBuilder();

        public String build(){
            return this.text.toString();
        }

        public TextBuilder t(String input){
            this.text.append(input);
            return this;
        }

        public TextBuilder green(){
            this.text.append(ChatColor.GREEN.toString());
            return this;
        }

        public TextBuilder white(){
            this.text.append(ChatColor.WHITE.toString());
            return this;
        }

        public TextBuilder red(){
            this.text.append(ChatColor.RED.toString());
            return this;
        }

        public TextBuilder aqua(){
            this.text.append(ChatColor.AQUA.toString());
            return this;
        }

        public TextBuilder lPurple(){
            this.text.append(ChatColor.LIGHT_PURPLE.toString());
            return this;
        }

        public TextBuilder gray(){
            this.text.append(ChatColor.GRAY.toString());
            return this;
        }

        public TextBuilder black(){
            this.text.append(ChatColor.BLACK.toString());
            return this;
        }

        public TextBuilder blue(){
            this.text.append(ChatColor.BLUE.toString());
            return this;
        }

        public TextBuilder dAqua(){
            this.text.append(ChatColor.DARK_AQUA.toString());
            return this;
        }

        public TextBuilder dGreen(){
            this.text.append(ChatColor.DARK_GREEN.toString());
            return this;
        }

        public TextBuilder dBlue(){
            this.text.append(ChatColor.DARK_BLUE.toString());
            return this;
        }

        public TextBuilder yellow(){
            this.text.append(ChatColor.YELLOW.toString());
            return this;
        }

        public TextBuilder m(){
            this.text.append(ChatColor.MAGIC.toString());
            return this;
        }

        public TextBuilder i(){
            this.text.append(ChatColor.ITALIC.toString());
            return this;
        }

        public TextBuilder dGray(){
            this.text.append(ChatColor.DARK_GRAY.toString());
            return this;
        }

        public TextBuilder dPurple(){
            this.text.append(ChatColor.DARK_PURPLE.toString());
            return this;
        }

        public TextBuilder dRed(){
            this.text.append(ChatColor.DARK_RED.toString());
            return this;
        }

        public TextBuilder gold(){
            this.text.append(ChatColor.GOLD.toString());
            return this;
        }

        public TextBuilder u(){
            this.text.append(ChatColor.UNDERLINE.toString());
            return this;
        }

        public TextBuilder s(){
            this.text.append(ChatColor.STRIKETHROUGH.toString());
            return this;
        }

        public TextBuilder b(){
            this.text.append(ChatColor.BOLD.toString());
            return this;
        }

        public TextBuilder r(){
            this.text.append(ChatColor.RESET.toString());
            return this;
        }
    }
}
