package me.jashscript.openspawners.utils;

import me.jashscript.openspawners.OSMain;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class ItemUtils {

    private static OSMain plugin = JavaPlugin.getPlugin(OSMain.class);

    public static boolean isOpenSpawner(ItemStack itemStack){
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return false;
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        if(!persistentDataContainer.has(new NamespacedKey(plugin, "type"), PersistentDataType.STRING)) return  false;
        if(!persistentDataContainer.has(new NamespacedKey(plugin, "amount"), PersistentDataType.INTEGER)) return  false;
        if(!persistentDataContainer.has(new NamespacedKey(plugin, "tier"), PersistentDataType.INTEGER)) return  false;
        return true;
    }

    public ItemUtils.ItemstackBuilder itemstackBuilder(Material material) {
        return new ItemUtils.ItemstackBuilder(material);
    }

    public ItemUtils.ItemstackBuilder itemstackBuilder(ItemStack itemStack) {
        return new ItemUtils.ItemstackBuilder(itemStack);
    }

    public ContainerBuilder NBTBuilder(ItemStack itemStack, OSMain plugin) {
        return new ItemUtils.ContainerBuilder(itemStack, plugin);
    }

    public static class ItemstackBuilder {
        private ItemStack itemStack;
        
        public ItemstackBuilder(Material material){
            this.itemStack = new ItemStack(material);
        }

        private ItemstackBuilder(ItemStack itemStack){
            this.itemStack = itemStack;
        }
        
        public ItemstackBuilder amount(int amount){
            itemStack.setAmount(amount);
            return this;
        }
        
        public ItemstackBuilder name(String name){
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(TextUtils.colorParse(name));
            itemStack.setItemMeta(itemMeta);
            return this;
        }
        
        public ItemstackBuilder LoreLine(String loreLine){
            ItemMeta itemMeta = itemStack.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();
            if(itemMeta.hasLore()) {
                lore = (ArrayList<String>) itemMeta.getLore();
            }
            lore.add(TextUtils.colorParse(loreLine));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemstackBuilder customModelData(int i){
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setCustomModelData(i);
            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemstackBuilder hideEnchants(){
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemstackBuilder enchant(Enchantment enchantment, int level){
            itemStack.addEnchantment(enchantment, level);
            return this;
        }

        public ItemStack build(){
            return itemStack;
        }
    }


    public static class ContainerBuilder {

        private ItemStack itemStack;
        private OSMain plugin;
        private ItemMeta itemMeta;
        private PersistentDataContainer persistentDataContainer;

        public ContainerBuilder(ItemStack itemstack, OSMain plugin){
            this.plugin = plugin;
            this.itemStack = itemstack;
            this.itemMeta = itemstack.getItemMeta();
            this.persistentDataContainer = itemMeta.getPersistentDataContainer();
        }

        public ItemStack build(){
            itemStack.setItemMeta(this.itemMeta);
            return itemStack;
        }

        public ContainerBuilder addString(String name, String value){
            NamespacedKey key = new NamespacedKey(this.plugin, name);
            this.persistentDataContainer.set(key, PersistentDataType.STRING, value);
            return this;
        }

        public ContainerBuilder addInt(String name, int value){
            NamespacedKey key = new NamespacedKey(this.plugin, name);
            this.persistentDataContainer.set(key, PersistentDataType.INTEGER, value);
            return this;
        }

    }
}
