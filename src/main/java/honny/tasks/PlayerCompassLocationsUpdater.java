package honny.tasks;

import honny.HonnyCompass;
import honny.MainConfigManager;
import honny.controllers.PlayerCompass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerCompassLocationsUpdater extends BukkitRunnable {
    private final HonnyCompass honnyCompass;

    int count = 0;

    public PlayerCompassLocationsUpdater(final HonnyCompass honnyCompass) {
        this.honnyCompass = honnyCompass;
    }

    @Override
    public void run() {
        final MainConfigManager mainConfig = honnyCompass.getMainConfig();

        this.count++;
        if (this.count < mainConfig.getCompassLocationsUpdateDelaySeconds()) {
            return;
        }
        this.count = 0;

        for (final Player player : Bukkit.getOnlinePlayers()) {
            honnyCompass.getCompass(player).ifPresent(PlayerCompass::updateCompassLocations);
        }
    }
}
