package com.Ben12345rocks.AylaChat.Commands.Executors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.Ben12345rocks.AdvancedCore.Objects.CommandHandler;
import com.Ben12345rocks.AylaChat.Main;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandVote.
 */
public class CommandAylaChat implements CommandExecutor {

	/** The instance. */
	private static CommandAylaChat instance = new CommandAylaChat();

	/** The plugin. */
	private static Main plugin;

	/**
	 * Gets the single instance of CommandVote.
	 *
	 * @return single instance of CommandVote
	 */
	public static CommandAylaChat getInstance() {
		return instance;
	}

	/**
	 * Instantiates a new command vote.
	 */
	private CommandAylaChat() {
	}

	/**
	 * Instantiates a new command vote.
	 *
	 * @param plugin
	 *            the plugin
	 */
	public CommandAylaChat(Main plugin) {
		CommandAylaChat.plugin = plugin;
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

		for (CommandHandler commandHandler : plugin.commands) {
			if (commandHandler.runCommand(sender, args)) {
				return true;
			}
		}

		// invalid command
		sender.sendMessage(ChatColor.RED + "No valid arguments, see /aylachat help!");
		return true;
	}

}
