package com.Ben12345rocks.AylaChat.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.Ben12345rocks.AylaChat.Main;
import com.Ben12345rocks.AylaChat.Objects.User;
import com.Ben12345rocks.AylaChat.Objects.UserManager;

public class PlayerJoinListener implements Listener {
	private Main plugin = Main.plugin;

	public PlayerJoinListener() {
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {
				if (p != null) {
					User user = UserManager.getInstance().getAylaChatUser(p);
					user.checkChannels();
				}
			}
		}, 20);
	}

}
