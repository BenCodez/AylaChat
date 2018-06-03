package com.Ben12345rocks.AylaChat;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.Ben12345rocks.AdvancedCore.AdvancedCoreHook;
import com.Ben12345rocks.AdvancedCore.Objects.CommandHandler;
import com.Ben12345rocks.AdvancedCore.Objects.UUID;
import com.Ben12345rocks.AdvancedCore.Objects.User;
import com.Ben12345rocks.AdvancedCore.Objects.UserStartup;
import com.Ben12345rocks.AdvancedCore.Util.Metrics.BStatsMetrics;
import com.Ben12345rocks.AdvancedCore.Util.Misc.PluginUtils;
import com.Ben12345rocks.AdvancedCore.Util.PluginMessage.PluginMessage;
import com.Ben12345rocks.AdvancedCore.Util.Updater.Updater;
import com.Ben12345rocks.AylaChat.Commands.CommandLoader;
import com.Ben12345rocks.AylaChat.Commands.Executors.CommandAylaChat;
import com.Ben12345rocks.AylaChat.Commands.TabComplete.AylaChatTabCompleter;
import com.Ben12345rocks.AylaChat.Config.Config;
import com.Ben12345rocks.AylaChat.Listeners.PlayerChatListener;
import com.Ben12345rocks.AylaChat.Objects.ChannelHandler;
import com.Ben12345rocks.AylaChat.Objects.UserManager;

public class Main extends JavaPlugin {

	public static Main plugin;
	public ArrayList<CommandHandler> commands;
	private Updater updater;

	@Override
	public void onEnable() {
		plugin = this;

		Config.getInstance().setup();

		PluginUtils.getInstance().registerEvents(new PlayerChatListener(plugin), plugin);

		AdvancedCoreHook.getInstance().addUserStartup(new UserStartup() {

			@Override
			public void onStartUp(User user) {
				UserManager.getInstance().getAylaChatUser(new UUID(user.getUUID())).checkChannels();
			}

			@Override
			public void onStart() {
				plugin.debug("Checking channels and socialspy");
			}

			@Override
			public void onFinish() {
				plugin.debug("Finished checking channels and socialspy");
			}
		});

		AdvancedCoreHook.getInstance().setJenkinsSite("ben12345rocks.com");
		AdvancedCoreHook.getInstance().setConfigData(Config.getInstance().getData());

		AdvancedCoreHook.getInstance().loadHook(plugin);

		Config.getInstance().loadValues();

		if (Config.getInstance().useBungeeCoord) {
			AdvancedCoreHook.getInstance().registerBungeeChannels();
		}

		ChannelHandler.getInstance().load();

		CommandLoader.getInstance().load();

		PluginUtils.getInstance().registerCommands(plugin, "aylachat", new CommandAylaChat(plugin),
				new AylaChatTabCompleter());

		BStatsMetrics metrics = new BStatsMetrics(this);
		metrics.addCustomChart(new BStatsMetrics.SimplePie("channels") {

			@Override
			public String getValue() {
				return "" + Config.getInstance().getChannels().size();
			}
		});

		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {
				checkUpdate();
			}
		}, 10L);

		plugin.getLogger()
				.info("Enabled " + plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion());
	}

	public void checkUpdate() {
		plugin.updater = new Updater(plugin, 55101, false);
		final Updater.UpdateResult result = plugin.updater.getResult();
		switch (result) {
		case FAIL_SPIGOT: {
			plugin.getLogger().info("Failed to check for update for " + plugin.getName() + "!");
			break;
		}
		case NO_UPDATE: {
			plugin.getLogger().info(plugin.getName() + " is up to date! Version: " + plugin.updater.getVersion());
			break;
		}
		case UPDATE_AVAILABLE: {
			plugin.getLogger().info(plugin.getName() + " has an update available! Your Version: "
					+ plugin.getDescription().getVersion() + " New Version: " + plugin.updater.getVersion());
			break;
		}
		default: {
			break;
		}
		}
	}

	@Override
	public void onDisable() {
		plugin = null;
	}

	public void debug(String msg) {
		AdvancedCoreHook.getInstance().debug(msg);
	}

	public void reload() {
		Config.getInstance().reloadData();
		AdvancedCoreHook.getInstance().setConfigData(Config.getInstance().getData());

		ChannelHandler.getInstance().load();
		AdvancedCoreHook.getInstance().reload();
	}

	public void sendPluginMessage(Player player, String channel, String... args) {
		if (Config.getInstance().useBungeeCoord) {
			PluginMessage.getInstance().sendPluginMessage(player, channel, args);
		}
	}
}
