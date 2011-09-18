package com.earth2me.essentials.yaml;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.snowleo.yaml.YamlClass;
import me.snowleo.yaml.YamlField;


@YamlClass
@Data
@EqualsAndHashCode(callSuper = false)
public class General extends BaseYaml<General>
{
	private boolean debug = false;
	private boolean signsDisabled = false;
	private int test = 1;
	private String test2 = "\tline1\nline2\nline3";
	@YamlField(comment = "Backup runs a command while saving is disabled")
	private Backup backup = new Backup();
	@YamlField(comment =
	{
		"Set the locale here, if you want to change the language of Essentials.",
		"If this is not set, Essentials will use the language of your computer.",
		"Available locales: da, de, en, fr, nl"
	})
	private String locale;
}
