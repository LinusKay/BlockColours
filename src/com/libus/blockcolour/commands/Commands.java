package com.libus.blockcolour.commands;

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

    private List<BlockColour> blockComparisonList = new ArrayList<>();

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

        BlockColour block = new BlockColour(plugin);
        block.setColourFromHex(blockColours.getString(itemType));

        if (args.length > 0) {
            String command = args[0];

            BlockGUI gui;
            /*
            Find blocks with colours similar to held block
             */
            if (command.equals("similar")) {
                int minDifference = 0;
                int maxDifference = 100;
                if(args.length > 2){
                    minDifference = Integer.parseInt(args[1]);
                    maxDifference = Integer.parseInt(args[2]);
                }
                if(args.length > 4){
                    int minExclusionDifference = Integer.parseInt(args[3]);
                    int maxExclusionDifference = Integer.parseInt(args[4]);
                    blockComparisonList = block.getSimilarBlocks(blockColours, minDifference, maxDifference, minExclusionDifference, maxExclusionDifference);
                }
                else {
                    blockComparisonList = block.getSimilarBlocks(blockColours, minDifference, maxDifference);
                }
                int slots = calculateSlots(blockComparisonList);
                gui = new BlockGUI(plugin, slots, "§3Similar Blocks");
                gui.addBlocks(blockComparisonList);
                gui.openInventory(player);
            }
            /*
            Find block with colours complementary to held block
            In this context, a complementary colour is one on the opposite end of the colour wheel
             */
            else if (command.equals("complement") || command.equals("complementary") || command.equals("opposite")) {
                int minDifference = 0;
                int maxDifference = 100;
                if(args.length > 2){
                    minDifference = Integer.parseInt(args[1]);
                    maxDifference = Integer.parseInt(args[2]);
                }
                if(args.length > 4){
                    int minExclusionDifference = Integer.parseInt(args[3]);
                    int maxExclusionDifference = Integer.parseInt(args[4]);
                    blockComparisonList = block.getComplementaryBlocks(blockColours, minDifference, maxDifference, minExclusionDifference, maxExclusionDifference);
                }
                else {
                    blockComparisonList = block.getComplementaryBlocks(blockColours, minDifference, maxDifference);
                }
                int slots = calculateSlots(blockComparisonList);
                gui = new BlockGUI(plugin, slots, "§cComplementary Blocks");
                gui.addBlocks(blockComparisonList);
                gui.openInventory(player);
            }
        }
        blockComparisonList.clear();
        return true;
    }

    /**
     * Calculate number of slots to initialise GUI with
     *
     * @param blockComparisonList list of blocks to be added into GUI
     * @return Integer value to pass into GUI, multiple of 9 to match row of slots
     */
    public int calculateSlots(List<BlockColour> blockComparisonList) {
        double blockCount = blockComparisonList.size();
        int slots;
        if (blockCount > 45) {
            slots = 54;
        } else {
            slots = (int) (Math.ceil(blockCount / 9) * 9);
        }
        if(slots == 0){slots = 9;}
        return slots;
    }

}
