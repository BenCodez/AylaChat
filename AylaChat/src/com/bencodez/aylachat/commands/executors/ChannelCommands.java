package com.bencodez.aylachat.commands.executors;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import com.bencodez.advancedcore.api.misc.ArrayUtils;
import com.bencodez.aylachat.AylaChatMain;
import com.bencodez.aylachat.objects.Channel;
import com.bencodez.aylachat.objects.ChannelHandler;
import com.bencodez.aylachat.objects.AylaChatUser;
import com.bencodez.aylachat.objects.UserManager;

public class ChannelCommands extends BukkitCommand {
	private Channel ch;

	public ChannelCommands(String name, Channel ch) {
		super(name);
		description = "ValueRequestInput";
		setAliases(new ArrayList<String>());
		this.ch = ch;
	}

	@Override
	public boolean execute(final CommandSender sender, String alias, final String[] args) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;

			Bukkit.getScheduler().runTaskAsynchronously(AylaChatMain.plugin, new Runnable() {

				@Override
				public void run() {
					AylaChatUser user = UserManager.getInstance().getAylaChatUser(player);
					if (args.length == 0) {
						user.setCurrentChannel(ch.getChannelName());
						sender.sendMessage("Channel set");
					} else {
						String msg = ArrayUtils.getInstance().makeString(1, args);

						ChannelHandler.getInstance().onChat(player, ch.getChannelName(), msg);
					}

				}
			});

			return true;
		}
		sender.sendMessage("Must be a player to use this");

		return true;
	}
}