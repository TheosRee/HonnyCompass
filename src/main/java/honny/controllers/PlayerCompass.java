package honny.controllers;

import honny.MainConfigManager;
import honny.utils.AngleUtil;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang.StringUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.feature.QuestCompass;
import org.betonquest.betonquest.id.CompassID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerCompass {

    private final List<QuestCompass> activeCompasses = new ArrayList<>();

    private final BossBar bossBarCompass;

    private final BossBar bossBarMessage;

    private final BetonQuest betonQuest;

    private final MainConfigManager mainConfig;

    private final Player player;

    private final BetonQuestLogger logger;

    @Setter
    @Nullable
    private Location targetLocation;

    public PlayerCompass(final BetonQuestLogger logger, final BetonQuest betonQuest, final MainConfigManager mainConfig,
            final Player player) {
        this.logger = logger;
        this.betonQuest = betonQuest;
        this.mainConfig = mainConfig;
        this.player = player;
        bossBarCompass = Bukkit.createBossBar("", mainConfig.getBarColor(), mainConfig.getBarStyle());
        bossBarCompass.addPlayer(player);

        bossBarMessage = Bukkit.createBossBar("", mainConfig.getBarColor(), mainConfig.getBarStyle());
        bossBarMessage.addPlayer(player);
        bossBarMessage.setVisible(false);

        this.update();
    }

    public void deleteBossBar() {
        bossBarCompass.removeAll();
        bossBarMessage.removeAll();
    }

    public void updateCompassLocations() {
        activeCompasses.clear();
        final OnlineProfile profile = betonQuest.getProfileProvider().getProfile(player);
        final PlayerData playerData = betonQuest.getPlayerDataStorage().get(profile);
        for (final Map.Entry<CompassID, QuestCompass> entry : betonQuest.getFeatureAPI().getCompasses().entrySet()) {
            if (playerData.hasTag(entry.getKey().getTag())) {
                activeCompasses.add(entry.getValue());
            }
        }
    }

    public void update() {
        final Location playerLocation = player.getLocation();
        if (playerLocation.getWorld() == null) {
            return;
        }

        // All compass is 20 + 40 + 20 sections length
        final int yawPerSection = 9; // 360 / 40 = 9

        // integer division; 2 / 9 = 0
        double yaw = playerLocation.getYaw();
        if (yaw < 0) {
            yaw += 360;
        }
        final int currentYaw = ((int) yaw / yawPerSection) + 20;

        List<String> compassList = new ArrayList<>(mainConfig.getOriginCompass());
        Map.Entry<String, Location> selectedTarget = null;
        boolean targetSelected = false;

        updateCompassLocations();
        final OnlineProfile profile = betonQuest.getProfileProvider().getProfile(player);
        for (final QuestCompass compass : activeCompasses) {
            final Location compassLocation;
            try {
                compassLocation = compass.location().getValue(profile);
            } catch (final QuestException e) {
                logger.warn("Could not parse compass location: " + e.getMessage(), e);
                continue;
            }
            if (!playerLocation.getWorld().equals(compassLocation.getWorld())) {
                continue;
            }

            final int pointYaw = AngleUtil.computeAngle(player, compassLocation) / yawPerSection + 20;

            final boolean selected = this.targetLocation != null && this.targetLocation.equals(compassLocation);

            if (pointYaw == currentYaw) {
                String targetName;
                try {
                    targetName = LegacyComponentSerializer.legacySection().serialize(compass.names().asComponent(profile));
                } catch (final QuestException e) {
                    logger.warn("Could not parse compass name: " + e.getMessage(), e);
                    targetName = "Error";
                }
                targetSelected = selected;
                selectedTarget = Map.entry(targetName, compassLocation);
            }

            final String point;
            if (selected) {
                // below
                if (playerLocation.getY() - compassLocation.getY() > mainConfig.getYDifferenceIcons()) {
                    point = pointYaw == currentYaw ? mainConfig.getSelectedCompassTargetSelectedBelow()
                            : mainConfig.getSelectedCompassTargetBelow();
                }
                // above
                else if (compassLocation.getY() - playerLocation.getY() > mainConfig.getYDifferenceIcons()) {
                    point = pointYaw == currentYaw ? mainConfig.getSelectedCompassTargetSelectedAbove()
                            : mainConfig.getSelectedCompassTargetAbove();
                }
                // at player level
                else {
                    point = pointYaw == currentYaw ? mainConfig.getSelectedCompassTargetSelected()
                            : mainConfig.getSelectedCompassTarget();
                }
            } else {
                // below
                if (playerLocation.getY() - compassLocation.getY() > mainConfig.getYDifferenceIcons()) {
                    point = pointYaw == currentYaw ? mainConfig.getCompassTargetSelectedBelow() : mainConfig.getCompassTargetBelow();
                }
                // above
                else if (compassLocation.getY() - playerLocation.getY() > mainConfig.getYDifferenceIcons()) {
                    point = pointYaw == currentYaw ? mainConfig.getCompassTargetSelectedAbove() : mainConfig.getCompassTargetAbove();
                }
                // at player level
                else {
                    point = pointYaw == currentYaw ? mainConfig.getCompassTargetSelected() : mainConfig.getCompassTarget();
                }
            }

            compassList.set(pointYaw, point);
            if (pointYaw > 40) {
                compassList.set(pointYaw - 40, point);
            }
            if (pointYaw < 40) {
                compassList.set(pointYaw + 40, point);
            }
        }

        compassList = compassList.subList(currentYaw - 10, currentYaw + 11);

        final String currentItem = compassList.get(10);
        if (mainConfig.getReplacers().containsKey(currentItem)) {
            compassList.set(10, mainConfig.getReplacers().get(currentItem));
        }

        final String compass = mainConfig.getBarStart() + StringUtils.join(compassList, "") + mainConfig.getBarEnd();
        bossBarCompass.setTitle(PlaceholderAPI.setPlaceholders(player, compass));

        if (selectedTarget != null) {
            final String baseMessage = targetSelected ? mainConfig.getTitleMessageSelected() : mainConfig.getTitleMessage();
            final double distance = selectedTarget.getValue().distance(playerLocation);
            bossBarMessage.setTitle(PlaceholderAPI.setPlaceholders(player, baseMessage.replace(
                    "{name}", selectedTarget.getKey()
            ).replace(
                    "{distance}", Double.toString(Math.round(distance))
            )));
            bossBarMessage.setVisible(true);
        } else {
            bossBarMessage.setVisible(false);
        }
    }
}
