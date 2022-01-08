import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.IOException;
import java.util.regex.Pattern;

public class GUI implements Runnable {
	final int WINDOW_WIDTH = 550;
	final int WINDOW_HEIGHT = 700;
    final Font font400 = new Font ("Segoe UI", Font.PLAIN, 13);
    final Font font800 = new Font ("Segoe UI", Font.BOLD, 14);
    final Font fontConsole = new Font (Font.MONOSPACED, Font.ITALIC, 12);
    final int SEND_KEY = KeyEvent.VK_ENTER;
    
    JTextPane chatTextPane = null;
    JScrollPane jspChatTextPane = null;
    static JLabel logConsole = null;
    String chat_ip = null;		// Non fare troppo riferimento. Cambia con il JDialog
    
	@Override
	public void run () {
		JFrame frame = new JFrame ("Chat ad unica stanza | Created by Matteo Remorini � 2021");

    	try { UIManager.setLookAndFeel (UIManager.getSystemLookAndFeelClassName ()); }
    	catch (Exception e) {}

    	JOptionPane ipOptionPane = createIPDialog ();
    	ipOptionPane.addPropertyChangeListener (new PropertyChangeListener () {
    		public void propertyChange (PropertyChangeEvent e) { handleIPOptionPanePropertyChangeEvent (e); }
    	});
       
    	JMenuBar menuBar = createMenuBar (ipOptionPane);
    	frame.setJMenuBar (menuBar);
    	
    	JPanel bodyPanel = createBodyPanel ();
    	
    	frame.add (bodyPanel);
       
    	frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
    	frame.setSize (WINDOW_WIDTH, WINDOW_HEIGHT);
    	frame.setLocation (350, 180);
    	frame.setResizable (false);
    	frame.setVisible (true);
    	frame.addWindowListener (new WindowAdapter () {
    		public void windowClosing (WindowEvent e) {
    			Chat.chatClosed ();
    		}
    	});
    }
    
    JMenuBar createMenuBar (JOptionPane ipOptionPane) {
    	JMenuBar menuBar = new JMenuBar ();
    	JMenu connectMenu = new JMenu ("Connettiti alla chat");
    	JMenuItem connectItem = new JMenuItem (new AbstractAction("Connetti...") {
    	    public void actionPerformed (ActionEvent e) {
    	        JDialog ipDialog = ipOptionPane.createDialog ("Connessione alla chat");
    	        ipDialog.setVisible (true);
    	    }
    	});
    	JMenuItem disconnectItem = new JMenuItem (new AbstractAction("Disconnetti...") {
    	    public void actionPerformed (ActionEvent e) {
    	    	Chat.chatClosed ();
    	    }
    	});
    	JMenuItem wipeChatMenu = new JMenuItem (new AbstractAction("Cancella il contenuto della chat") {
    	    public void actionPerformed (ActionEvent e) {
    	    	if (chatTextPane == null) return;
    	    	chatTextPane.setText ("");
    	    	try { chatAppendText ("******&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CLIENT RIPULITO&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;******<br>"); }
    	    	catch (BadLocationException exc) {}
    	    }
    	});
    	connectMenu.add (connectItem);
    	connectMenu.add (disconnectItem);
    	menuBar.add (connectMenu);
    	menuBar.add(wipeChatMenu);
    	return menuBar;
    }
    
    JOptionPane createIPDialog () {
    	JPanel p = new JPanel (new GridLayout (2, 1, 0, 10));
    	
    	JTextField connectIPInput = new JTextField (12);
    	connectIPInput.getDocument ().addDocumentListener (new DocumentListener () {
	        public void insertUpdate(DocumentEvent e) { changedUpdate(e); }
	        public void removeUpdate(DocumentEvent e) { changedUpdate(e); }
            public void changedUpdate (DocumentEvent e) {
    			chat_ip = connectIPInput.getText ().trim ();
    		}
    	});
    	
        JLabel connectLabel = new JLabel ("Inserisci l'IP della chat");
        connectIPInput.setFont (font400);
        connectLabel.setFont(font800);
        
        p.add (connectLabel);
        p.add (connectIPInput);
        
        Object[] options = { "Connetti", "Cancella" };
        JOptionPane optp = new JOptionPane (
        		new JComponent[] { p },
        		JOptionPane.QUESTION_MESSAGE,
        		JOptionPane.OK_CANCEL_OPTION,
        		null, options, null
        );
                
        return optp;
    }
    
    void handleIPOptionPanePropertyChangeEvent (PropertyChangeEvent e) {
		if (e.getPropertyName ().equals ("value")) {
			if (e.getNewValue () != null && e.getNewValue ().equals ("Connetti")) {
				if (new IPVerifier ().verify (chat_ip)) {
					try { chatAppendText ("Mi sto connettendo a " + chat_ip + "..."); }
					catch (BadLocationException exc) {}
					Chat.connect (chat_ip);
				}
			}
		}
    }
    
    JPanel createBodyPanel () {
    	JPanel p = new JPanel (new FlowLayout ());
    	chatTextPane = new JTextPane ();
    	jspChatTextPane = new JScrollPane (chatTextPane);
    	
    	Dimension textPaneDims = new Dimension ((int) Math.round(WINDOW_WIDTH * 0.95),  (int) Math.round(WINDOW_HEIGHT * 0.78));
    	chatTextPane.setEditable (false);
    	jspChatTextPane.setPreferredSize (textPaneDims);
    	chatTextPane.setContentType ("text/html");
    	chatTextPane.putClientProperty (JTextPane.HONOR_DISPLAY_PROPERTIES, true);
    	chatTextPane.setFont (font400);
    	chatTextPane.setText ("");
    	
    	try { chatAppendText("******&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CLIENT AVVIATO&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;******<br>"); }
    	catch (BadLocationException exc) {}
    	
    	JPanel pSendMsg = new JPanel ();
    	JTextArea taSendMsg = new JTextArea ();
    	JScrollPane jspTaSendMsg = new JScrollPane (taSendMsg);
    	
    	Dimension pSendMsgDims = new Dimension ((int) Math.round(WINDOW_WIDTH * 0.95),  (int) Math.round(WINDOW_HEIGHT * 0.06));
    	Dimension taSendMsgDims = new Dimension ((int) Math.round(WINDOW_WIDTH * 0.9), 35);
    	pSendMsg.setPreferredSize (pSendMsgDims);
    	jspTaSendMsg.setPreferredSize (taSendMsgDims);
    	taSendMsg.setFont (font400);
    	taSendMsg.setLineWrap (true);
    	taSendMsg.setWrapStyleWord(true);
    	taSendMsg.putClientProperty (JTextPane.HONOR_DISPLAY_PROPERTIES, true);
    	taSendMsg.addKeyListener(new KeyListener () {
    	    public void keyPressed(KeyEvent e){
    	        if(e.getKeyCode() == SEND_KEY){
    	        	e.consume ();
    	        	String textToSend = taSendMsg.getText ().trim ();
    	        	if (textToSend.length () < 1) return;
    	        	try {
    	        		Chat.send (textToSend);
    	        		chatAppendText ("YOU> " + textToSend);
    	        		taSendMsg.setText ("");
    	        	} catch (BadLocationException exc) {}
    	        }
    	    }
    	    public void keyTyped(KeyEvent e) {}
    	    public void keyReleased(KeyEvent e) {}
    	});
    	logConsole = new JLabel ("Connettiti ad una chat di gruppo");
    	logConsole.setFont (fontConsole);
    	logConsole.setForeground (Color.BLUE);
    	
    	pSendMsg.add (jspTaSendMsg);
    	p.add (jspChatTextPane);
    	p.add (pSendMsg);
    	p.add (logConsole);
    	return p;
    }
    
    void chatAppendText (String text) throws BadLocationException {
    	HTMLDocument doc = (HTMLDocument) chatTextPane.getStyledDocument ();
    	try { doc.insertAfterEnd(doc.getCharacterElement (doc.getLength () < 1 ? 0 : doc.getLength () - 1), text + "<br>"); }
    	catch (IOException e) {
    		consolePrint ("Uno o pi� messaggi non sono visibili nella chat", 1);
    	}
		scrollToBottom ();
    }
    void scrollToBottom () {
    	JScrollBar vertScrollBar = jspChatTextPane.getVerticalScrollBar ();
    	vertScrollBar.setValue (vertScrollBar.getMaximum ());
    }
    
    public static void consolePrint (String msg, int type) {
    	logConsole.setText (msg);    	
    	if (type == 0) logConsole.setForeground (Color.BLACK);
    	else if (type == 1) logConsole.setForeground (Color.YELLOW);
    	else if (type == 2) logConsole.setForeground (Color.RED);
    	else logConsole.setForeground (Color.BLUE);
    }
    
}

class IPVerifier extends InputVerifier {
	final String IP_PATTERN = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
	public boolean verify (JComponent input) {
		return Pattern.matches (IP_PATTERN, ((JTextField) input).getText ().trim ());
	}
	public boolean verify (String str) {
		return Pattern.matches (IP_PATTERN, str.trim ());
	}
}