package com.earth2me.essentials.update;

import java.net.MalformedURLException;
import java.net.URL;
import org.bukkit.util.config.Configuration;


public class ModuleInfo
{
	private final transient URL url;
	private final transient String version;
	private final transient String hash;
	ModuleInfo(final Configuration updateConfig, final String path) throws MalformedURLException
	{
		url = new URL(updateConfig.getString(path+".url", null));
		version = updateConfig.getString(path+".version", null);
		hash = updateConfig.getString(path+".hash", null);
	}

	public URL getUrl()
	{
		return url;
	}

	public String getVersion()
	{
		return version;
	}

	public String getHash()
	{
		return hash;
	}
	
}
