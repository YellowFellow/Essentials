package f00f.net.irc.martyr.test;

import f00f.net.irc.martyr.IRCConnection;
import f00f.net.irc.martyr.commands.QuitCommand;
import f00f.net.irc.martyr.commands.WhoisCommand;
import f00f.net.irc.martyr.services.AutoReconnect;
import f00f.net.irc.martyr.services.AutoRegister;
import f00f.net.irc.martyr.services.AutoResponder;
import java.util.logging.Logger;

public class WhoisTest
{
    static Logger log = Logger.getLogger(WhoisTest.class.getName());


    /** A really simple test, all this does is login to a network and
     * issue a WHOIS on args[0]
     *
     * @param args Args passed to program
     * @throws Exception if something bad occurs  =)
     * */
    public static void main( String args[] )
        throws Exception
    {
        // Please do NOT copy-and-paste this code, you must have an
        // AutoService of your own in order to do anything useful (ie
        // interactive).
        log.info("Starting test");

        if( args.length != 3 )
        {
            log.severe("<server> <port> <target>");
            return;
        }

        String server = args[0];
        int port = Integer.parseInt( args[1] );
        String whois = args[2];

        IRCConnection connection = new IRCConnection();

        // Alive connections do not keep the program running
        connection.setDaemon(true);

        AutoReconnect autoReconnect = new AutoReconnect( connection );
        AutoRegister autoReg = new AutoRegister( connection, "foo_nick", "foo_user", "foo_name" );
        AutoResponder autoRes = new AutoResponder( connection );
        //AutoJoin autoJoin = new AutoJoin( connection, "#martyr-", "test" );
        //AutoJoin autoJoin = new AutoJoin( connection, "#martyr" );

        log.info("Connecting");

        // Note: Doesn't return until a connection is made
        autoReconnect.go( server, port );

        Thread.sleep(4000);
        connection.sendCommand( new WhoisCommand( whois ) );

        // Allow 15 seconds for the authentication and auto-join.
        // Of course no real program would do something idiotic like this.
        // See Justin for a better example.
        Thread.sleep(15000);

        //autoReconnect.disable();
        connection.sendCommand( new QuitCommand("Time to die."));

        // Another few seconds to watch the aftermath of disconnecting
        // This might be enough time to see the AutoReconnect re-connect.
        //Thread.sleep(10000);

        // Now it will really die.
    }

}

