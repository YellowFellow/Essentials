package com.earth2me.essentials.yaml;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;


public abstract class YamlFactory<T>
{
	private final transient Yaml yaml;

	public YamlFactory()
	{
		final DumperOptions ops = new DumperOptions();
		yaml = new Yaml(ops);
	}

	public static <T> YamlFactory<T> get(final BaseYaml<? extends T> clazz)
	{
		try
		{

			final String newClassname = clazz.getClass().getName() + "Yaml";
			final ClassLoader classLoader = YamlFactory.class.getClassLoader();
			final Class<YamlFactory<T>> newClass = (Class<YamlFactory<T>>)classLoader.loadClass(newClassname);
			return newClass.newInstance();
		}
		catch (Exception ex)
		{
			return null;
		}
	}

	public abstract T read();

	public void write(final T data, final PrintWriter writer)
	{
		write(data, writer, 0);
	}

	public abstract void write(final T data, final PrintWriter writer, final int depth);

	protected void writeData(final String name, final Object data, final PrintWriter writer, final int depth, final boolean comment)
	{
		if (data == null && !comment)
		{
			return;
		}
		writeIndention(writer, depth);
		if (data == null && comment)
		{
			writer.print('#');
		}
		writer.print(name);
		writer.print(": ");
		if (data == null && comment)
		{
			writer.println();
			return;
		}
		if (data instanceof BaseYaml)
		{
			writer.println();
			final BaseYaml yamlData = (BaseYaml)data;
			yamlData.write(writer, depth + 1);
		}
		else if (data instanceof Map)
		{
			writer.println();
			for (Entry<String, Object> entry : ((Map<String, Object>)data).entrySet())
			{
				writeData(entry.getKey(), entry.getValue(), writer, depth + 1, false);
			}
		}
		else if (data instanceof Collection)
		{
			writer.println();
			for (Object entry : (Collection<Object>)data)
			{
				writeList(entry, writer, depth);
			}
		}
		else if (data instanceof String || data instanceof Boolean || data instanceof Number)
		{
			yaml.dumpAll(Collections.singletonList(data).iterator(), writer);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

	protected void writeComment(final PrintWriter writer, final int depth, final String... comments)
	{
		for (String comment : comments)
		{
			final String trimmed = comment.trim();
			if (trimmed.isEmpty())
			{
				continue;
			}
			writeIndention(writer, depth);
			writer.print("# ");
			writer.print(trimmed);
			writer.println();
		}
	}

	private void writeIndention(final PrintWriter writer, final int depth)
	{
		for (int i = 0; i < depth; i++)
		{
			writer.print("  ");
		}
	}

	private void writeList(final Object data, final PrintWriter writer, final int depth)
	{
		writeIndention(writer, depth);
		writer.print("- ");
		if (data instanceof String || data instanceof Boolean || data instanceof Number)
		{
			yaml.dumpAll(Collections.singletonList(data).iterator(), writer);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}
}
