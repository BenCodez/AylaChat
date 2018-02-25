package com.Ben12345rocks.AylaChat.Commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Ben12345rocks.AdvancedCore.Objects.CommandHandler;
import com.Ben12345rocks.AdvancedCore.Objects.TabCompleteHandle;
import com.Ben12345rocks.AdvancedCore.Objects.TabCompleteHandler;
import com.Ben12345rocks.AdvancedCore.Util.Misc.ArrayUtils;
import com.Ben12345rocks.AylaChat.Main;
import com.Ben12345rocks.AylaChat.Commands.Executors.CommandAliases;
import com.Ben12345rocks.AylaChat.Commands.TabComplete.AliasesTabCompleter;
import com.Ben12345rocks.AylaChat.Objects.ChannelHandler;
import com.Ben12345rocks.AylaChat.Objects.PluginMessageHandler;
import com.Ben12345rocks.AylaChat.Objects.User;
import com.Ben12345rocks.AylaChat.Objects.UserManager;
import com.google.common.collect.Iterables;

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

		plugin.commands.add(new CommandHandler(new String[] { "SocialSpy", "(Boolean)" }, "AylaChat.SocialSpy",
				"Set whether or not social spy is enabled", false) {

			@Override
			public void execute(CommandSender sender, String[] args) {
				UserManager.getInstance().getAylaChatUser((Player) sender)
						.setSocialSpyEnabled(Boolean.valueOf(args[1]));
				sendMessage(sender, "Set Socialspy to " + args[1]);
			}
		});

		plugin.commands.add(new CommandHandler(new String[] { "Mute", "(Player)" }, "AylaChat.Mute", "Mute player") {

			@Override
			public void execute(CommandSender sender, String[] args) {
				User user = UserManager.getInstance().getAylaChatUser(args[1]);
				boolean muted = !user.getMuted();
				user.setMuted(muted);
				sendMessage(sender, "&cSet muted for " + args[1] + " to " + muted);
			}
		});

		plugin.commands.add(
				new CommandHandler(new String[] { "Msg", "(Player)", "(List)" }, "AylaChat.Msg", "Msg other players") {

					@Override
					public void execute(CommandSender sender, String[] args) {
						CommandLoader.this.sendMessage(sender, args[1], ArrayUtils.getInstance().makeString(2, args));
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

		loadAliases();

		plugin.pluginMessages.add(new PluginMessageHandler() {

			@Override
			public void onRecieve(String subChannel, ArrayList<String> messageData) {
				if (subChannel.equals("Msg")) {
					String sender = messageData.get(0);
					String toSend = messageData.get(1);
					String msg = messageData.get(2);

					Player p = Bukkit.getPlayer(toSend);
					if (p != null) {
						p.sendMessage(sender + " -> You: " + msg);
					}
				}
			}
		});
	}

	public void loadAliases() {
		for (CommandHandler cmdHandle : plugin.commands) {
			if (cmdHandle.getArgs().length > 0) {
				String[] args = cmdHandle.getArgs()[0].split("&");
				for (String arg : args) {
					try {
						plugin.getCommand("aylachat" + arg).setExecutor(new CommandAliases(cmdHandle));

						plugin.getCommand("aylachat" + arg)
								.setTabCompleter(new AliasesTabCompleter().setCMDHandle(cmdHandle));
					} catch (Exception ex) {
						plugin.debug("Failed to load command and tab completer for /aylachat" + arg);
					}
				}

			}
		}
	}

	public void sendMessage(CommandSender player, String toSend, String msg) {
		String sender = player.getName();
		if (!(player instanceof Player)) {
			sender = "CONSOLE";
		}
		player.sendMessage("YOU -> " + toSend + ": " + msg);

		plugin.sendPluginMessage(Iterables.getFirst(Bukkit.getOnlinePlayers(), null), "Msg", sender, toSend, msg);

	}
}
