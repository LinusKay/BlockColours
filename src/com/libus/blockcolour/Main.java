package com.libus.blockcolour;

import com.libus.blockcolour.commands.Commands;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("blockcolour").setExecutor(new Commands(this));
        saveResource("blocks.yml", false);
        saveResource("blocks_full_only.yml", false);
        saveResource("colours.yml", false);
        saveResource("config.yml", false);
    }
}
