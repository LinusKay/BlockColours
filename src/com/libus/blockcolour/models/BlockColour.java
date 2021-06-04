package com.libus.blockcolour.models;

import com.libus.blockcolour.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Would be wise to move a large number of methods into a separate ColourUtils class
 */
public class BlockColour {

    private final Main plugin;

    private Color colour;
    private String block;
    private double difference;

    private final List<BlockColour> blockComparisonList = new ArrayList<>();

    /**
     * Initialise empty object
     *
     * @param plugin Main plugin object
     */
    public BlockColour(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Initialise object with parameters
     *
     * @param plugin     Main plugin object
     * @param block      String name of block type
     * @param colour     Color object containing colour of block
     * @param difference Value of colour difference between original and this block
     */
    public BlockColour(Main plugin, String block, Color colour, double difference) {
        this.plugin = plugin;
        this.block = block;
        this.colour = colour;
        this.difference = difference;
    }

    /**
     * Get Color object from block
     *
     * @return Color block
     */
    public Color getColour() {
        return this.colour;
    }

    /**
     * Get name of block type
     *
     * @return String containing type of block
     */
    public String getBlockName() {
        return this.block;
    }

    /**
     * Get different value from original block
     *
     * @return double containing difference value
     */
    public double getDifference() {
        return this.difference;
    }

    /**
     * Convert block colour into hex code
     *
     * @return String containing colour hex code
     */
    public String getColourHex() {
        return "#" + Integer.toHexString(this.colour.getRGB()).substring(2);
    }

    /**
     * Get matching colour name from hex code
     * If no exact match found, find nearest match
     * MSE calculation based on code by XiaoxiaoLi on https://gist.github.com/XiaoxiaoLi/8031146
     *
     * @return String containing colour name
     */
    public String getColourName() {
        File colourFile = new File(plugin.getDataFolder() + "/colours.yml");
        YamlConfiguration colours = YamlConfiguration.loadConfiguration(colourFile);

        int minMSE = Integer.MAX_VALUE;
        int mse;
        String closestHexcode = null;
        for (String hexcode : colours.getConfigurationSection("").getKeys(false)) {
            if (hexcode.equals(getColourHex())) {
                return colours.getString(getColourHex());
            }
            Color newColour = Color.decode("#" + hexcode.toUpperCase());
            mse = ((
                    (newColour.getRed() - this.colour.getRed()) * (newColour.getRed() - this.colour.getRed()) +
                            (newColour.getGreen() - this.colour.getGreen()) * (newColour.getGreen() - this.colour.getGreen()) +
                            (newColour.getBlue() - this.colour.getBlue()) * (newColour.getBlue() - this.colour.getBlue()))
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
     * Set colour parameter using hexcode string
     *
     * @param hexCode String hexcode to convert into Color object
     */
    public void setColourFromHex(String hexCode) {
        this.colour = Color.decode(hexCode);
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
    public List<BlockColour> getSimilarBlocks(YamlConfiguration blockList, int minDifference, int maxDifference) {
        this.blockComparisonList.clear();
        for (String block : blockList.getConfigurationSection("").getKeys(false)) {
            String colourString = blockList.getString(block);
            Color secondColour = Color.decode(colourString);

            double difference = calculateColourDifference(this.colour, secondColour);
            if (difference >= minDifference && difference <= maxDifference) {
                BlockColour comparisonBlock = new BlockColour(plugin, block, secondColour, difference);
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
    public List<BlockColour> getSimilarBlocks(YamlConfiguration blockList, int minDifference, int maxDifference, int minExclusionDifference, int maxExclusionDifference) {
        this.blockComparisonList.clear();
        for (String block : blockList.getConfigurationSection("").getKeys(false)) {
            String colourString = blockList.getString(block);
            Color secondColour = Color.decode(colourString);

            double difference = calculateColourDifference(this.colour, secondColour);
            if (difference >= minDifference && difference <= maxDifference && (difference <= minExclusionDifference || difference >= maxExclusionDifference)) {
                BlockColour comparisonBlock = new BlockColour(plugin, block, secondColour, difference);
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
    public List<BlockColour> getComplementaryBlocks(YamlConfiguration blockList, int minDifference, int maxDifference) {
        this.blockComparisonList.clear();
        for (String block : blockList.getConfigurationSection("").getKeys(false)) {
            String colourString = blockList.getString(block);
            Color secondColour = Color.decode(colourString);

            Color complementary = calculateComplementaryColour(this.colour);

            double difference = calculateColourDifference(complementary, secondColour);
            if (difference >= minDifference && difference <= maxDifference) {
                BlockColour comparisonBlock = new BlockColour(plugin, block, secondColour, difference);
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
    public List<BlockColour> getComplementaryBlocks(YamlConfiguration blockList, int minDifference, int maxDifference, int minExclusionDifference, int maxExclusionDifference) {
        this.blockComparisonList.clear();
        for (String block : blockList.getConfigurationSection("").getKeys(false)) {
            String colourString = blockList.getString(block);
            Color secondColour = Color.decode(colourString);

            Color complementary = calculateComplementaryColour(this.colour);

            double difference = calculateColourDifference(complementary, secondColour);
            if (difference >= minDifference && difference <= maxDifference && (difference <= minExclusionDifference || difference >= maxExclusionDifference)) {
                BlockColour comparisonBlock = new BlockColour(plugin, block, secondColour, difference);
                blockComparisonList.add(comparisonBlock);
            }
        }
        blockComparisonList.sort(Comparator.comparing(BlockColour::getDifference).reversed());
        return this.blockComparisonList;
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
    private double calculateColourDifference(Color firstColour, Color secondColour) {
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
    private Color calculateComplementaryColour(Color firstColour) {
        int r1 = firstColour.getRed();
        int g1 = firstColour.getGreen();
        int b1 = firstColour.getBlue();
        int calculation = Math.max(Math.max(r1, g1), b1) + Math.min(Math.min(r1, g1), b1);
        int r2 = calculation - r1;
        int g2 = calculation - g1;
        int b2 = calculation - b1;
        return new Color(r2, g2, b2);
    }

}
