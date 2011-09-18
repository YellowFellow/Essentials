package com.earth2me.essentials.yaml;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.snowleo.yaml.YamlClass;
import org.bukkit.Server;

@YamlClass
@Data
@EqualsAndHashCode(callSuper = false)
public class Location extends BaseYaml<Location>
{
	private String worldName = "Test";
	private double x;
	private double y;
	private double z;
	private Float yaw;
	private Float pitch;
	
	public org.bukkit.Location getBukkit(Server server) {
		if (yaw == null || pitch == null) {
			return new org.bukkit.Location(server.getWorld(worldName), x, y, z);
		}
		return new org.bukkit.Location(server.getWorld(worldName), x, y, z, yaw, pitch);
	}
}
