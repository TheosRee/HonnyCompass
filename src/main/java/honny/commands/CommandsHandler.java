package honny.commands;

import honny.HonnyCompass;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class CommandsHandler implements CommandExecutor, TabCompleter {
    private final HonnyCompass honnyCompass;

    public CommandsHandler(final HonnyCompass honnyCompass) {
        this.honnyCompass = honnyCompass;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            honnyCompass.reloadMainConfig();
            sender.sendMessage("§e[HonnyCompass] Plugin reloaded");
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(final CommandSender commandSender, final Command command, final String alias, final String[] args) {
        if (args.length == 1 && "reload".startsWith(args[0].toLowerCase(Locale.ROOT))) {
            return List.of("reload");
        }
        return List.of();
    }
}
