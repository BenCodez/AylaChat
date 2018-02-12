package com.Ben12345rocks.AylaChat;

import java.util.ArrayList;

import org.bukkit.plugin.java.JavaPlugin;

import com.Ben12345rocks.AdvancedCore.AdvancedCoreHook;
import com.Ben12345rocks.AdvancedCore.Objects.CommandHandler;
import com.Ben12345rocks.AdvancedCore.Util.Misc.PluginUtils;
import com.Ben12345rocks.AylaChat.Commands.CommandLoader;
import com.Ben12345rocks.AylaChat.Commands.Executors.CommandAylaChat;
import com.Ben12345rocks.AylaChat.Commands.TabComplete.AylaChatTabCompleter;
import com.Ben12345rocks.AylaChat.Config.Config;
import com.Ben12345rocks.AylaChat.Listeners.PlayerChatListener;
import com.Ben12345rocks.AylaChat.Objects.ChannelHandler;

public class Main extends JavaPlugin {

	public static Main plugin;
	public ArrayList<CommandHandler> commands;

	@Override
	public void onEnable() {
		plugin = this;

		Config.getInstance().setup();

		PluginUtils.getInstance().registerEvents(new PlayerChatListener(plugin), plugin);

		AdvancedCoreHook.getInstance().setConfigData(Config.getInstance().getData());

		AdvancedCoreHook.getInstance().loadHook(plugin);

		ChannelHandler.getInstance().load();

		CommandLoader.getInstance().load();

		PluginUtils.getInstance().registerCommands(plugin, "aylachat", new CommandAylaChat(plugin),
				new AylaChatTabCompleter());
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
		AdvancedCoreHook.getInstance().reload();
		ChannelHandler.getInstance().load();
	}
}
