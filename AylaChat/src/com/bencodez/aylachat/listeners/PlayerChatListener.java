package com.bencodez.aylachat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.bencodez.aylachat.AylaChatMain;
import com.bencodez.aylachat.objects.ChannelHandler;
import com.bencodez.aylachat.objects.AylaChatUser;
import com.bencodez.aylachat.objects.UserManager;

public class PlayerChatListener implements Listener {

	/** The plugin. */
	@SuppressWarnings("unused")
	private static AylaChatMain plugin;

	/**
	 * Instantiates a new player join event.
	 *
	 * @param plugin
	 *            the plugin
	 */
	public PlayerChatListener(AylaChatMain plugin) {
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
		AylaChatUser user = UserManager.getInstance().getAylaChatUser(event.getPlayer());
		ChannelHandler.getInstance().onChat(event.getPlayer(), user.getCurrentChannel(), event.getMessage());
	}

}
