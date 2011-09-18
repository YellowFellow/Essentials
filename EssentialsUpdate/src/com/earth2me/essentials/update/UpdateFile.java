package com.earth2me.essentials.update;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;


public class UpdateFile
{
	private final static Logger LOGGER = Logger.getLogger("Minecraft");
	private final static String UPDATE_URL = "http://goo.gl/xxxx";
	private final static BigInteger PUBLIC_KEY = new BigInteger("5ha6a2d4qdy17ttkg8evh74sl5a87djojwenu12k1lvy8ui6003e6l06rntczpoh99mhc3txj8mqlxw111oyy9yl7s7qpyluyzix3j1odxrxx4u52gxvyu6qiteapczkzvi7rxgeqsozz7b19rdx73a7quo9ybwpz1cr82r7x5k0pg2a73pjjsv2j1awr13azo7klrcxp9y5xxwf5qv1s3tw4zqftli18u0ek5qkbzfbgk1v5n2f11pkwwk6p0mibrn26wnjbv11vyiqgu95o7busmt6vf5q7grpcenl637w83mbin56s3asj1131b2mscj9xep3cbj7la9tgsxl5bj87vzy8sk2d34kzwqdqgh9nry43nqqus12l1stmiv184r8r3jcy8w43e8h1u1mzklldb5eytkuhayqik8l3ns04hwt8sgacvw534be8sx26qrn5s1", 36);
	private final transient File file;
	private final transient Configuration updateConfig;
	private final transient Plugin plugin;
	private final transient TreeMap<Version, VersionInfo> versions = new TreeMap<Version, VersionInfo>();

	public UpdateFile(final Plugin plugin)
	{
		this.plugin = plugin;
		final long lastUpdate = Long.parseLong(plugin.getConfiguration().getString("lastupdate", "0"));
		file = new File(plugin.getDataFolder(), "update.yml");
		updateConfig = new Configuration(file);
		if (lastUpdate < System.currentTimeMillis() - 1000 * 60 * 60 * 6 || !file.exists())
		{
			if (file.exists() && !file.delete())
			{
				LOGGER.log(Level.SEVERE, "Could not delete file update.yml!");
				return;
			}
			if (!downloadFile() || !checkFile())
			{
				LOGGER.log(Level.SEVERE, "Could not download and verify file update.yml!");
				return;
			}
		}
		readVersions();
	}

	private boolean downloadFile()
	{
		GetFile getFile;
		try
		{
			getFile = new GetFile(UPDATE_URL);
			getFile.saveTo(file);
			plugin.getConfiguration().setProperty("lastupdate", System.currentTimeMillis());
			plugin.getConfiguration().save();
			return true;
		}
		catch (IOException ex)
		{
			LOGGER.log(Level.SEVERE, "Error while downloading update.yml", ex);
			return false;
		}
	}

	private boolean checkFile()
	{
		BufferedInputStream bis = null;
		try
		{
			bis = new BufferedInputStream(new FileInputStream(file));
			if (bis.read() != '#')
			{
				throw new IOException("File has to start with #");
			}
			final StringBuilder length = new StringBuilder();
			final StringBuilder signature = new StringBuilder();
			boolean isSignature = false;
			do
			{
				final int cur = bis.read();
				if (cur == -1)
				{
					break;
				}
				if (cur == ':')
				{
					isSignature = true;
				}
				else if (cur == '\n')
				{
					break;
				}
				else if ((cur >= '0' && cur <= '9')
						 || (cur >= 'a' && cur <= 'z'))
				{
					if (isSignature)
					{
						signature.append((char)cur);
					}
					else
					{
						length.append((char)cur);
					}
				}
				else
				{
					throw new IOException("Illegal character in signature!");
				}
			}
			while (true);
			if (length.length() == 0 || signature.length() == 0)
			{
				throw new IOException("Broken signature!");
			}
			final int sigLength = new BigInteger(length.toString(), 36).intValue();
			if (sigLength < 0 || sigLength > 2048)
			{
				throw new IOException("Invalid signature length!");
			}
			final byte[] sigBytes = new BigInteger(signature.toString(), 36).toByteArray();
			if (sigLength < sigBytes.length)
			{
				throw new IOException("Length is less then available bytes.");
			}
			byte[] realBytes;
			if (sigLength == sigBytes.length)
			{
				realBytes = sigBytes;
			}
			else
			{
				realBytes = new byte[sigLength];
				System.arraycopy(sigBytes, 0, realBytes, sigLength - sigBytes.length, sigBytes.length);
			}
			final X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(PUBLIC_KEY.toByteArray());
			final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			final PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
			final Signature rsa = Signature.getInstance("SHA256withRSA");
			rsa.initVerify(pubKey);
			final byte[] buffer = new byte[2048];
			int readLength;
			do
			{
				readLength = bis.read(buffer);
				if (readLength >= 0)
				{
					rsa.update(buffer, 0, readLength);
				}
			}
			while (readLength >= 0);
			return rsa.verify(realBytes);
		}
		catch (IOException ex)
		{
			Logger.getLogger(UpdateFile.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (InvalidKeyException ex)
		{
			Logger.getLogger(UpdateFile.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (InvalidKeySpecException ex)
		{
			Logger.getLogger(UpdateFile.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (NoSuchAlgorithmException ex)
		{
			Logger.getLogger(UpdateFile.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (SignatureException ex)
		{
			Logger.getLogger(UpdateFile.class.getName()).log(Level.SEVERE, null, ex);
		}
		finally
		{
			try
			{
				if (bis != null)
				{
					bis.close();
				}
			}
			catch (IOException ex)
			{
				Logger.getLogger(UpdateFile.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return false;
	}

	private void readVersions()
	{
		versions.clear();
		for (String versionString : updateConfig.getKeys())
		{
			final Version version = new Version(versionString);
			final VersionInfo info = new VersionInfo(updateConfig, versionString);
			versions.put(version, info);
		}
	}

	public Map<Version, VersionInfo> getVersions()
	{
		return Collections.unmodifiableMap(versions.descendingMap());
	}
}
