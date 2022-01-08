import java.io.IOException;
import java.net.Socket;

import javax.swing.text.BadLocationException;

public class Chat {
	private static Server s = null;
	private static GUI gui = null;
	
	public static void main (String[] args) {
		gui = new GUI ();
		new Thread (gui).start ();
	}
	
	public static void connect (String chat_ip) {
		try { s = new Server (new Socket (chat_ip, 4000)); }
		catch (Exception e) {
			GUI.consolePrint ("Impossibile contattare il server", 2);
			print ("Chat non raggiungibile");
			s = null;
			return;
		}
		if (s == null) return;
		s.enableReceiver ();
	}
	
	public synchronized static void send (String msg) {
		if (s == null) return;
		try { s.write (msg); }
		catch (IOException e) {
			GUI.consolePrint ("Impossibile inviare il messaggio al server", 2);
		} catch (Exception e) {
			GUI.consolePrint ("E' avvenuto un errore generale", 2);
			e.printStackTrace ();
		}
	}
	public static void chatClosed () {
		if (s != null) {
			print ("Ti stai disconnettendo dalla chat...");
			GUI.consolePrint ("Disconnesso", 0);
			try { s.closeCommunication (); } catch (IOException e) {}
		}
	}
	
	public static void print (String text) {
		try { gui.chatAppendText (text); }
		catch (BadLocationException e) {}
	}
}
