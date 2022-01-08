import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

public class MessageReceiver implements Runnable {
	Host _h;
	
	MessageReceiver (Host h) { _h = h; }

	@Override
	public void run () {
		BufferedReader br = null;
		try { br = _h.getBufferedReader (); }
		catch (IOException e) {
			System.out.println ("Impossibile istanziare il ricevitore per l'host, questo host non sarà in grado di ricevere messaggi :(");
			return;
		}
		if (br == null) return;
		
		ArrayList <Host> blackList = new ArrayList <Host> ();
		blackList.add (_h);
		
		while (true) {
			String msg = "";
			try {
				try { msg = br.readLine(); }
				catch (SocketException e) {
					System.out.println ("La connessione con " + _h.getIP () + " è stata interrotta");
					ChatServer.removeHost (_h); 
					return;
				}
			} catch (IOException e) {
				System.out.println ("Errore durante la ricezione del messaggio da parte di " + _h.getIP ());
			}
			if (msg != null) {
				if (_h.isNicknameReceived ()) {
					ChatServer.sendBroadcast(_h, msg, blackList);
				} else {
					_h.setNickname (msg);
					_h.setNicknameReceived ();
				}
					
			}
		}
	}
}
