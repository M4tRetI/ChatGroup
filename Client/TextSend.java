/**
 * Dopo l'aggiunta della GUI questo classe non ha senso 
 * */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TextSend implements Runnable {
	@Override
	public void run () {
		BufferedReader br = new BufferedReader (new InputStreamReader (System.in, StandardCharsets.UTF_8));
		
		String text = "blank";
		while (text != null) {
			if (text != "") System.out.print ("\rYOU> ");
			try {
				text = br.readLine();
			} catch (IOException e) {
				GUI.consolePrint ("Errore durante la scrittura della bozza del messaggio", 2);
			}
			if (text != null && text != "") Chat.send (text);
		}
		Chat.chatClosed ();
	}
}
