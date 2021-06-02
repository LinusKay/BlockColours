package com.libus.blockcolour.util;

import com.libus.blockcolour.models.BlockComparison;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;

public class BlockGUI implements Listener {
    private final Inventory inv;

    public BlockGUI(int size, String name) {
        // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
        inv = Bukkit.createInventory(null, size, name);
    }

    // You can open the inventory with this
    public void openInventory(final HumanEntity ent) {
        ent.openInventory(inv);
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory() != inv) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        final Player p = (Player) e.getWhoClicked();

        // Using slots click is a best option for your inventory click's
        p.sendMessage("You clicked at slot " + e.getRawSlot());
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory() == inv) {
            e.setCancelled(true);
        }
    }

    public void addBlocks(List<BlockComparison> blockComparisons) {
        blockComparisons.sort(Comparator.comparing(BlockComparison::getDifference));
        for (BlockComparison item : blockComparisons) {
            try {
                inv.addItem(new ItemStack(Material.getMaterial(item.getBlock().toUpperCase()), 1));
            } catch (IllegalArgumentException e) {
                System.out.println("Could not add block " + item.getBlock().toUpperCase());
            }
        }
    }
}