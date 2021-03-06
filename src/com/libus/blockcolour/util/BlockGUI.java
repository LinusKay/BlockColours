package com.libus.blockcolour.util;

import com.libus.blockcolour.Main;
import com.libus.blockcolour.models.BlockColour;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import static org.bukkit.Bukkit.getServer;

public class BlockGUI implements Listener {
    private Inventory inv;
    private Main plugin;
    private BlockColourUtils blockColourUtils = null;

    /**
     * Initialise GUI
     *
     * @param plugin Main plugin
     * @param size Number of slots for GUI
     * @param name Name to display above GUI
     */
    public BlockGUI(Main plugin, int size, String name) {
        this.plugin = plugin;
        inv = Bukkit.createInventory(null, size, name);
        getServer().getPluginManager().registerEvents(this, plugin);
        blockColourUtils = new BlockColourUtils(plugin);
    }

    /**
     * Open GUI
     *
     * @param ent Entity which owns the inventory
     */
    public void openInventory(final HumanEntity ent) {
        ent.openInventory(inv);
    }

    /**
     * Add item from GUI inventory to player inventory when clicked
     *
     * @param e InventoryClickEvent
     */
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory() != inv) return;

        e.setCancelled(true);
        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        final Player p = (Player) e.getWhoClicked();

        if(e.getRawSlot() < inv.getSize()) {
            ItemStack item = new ItemStack(inv.getItem(e.getRawSlot()).getType(), 1);
            item.setItemMeta(null);
            p.getInventory().addItem(item);
        }
    }

    /**
     * Disallow dragging from GUI inventory
     *
     * @param e InventoryDragEvent
     */
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory() == inv) {
            e.setCancelled(true);
        }
    }

    /**
     * Add blocks to GUI
     * Block lore will include the hex code and colour name matching the block
     *
     * @param blockComparisons
     */
    public void addBlocks(List<BlockColour> blockComparisons) {
        for (BlockColour block : blockComparisons) {
            File configFile = new File(plugin.getDataFolder() + "/config.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            List<String> blacklist = config.getStringList("blacklist");

            for(int i = 0; i < blacklist.size(); i++){
                blacklist.set(i, blacklist.get(i).toUpperCase());
            }

            if(block.getBlockMaterial() != null && !blacklist.contains(block.getBlockMaterial().toString().toUpperCase())) {
                try {
                    ItemStack item = new ItemStack(block.getBlockMaterial(), 1);
                    ItemMeta meta = item.getItemMeta();
                    meta.setLore(Arrays.asList("Hex: " + block.getColourHex(), "Colour: " + blockColourUtils.getClosestColourName(block), "Difference: " + (int) block.getDifference()));
                    item.setItemMeta(meta);
                    inv.addItem(item);
                } catch (IllegalArgumentException e) {
                    System.out.println("Could not add block " + block.getBlockMaterial());
                }
            }
        }
    }
}