import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;

public class Receiver implements Runnable {
	Server _s;
	
	Receiver (Server s) { _s = s; }

	@Override
	public void run () {
		BufferedReader br = null;
		try { br = _s.getBufferedReader (); }
		catch (IOException e) {
			GUI.consolePrint ("Impossibile creare il ricevitore del server, non potrai ricevere messaggi :(", 2);
			return;
		}
		if (br == null) return;
		
		while (true) {
			String msg = "";
			try {
				try { msg = br.readLine(); }
				catch (SocketException e) {
					Chat.print ("La connessione è stata interrotta --------------------");
					Thread.currentThread ().interrupt ();
				}
			} catch (IOException e) {
				GUI.consolePrint ("Errore durante la ricezione del messaggio: " + Thread.currentThread ().getName (), 2);
			}
			
			Chat.print (msg);
		}
	}
}
