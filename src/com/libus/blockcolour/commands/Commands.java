package com.libus.blockcolour.commands;

import com.libus.blockcolour.Main;
import com.libus.blockcolour.models.BlockColour;
import com.libus.blockcolour.util.BlockColourUtils;
import com.libus.blockcolour.util.BlockGUI;
import org.bukkit.Material;
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

    private BlockColourUtils blockColourUtils = null;

    public Commands(Main plugin) {
        this.plugin = plugin;
        this.blockColourUtils = new BlockColourUtils(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        Player player = (Player) sender;

        File colourFile = new File(plugin.getDataFolder() + "/blocks.yml");
        YamlConfiguration blockColours = YamlConfiguration.loadConfiguration(colourFile);


        if (args.length > 0) {
            String command = args[0];

            BlockGUI gui;
            /*
            Find blocks with colours similar to held block
             */
            switch (command) {
                case "similar" -> {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    String itemType = item.getType().toString().toLowerCase();
                    BlockColour block = new BlockColour(plugin);
                    block.setMaterial(item.getType());
                    block.setColourFromHex(blockColours.getString(itemType));
                    int minDifference = 0;
                    int maxDifference = 100;
                    if (args.length > 2) {
                        minDifference = Integer.parseInt(args[1]);
                        maxDifference = Integer.parseInt(args[2]);
                    }
                    if (args.length > 4) {
                        int minExclusionDifference = Integer.parseInt(args[3]);
                        int maxExclusionDifference = Integer.parseInt(args[4]);
                        blockComparisonList = blockColourUtils.getSimilarBlocks(block, blockColours, minDifference, maxDifference, minExclusionDifference, maxExclusionDifference);
                    } else {
                        blockComparisonList = blockColourUtils.getSimilarBlocks(block, blockColours, minDifference, maxDifference);
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
                case "complement", "complementary", "opposite" -> {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    String itemType = item.getType().toString().toLowerCase();
                    BlockColour block = new BlockColour(plugin);
                    block.setMaterial(item.getType());
                    block.setColourFromHex(blockColours.getString(itemType));
                    this.blockComparisonList.clear();
                    int minDifference = 0;
                    int maxDifference = 100;
                    if (args.length > 2) {
                        minDifference = Integer.parseInt(args[1]);
                        maxDifference = Integer.parseInt(args[2]);
                    }
                    if (args.length > 4) {
                        int minExclusionDifference = Integer.parseInt(args[3]);
                        int maxExclusionDifference = Integer.parseInt(args[4]);
                        blockComparisonList = blockColourUtils.getComplementaryBlocks(block, blockColours, minDifference, maxDifference, minExclusionDifference, maxExclusionDifference);
                    } else {
                        blockComparisonList = blockColourUtils.getComplementaryBlocks(block, blockColours, minDifference, maxDifference);
                    }
                    int slots = calculateSlots(blockComparisonList);
                    gui = new BlockGUI(plugin, slots, "§cComplementary Blocks");
                    gui.addBlocks(blockComparisonList);
                    gui.openInventory(player);
                }


            /*
            Get gradient of x blocks between two given blocks
             */
                case "gradient" -> {
                    BlockColour blockOne = new BlockColour(plugin);
                    BlockColour blockTwo = new BlockColour(plugin);
                    ItemStack itemOne = null;
                    ItemStack itemTwo = null;
                    int size = 5;
                    if (args.length == 1) {
                        itemOne = player.getInventory().getItemInOffHand();
                        itemTwo = player.getInventory().getItemInMainHand();
                    } else if (args.length == 2) {
                        itemOne = player.getInventory().getItemInOffHand();
                        itemTwo = player.getInventory().getItemInMainHand();
                        size = Integer.parseInt(args[1]);
                    } else if (args.length > 2) {
                        String inputOne = args[1].toUpperCase();
                        String inputTwo = args[2].toUpperCase();
                        if (inputOne.matches("-?\\d+(\\.\\d+)?") && inputTwo.matches("-?\\d+(\\.\\d+)?")) {
                            int slotOne = Integer.parseInt(args[1]);
                            int slotTwo = Integer.parseInt(args[2]);
                            if (slotOne > 0 && slotOne < 10 && slotTwo > 0 && slotTwo < 10) {
                                itemOne = player.getInventory().getItem(slotOne - 1);
                                itemTwo = player.getInventory().getItem(slotTwo - 1);
                            } else {
                                player.sendMessage("Slot number must be between 1 - 9");
                                return true;
                            }
                        } else {
                            Material materialOne = Material.getMaterial(inputOne);
                            Material materialTwo = Material.getMaterial(inputTwo);
                            if (materialOne != null && materialTwo != null) {
                                itemOne = new ItemStack(materialOne, 1);
                                itemTwo = new ItemStack(materialTwo, 1);
                            } else {
                                player.sendMessage("Invalid block type");
                                return true;
                            }
                        }
                        if (args.length > 3) {
                            if (args[3].matches("-?\\d+(\\.\\d+)?")) {
                                size = Integer.parseInt(args[3]);
                            } else {
                                player.sendMessage("Size must be a number");
                                return true;
                            }
                        }
                    }
                    if (size > 54) {
                        player.sendMessage("Size must be a number less than 55");
                        return true;
                    }
                    if (itemOne != null && itemOne.getType() != Material.AIR && itemTwo != null && itemTwo.getType() != Material.AIR) {
                        blockOne.setColourFromHex(blockColours.getString(itemOne.getType().toString().toLowerCase()));
                        blockOne.setMaterial(itemOne.getType());
                        blockTwo.setColourFromHex(blockColours.getString(itemTwo.getType().toString().toLowerCase()));
                        blockOne.setMaterial(itemTwo.getType());
                        BlockColourUtils blockColourUtils = new BlockColourUtils(plugin);
                        blockComparisonList = blockColourUtils.getGradient(blockTwo, blockOne, size);
                        int slots = calculateSlots(blockComparisonList);
                        gui = new BlockGUI(plugin, slots, "Gradient");
                        gui.addBlocks(blockComparisonList);
                        gui.openInventory(player);
                    } else {
                        player.sendMessage("please specify gradient start/end blocks");
                    }
                }
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
        if (slots == 0) {
            slots = 9;
        }
        return slots;
    }

}
