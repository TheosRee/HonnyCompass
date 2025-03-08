package honny.handlers;

import honny.HonnyCompass;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitHandler implements Listener {
    private final HonnyCompass honnyCompass;

    public PlayerQuitHandler(final HonnyCompass honnyCompass) {
        this.honnyCompass = honnyCompass;
    }

    @EventHandler
    public void PlayerQuit(final PlayerQuitEvent event) {
        honnyCompass.deleteCompass(event.getPlayer());
    }
}
