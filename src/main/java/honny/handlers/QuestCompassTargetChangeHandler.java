package honny.handlers;

import honny.HonnyCompass;
import org.betonquest.betonquest.api.bukkit.event.QuestCompassTargetChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestCompassTargetChangeHandler implements Listener {
    private final HonnyCompass honnyCompass;

    public QuestCompassTargetChangeHandler(final HonnyCompass honnyCompass) {
        this.honnyCompass = honnyCompass;
    }

    @EventHandler
    public void QuestCompassTargetChangeEvent(final QuestCompassTargetChangeEvent event) {
        honnyCompass.getCompass(event.getProfile())
                .ifPresent(playerCompass -> playerCompass.setTargetLocation(event.getLocation()));
    }
}
