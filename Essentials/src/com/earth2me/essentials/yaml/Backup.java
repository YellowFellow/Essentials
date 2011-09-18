package com.earth2me.essentials.yaml;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.snowleo.yaml.YamlClass;
import me.snowleo.yaml.YamlField;

@YamlClass
@Data
@EqualsAndHashCode(callSuper = false)
public class Backup extends BaseYaml<Backup>
{
	@YamlField(comment="Interval in minutes")
	private long interval = 60;
	@YamlField(comment="Add a command that backups your data, e.g. 'rdiff-backup World1 backups/World1'")
	private String command;
}
