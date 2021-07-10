package com.bencodez.aylachat.objects;

import java.util.ArrayList;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.bencodez.advancedcore.api.misc.PlayerUtils;
import com.bencodez.advancedcore.api.user.UUID;

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
		return com.bencodez.advancedcore.api.user.UserManager.getInstance().getAllUUIDs();
	}

	public AylaChatUser getAylaChatUser(java.util.UUID uuid) {
		return getAylaChatUser(new UUID(uuid.toString()));

	}

	public AylaChatUser getAylaChatUser(OfflinePlayer player) {
		return getAylaChatUser(player.getName());
	}

	public AylaChatUser getAylaChatUser(Player player) {
		return getAylaChatUser(player.getName());
	}

	public AylaChatUser getAylaChatUser(String playerName) {
		return getAylaChatUser(new UUID(PlayerUtils.getInstance().getUUID(playerName)));
	}

	@SuppressWarnings("deprecation")
	public AylaChatUser getAylaChatUser(UUID uuid) {
		return new AylaChatUser(uuid);
	}
}
