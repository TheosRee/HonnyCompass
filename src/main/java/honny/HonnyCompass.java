package honny;

import honny.commands.CommandsHandler;
import honny.controllers.PlayerCompass;
import honny.handlers.PlayerQuitHandler;
import honny.handlers.QuestCompassTargetChangeHandler;
import honny.tasks.CompassUpdater;
import honny.tasks.PlayerCompassLocationsUpdater;
import lombok.Getter;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class HonnyCompass extends JavaPlugin {

    private final Map<UUID, PlayerCompass> compasses = new HashMap<>();

    @Getter
    private MainConfigManager mainConfig;

    private BetonQuest betonQuest;

    @Override
    public void onDisable() {
        for (final PlayerCompass value : this.compasses.values()) {
            value.deleteBossBar();
        }
        this.compasses.clear();
    }

    @Override
    public void onEnable() {
        betonQuest = BetonQuest.getInstance();

        // Cybermedium
        this.getLogger().info("§d_  _ ____ _  _ _  _ _   _ ____ ____ _  _ ___  ____ ____ ____ ");
        this.getLogger().info("§d|__| |  | |\\ | |\\ |  \\_/  |    |  | |\\/| |__] |__| [__  [__  ");
        this.getLogger().info("§d|  | |__| | \\| | \\|   |   |___ |__| |  | |    |  | ___] ___] ");
        this.getLogger().info("§d                                                             ");

        final CommandsHandler commandsHandler = new CommandsHandler(this);
        final PluginCommand command = Objects.requireNonNull(this.getCommand("honnycompass-reload"));
        command.setExecutor(commandsHandler);
        command.setTabCompleter(commandsHandler);

        this.saveDefaultConfig();
        this.reloadMainConfig();

        getServer().getPluginManager().registerEvents(new PlayerQuitHandler(this), this);
        getServer().getPluginManager().registerEvents(new QuestCompassTargetChangeHandler(this), this);

        final CompassUpdater compassUpdater = new CompassUpdater(this);
        compassUpdater.runTaskTimer(this, 20, 1);

        final PlayerCompassLocationsUpdater playerCompassLocationsUpdater = new PlayerCompassLocationsUpdater(this);
        playerCompassLocationsUpdater.runTaskTimerAsynchronously(this, 20, 20);

        this.getLogger().info("§dPlugin loaded");
    }

    public void reloadMainConfig() {
        for (final PlayerCompass value : this.compasses.values()) {
            value.deleteBossBar();
        }
        this.compasses.clear();
        this.reloadConfig();

        mainConfig = new MainConfigManager(getLogger(), this.getConfig());
        this.getLogger().info("§dConfig loaded");
    }

    public PlayerCompass createCompass(final Player player) {
        final BetonQuestLogger logger = betonQuest.getLoggerFactory().create(this, "PlayerCompass");
        final PlayerCompass playerCompass = new PlayerCompass(logger, betonQuest, mainConfig, player);
        this.compasses.put(player.getUniqueId(), playerCompass);
        return playerCompass;
    }

    public Optional<PlayerCompass> getCompass(final Profile profile) {
        return Optional.ofNullable(compasses.get(profile.getPlayerUUID()));
    }

    public Optional<PlayerCompass> getCompass(final Player player) {
        return Optional.ofNullable(compasses.get(player.getUniqueId()));
    }

    public void deleteCompass(final Player player) {
        final PlayerCompass removed = compasses.remove(player.getUniqueId());
        if (removed != null) {
            removed.deleteBossBar();
        }
    }
}
