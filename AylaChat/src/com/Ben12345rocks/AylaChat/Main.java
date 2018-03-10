package com.Ben12345rocks.AylaChat;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
import com.Ben12345rocks.AylaChat.Objects.ChannelHandler;
import com.Ben12345rocks.AylaChat.Objects.PluginMessageHandler;
import com.Ben12345rocks.AylaChat.Objects.UserManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class Main extends JavaPlugin implements PluginMessageListener {

	public static Main plugin;
	public ArrayList<CommandHandler> commands;
	public ArrayList<PluginMessageHandler> pluginMessages;

	@Override
	public void onEnable() {
		plugin = this;
		pluginMessages = new ArrayList<PluginMessageHandler>();

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

		Config.getInstance().loadValues();

		ChannelHandler.getInstance().load();

		CommandLoader.getInstance().load();

		PluginUtils.getInstance().registerCommands(plugin, "aylachat", new CommandAylaChat(plugin),
				new AylaChatTabCompleter());

		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "AylaChat");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "AylaChat", this);

		plugin.getLogger()
				.info("Enabled " + plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion());
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		// plugin.getLogger().info("Got plugin message " + channel + " : " + message);
		if (!channel.equals("AylaChat")) {
			return;
		}
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		ArrayList<String> list = new ArrayList<String>();
		String subChannel = in.readUTF();
		int size = in.readInt();
		for (int i = 0; i < size; i++) {
			try {
				String str = in.readUTF();
				if (str != null) {
					list.add(str);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (PluginMessageHandler handle : pluginMessages) {
			if (handle.getSubChannel().equalsIgnoreCase(subChannel)) {
				handle.onRecieve(subChannel, list);
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

	public void sendPluginMessage(Player p, String channel, String... messageData) {
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOutStream);
		try {
			out.writeUTF(channel);
			out.writeInt(messageData.length);
			for (String message : messageData) {
				out.writeUTF(message);
			}
			// plugin.debug("Sending plugin message: " +
			// ArrayUtils.getInstance().makeStringList(ArrayUtils.getInstance().messageData));
			p.sendPluginMessage(plugin, "AylaChat", byteOutStream.toByteArray());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
