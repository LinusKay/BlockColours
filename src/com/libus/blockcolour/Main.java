package com.libus.blockcolour;

import com.libus.blockcolour.commands.BlockColour;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("compareblock").setExecutor(new BlockColour(this));
        saveResource("blocks.yml", false);
    }
}
