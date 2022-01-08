import java.io.IOException;
import java.net.ServerSocket;

public class ConnectionsHandler implements Runnable {
	ServerSocket _ss;
	
	ConnectionsHandler (ServerSocket ss) {
		_ss = ss;
	}

	@Override
	public void run () {
		while (true) {
			try {
				Host newHost = new Host (_ss.accept());
				boolean ok = sendHello (newHost);
				ok &= ChatServer.addHost(newHost);
				if (!ok) {
					System.out.println ("Impossibile connettere l'host " + newHost.getIP ());
					newHost.closeCommunication ();
					continue;
				}
				newHost.enableReceiver ();
				System.out.println (newHost.getIP () + " si è connesso | " + ChatServer.getNumHosts () + " host totali");
			} catch (IOException e) {
				System.out.println ("Un host ha provato a connettersi senza successo");
				e.printStackTrace();
			}
		}
	}
	
	boolean sendHello (Host h) {
		boolean ok = true;
		try {
			h.write ("Connesso con successo");
		} catch (IOException e) { ok = false; }
		return ok;
	}
}
