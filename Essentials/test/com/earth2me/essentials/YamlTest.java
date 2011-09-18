package com.earth2me.essentials;

import com.earth2me.essentials.yaml.Location;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import com.earth2me.essentials.yaml.Settings;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;


public class YamlTest extends TestCase
{
	public void testBasic()
	{
		final Settings set = new Settings();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(baos);
		set.write(writer, 0);
		try
		{
			System.out.println(new String(baos.toByteArray(), "UTF-8"));
		}
		catch (UnsupportedEncodingException ex)
		{
			Logger.getLogger(YamlTest.class.getName()).log(Level.SEVERE, null, ex);
		}
		Constructor constructor = new Constructor(Settings.class);
		TypeDescription settingsDescription = new TypeDescription(Settings.class);
		settingsDescription.putListPropertyType("m_o_t_d", String.class);
		settingsDescription.putMapPropertyType("locations", String.class, Location.class);
		constructor.addTypeDescription(settingsDescription);
		Yaml yaml = new Yaml(constructor);
		try
		{
			Settings test = (Settings)yaml.load(new String(baos.toByteArray(), "UTF-8"));
			assertEquals(set, test);
			ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
			PrintWriter writer2 = new PrintWriter(baos2);
			test.write(writer2, 0);

			System.out.println(new String(baos2.toByteArray(), "UTF-8"));
			System.out.println(test.getGeneral().getTest2());
		}
		catch (UnsupportedEncodingException ex)
		{
			Logger.getLogger(YamlTest.class.getName()).log(Level.SEVERE, null, ex);
		}

		set.read();
		set.getLol();
		set.setTest(true);
	}
}
