package com.earth2me.essentials.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;


public class GetFile
{
	private final transient URL url;
	private transient MessageDigest digest;

	public GetFile(final String urlString) throws MalformedURLException
	{
		this.url = new URL(urlString);
	}

	public void saveTo(final File file) throws IOException
	{
		try
		{
			saveTo(file, null);
		}
		catch (NoSuchAlgorithmException ex)
		{
			// Ignore because the code is never called
		}
	}

	public void saveTo(final File file, final String key) throws IOException, NoSuchAlgorithmException
	{
		if (key != null)
		{
			digest = MessageDigest.getInstance("SHA256");
		}
		final byte[] buffer = new byte[1024 * 8];
		boolean brokenFile = false;
		final BufferedInputStream input = new BufferedInputStream(url.openStream());
		try
		{
			final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
			try
			{
				int length;
				do
				{
					length = input.read(buffer);
					if (length >= 0)
					{
						if (key != null)
						{
							digest.update(buffer, 0, length);
						}
						output.write(buffer, 0, length);
					}
				}
				while (length >= 0);
				if (key != null)
				{
					final byte[] checksum = digest.digest();
					final String checksumString = new BigInteger(checksum).toString(36);
					if (!checksumString.equals(key))
					{
						brokenFile = true;
					}
				}
			}
			finally
			{
				output.close();
			}
			if (brokenFile && !file.delete())
			{
				Logger.getLogger("Minecraft").severe("Could not delete file " + file.getPath());
			}
		}
		finally
		{
			input.close();
		}
		if (brokenFile)
		{
			throw new IOException("Checksum check failed.");
		}
	}
}
