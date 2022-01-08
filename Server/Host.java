import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Host {
	Socket s;
	String IP;
	boolean nicknameReceived;
	String nick;
	
	OutputStreamWriter osw;
	
	Thread receiver;
	
	Host (Socket _s) throws IOException {
		s = _s;
		IP = s.getInetAddress ().getHostAddress ();
		osw = new OutputStreamWriter (s.getOutputStream (), StandardCharsets.UTF_8);
		nicknameReceived = false;
		nick = IP;
	}
	
	public boolean isNicknameReceived () {
		return nicknameReceived;
	}
	public void setNicknameReceived () {
		nicknameReceived = true;
	}
	
	public String getNickname () {
		return nick;
	}
	public void setNickname (String _n) {
		nick = _n;
	}

	public void closeCommunication () throws IOException {
		s.close ();
	}
	
	public synchronized BufferedReader getBufferedReader () throws IOException {
		return new BufferedReader (new InputStreamReader (s.getInputStream (), StandardCharsets.UTF_8));
	}
	public String getIP () {
		return IP;
	}

	public synchronized void write (String text) throws IOException {
		osw.append (text + "\n").flush ();
	}
	public void enableReceiver () {
		receiver = new Thread (new MessageReceiver (this));
		receiver.start ();
	}
}
