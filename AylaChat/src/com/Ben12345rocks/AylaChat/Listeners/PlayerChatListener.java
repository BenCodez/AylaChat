package com.Ben12345rocks.AylaChat.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.Ben12345rocks.AylaChat.Main;
import com.Ben12345rocks.AylaChat.Objects.ChannelHandler;
import com.Ben12345rocks.AylaChat.Objects.User;
import com.Ben12345rocks.AylaChat.Objects.UserManager;

public class PlayerChatListener implements Listener {

	/** The plugin. */
	@SuppressWarnings("unused")
	private static Main plugin;

	/**
	 * Instantiates a new player join event.
	 *
	 * @param plugin
	 *            the plugin
	 */
	public PlayerChatListener(Main plugin) {
		PlayerChatListener.plugin = plugin;
	}

	/**
	 * On player login.
	 *
	 * @param event
	 *            the event
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);
		User user = UserManager.getInstance().getAylaChatUser(event.getPlayer());
		ChannelHandler.getInstance().onChat(event.getPlayer(), user.getCurrentChannel(), event.getMessage());
	}

}
