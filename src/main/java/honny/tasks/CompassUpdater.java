package honny.tasks;

import honny.HonnyCompass;
import honny.MainConfigManager;
import honny.controllers.PlayerCompass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class CompassUpdater extends BukkitRunnable {
    private final HonnyCompass honnyCompass;

    int count = 0;

    public CompassUpdater(final HonnyCompass honnyCompass) {
        this.honnyCompass = honnyCompass;
    }

    @Override
    public void run() {
        final MainConfigManager mainConfig = honnyCompass.getMainConfig();

        count++;
        if (count < mainConfig.getTicksUpdateCompass()) {
            return;
        }
        count = 0;

        for (final Player player : Bukkit.getOnlinePlayers()) {
            final Optional<PlayerCompass> optionalPlayerCompass = honnyCompass.getCompass(player);
            if (optionalPlayerCompass.isEmpty()) {
                honnyCompass.createCompass(player);
            } else {
                optionalPlayerCompass.get().update();
            }
        }
    }
}
