package com.bencodez.aylachat;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.bencodez.advancedcore.AdvancedCorePlugin;
import com.bencodez.advancedcore.api.command.CommandHandler;
import com.bencodez.advancedcore.api.metrics.BStatsMetrics;
import com.bencodez.advancedcore.api.misc.PluginUtils;
import com.bencodez.advancedcore.api.updater.Updater;
import com.bencodez.advancedcore.api.user.AdvancedCoreUser;
import com.bencodez.advancedcore.api.user.UUID;
import com.bencodez.advancedcore.api.user.UserStartup;
import com.bencodez.aylachat.commands.CommandLoader;
import com.bencodez.aylachat.commands.executors.CommandAylaChat;
import com.bencodez.aylachat.commands.tabcomplete.AylaChatTabCompleter;
import com.bencodez.aylachat.config.Config;
import com.bencodez.aylachat.listeners.PlayerChatListener;
import com.bencodez.aylachat.listeners.PlayerJoinListener;
import com.bencodez.aylachat.objects.ChannelHandler;
import com.bencodez.aylachat.objects.UserManager;

import lombok.Getter;
import lombok.Setter;

public class AylaChatMain extends AdvancedCorePlugin {

	public static AylaChatMain plugin;
	
	@Getter
	@Setter
	private ArrayList<CommandHandler> commands;
	
	private Updater updater;
	
	@Getter
	private Config configFile;

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
	public void onUnLoad() {
		plugin = null;
	}

	@Override
	public void onPostLoad() {
		if (configFile.useBungeeCoord) {
			registerBungeeChannels("aylachat:aylachat");
		}

		PluginUtils.getInstance().registerEvents(new PlayerChatListener(plugin), plugin);
		PluginUtils.getInstance().registerEvents(new PlayerJoinListener(), plugin);

		CommandLoader.getInstance().load();

		PluginUtils.getInstance().registerCommands(plugin, "aylachat", new CommandAylaChat(plugin),
				new AylaChatTabCompleter());

		BStatsMetrics metrics = new BStatsMetrics(this, 2380);
		metrics.addCustomChart(new BStatsMetrics.SimplePie("channels", new Callable<String>() {

			@Override
			public String call() throws Exception {
				return "" + configFile.getChannels().size();
			}
		}));

		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {
				checkUpdate();
			}
		}, 10L);

		plugin.getLogger()
				.info("Enabled " + plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion());
	}

	@Override
	public void onPreLoad() {
		plugin = this;

		configFile = new Config(this);

		ChannelHandler.getInstance().load();

		addUserStartup(new UserStartup() {

			@Override
			public void onFinish() {
				plugin.debug("Finished checking channels and socialspy");
			}

			@Override
			public void onStart() {
				plugin.debug("Checking channels and socialspy");
			}

			@Override
			public void onStartUp(AdvancedCoreUser user) {
				UserManager.getInstance().getAylaChatUser(new UUID(user.getUUID())).checkChannels();
			}
		});

		setJenkinsSite("bencodez.com");
		setConfigData(configFile.getData());
	}

	@Override
	public void reload() {
		configFile.reloadData();
		setConfigData(configFile.getData());

		ChannelHandler.getInstance().load();
		reloadAdvancedCore(true);
	}

	public void sendPluginMessage(Player player, String channel, String... args) {
		if (configFile.useBungeeCoord) {
			plugin.getPluginMessaging().sendPluginMessage(player, channel, args);
		}
	}
}
