package com.earth2me.essentials.yaml;

import java.io.PrintWriter;


public class BaseYaml<T>
{
	private final transient YamlFactory<T> factory;

	public BaseYaml()
	{
		factory = YamlFactory.get(this);
	}
	
	public void write(PrintWriter writer, int depth) {
		factory.write((T)this, writer, depth);
	}
	
	public T read() {
		return factory.read();
	}
}
