package com.earth2me.essentials.update;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.util.config.Configuration;
import java.util.Collections;
import java.util.List;


public class VersionInfo
{
	private final transient List<String> changelog;
	private final transient int minBukkit;
	private final transient int maxBukkit;
	private final transient List<ModuleInfo> modules;

	public VersionInfo(final Configuration updateConfig, final String path)
	{
		changelog = updateConfig.getStringList(path + ".changelog", Collections.<String>emptyList());
		minBukkit = updateConfig.getInt(path + ".min-bukkit", 0);
		maxBukkit = updateConfig.getInt(path + ".max-bukkit", 0);
		modules = new ArrayList<ModuleInfo>();
		final String modulesPath = path + ".modules";
		for (String module : updateConfig.getKeys())
		{
			try
			{
				modules.add(new ModuleInfo(updateConfig, modulesPath + module));
			}
			catch (MalformedURLException ex)
			{
				Logger.getLogger("Minecraft").log(Level.WARNING, "Invalid url in update file", ex);
			}
		}
	}

	public List<String> getChangelog()
	{
		return Collections.unmodifiableList(changelog);
	}

	public int getMinBukkit()
	{
		return minBukkit;
	}

	public int getMaxBukkit()
	{
		return maxBukkit;
	}

	public List<ModuleInfo> getModules()
	{
		return Collections.unmodifiableList(modules);
	}
}
