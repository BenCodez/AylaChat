package com.Ben12345rocks.AylaChat.Commands.GUI;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.Ben12345rocks.AdvancedCore.Util.Inventory.BInventory;
import com.Ben12345rocks.AdvancedCore.Util.Inventory.BInventory.ClickEvent;
import com.Ben12345rocks.AdvancedCore.Util.Inventory.BInventoryButton;
import com.Ben12345rocks.AdvancedCore.Util.Item.ItemBuilder;
import com.Ben12345rocks.AdvancedCore.Util.Misc.ArrayUtils;
import com.Ben12345rocks.AdvancedCore.Util.Misc.StringUtils;
import com.Ben12345rocks.AdvancedCore.Util.ValueRequest.InputMethod;
import com.Ben12345rocks.AdvancedCore.Util.ValueRequest.ValueRequestBuilder;
import com.Ben12345rocks.AdvancedCore.Util.ValueRequest.Listeners.BooleanListener;
import com.Ben12345rocks.AdvancedCore.Util.ValueRequest.Listeners.NumberListener;
import com.Ben12345rocks.AdvancedCore.Util.ValueRequest.Listeners.StringListener;
import com.Ben12345rocks.AylaChat.Main;
import com.Ben12345rocks.AylaChat.Objects.Channel;
import com.Ben12345rocks.AylaChat.Objects.ChannelHandler;

public class EditingGUI {
	static EditingGUI instance = new EditingGUI();

	/**
	 * Gets the single instance of Commands.
	 *
	 * @return single instance of Commands
	 */
	public static EditingGUI getInstance() {
		return instance;
	}

	/** The plugin. */
	Main plugin = Main.plugin;

	/**
	 * Instantiates a new commands.
	 */
	private EditingGUI() {
	}

	public void openChannelGUI(Player player, final Channel channel) {
		BInventory inv = new BInventory("Channel: " + channel.getChannelName());

		LinkedHashMap<String, ArrayList<String>> stringOptions = new LinkedHashMap<String, ArrayList<String>>();

		stringOptions.put("Format", new ArrayList<String>());

		stringOptions.put("Permission", new ArrayList<String>());

		for (final Entry<String, ArrayList<String>> entry : stringOptions.entrySet()) {
			inv.addButton(new BInventoryButton(new ItemBuilder(Material.PAPER)
					.setName("&c" + entry.getKey() + " = " + channel.getData().getString(entry.getKey()))) {

				@Override
				public void onClick(ClickEvent event) {
					new ValueRequestBuilder(new StringListener() {

						@Override
						public void onInput(Player player, String value) {
							if (value.equals("null")) {
								value = "";
							}
							channel.setValue(entry.getKey(), value);
							player.sendMessage(
									StringUtils.getInstance().colorize("&cSetting " + entry.getKey() + " to " + value));
							plugin.reload();
						}
					}, ArrayUtils.getInstance().convert(entry.getValue())).allowCustomOption(true)
							.currentValue(channel.getData().getString(entry.getKey(), ""))
							.usingMethod(InputMethod.INVENTORY).request(event.getPlayer());

				}
			});
		}

		ArrayList<String> booleanOptions = new ArrayList<String>();
		booleanOptions.add("Bungeecoord");
		booleanOptions.add("AutoJoin");
		booleanOptions.add("LoadMainChannelCommand");
		booleanOptions.add("LoadAliasChannelCommands");

		for (final String key : booleanOptions) {
			Material material = Material.REDSTONE_BLOCK;
			if (channel.getData().getBoolean(key)) {
				material = Material.EMERALD_BLOCK;
			}
			inv.addButton(new BInventoryButton(
					new ItemBuilder(material).setName("&c" + key + " = " + channel.getData().getBoolean(key))) {

				@Override
				public void onClick(ClickEvent event) {
					new ValueRequestBuilder(new BooleanListener() {

						@Override
						public void onInput(Player player, boolean value) {
							channel.setValue(key, value);
							player.sendMessage(StringUtils.getInstance().colorize("&cSetting " + key + " to " + value));
							plugin.reload();
						}
					}).currentValue("" + channel.getData().getBoolean(key)).request(event.getPlayer());
				}
			});
		}

		ArrayList<String> intOptions = new ArrayList<String>();
		intOptions.add("Distance");

		for (final String key : intOptions) {
			inv.addButton(new BInventoryButton(
					new ItemBuilder(Material.STONE).setName("&c" + key + " = " + channel.getData().getInt(key))) {

				@Override
				public void onClick(ClickEvent event) {
					new ValueRequestBuilder(new NumberListener() {

						@Override
						public void onInput(Player player, Number value) {
							channel.setValue(key, value.intValue());
							player.sendMessage(
									StringUtils.getInstance().colorize("&cSetting " + key + " to " + value.intValue()));
							plugin.reload();
						}
					}, new Number[] { -1, 0, 100, 250, 500, 1000 }).allowCustomOption(true)
							.currentValue("" + channel.getData().getInt(key)).request(event.getPlayer());
				}
			});
		}

		inv.addButton(new BInventoryButton(
				new ItemBuilder(Material.STONE).setName("&aAdd alias").setLore(channel.getAliases())) {

			@Override
			public void onClick(ClickEvent event) {
				new ValueRequestBuilder(new StringListener() {

					@Override
					public void onInput(Player player, String value) {
						ArrayList<String> list = channel.getAliases();
						list.add(value);
						channel.setValue("Aliases", list);
					}
				}, new String[] {}).allowCustomOption(true).request(event.getPlayer());
			}
		});

		inv.addButton(new BInventoryButton(
				new ItemBuilder(Material.STONE).setName("&cRemove alias").setLore(channel.getAliases())) {

			@Override
			public void onClick(ClickEvent event) {
				new ValueRequestBuilder(new StringListener() {

					@Override
					public void onInput(Player player, String value) {
						ArrayList<String> list = channel.getAliases();
						list.remove(value);
						channel.setValue("Aliases", list);
					}
				}, ArrayUtils.getInstance().convert(channel.getAliases())).allowCustomOption(true)
						.request(event.getPlayer());
			}
		});

		inv.openInventory(player);

	}

	public void openGUI(Player player) {
		BInventory inv = new BInventory("Channels");

		for (Channel ch : ChannelHandler.getInstance().getChannels()) {
			BInventoryButton b = new BInventoryButton(new ItemBuilder(Material.STONE).setName(ch.getChannelName())) {

				@Override
				public void onClick(ClickEvent event) {
					openChannelGUI(event.getPlayer(), (Channel) event.getButton().getData().get("Handle"));
				}
			};
			b.addData("Channel", ch);
			inv.addButton(b);
		}

		inv.addButton(new BInventoryButton(new ItemBuilder(Material.PAPER).setName("&cCreate")
				.addLoreLine("&cCreate channel, set name").addLoreLine("&cEdit other values afterwords")) {

			@Override
			public void onClick(ClickEvent event) {
				new ValueRequestBuilder(new StringListener() {

					@Override
					public void onInput(Player player, String value) {
						ChannelHandler.getInstance().create(value);
						openGUI(player);
					}
				}, new String[] {}).usingMethod(InputMethod.CHAT).allowCustomOption(true).currentValue("")
						.request(event.getPlayer());
			}

		});

		inv.openInventory(player);
	}
}