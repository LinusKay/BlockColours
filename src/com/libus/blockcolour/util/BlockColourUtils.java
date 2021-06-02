package com.libus.blockcolour.util;

import com.libus.blockcolour.Main;
import com.libus.blockcolour.models.BlockColour;
import org.bukkit.configuration.file.YamlConfiguration;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BlockColourUtils {

    private Main plugin;

    public BlockColourUtils(Main plugin) {
        this.plugin = plugin;
    }

    public List<BlockColour> getGradient(BlockColour blockOne, BlockColour blockTwo, double size){
        Color colorOne = blockOne.getColour();
        Color colorTwo = blockTwo.getColour();
        List<BlockColour> blockColourList = new ArrayList<>();
        double pieceSize = (100/(size-1))/100;
        System.out.println("start: " + colorOne.getRed() + "," + colorOne.getGreen() + "," + colorOne.getBlue());
        File colourFile = new File(plugin.getDataFolder() + "/blocks_full_only.yml");
        YamlConfiguration blocks = YamlConfiguration.loadConfiguration(colourFile);
        for(int i = 0; i < size; i++){
            double r = colorOne.getRed() * (pieceSize*i) + colorTwo.getRed() * (1 - (pieceSize*i));
            double g = colorOne.getGreen() * (pieceSize*i) + colorTwo.getGreen() * (1 - (pieceSize*i));
            double b = colorOne.getBlue() * (pieceSize*i) + colorTwo.getBlue() * (1 - (pieceSize*i));
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
            BlockColour blockColour = new BlockColour(plugin, blockType, newColour, 0);
            blockColourList.add(blockColour);
        }
        System.out.println("end: " + colorTwo.getRed() + "," + colorTwo.getGreen() + "," + colorTwo.getBlue());
        return blockColourList;
    }

}
