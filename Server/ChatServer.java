import java.util.ArrayList;
import java.util.Vector;
import java.io.*;
import java.net.ServerSocket;
import java.net.SocketException;

public class ChatServer {
	public static Vector <Host> connectedHosts = new Vector <Host> ();
	
	public static void main (String[] args) {
		connectedHosts = new Vector <Host> ();
		ServerSocket ss = null;
		try { ss = new ServerSocket (4000); }
		catch (IOException e) {
			System.out.println ("Impossibile avviare il server");
			System.exit (0x01);
		}
		
		if (ss == null) System.exit (0x02);
		new Thread (new ConnectionsHandler (ss)).start ();
	}
	
	public static boolean addHost (Host h) {
		connectedHosts.add (h);
		return true;
	}
	public static void removeHost (Host h) {
		int host = connectedHosts.indexOf (h);
		if (host < 0) return;
		try { connectedHosts.remove (host).closeCommunication (); } catch (IOException e) {}
	}
	public static int getNumHosts () {
		return connectedHosts.size ();
	}
	
	/**
	 * Manda a tutti gli host connessi al server della chat il messaggio ricevuto da un host
	 * @param h host che ha inviato il messaggio
	 * @param msg messaggio da inoltrare
	 * @param blackList lista degli host a cui non inviare il messaggio, 
	 * 				    normalmente l'host che invia il messaggio dovrebbe essere in questa lista
	 * */
	public synchronized static void sendBroadcast (Host h, String msg, ArrayList <Host> blackList) {
		Vector <Host> toDelete = new Vector <Host> ();
		connectedHosts.forEach((host) -> {
			if (blackList.contains(host)) return;
			
			try {
				try {
					String msg_css = "color: " + h.getColor () + "; font-weight: bold;";
					host.write ("<span style='" + msg_css + "'>" + h.getNickname () + "</span>> " + msg);
				} catch (SocketException se) {
					System.out.println (host.getIP () + " si è disconesso");
					toDelete.add (host);
				}
			} catch (IOException e) {
				System.out.println ("Impossibile inoltrare il messaggio a " + host.getIP ());
			}
		});
		connectedHosts.removeAll(toDelete);
	}
}
