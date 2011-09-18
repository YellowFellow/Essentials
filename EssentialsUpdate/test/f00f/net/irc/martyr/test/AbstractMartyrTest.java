package f00f.net.irc.martyr.test;

import org.junit.Before;

/**
 * JUnit test cases; superclass for martyr test cases to provide
 * common functions.
 * @author Benjamin Damm
 * */
public class AbstractMartyrTest
{
	/**
	 * Initialize log4j
	 * */
	@Before
	public void initTest()
	{
		// Load log4j
		//new PropertyConfigurator();
	}
}

