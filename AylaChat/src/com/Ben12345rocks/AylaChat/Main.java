package com.Ben12345rocks.AylaChat;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.Ben12345rocks.AdvancedCore.AdvancedCoreHook;
import com.Ben12345rocks.AdvancedCore.Objects.CommandHandler;
import com.Ben12345rocks.AdvancedCore.Objects.UUID;
import com.Ben12345rocks.AdvancedCore.Objects.User;
import com.Ben12345rocks.AdvancedCore.Objects.UserStartup;
import com.Ben12345rocks.AdvancedCore.Util.Misc.PluginUtils;
import com.Ben12345rocks.AylaChat.Commands.CommandLoader;
import com.Ben12345rocks.AylaChat.Commands.Executors.CommandAylaChat;
import com.Ben12345rocks.AylaChat.Commands.TabComplete.AylaChatTabCompleter;
import com.Ben12345rocks.AylaChat.Config.Config;
import com.Ben12345rocks.AylaChat.Listeners.PlayerChatListener;
import com.Ben12345rocks.AylaChat.Objects.Channel;
import com.Ben12345rocks.AylaChat.Objects.ChannelHandler;
import com.Ben12345rocks.AylaChat.Objects.UserManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class Main extends JavaPlugin implements PluginMessageListener {

	public static Main plugin;
	public ArrayList<CommandHandler> commands;

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

		AdvancedCoreHook.getInstance().setConfigData(Config.getInstance().getData());

		AdvancedCoreHook.getInstance().loadHook(plugin);

		ChannelHandler.getInstance().load();

		CommandLoader.getInstance().load();

		PluginUtils.getInstance().registerCommands(plugin, "aylachat", new CommandAylaChat(plugin),
				new AylaChatTabCompleter());

		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "AylaChat");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "AylaChat", this);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		//plugin.getLogger().info("Got plugin message " + channel + " : " + message);
		if (!channel.equals("AylaChat")) {
			return;
		}
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		if (subchannel.equals("Chat")) {
			String chatchannel = in.readUTF();
			String msg = in.readUTF();

			Channel ch = ChannelHandler.getInstance().getChannel(chatchannel);
			if (ch.isBungeecoord()) {
				ChannelHandler.getInstance().forceChat(null, ch, msg);
			} else {
				plugin.debug(ch.getChannelName() + " isn't bungeecoord, error?");
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
		AdvancedCoreHook.getInstance().reload();
		ChannelHandler.getInstance().load();
	}
}
