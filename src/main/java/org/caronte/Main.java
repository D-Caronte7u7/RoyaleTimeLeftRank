package org.caronte;

import net.luckperms.api.LuckPerms;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.caronte.commands.TimeRankCommand;
import org.caronte.managers.MessageManager;
import org.caronte.services.TimeRankService;

public class Main extends JavaPlugin {

    private static Main instance;
    private LuckPerms luckPerms;
    private MessageManager messageManager;
    private TimeRankService timeRankService;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        setupLuckPerms();

        messageManager = new MessageManager(this);
        timeRankService = new TimeRankService(luckPerms);

        getCommand("timerank").setExecutor(
                new TimeRankCommand(timeRankService, messageManager)
        );
    }

    private void setupLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider =
                getServer().getServicesManager().getRegistration(LuckPerms.class);

        if (provider != null) {
            luckPerms = provider.getProvider();
        } else {
            getLogger().severe("LuckPerms no encontrado!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public static Main getInstance() {
        return instance;
    }
}