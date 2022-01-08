import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
	Socket s;
	
	OutputStreamWriter osw;
	
	Thread receiver;
	
	Server (Socket _s) throws IOException {
		s = _s;
		osw = new OutputStreamWriter (s.getOutputStream (), StandardCharsets.UTF_8);
	}
	
	public void closeCommunication () throws IOException {
		s.close ();
	}
	
	public synchronized BufferedReader getBufferedReader () throws IOException {
		return new BufferedReader (new InputStreamReader (s.getInputStream (), StandardCharsets.UTF_8));
	}
	
	public synchronized void write (String text) throws IOException {
		osw.append (text + "\n").flush ();
	}
	public void enableReceiver () {
		receiver = new Thread (new Receiver (this));
		receiver.start ();
	}
}
