package com.bencodez.aylachat.commands.executors;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.bencodez.advancedcore.api.command.CommandHandler;
import com.bencodez.advancedcore.api.misc.ArrayUtils;
import com.bencodez.aylachat.AylaChatMain;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandAliases.
 */
public class CommandAliasHandle implements CommandExecutor {

	/** The plugin. */
	@SuppressWarnings("unused")
	private AylaChatMain plugin = AylaChatMain.plugin;

	/** The cmd handle. */
	private ArrayList<CommandHandler> cmdHandles;

	public CommandAliasHandle(CommandHandler cmdHandle) {
		this.cmdHandles = new ArrayList<CommandHandler>();
		this.cmdHandles.add(cmdHandle);
	}

	public void add(CommandHandler cmdHandle) {
		cmdHandles.add(cmdHandle);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.
	 * CommandSender , org.bukkit.command.Command, java.lang.String,
	 * java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		ArrayList<String> argsNew = new ArrayList<String>();
		argsNew.add(cmdHandles.get(0).getArgs()[0]);
		for (String arg : args) {
			argsNew.add(arg);
		}

		for (CommandHandler cmdHandle : cmdHandles) {
			if (cmdHandle.runCommand(sender, ArrayUtils.getInstance().convert(argsNew))) {
				return true;
			}
		}

		// invalid command

		sender.sendMessage(ChatColor.RED + "No valid arguments, see /alyachat help!");

		return true;
	}
}
