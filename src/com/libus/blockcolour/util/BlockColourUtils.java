package com.libus.blockcolour.util;

import com.libus.blockcolour.Main;
import com.libus.blockcolour.models.BlockColour;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class BlockColourUtils {

    private final Main plugin;

    private File blockFile;
    private YamlConfiguration blocksYaml;

    public BlockColourUtils(Main plugin) {
        this.plugin = plugin;

        blockFile = new File(plugin.getDataFolder() + "/blocks_full_only.yml");
        blocksYaml = YamlConfiguration.loadConfiguration(blockFile);
    }

    public List<BlockColour> getGradient(BlockColour blockOne, BlockColour blockTwo, double size){
        Color colorOne = blockOne.getColour();
        Color colorTwo = blockTwo.getColour();
        List<BlockColour> blockColourList = new ArrayList<>();
        double pieceSize = (100/(size-1))/100;
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
        return blockColourList;
    }


    /**
     * Based on code from jittagornp on https://gist.github.com/jittagornp/6c7fcdab388fe4863c34
     * @param originalImage
     * @return
     */
    public List<BlockColour> getHexColor(BufferedImage originalImage) {

        BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics2D.drawImage(originalImage, 0, 0, 50, 50, null);
        graphics2D.dispose();

        try {
            ImageIO.write(image, "jpg", new File(plugin.getDataFolder() + "/image.jpg"));
        } catch(IOException e){
            e.printStackTrace();
        }

        Map<Integer, Integer> colorMap = new HashMap<>();
        int height = image.getHeight();
        int width = image.getWidth();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = image.getRGB(i, j);
                if (!isGray(getRGBArr(rgb))) {
                    Integer counter = colorMap.get(rgb);
                    if (counter == null) {
                        counter = 0;
                    }

                    colorMap.put(rgb, ++counter);
                }
            }
        }

        List<BlockColour> blockColourList = new ArrayList<>();
        List<String> mostCommonColours = getMostCommonColors(colorMap);
        System.out.println(mostCommonColours);
        for(String colour : mostCommonColours){
            blockColourList.add(getNearestBlock(colour, blocksYaml));
        }
        return blockColourList;
    }

    private List<String> getMostCommonColors(Map<Integer, Integer> map) {
        List<Map.Entry<Integer, Integer>> list = new LinkedList<>(map.entrySet());

        Collections.sort(list, (Map.Entry<Integer, Integer> obj1, Map.Entry<Integer, Integer> obj2)
                -> ((Comparable) obj1.getValue()).compareTo(obj2.getValue()));


        List<String> colours = new ArrayList<>();

        int[] itemOne = getRGBArr(list.get(list.size() - 1).getKey());
        Color colourOne = Color.decode("#" + Integer.toHexString(itemOne[0]) + Integer.toHexString(itemOne[1]) + Integer.toHexString(itemOne[2]));
        for(int i = 1; i < list.size(); i++){
            if(i > 1){
                int[] itemTwo = getRGBArr(list.get(list.size() - i).getKey());
                Color colourTwo = Color.decode("#" + Integer.toHexString(itemTwo[0]) + Integer.toHexString(itemTwo[1]) + Integer.toHexString(itemTwo[2]));

                if(calculateColourDifference(colourOne, colourTwo) > 200){
                    colours.add("#" + Integer.toHexString(itemTwo[0]) + Integer.toHexString(itemTwo[1]) + Integer.toHexString(itemTwo[2]));
                }
            }
            else {
                int[] item = getRGBArr(list.get(list.size() - i).getKey());
                colours.add("#" + Integer.toHexString(item[0]) + Integer.toHexString(item[1]) + Integer.toHexString(item[2]));
            }
        }
        return colours;
    }

    private int[] getRGBArr(int pixel) {
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        return new int[]{red, green, blue};
    }

    private boolean isGray(int[] rgbArr) {
        int rgDiff = rgbArr[0] - rgbArr[1];
        int rbDiff = rgbArr[0] - rgbArr[2];
        // Filter out black, white and grays...... (tolerance within 10 pixels)
        int tolerance = 10;
        if (rgDiff > tolerance || rgDiff < -tolerance) {
            if (rbDiff > tolerance || rbDiff < -tolerance) {
                return false;
            }
        }
        return true;
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



    public BlockColour getNearestBlock(String hex, YamlConfiguration blocks) {
        if(hex.length() == 5){
            hex = hex + "00";
        }
        Color originalColour = Color.decode(hex);
        System.out.println(originalColour);
        double r = originalColour.getRed();
        double g = originalColour.getGreen();
        double b = originalColour.getBlue();
        Color newColour = new Color((int) r, (int) g, (int) b);

        int minMSE = Integer.MAX_VALUE;
        int mse;
        String blockType = null;
        for (String block : blocks.getConfigurationSection("").getKeys(false)) {
            String colour = blocks.getString(block);
            if (colour.equals(hex)) {
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
        return blockColour;
    }


}
