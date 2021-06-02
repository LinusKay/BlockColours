package com.libus.blockcolour;

import com.libus.blockcolour.commands.Commands;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("compareblock").setExecutor(new Commands(this));
        saveResource("blocks.yml", false);
        saveResource("colours.yml", false);
    }
}
