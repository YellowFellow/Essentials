package f00f.net.irc.martyr.test;

import java.util.logging.Logger;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * JUnit test case for general framework functions.
 * @author Benjamin Damm
 * */
public class TestMartyrJunit extends AbstractMartyrTest
{
    private static Logger log = Logger.getLogger(TestMartyrJunit.class.getName());

    /** Hello world unit test.
     * */
    @Test
    public void testHello()
    {
        log.info("Hello");
        assertTrue(true);
    }

    @Before
    public void setUp() {
        log.info("Before!");
    }
}

