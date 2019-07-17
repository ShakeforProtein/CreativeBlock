package me.shakeforprotein.creativeblock;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class CreativeBlock extends JavaPlugin implements Listener {

    private String badge = ChatColor.translateAlternateColorCodes('&', getConfig().getString("badge"));
    private String err = ChatColor.translateAlternateColorCodes('&', getConfig().getString("error"));
    private boolean useConfigLevels = getConfig().getBoolean("useConfigLevels");

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        getConfig().set("version", this.getDescription().getVersion());
        Bukkit.getPluginManager().registerEvents(this, this);
        if (getConfig().getBoolean("newConfig") || getConfig().getString("newConfig") == null) {
            for (Enchantment ench : Enchantment.values()) {
                getConfig().set("enchantments." + ench.getKey(), ench.getMaxLevel());
            }
            getConfig().set("newConfig", false);
        }
        saveConfig();
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            ItemStack heldItem = event.getItem();

            if (!event.getPlayer().isOp() && !event.getPlayer().hasPermission("creativeblock.bypass")) {
                World world = event.getPlayer().getWorld();
                boolean active = false;
                for (String listItem : getConfig().getStringList("ActiveWorlds")) {
                    if (world.getName().equalsIgnoreCase(listItem)) {
                        active = true;
                    }
                }

                if (heldItem.hasItemMeta()) {
                    ItemMeta itemMeta = heldItem.getItemMeta();
                    if (itemMeta.hasDisplayName() && getConfig().getBoolean("blockColouredItems")) {
                        if (!itemMeta.getDisplayName().equals(ChatColor.stripColor(itemMeta.getDisplayName()))) {
                            event.getPlayer().sendMessage(badge + err + getConfig().getString("messages.colouredItem"));
                            itemMeta.setDisplayName(null);
                            heldItem.setItemMeta(itemMeta);
                            event.setCancelled(true);
                            if (getConfig().getBoolean("verbose")) {
                                getLogger().info("Player '" + event.getPlayer().getName() + "' with UUID '" + event.getPlayer().getUniqueId() + "' attempted to use an item with coloured name '" + itemMeta.getDisplayName());
                            }
                            if (getConfig().getBoolean("destroyItems")) {
                                heldItem.setType(Material.AIR);
                            }
                        }
                    }

                    if (itemMeta.hasEnchants() && getConfig().getBoolean("blockUnsafeEnchantments")) {
                        HashMap<Enchantment, Integer> enchHash = new HashMap<>();
                        for (Enchantment ench : itemMeta.getEnchants().keySet()) {
                            if (itemMeta.getEnchantLevel(ench) > getConfig().getInt("enchantments." + ench.getKey())) {
                                enchHash.putIfAbsent(ench, itemMeta.getEnchantLevel(ench));
                            }
                        }
                        if (!enchHash.isEmpty()) {
                            event.getPlayer().sendMessage(badge + err + getConfig().getString("messages.unsafeEnchants"));
                            for (Enchantment ench : enchHash.keySet()) {
                                itemMeta.removeEnchant(ench);
                                itemMeta.addEnchant(ench, getConfig().getInt("enchantments." + ench.getKey()), false);
                            }
                            heldItem.setItemMeta(itemMeta);
                            event.setCancelled(true);
                            if (getConfig().getBoolean("verbose")) {
                                getLogger().info("Player '" + event.getPlayer().getName() + "' with UUID '" + event.getPlayer().getUniqueId() + "' attempted to use an item with unsafe Enchantments '" + enchHash.toString());
                            }

                            if (getConfig().getBoolean("destroyItems")) {
                                heldItem.setType(Material.AIR);
                            }
                        }

                    }
                }
            }
        }
    }

    @EventHandler
    private void playerHoldItem(PlayerItemHeldEvent event) {
        int i;
        HashMap itemHash = new HashMap<Integer, ItemStack>();

        ItemStack air = new ItemStack(Material.AIR);
        if(event.getPlayer().getInventory().getItemInMainHand() != null){
        itemHash.putIfAbsent(0, event.getPlayer().getInventory().getItemInMainHand());
        }
        else{
            itemHash.putIfAbsent(0, air);
        }
        if(event.getPlayer().getInventory().getItemInOffHand() != null) {
            itemHash.putIfAbsent(1, event.getPlayer().getInventory().getItemInOffHand());
        }
        else{
            itemHash.putIfAbsent(0, air);
        }

        for (i = 0; i < 2; i++) {
            ItemStack heldItem = (ItemStack) itemHash.get(i);

            if (!event.getPlayer().isOp() && !event.getPlayer().hasPermission("creativeblock.bypass")) {
                World world = event.getPlayer().getWorld();
                boolean active = false;
                for (String listItem : getConfig().getStringList("ActiveWorlds")) {
                    if (world.getName().equalsIgnoreCase(listItem)) {
                        active = true;
                    }
                }

                if (heldItem.hasItemMeta()) {
                    ItemMeta itemMeta = heldItem.getItemMeta();
                    if (itemMeta.hasDisplayName() && getConfig().getBoolean("blockColouredItems")) {
                        if (!itemMeta.getDisplayName().equals(ChatColor.stripColor(itemMeta.getDisplayName()))) {
                            event.getPlayer().sendMessage(badge + err + getConfig().getString("messages.colouredItem"));
                            itemMeta.setDisplayName(null);
                            heldItem.setItemMeta(itemMeta);
                            event.setCancelled(true);
                            if (getConfig().getBoolean("verbose")) {
                                getLogger().info("Player '" + event.getPlayer().getName() + "' with UUID '" + event.getPlayer().getUniqueId() + "' attempted to use an item with coloured name '" + itemMeta.getDisplayName());
                            }
                            if (getConfig().getBoolean("destroyItems")) {
                                heldItem.setType(Material.AIR);
                            }
                        }
                    }

                    if (itemMeta.hasEnchants() && getConfig().getBoolean("blockUnsafeEnchantments")) {
                        HashMap<Enchantment, Integer> enchHash = new HashMap<>();
                        for (Enchantment ench : itemMeta.getEnchants().keySet()) {
                            if (itemMeta.getEnchantLevel(ench) > getConfig().getInt("enchantments." + ench.getKey())) {
                                enchHash.putIfAbsent(ench, itemMeta.getEnchantLevel(ench));
                            }
                        }
                        if (!enchHash.isEmpty()) {
                            event.getPlayer().sendMessage(badge + err + getConfig().getString("messages.unsafeEnchants"));
                            for (Enchantment ench : enchHash.keySet()) {
                                itemMeta.removeEnchant(ench);
                                itemMeta.addEnchant(ench, getConfig().getInt("enchantments." + ench.getKey()), false);
                            }
                            heldItem.setItemMeta(itemMeta);
                            event.setCancelled(true);
                            if (getConfig().getBoolean("verbose")) {
                                getLogger().info("Player '" + event.getPlayer().getName() + "' with UUID '" + event.getPlayer().getUniqueId() + "' attempted to use an item with unsafe Enchantments '" + enchHash.toString());
                            }

                            if (getConfig().getBoolean("destroyItems")) {
                                heldItem.setType(Material.AIR);
                            }
                        }
                    }
                }
            }
        }
    }
}


