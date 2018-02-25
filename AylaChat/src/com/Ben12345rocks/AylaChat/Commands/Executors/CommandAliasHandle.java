package com.Ben12345rocks.AylaChat.Commands.Executors;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.Ben12345rocks.AdvancedCore.Objects.CommandHandler;
import com.Ben12345rocks.AdvancedCore.Util.Misc.ArrayUtils;
import com.Ben12345rocks.AylaChat.Main;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandAliases.
 */
public class CommandAliasHandle implements CommandExecutor {

	/** The plugin. */
	@SuppressWarnings("unused")
	private Main plugin = Main.plugin;

	/** The cmd handle. */
	private CommandHandler cmdHandle;

	public CommandAliasHandle(CommandHandler cmdHandle) {
		this.cmdHandle = cmdHandle;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.
	 * CommandSender , org.bukkit.command.Command, java.lang.String,
	 * java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		ArrayList<String> argsNew = new ArrayList<String>();
		argsNew.add(cmdHandle.getArgs()[0]);
		for (String arg : args) {
			argsNew.add(arg);
		}

		if (cmdHandle.runCommand(sender, ArrayUtils.getInstance().convert(argsNew))) {
			return true;
		}

		// invalid command

		sender.sendMessage(ChatColor.RED + "No valid arguments, see /alyachat help!");

		return true;
	}
}
