package com.libus.blockcolour.models;

import com.libus.blockcolour.Main;
import com.libus.blockcolour.util.BlockColourUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BlockColour {

    private final Main plugin;

    private Color colour;
    private Material material;
    private double difference;

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
     * @param material   String name of block type
     * @param colour     Color object containing colour of block
     * @param difference Value of colour difference between original and this block
     */
    public BlockColour(Main plugin, Material material, Color colour, double difference) {
        this.plugin = plugin;
        this.material = material;
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
     * Get type of block
     *
     * @return String containing type of block
     */
    public Material getBlockMaterial() {
        return this.material;
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
     * Set colour parameter using hexcode string
     *
     * @param hexCode String hexcode to convert into Color object
     */
    public void setColourFromHex(String hexCode) {
        this.colour = Color.decode(hexCode);
    }

    /**
     * Set Material (block type) of object
     *
     * @param material
     */
    public void setMaterial(Material material) {
        this.material = material;
    }
}
