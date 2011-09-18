package com.earth2me.essentials.update;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsUpdate extends JavaPlugin
{
	private final static Logger LOGGER = Logger.getLogger("Minecraft");
	private transient Player currentPlayer;
	private transient EssentialsHelp essentialsHelp;
	private transient UpdateFile updateFile;
	private transient Version currentVersion;
	private transient CheckResult result = CheckResult.UNKNOWN;
	private transient Version newVersion = null;
	private transient int bukkitResult = 0;
	private final static int CHECK_INTERVAL = 20 * 60 * 60 * 6;

	public EssentialsUpdate()
	{
	}

	@Override
	public void onEnable()
	{
		essentialsHelp = new EssentialsHelp(getServer());
		updateFile = new UpdateFile(this);
		checkForEssentials();
		final PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Type.PLAYER_JOIN, new PlayerListener(), Priority.Low, this);
		pm.registerEvent(Type.PLAYER_CHAT, new PlayerListener(), Priority.Low, this);
		LOGGER.info("Essentials Update loaded.");
		final boolean installed = getConfiguration().getBoolean("installed", false);
		if (installed)
		{

			checkForUpdates();
			Version myVersion = new Version(getDescription().getVersion());
			if (result == CheckResult.NEW_ESS && myVersion.equals(newVersion)) {
				updateEssentials();
			}
			scheduleUpdateTask();
		}
		else
		{
			LOGGER.info("Join the game and follow the instructions.");
		}
	}

	private void scheduleUpdateTask()
	{
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				updateFile = new UpdateFile(EssentialsUpdate.this);
				checkForUpdates();
			}
		}, CHECK_INTERVAL, CHECK_INTERVAL);
	}

	@Override
	public void onDisable()
	{
		essentialsHelp.onDisable();
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
	{
		if (command.getName().equalsIgnoreCase("essentialsupdate"))
		{
			if (sender instanceof Player && sender.hasPermission("essentials.install"))
			{
				if (currentPlayer == null)
				{
					currentPlayer = (Player)sender;
					if (!this.getConfiguration().getBoolean("installed", false))
					{
						sender.sendMessage("Thank you for choosing Essentials.");
						sender.sendMessage("The following installation wizard will guide you through the installation of Essentials.");
						sender.sendMessage("Your answers will be saved for a later update.");
						sender.sendMessage("Please answer the messages with yes or no, if not otherwise stated.");
						sender.sendMessage("Write \"bye\" if you want to exit the wizard at anytime.");

					}
					else
					{
						// Essentials installed, check for updates
					}
				}
				if (!currentPlayer.equals(sender))
				{
					sender.sendMessage("The player " + currentPlayer.getDisplayName() + " is already using the wizard.");
				}
			}
			else
			{
				sender.sendMessage("Please run the command as op from in game.");
			}
		}
		if (command.getName().equalsIgnoreCase("essentialshelp"))
		{
			essentialsHelp.onCommand(sender);
		}
		return true;
	}

	private void reactOnMessage(final String message)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	private void updateEssentials()
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}


	public enum CheckResult
	{
		NEW_ESS, NEW_ESS_BUKKIT, NEW_BUKKIT, OK, UNKNOWN
	}

	private void checkForUpdates()
	{
		if (currentVersion == null) {
			return;
		}
		final Map<Version, VersionInfo> versions = updateFile.getVersions();
		Set<Entry<Version, VersionInfo>> versionSet = versions.entrySet();
		final int bukkitVersion = getBukkitVersion();
		Version highest = null;
		Version higher = null;
		Version found = null;
		Version lower = null;
		int bukkitHigher = 0;
		int bukkitLower = 0;
		for (Entry<Version, VersionInfo> entry : versionSet)
		{
			if (highest != null)
			{
				highest = entry.getKey();
			}
			final int minBukkit = entry.getValue().getMinBukkit();
			final int maxBukkit = entry.getValue().getMaxBukkit();
			if (minBukkit == 0 || maxBukkit == 0)
			{
				continue;
			}
			if (bukkitVersion <= maxBukkit)
			{
				if (bukkitVersion < minBukkit)
				{
					higher = entry.getKey();
					bukkitHigher = minBukkit;
				}
				else
				{
					found = entry.getKey();
					break;
				}
			}
			else
			{
				lower = entry.getKey();
				bukkitLower = minBukkit;
				break;
			}
		}
		if (found != null)
		{
			if (found.compareTo(currentVersion) > 0)
			{
				result = CheckResult.NEW_ESS;
				newVersion = found;
			}
			else
			{
				result = CheckResult.OK;
			}
		}
		else if (higher != null)
		{
			if (higher.compareTo(currentVersion) > 0)
			{
				newVersion = highest;
				result = CheckResult.NEW_ESS_BUKKIT;
				bukkitResult = bukkitHigher;
			}
			else if (higher.compareTo(currentVersion) < 0)
			{
				result = CheckResult.UNKNOWN;
			}
			else
			{
				result = CheckResult.NEW_BUKKIT;
				bukkitResult = bukkitHigher;
			}
		}
		else if (lower != null)
		{
			if (lower.compareTo(currentVersion) > 0)
			{
				result = CheckResult.NEW_ESS_BUKKIT;
				newVersion = lower;
				bukkitResult = bukkitLower;
			}
			else if (lower.compareTo(currentVersion) < 0)
			{
				result = CheckResult.UNKNOWN;
			}
			else
			{
				result = CheckResult.NEW_BUKKIT;
				bukkitResult = bukkitLower;
			}
		}
		
	}

	public int getBukkitVersion()
	{
		final Matcher versionMatch = Pattern.compile("git-Bukkit-([0-9]+).([0-9]+).([0-9]+)-[0-9]+-[0-9a-z]+-b([0-9]+)jnks.*").matcher(getServer().getVersion());
		if (versionMatch.matches())
		{
			return Integer.parseInt(versionMatch.group(4));
		}
		throw new NumberFormatException("Bukkit Version changed!");
	}

	private void checkForEssentials()
	{
		PluginManager pm = getServer().getPluginManager();
		Plugin essentials = pm.getPlugin("Essentials");
		if (essentials == null)
		{
			if (new File(getDataFolder().getParentFile(), "Essentials.jar").exists())
			{
				//TODO: Broken Essentials
			}
		}
		else
		{
			currentVersion = new Version(essentials.getDescription().getVersion());
		}
	}


	class PlayerListener extends org.bukkit.event.player.PlayerListener
	{
		@Override
		public void onPlayerChat(final PlayerChatEvent event)
		{
			if (event.getPlayer() == EssentialsUpdate.this.currentPlayer)
			{
				EssentialsUpdate.this.reactOnMessage(event.getMessage());
				event.setCancelled(true);
				return;
			}

			essentialsHelp.handleChat(event);
		}

		@Override
		public void onPlayerJoin(final PlayerJoinEvent event)
		{
			final Player player = event.getPlayer();
			if (player.isOp() && !EssentialsUpdate.this.getConfiguration().getBoolean("installed", false))
			{
				player.sendMessage("Hello " + player.getDisplayName());
				player.sendMessage("Please type /essentialsupdate to start the installation of Essentials.");
			}
			if ((player.hasPermission("essentials.update") || player.isOp()) 
				&& (result != CheckResult.OK || result != CheckResult.UNKNOWN))
			{
				if (result == CheckResult.NEW_ESS) {
					player.sendMessage("The new version "+newVersion.toString() +" for Essentials is available. Please type /essentialsupdate to update.");
				}
				if (result == CheckResult.NEW_BUKKIT) {
					player.sendMessage("Your bukkit version is not the recommended build for Essentials, please update to version "+bukkitResult+".");
				}
				if (result == CheckResult.NEW_ESS_BUKKIT) {
					player.sendMessage("There is a new version "+newVersion.toString() +" of Essentials for Bukkit "+bukkitResult);
				}
			}
		}
	}
}
