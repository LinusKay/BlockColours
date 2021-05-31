package com.libus.blockcolour.commands;

import com.libus.blockcolour.BlockGUI;
import com.libus.blockcolour.Main;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.DuplicateFormatFlagsException;
import java.util.List;

public class BlockColour implements CommandExecutor {

    private final Main plugin;

    private BlockGUI gui;
    private List<String> items = new ArrayList<>();

    public BlockColour(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        String itemType = item.getType().toString().toLowerCase();

        File colourFile = new File(plugin.getDataFolder() + "/blocks.yml");
        YamlConfiguration blockColours = YamlConfiguration.loadConfiguration(colourFile);

        String itemColour = blockColours.getString(itemType);
        Color firstColour = Color.decode(itemColour);

        if(args.length > 0){
            String command = args[0];
            if(command.equals("similar")){
                int minDifference = 0;
                int maxDifference = 30;
                if(args.length > 1){
                    minDifference = Integer.parseInt(args[1]);
                }
                if(args.length > 2){
                    maxDifference = Integer.parseInt(args[2]);
                }
                gui = new BlockGUI(27, "Similar Blocks");
                for (String block : blockColours.getConfigurationSection("").getKeys(false)) {
                    String colourString = blockColours.getString(block);
                    Color secondColour = Color.decode(colourString);

                    double difference = compareColours(firstColour, secondColour);
                    if (difference >= minDifference && difference < maxDifference) {
                        items.add(block);
                    }
                }
                generateGUI(items);
                gui.openInventory(player);
            }
            else if(command.equals("different")){
                int minDifference = 225;
                int maxDifference = 255;
                if(args.length > 1){
                    minDifference = Integer.parseInt(args[1]);
                }
                if(args.length > 2){
                    maxDifference = Integer.parseInt(args[2]);
                }
                gui = new BlockGUI(27, "Different Blocks");
                for (String block : blockColours.getConfigurationSection("").getKeys(false)) {
                    String colourString = blockColours.getString(block);
                    Color secondColour = Color.decode(colourString);

                    double difference = compareColours(firstColour, secondColour);
                    if (difference >= minDifference && difference < maxDifference) {
                        items.add(block);
                    }
                }
                generateGUI(items);
                gui.openInventory(player);
                items.clear();
            }
        }
        return true;
    }

    /**
     * Compare the difference between two given colours
     *
     * @param firstColour  First Color object to compare
     * @param secondColour Second Color object to compare
     * @return value of colour difference
     */
    public double compareColours(Color firstColour, Color secondColour) {
        int r1 = firstColour.getRed();
        int g1 = firstColour.getGreen();
        int b1 = firstColour.getBlue();
        int r2 = secondColour.getRed();
        int g2 = secondColour.getGreen();
        int b2 = secondColour.getBlue();
        return Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));
    }

    /**
     * Pass items into gui
     *
     * @param items
     */
    public void generateGUI(List<String> items) {
        for (String item : items) {
            try {
                gui.addItem(new ItemStack(Material.getMaterial(item.toUpperCase()), 1));
            } catch (IllegalArgumentException e) {
                System.out.println("Could not add block " + item.toUpperCase());
            }
        }
    }


}