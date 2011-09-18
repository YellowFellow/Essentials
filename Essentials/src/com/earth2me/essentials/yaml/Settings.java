package com.earth2me.essentials.yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.snowleo.yaml.YamlClass;
import me.snowleo.yaml.YamlField;


@YamlClass
@Data
@EqualsAndHashCode(callSuper = false)
public class Settings extends BaseYaml<Settings>
{
	public Settings()
	{
		super();
		locations.put("Test",new Location());
		m_o_t_d.add("Welcome to the server!");
		m_o_t_d.add("Have a nice day!\nwoooooo");
	}
	private boolean test;
	private Boolean test2;
	@YamlField(comment =
	{
		"Hello!",
		"World"
	})
	private String yay = "null";
	private String lol = "lol: 1";
	private General general = new General();
	private Map<String,Location> locations = new HashMap<String,Location>();
	private List<String> m_o_t_d = new ArrayList<String>(); 
}
