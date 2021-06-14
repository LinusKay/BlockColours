package com.libus.blockcolour.util;

import com.libus.blockcolour.Main;
import com.libus.blockcolour.models.BlockColour;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BlockColourUtils {

    private final Main plugin;
    private final List<BlockColour> blockComparisonList = new ArrayList<>();

    public BlockColourUtils(Main plugin) {
        this.plugin = plugin;
    }

    public List<BlockColour> getGradient(BlockColour blockOne, BlockColour blockTwo, double size) {
        Color colorOne = blockOne.getColour();
        Color colorTwo = blockTwo.getColour();
        List<BlockColour> blockColourList = new ArrayList<>();
        double pieceSize = (100 / (size - 1)) / 100;
        File colourFile = new File(plugin.getDataFolder() + "/blocks_full_only.yml");
        YamlConfiguration blocks = YamlConfiguration.loadConfiguration(colourFile);
        for (int i = 0; i < size; i++) {
            double r = colorOne.getRed() * (pieceSize * i) + colorTwo.getRed() * (1 - (pieceSize * i));
            double g = colorOne.getGreen() * (pieceSize * i) + colorTwo.getGreen() * (1 - (pieceSize * i));
            double b = colorOne.getBlue() * (pieceSize * i) + colorTwo.getBlue() * (1 - (pieceSize * i));
            Color newColour = new Color((int) r, (int) g, (int) b);
            String hexcode = "#" + Integer.toHexString(newColour.getRGB()).substring(2);

            int minMSE = Integer.MAX_VALUE;
            int mse;
            String blockType = null;
            for (String block : blocks.getConfigurationSection("").getKeys(false)) {
                String colour = blocks.getString(block);
                if (colour.equals(hexcode)) {
                    blockType = block;
                    break;
                }
                Color testColour = Color.decode(colour.toUpperCase());
                mse = ((
                        (testColour.getRed() - newColour.getRed()) * (testColour.getRed() - newColour.getRed()) +
                                (testColour.getGreen() - newColour.getGreen()) * (testColour.getGreen() - newColour.getGreen()) +
                                (testColour.getBlue() - newColour.getBlue()) * (testColour.getBlue() - newColour.getBlue()))
                        / 3
                );
                if (mse < minMSE) {
                    minMSE = mse;
                    blockType = block;
                }
            }
            Material material = Material.getMaterial(blockType);
            BlockColour blockColour = new BlockColour(plugin, material, newColour, 0);
            blockColourList.add(blockColour);
        }
        return blockColourList;
    }

    /**
     * Calculate difference between two Color objects
     * The lower the value, the more similar two colours are, with 0 being an exact match
     * The higher the value, the more different two colours are, with 255 being most dissimilar
     * Calculation based upon code by Hovercraft Full Of Eels on https://stackoverflow.com/a/8688777
     *
     * @param firstColour  Color object to compare against
     * @param secondColour Color object to compare to original colour
     * @return numerical colour difference
     */
    public double calculateColourDifference(Color firstColour, Color secondColour) {
        int r1 = firstColour.getRed();
        int g1 = firstColour.getGreen();
        int b1 = firstColour.getBlue();
        int r2 = secondColour.getRed();
        int g2 = secondColour.getGreen();
        int b2 = secondColour.getBlue();
        return Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));
    }


    /**
     * Calculate complementary colour for given colour
     * Complementary colour refers to the colour on the opposite side of the colour wheel
     * Calculations are based on code by Poypoyan on https://stackoverflow.com/a/37675777
     *
     * @param firstColour colour to find complement to
     * @return Color object of complementary colour value
     */
    public Color calculateComplementaryColour(Color firstColour) {
        int r1 = firstColour.getRed();
        int g1 = firstColour.getGreen();
        int b1 = firstColour.getBlue();
        int calculation = Math.max(Math.max(r1, g1), b1) + Math.min(Math.min(r1, g1), b1);
        int r2 = calculation - r1;
        int g2 = calculation - g1;
        int b2 = calculation - b1;
        return new Color(r2, g2, b2);
    }


    /**
     * Get matching colour name from hex code
     * If no exact match found, find nearest match
     * MSE calculation based on code by XiaoxiaoLi on https://gist.github.com/XiaoxiaoLi/8031146
     *
     * @return String containing colour name
     */
    public String getClosestColourName(BlockColour block) {
        File colourFile = new File(plugin.getDataFolder() + "/colours.yml");
        YamlConfiguration colours = YamlConfiguration.loadConfiguration(colourFile);

        int minMSE = Integer.MAX_VALUE;
        int mse;
        String closestHexcode = null;
        for (String hexcode : colours.getConfigurationSection("").getKeys(false)) {
            if (hexcode.equals(block.getColourHex())) {
                return colours.getString(block.getColourHex());
            }
            Color newColour = Color.decode("#" + hexcode.toUpperCase());
            mse = ((
                    (newColour.getRed() - block.getColour().getRed()) * (newColour.getRed() - block.getColour().getRed()) +
                            (newColour.getGreen() - block.getColour().getGreen()) * (newColour.getGreen() - block.getColour().getGreen()) +
                            (newColour.getBlue() - block.getColour().getBlue()) * (newColour.getBlue() - block.getColour().getBlue()))
                    / 3
            );
            if (mse < minMSE) {
                minMSE = mse;
                closestHexcode = hexcode;
            }
        }
        return colours.getString(closestHexcode);
    }


    /**
     * Find blocks with similar colour
     * Parses through list of blocks and their colours in blocks.yml
     * Only returns blocks which have a colour difference within min/max tolerance range
     * This range can be set using optional command arguments
     *
     * @param blockList     list of similar blocks
     * @param minDifference low end of similarity tolerance range
     * @param maxDifference high end of similarity tolerance range
     * @return list of similar blocks (can be empty)
     */
    public List<BlockColour> getSimilarBlocks(BlockColour block, YamlConfiguration blockList, int minDifference, int maxDifference) {
        this.blockComparisonList.clear();
        for (String secondBlock : blockList.getConfigurationSection("").getKeys(false)) {
            String colourString = blockList.getString(secondBlock);
            Color secondColour = Color.decode(colourString);

            double difference = calculateColourDifference(block.getColour(), secondColour);
            if (difference >= minDifference && difference <= maxDifference) {
                BlockColour comparisonBlock = new BlockColour(plugin, Material.getMaterial(secondBlock.toUpperCase()), secondColour, difference);
                blockComparisonList.add(comparisonBlock);
            }
        }
        blockComparisonList.sort(Comparator.comparing(BlockColour::getDifference));
        return this.blockComparisonList;
    }

    /**
     * Find blocks with similar colour
     * Parses through list of blocks and their colours in blocks.yml
     * Only returns blocks which have a colour difference within min/max tolerance range
     * This range can be set using optional command arguments
     * Excludes blocks with a difference in the given exclusion range
     *
     * @param blockList     list of similar blocks
     * @param minDifference low end of similarity tolerance range
     * @param maxDifference high end of similarity tolerance range
     * @return list of similar blocks (can be empty)
     */
    public List<BlockColour> getSimilarBlocks(BlockColour block, YamlConfiguration blockList, int minDifference, int maxDifference, int minExclusionDifference, int maxExclusionDifference) {
        this.blockComparisonList.clear();
        for (String secondBlock : blockList.getConfigurationSection("").getKeys(false)) {
            String colourString = blockList.getString(secondBlock);
            Color secondColour = Color.decode(colourString);

            double difference = calculateColourDifference(block.getColour(), secondColour);
            if (difference >= minDifference && difference <= maxDifference && (difference <= minExclusionDifference || difference >= maxExclusionDifference)) {
                BlockColour comparisonBlock = new BlockColour(plugin, Material.getMaterial(secondBlock), secondColour, difference);
                blockComparisonList.add(comparisonBlock);
            }
        }
        blockComparisonList.sort(Comparator.comparing(BlockColour::getDifference));
        return this.blockComparisonList;
    }


    /**
     * Find blocks with complementary/opposite colour
     * Parses through list of blocks and their colours in blocks.yml
     * Only returns blocks which have a colour difference within min/max tolerance range
     * This range can be set using optional command arguments
     *
     * @param blockList     list of similar blocks
     * @param minDifference low end of similarity tolerance range
     * @param maxDifference high end of similarity tolerance range
     * @return list of similar blocks (can be empty)
     */
    public List<BlockColour> getComplementaryBlocks(BlockColour block, YamlConfiguration blockList, int minDifference, int maxDifference) {
        this.blockComparisonList.clear();
        for (String secondBlock : blockList.getConfigurationSection("").getKeys(false)) {
            String colourString = blockList.getString(secondBlock);
            Color secondColour = Color.decode(colourString);

            Color complementary = calculateComplementaryColour(block.getColour());

            double difference = calculateColourDifference(complementary, secondColour);
            if (difference >= minDifference && difference <= maxDifference) {
                BlockColour comparisonBlock = new BlockColour(plugin, Material.getMaterial(secondBlock), secondColour, difference);
                blockComparisonList.add(comparisonBlock);
            }
        }
        blockComparisonList.sort(Comparator.comparing(BlockColour::getDifference).reversed());
        return this.blockComparisonList;
    }


    /**
     * Find blocks with complementary/opposite colour
     * Parses through list of blocks and their colours in blocks.yml
     * Only returns blocks which have a colour difference within min/max tolerance range
     * This range can be set using optional command arguments
     * Excludes blocks with a difference in the given exclusion range
     *
     * @param blockList     list of similar blocks
     * @param minDifference low end of similarity tolerance range
     * @param maxDifference high end of similarity tolerance range
     * @return list of similar blocks (can be empty)
     */
    public List<BlockColour> getComplementaryBlocks(BlockColour block, YamlConfiguration blockList, int minDifference, int maxDifference, int minExclusionDifference, int maxExclusionDifference) {
        this.blockComparisonList.clear();
        for (String secondBlock : blockList.getConfigurationSection("").getKeys(false)) {
            String colourString = blockList.getString(secondBlock);
            Color secondColour = Color.decode(colourString);

            Color complementary = calculateComplementaryColour(block.getColour());

            double difference = calculateColourDifference(complementary, secondColour);
            if (difference >= minDifference && difference <= maxDifference && (difference <= minExclusionDifference || difference >= maxExclusionDifference)) {
                BlockColour comparisonBlock = new BlockColour(plugin, Material.getMaterial(secondBlock), secondColour, difference);
                blockComparisonList.add(comparisonBlock);
            }
        }
        blockComparisonList.sort(Comparator.comparing(BlockColour::getDifference).reversed());
        return this.blockComparisonList;
    }
}
