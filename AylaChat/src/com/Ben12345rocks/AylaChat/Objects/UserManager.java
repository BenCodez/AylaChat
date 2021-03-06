package com.Ben12345rocks.AylaChat.Objects;

import java.util.ArrayList;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.Ben12345rocks.AdvancedCore.UserManager.UUID;
import com.Ben12345rocks.AdvancedCore.Util.Misc.PlayerUtils;

public class UserManager {
	/** The instance. */
	static UserManager instance = new UserManager();

	/**
	 * Gets the single instance of UserManager.
	 *
	 * @return single instance of UserManager
	 */
	public static UserManager getInstance() {
		return instance;
	}

	public UserManager() {
		super();
	}

	public ArrayList<String> getAllUUIDs() {
		return com.Ben12345rocks.AdvancedCore.UserManager.UserManager.getInstance().getAllUUIDs();
	}

	public User getAylaChatUser(java.util.UUID uuid) {
		return getAylaChatUser(new UUID(uuid.toString()));

	}

	public User getAylaChatUser(OfflinePlayer player) {
		return getAylaChatUser(player.getName());
	}

	public User getAylaChatUser(Player player) {
		return getAylaChatUser(player.getName());
	}

	public User getAylaChatUser(String playerName) {
		return getAylaChatUser(new UUID(PlayerUtils.getInstance().getUUID(playerName)));
	}

	@SuppressWarnings("deprecation")
	public User getAylaChatUser(UUID uuid) {
		return new User(uuid);
	}
}
