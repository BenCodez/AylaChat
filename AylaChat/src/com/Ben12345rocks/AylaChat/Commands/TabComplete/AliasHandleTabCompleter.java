package com.Ben12345rocks.AylaChat.Commands.TabComplete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.Ben12345rocks.AdvancedCore.CommandAPI.CommandHandler;
import com.Ben12345rocks.AdvancedCore.CommandAPI.TabCompleteHandler;
import com.Ben12345rocks.AdvancedCore.Util.Messages.StringParser;
import com.Ben12345rocks.AdvancedCore.Util.Misc.ArrayUtils;
import com.Ben12345rocks.AylaChat.Main;

/**
 * The Class AliasesTabCompleter.
 */
public class AliasHandleTabCompleter implements TabCompleter {

	/** The plugin. */
	Main plugin = Main.plugin;

	/** The cmd handle. */
	public ArrayList<CommandHandler> cmdHandles = new ArrayList<CommandHandler>();

	public AliasHandleTabCompleter add(CommandHandler cmd) {
		cmdHandles.add(cmd);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bukkit.command.TabCompleter#onTabComplete(org.bukkit.command.
	 * CommandSender, org.bukkit.command.Command, java.lang.String,
	 * java.lang.String[])
	 */
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] argsIn) {
		ArrayList<String> msgArray = new ArrayList<String>();
		msgArray.add("");
		for (String arg : argsIn) {
			msgArray.add(arg);
		}

		String[] args = ArrayUtils.getInstance().convert(msgArray);

		ArrayList<String> tab = new ArrayList<String>();

		Set<String> cmds = new HashSet<String>();

		ConcurrentHashMap<String, ArrayList<String>> tabCompletes = TabCompleteHandler.getInstance()
				.getTabCompleteOptions();
		for (CommandHandler cmdHandle : cmdHandles) {
			if (cmdHandle.getArgs().length >= argsIn.length) {
				for (String arg : cmdHandle.getArgs()[0].split("&")) {
					args[0] = arg;
					boolean argsMatch = true;
					for (int i = 0; i < argsIn.length; i++) {
						if (args.length >= i) {
							if (!cmdHandle.argsMatch(args[i], i)) {
								argsMatch = false;
							}
						}
					}

					if (argsMatch) {

						cmds.addAll(cmdHandle.getTabCompleteOptions(sender, args, argsIn.length, tabCompletes));
					}

				}
			}
		}

		for (String str : cmds) {
			if (StringParser.getInstance().startsWithIgnoreCase(str, args[args.length - 1])) {
				tab.add(str);
			}
		}

		Collections.sort(tab, String.CASE_INSENSITIVE_ORDER);

		return tab;
	}

}
