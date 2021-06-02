package com.libus.blockcolour.commands;

import com.libus.blockcolour.models.BlockComparison;
import com.libus.blockcolour.models.BlockColour;
import com.libus.blockcolour.util.BlockGUI;
import com.libus.blockcolour.Main;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor {

    private final Main plugin;

    private List<BlockComparison> blockComparisonList = new ArrayList<>();

    public Commands(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        String itemType = item.getType().toString().toLowerCase();

        File colourFile = new File(plugin.getDataFolder() + "/blocks.yml");
        YamlConfiguration blockColours = YamlConfiguration.loadConfiguration(colourFile);

        BlockColour block = new BlockColour();
        block.setColourFromString(blockColours.getString(itemType));

        if (args.length > 0) {
            String command = args[0];
            int minDifference = 0;
            int maxDifference = 30;
            if (args.length > 1) {
                minDifference = Integer.parseInt(args[1]);
            }
            if (args.length > 2) {
                maxDifference = Integer.parseInt(args[2]);
            }
            /*
            Find blocks with colours similar to held block
             */
            BlockGUI gui;
            if (command.equals("similar")) {
                blockComparisonList = block.getSimilarBlocks(blockColours, minDifference, maxDifference);
                double blockCount = blockComparisonList.size();
                int slots;
                if(blockCount > 45){
                    slots = 45;
                }
                else{
                    slots = (int) (Math.ceil(blockCount/9) * 9);
                }
                gui = new BlockGUI(slots, "Similar Blocks");
                gui.addBlocks(blockComparisonList);
                gui.openInventory(player);
            }
            /*
            Find block with colours complementary to held block
            In this context, a complementary colour is one on the opposite end of the colour wheel
             */
            else if(command.equals("complement") || command.equals("opposite")){
                blockComparisonList = block.getComplementaryBlocks(blockColours, minDifference, maxDifference);
                double blockCount = blockComparisonList.size();
                int slots;
                if(blockCount > 45){
                    slots = 45;
                }
                else{
                    slots = (int) (Math.ceil(blockCount/9) * 9);
                }
                gui = new BlockGUI(slots, "Complementary Blocks");
                gui.addBlocks(blockComparisonList);
                gui.openInventory(player);
            }
        }
        blockComparisonList.clear();
        return true;
    }
}
