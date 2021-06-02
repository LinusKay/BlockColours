package com.libus.blockcolour.models;

import org.bukkit.configuration.file.YamlConfiguration;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BlockColour {

    private Color colour;
    private final List<BlockComparison> blockComparisonList = new ArrayList<>();

    public BlockColour(){

    }

    public void setColourFromString(String colourString){
        this.colour = Color.decode(colourString);
    }

    public List<BlockComparison> getSimilarBlocks(YamlConfiguration blockList, int minDifference, int maxDifference){
        this.blockComparisonList.clear();
        for (String block : blockList.getConfigurationSection("").getKeys(false)) {
            String colourString = blockList.getString(block);
            Color secondColour = Color.decode(colourString);

            double difference = calculateColourDifference(this.colour, secondColour);
            if (difference >= minDifference && difference < maxDifference) {

                BlockComparison blockComparison = new BlockComparison(block, difference);
                blockComparisonList.add(blockComparison);
            }
        }
        return this.blockComparisonList;
    }

    public List<BlockComparison> getComplementaryBlocks(YamlConfiguration blockList, int minDifference, int maxDifference){
        this.blockComparisonList.clear();
        for (String block : blockList.getConfigurationSection("").getKeys(false)) {
            String colourString = blockList.getString(block);
            Color secondColour = Color.decode(colourString);

            Color complementary = calculateComplementaryColour(this.colour);
            double difference = calculateColourDifference(complementary, secondColour);
            if (difference >= minDifference && difference < maxDifference) {
                BlockComparison blockComparison = new BlockComparison(block, difference);
                blockComparisonList.add(blockComparison);
            }
        }
        return this.blockComparisonList;
    }

    private double calculateColourDifference(Color firstColour, Color secondColour) {
        int r1 = firstColour.getRed();
        int g1 = firstColour.getGreen();
        int b1 = firstColour.getBlue();
        int r2 = secondColour.getRed();
        int g2 = secondColour.getGreen();
        int b2 = secondColour.getBlue();
        return Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));
    }

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
