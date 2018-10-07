package com.Ben12345rocks.AylaChat.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Ben12345rocks.AdvancedCore.CommandAPI.CommandHandler;
import com.Ben12345rocks.AdvancedCore.CommandAPI.TabCompleteHandle;
import com.Ben12345rocks.AdvancedCore.CommandAPI.TabCompleteHandler;
import com.Ben12345rocks.AdvancedCore.Rewards.RewardBuilder;
import com.Ben12345rocks.AdvancedCore.Util.Inventory.BInventory;
import com.Ben12345rocks.AdvancedCore.Util.Inventory.BInventory.ClickEvent;
import com.Ben12345rocks.AdvancedCore.Util.Inventory.BInventoryButton;
import com.Ben12345rocks.AdvancedCore.Util.Item.ItemBuilder;
import com.Ben12345rocks.AdvancedCore.Util.Misc.ArrayUtils;
import com.Ben12345rocks.AdvancedCore.Util.Misc.MiscUtils;
import com.Ben12345rocks.AdvancedCore.Util.Misc.PlayerUtils;
import com.Ben12345rocks.AdvancedCore.Util.Misc.StringUtils;
import com.Ben12345rocks.AdvancedCore.Util.PluginMessage.PluginMessage;
import com.Ben12345rocks.AdvancedCore.Util.PluginMessage.PluginMessageHandler;
import com.Ben12345rocks.AylaChat.Main;
import com.Ben12345rocks.AylaChat.Commands.Executors.CommandAliasHandle;
import com.Ben12345rocks.AylaChat.Commands.Executors.CommandAliases;
import com.Ben12345rocks.AylaChat.Commands.GUI.EditingGUI;
import com.Ben12345rocks.AylaChat.Commands.TabComplete.AliasHandleTabCompleter;
import com.Ben12345rocks.AylaChat.Commands.TabComplete.AliasesTabCompleter;
import com.Ben12345rocks.AylaChat.Config.Config;
import com.Ben12345rocks.AylaChat.Objects.ChannelHandler;
import com.Ben12345rocks.AylaChat.Objects.MessageData;
import com.Ben12345rocks.AylaChat.Objects.User;
import com.Ben12345rocks.AylaChat.Objects.UserManager;
import com.google.common.collect.Iterables;

import net.md_5.bungee.api.chat.TextComponent;

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
				ArrayList<TextComponent> texts = new ArrayList<TextComponent>();
				HashMap<String, TextComponent> unsorted = new HashMap<String, TextComponent>();
				texts.add(StringUtils.getInstance().stringToComp(Config.getInstance().formatHelpTitle));

				boolean requirePerms = Config.getInstance().formatHelpRequirePermission;

				for (CommandHandler cmdHandle : plugin.commands) {
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

		plugin.commands
				.add(new CommandHandler(new String[] { "AdvancedCoreHelp" }, "AylaChat.Help", "View help information") {

					@Override
					public void execute(CommandSender sender, String[] args) {
						ArrayList<TextComponent> texts = new ArrayList<TextComponent>();
						HashMap<String, TextComponent> unsorted = new HashMap<String, TextComponent>();
						texts.add(StringUtils.getInstance().stringToComp(Config.getInstance().formatHelpTitle));

						boolean requirePerms = Config.getInstance().formatHelpRequirePermission;

						for (CommandHandler cmdHandle : plugin.commands) {
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
				sendMessage(sender, StringUtils.getInstance().replacePlaceHolder(Config.getInstance().formatChannelSet,
						"channel", ChannelHandler.getInstance().getChannel(args[1]).getChannelName()));
			}
		});

		plugin.commands.add(new CommandHandler(new String[] { "ClearChat" }, "AylaChat.ClearChat", "Clear Chat") {

			@Override
			public void execute(CommandSender sender, String[] args) {
				ChannelHandler.getInstance().clearChatAll();
				plugin.sendPluginMessage(PlayerUtils.getInstance().getRandomPlayer(), "ClearChat", "All");
				sendMessage(sender, "&cChat cleared");
			}
		});

		plugin.commands.add(new CommandHandler(new String[] { "ClearChat", "(Player)" }, "AylaChat.ClearChat.Player",
				"Clear Chat for a specific player") {

			@Override
			public void execute(CommandSender sender, String[] args) {
				Player p = Bukkit.getPlayer(args[1]);
				if (p != null) {
					ChannelHandler.getInstance().clearChat(p);
					sendMessage(sender, "&cChat cleared for " + p.getName());
				} else {
					if (Config.getInstance().useBungeeCoord) {
						plugin.sendPluginMessage(PlayerUtils.getInstance().getRandomPlayer(), "ClearChat", args[1]);
						sendMessage(sender, "&cChat cleared for " + args[1]);
						return;
					}
					sendMessage(sender, "&cPlayer " + args[1] + " not found/online");
				}
			}
		});

		plugin.commands.add(new CommandHandler(new String[] { "SocialSpy", "(Boolean)" }, "AylaChat.SocialSpy",
				"Set whether or not social spy is enabled", false) {

			@Override
			public void execute(CommandSender sender, String[] args) {
				User user = UserManager.getInstance().getAylaChatUser((Player) sender);
				user.setSocialSpyEnabled(Boolean.valueOf(args[1]));
				if (user.getSocialSpyEnabled()) {
					sendMessage(sender, "&cEnabled socialspy");
				} else {
					sendMessage(sender, "&cDisabled socialspy");
				}
			}
		});

		plugin.commands.add(new CommandHandler(new String[] { "SocialSpy" }, "AylaChat.SocialSpy",
				"Set whether or not social spy is enabled", false) {

			@Override
			public void execute(CommandSender sender, String[] args) {
				User user = UserManager.getInstance().getAylaChatUser((Player) sender);
				user.setSocialSpyEnabled(!user.getSocialSpyEnabled());
				if (user.getSocialSpyEnabled()) {
					sendMessage(sender, "&cEnabled socialspy");
				} else {
					sendMessage(sender, "&cDisabled socialspy");
				}
			}
		});

		plugin.commands.add(new CommandHandler(new String[] { "Mute", "(Player)" }, "AylaChat.Mute", "Mute player") {

			@Override
			public void execute(CommandSender sender, String[] args) {
				User user = UserManager.getInstance().getAylaChatUser(args[1]);
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

		plugin.commands.add(
				new CommandHandler(new String[] { "Msg", "(Player)", "(List)" }, "AylaChat.Msg", "Msg other players") {

					@Override
					public void execute(CommandSender sender, String[] args) {
						CommandLoader.this.sendMessage(sender, args[1], ArrayUtils.getInstance().makeString(2, args));
					}
				});

		plugin.commands
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

		plugin.commands.add(
				new CommandHandler(new String[] { "ChannelEdit" }, "AylaChat.ChannelEdit", "Edit channels", false) {

					@Override
					public void execute(CommandSender sender, String[] args) {
						EditingGUI.getInstance().openGUI((Player) sender);
					}
				});

		plugin.commands.add(new CommandHandler(new String[] { "button", "(Number)" }, "AylaChat.Button",
				"Command to access Json Button", false) {

			@Override
			public void execute(CommandSender sender, String[] args) {
				MessageData data = ChannelHandler.getInstance().getMessageHistory().get(Integer.parseInt(args[1]));
				HashMap<String, String> placeholders = new HashMap<String, String>();
				placeholders.put("Message", data.getMessage());
				placeholders.put("Channel", data.getChannel());
				placeholders.put("player", data.getPlayer());
				BInventory inv = new BInventory("Player: " + data.getPlayer() + " (" + args[1] + ")");
				for (final String key : Config.getInstance().JsonButtonGUI()) {
					inv.addButton(new BInventoryButton(new ItemBuilder(
							Config.getInstance().getData().getConfigurationSection("JsonButtonGUI." + key))
									.setPlaceholders(placeholders)) {

						@Override
						public void onClick(ClickEvent event) {
							Player player = Bukkit.getPlayer(data.getPlayer());
							if (player != null) {
								ArrayList<String> cmds = Config.getInstance().getJsonButtonGUIKeyCommands(key);
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
		 * plugin.commands.add(new CommandHandler(new String[] { "RemoveMessage",
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

		PluginMessage.getInstance().add(new PluginMessageHandler("GUICommand") {

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
							Config.getInstance().getJsonButtonGUIKeyCommands(key), placeholders);
				}
			}
		});

		PluginMessage.getInstance().add(new PluginMessageHandler("Msg") {

			@Override
			public void onRecieve(String subChannel, ArrayList<String> messageData) {
				String sender = messageData.get(0);
				String toSend = messageData.get(1);
				String msg = messageData.get(2);

				messageReceived(sender, toSend, msg);
			}
		});

		PluginMessage.getInstance().add(new PluginMessageHandler("Mute") {

			@Override
			public void onRecieve(String subChannel, ArrayList<String> messageData) {
				String player = messageData.get(0);
				String muted = messageData.get(1);

				User user = UserManager.getInstance().getAylaChatUser(player);
				if (Boolean.valueOf(muted)) {
					user.mute();
				} else {
					user.unMute();
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
		toSend = com.Ben12345rocks.AdvancedCore.UserManager.UserManager.getInstance().getProperName(toSend);
		sender = com.Ben12345rocks.AdvancedCore.UserManager.UserManager.getInstance().getProperName(sender);
		HashMap<String, String> placeholders = new HashMap<String, String>();
		placeholders.put("player", toSend);
		placeholders.put("fromsender", sender);
		placeholders.put("message", msg);
		String format = StringUtils.getInstance().replacePlaceHolder(Config.getInstance().formatMessageReceive,
				placeholders);

		Player p = Bukkit.getPlayer(toSend);
		if (p != null) {
			p.sendMessage(format);
			UserManager.getInstance().getAylaChatUser(p).setlastMessageSender(sender);
		}

		new RewardBuilder(Config.getInstance().getData(), Config.getInstance().formatMessageRewards).send(p);

		ChannelHandler.getInstance().socialSpyMessage(format);

	}

	public void sendMessage(CommandSender player, String toSend, String msg) {
		if (toSend.equals("")) {
			player.sendMessage(StringUtils.getInstance().colorize(Config.getInstance().formatMessageNoReply));
			return;
		}

		String sender = player.getName();
		if (!(player instanceof Player)) {
			sender = "CONSOLE";
		} else {
			UserManager.getInstance().getAylaChatUser(player.getName()).setlastMessageSender(toSend);
		}
		toSend = com.Ben12345rocks.AdvancedCore.UserManager.UserManager.getInstance().getProperName(toSend);
		HashMap<String, String> placeholders = new HashMap<String, String>();
		placeholders.put("player", sender);
		placeholders.put("toSend", toSend);
		placeholders.put("message", msg);
		String format = StringUtils.getInstance().replacePlaceHolder(Config.getInstance().formatMessageSend,
				placeholders);

		player.sendMessage(StringUtils.getInstance().colorize(format));

		if (Config.getInstance().useBungeeCoord) {
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
