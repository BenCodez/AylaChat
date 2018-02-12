package com.Ben12345rocks.AylaChat.Commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Ben12345rocks.AdvancedCore.Objects.CommandHandler;
import com.Ben12345rocks.AdvancedCore.Objects.TabCompleteHandle;
import com.Ben12345rocks.AdvancedCore.Objects.TabCompleteHandler;
import com.Ben12345rocks.AylaChat.Main;
import com.Ben12345rocks.AylaChat.Objects.ChannelHandler;
import com.Ben12345rocks.AylaChat.Objects.UserManager;

public class CommandLoader {

	/** The instance. */
	static CommandLoader instance = new CommandLoader();

	/** The plugin. */
	static Main plugin = Main.plugin;

	/**
	 * Gets the single instance of CommandLoader.
	 *
	 * @return single instance of CommandLoader
	 */
	public static CommandLoader getInstance() {
		return instance;
	}

	/**
	 * Instantiates a new command loader.
	 */
	private CommandLoader() {
	}

	public void load() {
		plugin.commands = new ArrayList<CommandHandler>();

		plugin.commands.addAll(
				com.Ben12345rocks.AdvancedCore.Commands.CommandLoader.getInstance().getBasicCommands("AylaChat"));
		plugin.commands.addAll(
				com.Ben12345rocks.AdvancedCore.Commands.CommandLoader.getInstance().getBasicAdminCommands("AylaChat"));

		plugin.commands.add(new CommandHandler(new String[] { "Help" }, "AylaChat.Help", "View help information") {

			@Override
			public void execute(CommandSender sender, String[] args) {
				sendMessage(sender, "No help available yet");
			}
		});

		plugin.commands.add(new CommandHandler(new String[] { "Reload" }, "AylaChat.Reload", "Reload the plugin") {

			@Override
			public void execute(CommandSender sender, String[] args) {
				plugin.reload();
				sendMessage(sender, "&cReloaded plugin");
			}
		});

		plugin.commands.add(new CommandHandler(new String[] { "SetChannel", "(Channel)" }, "AylaChat.SetChannel",
				"Set your channel", false) {

			@Override
			public void execute(CommandSender sender, String[] args) {
				UserManager.getInstance().getAylaChatUser((Player) sender).setCurrentChannel(args[1]);
				sendMessage(sender, "Set channel to " + args[1]);
			}
		});

		plugin.commands.add(new CommandHandler(new String[] { "ClearChat" }, "AylaChat.ClearChat", "Clear Chat") {

			@Override
			public void execute(CommandSender sender, String[] args) {
				ChannelHandler.getInstance().clearChatAll();
			}
		});

		plugin.commands.add(new CommandHandler(new String[] { "ClearChat", "(Player)" }, "AylaChat.ClearChat.Player",
				"Clear Chat for a specific player") {

			@Override
			public void execute(CommandSender sender, String[] args) {
				ChannelHandler.getInstance().clearChat(Bukkit.getPlayer(args[1]));
			}
		});

		TabCompleteHandler.getInstance().addTabCompleteOption(
				new TabCompleteHandle("(Channel)", ChannelHandler.getInstance().getChannelNames()) {

					@Override
					public void updateReplacements() {

					}

					@Override
					public void reload() {
						setReplace(ChannelHandler.getInstance().getChannelNames());
					}
				});
	}
}
