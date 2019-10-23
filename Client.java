import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Canvas;
import java.awt.TextArea;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Panel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;

public class Client extends JFrame implements Playable, Runnable {

	private JPanel contentPane;
	private JTextField textField;
	private JCheckBox[] boxes;
	private DataInputStream fromServer;
	private DataOutputStream toServer;
	private TextArea textArea;
	private ArrayList<Integer> cardsInHand = new ArrayList<Integer>();
	private ArrayList<Integer> played = new ArrayList<Integer>();
	private JButton btnPlay;
	private JButton btnPass;
	private JButton btnStartNewGame;
	private JButton btnQuit;
	private Socket socket;
	private MyCanvas canvas0;
	private MyCanvas canvas1;
	private static String hostAddr;
	private String host;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
			
		
		if(args[0].equals("-host"))
		{
			hostAddr = args[1];
		}
		Client frame = new Client(hostAddr);
	}

	/**
	 * Create the frame.
	 */
	public Client(String host) {
		this.host = host; //setting up host address
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		drawCards();
		

		textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setBounds(10, 257, 203, 221);
		contentPane.add(textArea);
		
		JLabel lblChat = new JLabel("Chat");
		lblChat.setBounds(10, 492, 31, 20);
		contentPane.add(lblChat);
		
		textField = new JTextField();
		textField.setBounds(44, 492, 169, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		drawPlayed();
		
		
		btnPlay = new JButton("Play"); //function of button play
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try
				{
					int i = 0;
					ArrayList<Integer> pos = new ArrayList<Integer>();
					for(JCheckBox box : boxes)
					{
						if(box.isSelected())
						{	 
							pos.add((Integer) i); // adding position of the cards
						}
						i++;
					}
					if(pos.size() != 0)
					{
						toServer.writeInt(PLAY); //request play
						toServer.writeInt(pos.size()); // tell server how many cards want to play
						for(int k = 0; k < pos.size(); k++) //give the positions to server
						{
							toServer.writeInt((int) pos.get(k));
						}
						toServer.flush();
					}
					else
					{
						textArea.append("No cards played, play again!!!\n");
					}
				}
				catch(IOException e1)
				{
					textArea.append(e1.toString() + "\n");
				}
			}
		});
		btnPlay.setBounds(817, 305, 124, 39);
		btnPlay.setEnabled(false);
		contentPane.add(btnPlay);
		
		btnPass = new JButton("Pass");
		btnPass.setBounds(817, 355, 124, 37);
		btnPass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try
				{
					toServer.writeInt(PASS); //request pass
				}
				catch(IOException e2)
				{
					textArea.append(e2.toString() + "\n");
				}
			}
			});
		btnPass.setEnabled(false);
		contentPane.add(btnPass);
		
		btnStartNewGame = new JButton("Start New Game");
		btnStartNewGame.setEnabled(false);
		btnStartNewGame.setBounds(817, 43, 163, 55);
		
		btnStartNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					connection();
			}
			});
		contentPane.add(btnStartNewGame);
		
		btnQuit = new JButton("Quit");
		btnQuit.setBounds(817, 126, 163, 49);
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try
				{
					toServer.writeInt(QUIT); //request pass
					textArea.append("You quit game and lost\n");
				}
				catch(IOException e3)
				{
					textArea.append(e3.toString() + "\n");
				}
			}
			});
		contentPane.add(btnQuit);
		
		//check box
		boxes = new JCheckBox[10];
		JCheckBox checkBox = new JCheckBox("");
		checkBox.setBounds(280, 494, 20, 23);
		boxes[0] = checkBox;
		contentPane.add(checkBox);
		
		JCheckBox checkBox_1 = new JCheckBox("");
		checkBox_1.setBounds(321, 494, 20, 23);
		boxes[1] = checkBox_1;
		contentPane.add(checkBox_1);
		
		JCheckBox checkBox_2 = new JCheckBox("");
		checkBox_2.setBounds(358, 494, 20, 23);
		boxes[2] = checkBox_2;
		contentPane.add(checkBox_2);
		
		JCheckBox checkBox_3 = new JCheckBox("");
		checkBox_3.setBounds(394, 494, 20, 23);
		boxes[3] = checkBox_3;
		contentPane.add(checkBox_3);
		
		JCheckBox checkBox_4 = new JCheckBox("");
		checkBox_4.setBounds(432, 494, 20, 23);
		boxes[4] = checkBox_4;
		contentPane.add(checkBox_4);
		
		JCheckBox checkBox_5 = new JCheckBox("");
		checkBox_5.setBounds(477, 494, 20, 23);
		boxes[5] = checkBox_5;
		contentPane.add(checkBox_5);
		
		JCheckBox checkBox_6 = new JCheckBox("");
		checkBox_6.setBounds(518, 494, 20, 23);
		boxes[6] = checkBox_6;
		contentPane.add(checkBox_6);
		
		JCheckBox checkBox_7 = new JCheckBox("");
		checkBox_7.setBounds(551, 494, 20, 23);
		boxes[7] = checkBox_7;
		contentPane.add(checkBox_7);
		
		JCheckBox checkBox_8 = new JCheckBox("");
		checkBox_8.setBounds(588, 494, 20, 23);
		boxes[8] = checkBox_8;
		contentPane.add(checkBox_8);
		
		JCheckBox checkBox_9 = new JCheckBox("");
		checkBox_9.setBounds(630, 494, 20, 23);
		boxes[9] = checkBox_9;
		contentPane.add(checkBox_9);
		setVisible(true);
		
		connection();
	}

	
	public void drawCards()
	{
		canvas0 = new MyCanvas(cardsInHand, 40 , 50);
		canvas0.setBounds(233, 256, 561, 231);
		contentPane.add(canvas0);
	}
	public void drawPlayed()
	{
		canvas1 = new MyCanvas(played, 80, 100);
		canvas1.setBounds(138, 10, 656, 240);
		contentPane.add(canvas1);
	}
	
	/**
	 * Connect to server
	 */
	public void connection()
	{
		try
		{
			//Create a socket to connect to the server
			socket = new Socket(host , PORT);
			
			textArea.append("Connected to server\n");
			
			//receive data from server
			fromServer = new DataInputStream(
					socket.getInputStream());
			//send data to server
			toServer = new DataOutputStream(
					socket.getOutputStream());
			
		}
		catch(IOException e)
		{
			textArea.append(e.toString() + "\n");
		}
		
		//start the thread
		Thread thread = new Thread(this);
		thread.start();
	}
	
	/**
	 * Thread to run
	 */
	public void run()
	{
		
		try
		{	
			//read command for server
			int cmd = fromServer.readInt();
			played.clear();
			cardsInHand.clear();
			while (cmd != WON)
			{	
				switch(cmd) {
				
					//receive cards from server
					case CARDS:
						for(int i = 0; i < 10; i++)
						{
							int n = fromServer.readInt();
							cardsInHand.add((Integer) n);
						}
						btnStartNewGame.setEnabled(false); //disable start new game button
						canvas0.repaint(); //draw cards in hand
						textArea.append("Game started\n");
						break;
					
					//play turn
					case TURN:
						played.clear();
						int n = fromServer.readInt(); //how many cards played
						for(int i = 0; i < n; i++)
						{
							int card = fromServer.readInt();  //know what cards played
							played.add(card);
						}
						textArea.append("Your turn to play\n");
						canvas1.repaint();
						btnPlay.setEnabled(true); //enable buttons 
						btnPass.setEnabled(true);
						break;
						
					case VALID:
						played.clear();
						btnPlay.setEnabled(false); //disable buttons
						btnPass.setEnabled(false);
						int k = fromServer.readInt(); //know how many cards to play
						for(int i = 0; i < k; i++)
						{
							int x = fromServer.readInt(); //get position of the card
							played.add(cardsInHand.get(x)); //add the card to played
							cardsInHand.remove(x); //remove the cards from hand
						}
						canvas0.repaint(); //draw cards in hand
						canvas1.repaint(); // draw cards played
						textArea.append("The other player's turn to play\n");
						break;
					
					case NOVALID:
						textArea.append("Your previous play is not valid, play again!!!\n");
						
					case PLAYER:
						int numPlayer = fromServer.readInt();
						setTitle("Player " + numPlayer);
						break;
						
					default:
						textArea.append("Invalid command!!!!\n");
						break;
				}
				cmd = fromServer.readInt();
				if(cmd == WON)
				{
					int num = fromServer.readInt();
					textArea.append("Player " + num + " has won!!\n");
					btnPlay.setEnabled(false); //disable buttons
					btnPass.setEnabled(false);
					btnStartNewGame.setEnabled(true);
					socket.close();
					break;
				}
			}

			
		}
			catch(IOException e1)
			{
				textArea.append(e1.toString() + "\n");
			}
		}
	
	/**
	 * Canvas to draw the cards
	 * @author XP
	 *
	 */
	private class MyCanvas extends JPanel
	{	
		private ArrayList<Integer> cards;
		private int width;
		private int height;
		public MyCanvas(ArrayList<Integer> cards, int width, int height)
		{
			this.cards = cards; 
			this.width = width;
			this.height = height;
		}
		
		@Override
		public void paintComponent(Graphics g)
		{	
			g.clearRect(0, 0, 1000, 1000);
			int x = 20;
			for(int i = 0; i < cards.size(); i++)	
			{
				g.drawRect(x, 35, width, height);
				g.drawString(cards.get(i).toString() , x + 10, 50);
				x += (width + 5);	
			}
			
		}
	}
	
}