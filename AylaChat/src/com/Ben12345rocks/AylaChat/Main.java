package com.Ben12345rocks.AylaChat;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.Ben12345rocks.AdvancedCore.AdvancedCorePlugin;
import com.Ben12345rocks.AdvancedCore.CommandAPI.CommandHandler;
import com.Ben12345rocks.AdvancedCore.UserManager.UUID;
import com.Ben12345rocks.AdvancedCore.UserManager.User;
import com.Ben12345rocks.AdvancedCore.UserManager.UserStartup;
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

import lombok.Getter;
import lombok.Setter;

public class Main extends AdvancedCorePlugin {

	public static Main plugin;
	@Getter
	@Setter
	private ArrayList<CommandHandler> commands;
	private Updater updater;

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
		if (Config.getInstance().useBungeeCoord) {
			registerBungeeChannels();
		}
		
		PluginUtils.getInstance().registerEvents(new PlayerChatListener(plugin), plugin);

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

	@Override
	public void onPreLoad() {
		plugin = this;

		Config.getInstance().setup();
		Config.getInstance().loadValues();

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
			public void onStartUp(User user) {
				UserManager.getInstance().getAylaChatUser(new UUID(user.getUUID())).checkChannels();
			}
		});

		setJenkinsSite("ben12345rocks.com");
		setConfigData(Config.getInstance().getData());
	}

	public void reload() {
		Config.getInstance().reloadData();
		setConfigData(Config.getInstance().getData());

		ChannelHandler.getInstance().load();
		super.reload();
	}

	public void sendPluginMessage(Player player, String channel, String... args) {
		if (Config.getInstance().useBungeeCoord) {
			PluginMessage.getInstance().sendPluginMessage(player, channel, args);
		}
	}
}
