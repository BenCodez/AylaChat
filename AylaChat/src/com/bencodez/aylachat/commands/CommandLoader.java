package com.bencodez.aylachat.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bencodez.advancedcore.api.command.CommandHandler;
import com.bencodez.advancedcore.api.command.TabCompleteHandle;
import com.bencodez.advancedcore.api.command.TabCompleteHandler;
import com.bencodez.advancedcore.api.inventory.BInventory;
import com.bencodez.advancedcore.api.inventory.BInventory.ClickEvent;
import com.bencodez.advancedcore.api.inventory.BInventoryButton;
import com.bencodez.advancedcore.api.item.ItemBuilder;
import com.bencodez.advancedcore.api.messages.StringParser;
import com.bencodez.advancedcore.api.misc.ArrayUtils;
import com.bencodez.advancedcore.api.misc.MiscUtils;
import com.bencodez.advancedcore.api.misc.PlayerUtils;
import com.bencodez.advancedcore.api.rewards.RewardBuilder;
import com.bencodez.advancedcore.bungeeapi.pluginmessage.PluginMessageHandler;
import com.bencodez.aylachat.AylaChatMain;
import com.bencodez.aylachat.commands.executors.CommandAliasHandle;
import com.bencodez.aylachat.commands.executors.CommandAliases;
import com.bencodez.aylachat.commands.gui.EditingGUI;
import com.bencodez.aylachat.commands.tabcomplete.AliasHandleTabCompleter;
import com.bencodez.aylachat.commands.tabcomplete.AliasesTabCompleter;
import com.bencodez.aylachat.objects.AylaChatUser;
import com.bencodez.aylachat.objects.ChannelHandler;
import com.bencodez.aylachat.objects.MessageData;
import com.bencodez.aylachat.objects.UserManager;
import com.google.common.collect.Iterables;

import net.md_5.bungee.api.chat.TextComponent;

public class CommandLoader {

	/** The instance. */
	static CommandLoader instance = new CommandLoader();

	/** The plugin. */
	static AylaChatMain plugin = AylaChatMain.plugin;

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

		plugin.setCommands(new ArrayList<CommandHandler>());

		ArrayList<CommandHandler> advancedCoreCommands = new ArrayList<CommandHandler>();
		advancedCoreCommands.addAll(com.bencodez.advancedcore.command.CommandLoader.getInstance()
				.getBasicAdminCommands(AylaChatMain.plugin.getName()));
		advancedCoreCommands.addAll(com.bencodez.advancedcore.command.CommandLoader.getInstance()
				.getBasicCommands(AylaChatMain.plugin.getName()));
		for (CommandHandler handle : advancedCoreCommands) {
			String[] args = handle.getArgs();
			String[] newArgs = new String[args.length + 1];
			newArgs[0] = "AdvancedCore";
			for (int i = 0; i < args.length; i++) {
				newArgs[i + 1] = args[i];
			}
			handle.setArgs(newArgs);
			plugin.getCommands().add(handle);
		}

		plugin.getCommands().add(new CommandHandler(new String[] { "Help" }, "AylaChat.Help", "View help information") {

			@Override
			public void execute(CommandSender sender, String[] args) {
				ArrayList<TextComponent> texts = new ArrayList<TextComponent>();
				HashMap<String, TextComponent> unsorted = new HashMap<String, TextComponent>();
				texts.add(StringParser.getInstance().stringToComp(plugin.getConfigFile().formatHelpTitle));

				boolean requirePerms = plugin.getConfigFile().formatHelpRequirePermission;

				for (CommandHandler cmdHandle : plugin.getCommands()) {
					if (!cmdHandle.isAdvancedCoreCommand()) {
						if (cmdHandle.hasPerm(sender)) {
							unsorted.put(cmdHandle.getHelpLineCommand("/aylachat"), cmdHandle.getHelpLine("/aylachat"));
						} else if (!requirePerms) {
							unsorted.put(cmdHandle.getHelpLineCommand("/aylachat"), cmdHandle.getHelpLine("/aylachat"));
						}
					}
				}

				ArrayList<String> unsortedList = new ArrayList<String>();
				unsortedList.addAll(unsorted.keySet());
				Collections.sort(unsortedList, String.CASE_INSENSITIVE_ORDER);
				for (String cmd : unsortedList) {
					texts.add(unsorted.get(cmd));
				}
				sendMessageJson(sender, texts);
			}
		});

		plugin.getCommands()
				.add(new CommandHandler(new String[] { "AdvancedCoreHelp" }, "AylaChat.Help", "View help information") {

					@Override
					public void execute(CommandSender sender, String[] args) {
						ArrayList<TextComponent> texts = new ArrayList<TextComponent>();
						HashMap<String, TextComponent> unsorted = new HashMap<String, TextComponent>();
						texts.add(StringParser.getInstance().stringToComp(plugin.getConfigFile().formatHelpTitle));

						boolean requirePerms = plugin.getConfigFile().formatHelpRequirePermission;

						for (CommandHandler cmdHandle : plugin.getCommands()) {
							if (cmdHandle.isAdvancedCoreCommand()) {
								if (cmdHandle.hasPerm(sender)) {
									unsorted.put(cmdHandle.getHelpLineCommand("/aylachat"),
											cmdHandle.getHelpLine("/aylachat"));
								} else if (!requirePerms) {
									unsorted.put(cmdHandle.getHelpLineCommand("/aylachat"),
											cmdHandle.getHelpLine("/aylachat"));
								}
							}
						}

						ArrayList<String> unsortedList = new ArrayList<String>();
						unsortedList.addAll(unsorted.keySet());
						Collections.sort(unsortedList, String.CASE_INSENSITIVE_ORDER);
						for (String cmd : unsortedList) {
							texts.add(unsorted.get(cmd));
						}
						sendMessageJson(sender, texts);
					}
				});

		plugin.getCommands().add(new CommandHandler(new String[] { "Reload" }, "AylaChat.Reload", "Reload the plugin") {

			@Override
			public void execute(CommandSender sender, String[] args) {
				plugin.reload();
				sendMessage(sender, "&cReloaded plugin");
			}
		});

		plugin.getCommands().add(new CommandHandler(new String[] { "SetChannel", "(Channel)" }, "AylaChat.SetChannel",
				"Set your channel", false) {

			@Override
			public void execute(CommandSender sender, String[] args) {
				UserManager.getInstance().getAylaChatUser((Player) sender).setCurrentChannel(args[1]);
				sendMessage(sender,
						StringParser.getInstance().replacePlaceHolder(plugin.getConfigFile().formatChannelSet,
								"channel", ChannelHandler.getInstance().getChannel(args[1]).getChannelName()));
			}
		});

		plugin.getCommands().add(new CommandHandler(new String[] { "ClearChat" }, "AylaChat.ClearChat", "Clear Chat") {

			@Override
			public void execute(CommandSender sender, String[] args) {
				ChannelHandler.getInstance().clearChatAll();
				plugin.sendPluginMessage(PlayerUtils.getInstance().getRandomPlayer(), "ClearChat", "All");
				sendMessage(sender, "&cChat cleared");
			}
		});

		plugin.getCommands().add(new CommandHandler(new String[] { "ClearChat", "(Player)" },
				"AylaChat.ClearChat.Player", "Clear Chat for a specific player") {

			@Override
			public void execute(CommandSender sender, String[] args) {
				Player p = Bukkit.getPlayer(args[1]);
				if (p != null) {
					ChannelHandler.getInstance().clearChat(p);
					sendMessage(sender, "&cChat cleared for " + p.getName());
				} else {
					if (plugin.getConfigFile().useBungeeCoord) {
						plugin.sendPluginMessage(PlayerUtils.getInstance().getRandomPlayer(), "ClearChat", args[1]);
						sendMessage(sender, "&cChat cleared for " + args[1]);
						return;
					}
					sendMessage(sender, "&cPlayer " + args[1] + " not found/online");
				}
			}
		});

		plugin.getCommands().add(new CommandHandler(new String[] { "SocialSpy", "(Boolean)" }, "AylaChat.SocialSpy",
				"Set whether or not social spy is enabled", false) {

			@Override
			public void execute(CommandSender sender, String[] args) {
				AylaChatUser user = UserManager.getInstance().getAylaChatUser((Player) sender);
				user.setSocialSpyEnabled(Boolean.valueOf(args[1]));
				if (user.getSocialSpyEnabled()) {
					sendMessage(sender, "&cEnabled socialspy");
				} else {
					sendMessage(sender, "&cDisabled socialspy");
				}
			}
		});

		plugin.getCommands().add(new CommandHandler(new String[] { "SocialSpy" }, "AylaChat.SocialSpy",
				"Set whether or not social spy is enabled", false) {

			@Override
			public void execute(CommandSender sender, String[] args) {
				AylaChatUser user = UserManager.getInstance().getAylaChatUser((Player) sender);
				user.setSocialSpyEnabled(!user.getSocialSpyEnabled());
				if (user.getSocialSpyEnabled()) {
					sendMessage(sender, "&cEnabled socialspy");
				} else {
					sendMessage(sender, "&cDisabled socialspy");
				}
			}
		});

		plugin.getCommands()
				.add(new CommandHandler(new String[] { "Mute", "(Player)" }, "AylaChat.Mute", "Mute player") {

					@Override
					public void execute(CommandSender sender, String[] args) {
						AylaChatUser user = UserManager.getInstance().getAylaChatUser(args[1]);
						if (user.isMuted()) {
							user.unMute();
							sendMessage(sender, "&cUnmuted " + user.getPlayerName());
						} else {
							user.mute();
							sendMessage(sender, "&cMuted " + user.getPlayerName());
						}
						plugin.sendPluginMessage(user.getPlayer(), "Mute", user.getPlayerName(), "" + user.isMuted());
					}
				});

		plugin.getCommands().add(
				new CommandHandler(new String[] { "Msg", "(Player)", "(List)" }, "AylaChat.Msg", "Msg other players") {

					@Override
					public void execute(CommandSender sender, String[] args) {
						CommandLoader.this.sendMessage(sender, args[1], ArrayUtils.getInstance().makeString(2, args));
					}
				});

		plugin.getCommands()
				.add(new CommandHandler(new String[] { "Reply", "(List)" }, "AylaChat.Msg", "Reply to last message") {

					@Override
					public void execute(CommandSender sender, String[] args) {
						String toSend = "";
						if (sender instanceof Player) {
							toSend = UserManager.getInstance().getAylaChatUser(sender.getName()).getlastMessageSender();
						}
						CommandLoader.this.sendMessage(sender, toSend, ArrayUtils.getInstance().makeString(1, args));
					}
				});

		plugin.getCommands().add(
				new CommandHandler(new String[] { "ChannelEdit" }, "AylaChat.ChannelEdit", "Edit channels", false) {

					@Override
					public void execute(CommandSender sender, String[] args) {
						EditingGUI.getInstance().openGUI((Player) sender);
					}
				});

		plugin.getCommands().add(new CommandHandler(new String[] { "button", "(Number)" }, "AylaChat.Button",
				"Command to access Json Button", false) {

			@Override
			public void execute(CommandSender sender, String[] args) {
				MessageData data = ChannelHandler.getInstance().getMessageHistory().get(Integer.parseInt(args[1]));
				HashMap<String, String> placeholders = new HashMap<String, String>();
				placeholders.put("Message", data.getMessage());
				placeholders.put("Channel", data.getChannel());
				placeholders.put("player", data.getPlayer());
				BInventory inv = new BInventory("Player: " + data.getPlayer() + " (" + args[1] + ")");
				for (final String key : plugin.getConfigFile().JsonButtonGUI()) {
					inv.addButton(new BInventoryButton(new ItemBuilder(
							plugin.getConfigFile().getData().getConfigurationSection("JsonButtonGUI." + key))
									.setPlaceholders(placeholders)) {

						@Override
						public void onClick(ClickEvent event) {
							Player player = Bukkit.getPlayer(data.getPlayer());
							if (player != null) {
								ArrayList<String> cmds = plugin.getConfigFile().getJsonButtonGUIKeyCommands(key);
								if (!cmds.isEmpty()) {
									MiscUtils.getInstance().executeConsoleCommands(player, cmds, placeholders);
								}
							} else {
								plugin.sendPluginMessage(event.getPlayer(), "GUICommand", data.getPlayer(), key,
										data.getChannel(), data.getMessage());
							}
						}
					});
				}

				inv.openInventory((Player) sender);
			}
		});

		/*
		 * plugin.getCommands().add(new CommandHandler(new String[] { "RemoveMessage",
		 * "(Number)" }, "AylaChat.RemoveMessage", "Remove Message", false) {
		 * 
		 * @Override public void execute(CommandSender sender, String[] args) {
		 * MessageData data =
		 * ChannelHandler.getInstance().getMessageHistory().get(Integer.parseInt(args[1]
		 * )); data.setMessage("&cMessage removed");
		 * ChannelHandler.getInstance().removedMessage(Bukkit.getPlayer(data.getPlayer()
		 * ), ChannelHandler.getInstance().getChannel(data.getChannel())); } });
		 */

		TabCompleteHandler.getInstance().addTabCompleteOption(
				new TabCompleteHandle("(Channel)", ChannelHandler.getInstance().getChannelNames()) {

					@Override
					public void reload() {
						setReplace(ChannelHandler.getInstance().getChannelNames());
					}

					@Override
					public void updateReplacements() {

					}
				});

		loadAliases();

		plugin.getPluginMessaging().add(new PluginMessageHandler("GUICommand") {

			@Override
			public void onRecieve(String subChannel, ArrayList<String> messageData) {
				String p = messageData.get(0);
				String msg = messageData.get(3);
				String channel = messageData.get(2);
				String key = messageData.get(1);

				HashMap<String, String> placeholders = new HashMap<String, String>();
				placeholders.put("Message", msg);
				placeholders.put("Channel", channel);
				placeholders.put("player", p);

				Player player = Bukkit.getPlayer(p);
				if (player != null) {
					MiscUtils.getInstance().executeConsoleCommands(p,
							plugin.getConfigFile().getJsonButtonGUIKeyCommands(key), placeholders);
				}
			}
		});

		plugin.getPluginMessaging().add(new PluginMessageHandler("Msg") {

			@Override
			public void onRecieve(String subChannel, ArrayList<String> messageData) {
				String sender = messageData.get(0);
				String toSend = messageData.get(1);
				String msg = messageData.get(2);

				messageReceived(sender, toSend, msg);
			}
		});

		plugin.getPluginMessaging().add(new PluginMessageHandler("Mute") {

			@Override
			public void onRecieve(String subChannel, ArrayList<String> messageData) {
				String player = messageData.get(0);
				String muted = messageData.get(1);

				AylaChatUser user = UserManager.getInstance().getAylaChatUser(player);
				if (Boolean.valueOf(muted)) {
					user.mute();
				} else {
					user.unMute();
				}
			}
		});
	}

	public void loadAliases() {
		for (CommandHandler cmdHandle : plugin.getCommands()) {
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

					// special commands
					if (arg.equalsIgnoreCase("msg")) {
						setCommand("msg", cmdHandle);
					} else if (arg.equalsIgnoreCase("reply")) {
						setCommand("reply", cmdHandle);
					} else if (arg.equalsIgnoreCase("socialspy")) {
						setCommand("socialspy", cmdHandle);
					} else if (arg.equalsIgnoreCase("clearchat")) {
						setCommand("clearchat", cmdHandle);
					} else if (arg.equalsIgnoreCase("mute")) {
						setCommand("mute", cmdHandle);
					}

				}

			}
		}
	}

	public void messageReceived(String sender, String toSend, String msg) {
		toSend = com.bencodez.advancedcore.api.user.UserManager.getInstance().getProperName(toSend);
		sender = com.bencodez.advancedcore.api.user.UserManager.getInstance().getProperName(sender);
		HashMap<String, String> placeholders = new HashMap<String, String>();
		placeholders.put("player", toSend);
		placeholders.put("fromsender", sender);
		placeholders.put("message", msg);
		String format = StringParser.getInstance().replacePlaceHolder(plugin.getConfigFile().formatMessageReceive,
				placeholders);

		Player p = Bukkit.getPlayer(toSend);
		if (p != null) {
			p.sendMessage(format);
			UserManager.getInstance().getAylaChatUser(p).setlastMessageSender(sender);
		}

		new RewardBuilder(plugin.getConfigFile().getData(), plugin.getConfigFile().formatMessageRewards).send(p);

		ChannelHandler.getInstance().socialSpyMessage(format);

	}

	public void sendMessage(CommandSender player, String toSend, String msg) {
		if (toSend.equals("")) {
			player.sendMessage(StringParser.getInstance().colorize(plugin.getConfigFile().formatMessageNoReply));
			return;
		}

		String sender = player.getName();
		if (!(player instanceof Player)) {
			sender = "CONSOLE";
		} else {
			UserManager.getInstance().getAylaChatUser(player.getName()).setlastMessageSender(toSend);
		}
		toSend = com.bencodez.advancedcore.api.user.UserManager.getInstance().getProperName(toSend);
		HashMap<String, String> placeholders = new HashMap<String, String>();
		placeholders.put("player", sender);
		placeholders.put("toSend", toSend);
		placeholders.put("message", msg);
		String format = StringParser.getInstance().replacePlaceHolder(plugin.getConfigFile().formatMessageSend,
				placeholders);

		player.sendMessage(StringParser.getInstance().colorize(format));

		if (plugin.getConfigFile().useBungeeCoord) {
			plugin.sendPluginMessage(Iterables.getFirst(Bukkit.getOnlinePlayers(), null), "Msg", sender, toSend, msg);
		} else {
			messageReceived(sender, toSend, msg);
		}

	}

	private void setCommand(String command, CommandHandler cmdHandle) {
		try {
			CommandExecutor handle = plugin.getCommand(command).getExecutor();
			if (handle != null) {
				if (handle instanceof CommandAliasHandle) {
					CommandAliasHandle exec = (CommandAliasHandle) plugin.getCommand(command).getExecutor();
					exec.add(cmdHandle);
					AliasHandleTabCompleter tab = (AliasHandleTabCompleter) plugin.getCommand(command)
							.getTabCompleter();
					tab.add(cmdHandle);
				} else {
					plugin.getCommand(command).setExecutor(new CommandAliasHandle(cmdHandle));
					plugin.getCommand(command).setTabCompleter(new AliasHandleTabCompleter().add(cmdHandle));

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
