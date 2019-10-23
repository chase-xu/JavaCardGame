import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.TextArea;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JLabel;

public class Server extends JFrame implements Playable{

	private JPanel contentPane;
	private ArrayList<Socket> clients;
	private TextArea textArea;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		Server frame = new Server();		
	}

	
	/**
	 * Constructor 
	 * @param 
	 * @return 
	 */
	public Server() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Server");
		setVisible(true);
		setBounds(100, 100, 816, 309);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textArea = new TextArea();
		textArea.setBounds(74, 74, 601, 166);
		textArea.setEditable(false);
		contentPane.add(textArea);
		
		JLabel lblServerMessage = new JLabel("Server Message");
		lblServerMessage.setBounds(74, 11, 124, 57);
		contentPane.add(lblServerMessage);
		
		try 
		{
			ServerSocket serverSocket = new ServerSocket(PORT);
			
			int clientNo = 1;
			int game = 1;
			while(true)  // continue accepting players 
			{	
				textArea.append("Wating for player to join\n");
				Socket playerSocket1 = serverSocket.accept();
				//clients.add(playerSocket);
				textArea.append("Player 1 joined game.\n");
				new DataOutputStream(
						playerSocket1.getOutputStream()).writeInt(PLAYER);
				new DataOutputStream(
						playerSocket1.getOutputStream()).writeInt(1);
				
				Socket playerSocket2 = serverSocket.accept();
				textArea.append("Player 2 joined game.\n");
				new DataOutputStream(
						playerSocket2.getOutputStream()).writeInt(PLAYER);
				new DataOutputStream(
						playerSocket2.getOutputStream()).writeInt(2);

				HandleAClient task = new HandleAClient(playerSocket1, playerSocket2);
				new Thread(task).start();
				textArea.append("Game No." + game + " started\n");

				clientNo++;
			}
			
		}
		catch (IOException e)
		{
			textArea.append("Can't find port " + PORT);
		}
		
		/*try
		{
			ServerSocket chat = new ServerSocket(PORT + 1);
			
			while(true)
			{
				Socket player1 = chat.accept();
				Socket player2 = chat.accept();
				HandleMessage task = new HandleMessage(player1, player2);
				new Thread(task).start();
				HandleMessage task1 = new HandleMessage(player2 , player1);
				new Thread(task1).start();
			}
		}
		catch(IOException e)
		{
			textArea.append(e.toString() + "\n");
		}
	}*/
	
	/**
	 * Thread to run
	 */
	}
	class HandleAClient implements Runnable
	{
		private Socket player1;
		private Socket player2;
		private int[] cardsList = new int[] {1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3,
				4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 
			8, 8, 8, 8, 9, 9, 9, 9, 10, 10, 10, 10, 
			11, 11, 11, 11, 12, 12, 12, 12, 13, 13, 13, 13, 14, 14};
		private ArrayList<Integer> deck = new ArrayList<Integer>();
		private ArrayList<Integer> player1Cards = new ArrayList<Integer>();
		private ArrayList<Integer> player2Cards = new ArrayList<Integer>();
		private ArrayList<Integer> lastPlay = new ArrayList<Integer>();

		
		public HandleAClient(Socket socket, Socket socket2)
		{
			this.player1 = socket;
			this.player2 = socket2;
			deck = shuffle(cardsList);
		}
		
		/**
		 * Shuffle cards into deck
		 * @param a array of cards
		 * @return a array list called deck
		 */
		public ArrayList<Integer> shuffle(int[] list) //Shuffle the cards into deck
		{	
			ArrayList<Integer> array = new ArrayList<Integer>();
			for(int i = 0; i < list.length; i++)  //shuffle cards
			{
				int n = (int) Math.random() * 55;
				int j = list[i];
				list[i] = list[n];
				list[n] = j;
			}
			for(int i = 0; i < list.length; i++) //put cards into the deck
			{
				array.add(list[i]);
			}
			return array;
		}
		
		/**
		 * method to run when thread start
		 */
		public void run()
		{
			try
			{
				DataInputStream fromPlayer1 = new DataInputStream(
						player1.getInputStream());
				DataOutputStream toPlayer1 = new DataOutputStream(
						player1.getOutputStream());
				DataInputStream fromPlayer2 = new DataInputStream(
						player2.getInputStream());
				DataOutputStream toPlayer2 = new DataOutputStream(
						player2.getOutputStream());
				
				//Assigning cards to each player
				toPlayer1.writeInt(CARDS);
				for(int i = 0; i < 10; i++)
				{
					toPlayer1.writeInt((int) deck.get(i));
					player1Cards.add(deck.get(i));
					deck.remove(i);
				}
				toPlayer1.flush();
				textArea.append("Assigned cards " + player1Cards.toString() + " to player1\n");
				
				toPlayer2.writeInt(CARDS);
				for(int i = 0; i < 10; i++)
				{
					toPlayer2.writeInt((int) deck.get(i));
					player2Cards.add(deck.get(i));
					deck.remove(i);
				}
				toPlayer2.flush();
				textArea.append("Assigned cards " + player2Cards.toString() + " to player2\n");
				
				//Tell player1 to play
				toPlayer1.writeInt(TURN);
				toPlayer1.writeInt(0);
				toPlayer1.flush();
				textArea.append("Telling player 1 to start\n");
				
				boolean player1Turn = true;
				while (true) 
				{
					if(player1Turn) 
					{
						int cmd = fromPlayer1.readInt();
						switch(cmd) {
							//PLAY
							case PLAY:
								int n = fromPlayer1.readInt(); //player tells server how many cards gonna play
								ArrayList<Integer> cards = new ArrayList<Integer>(); //store position of the cards
								for(int i = 0; i < n; i++)
								{
									int card = fromPlayer1.readInt();
									cards.add((Integer) card);
								
								}
								textArea.append("Player 1 request to play the cards in positions " + cards.toString() + "\n");
								ArrayList<Integer> cardsWantPlay = new ArrayList<Integer>();
								
								for(int i = 0; i < cards.size(); i ++)
								{	
									if(cards.get(i) > player1Cards.size() - 1)
									{
										textArea.append("Player 1's play is not valid\n");
										toPlayer1.writeInt(NOVALID); // tell the player the play is no valid
										cardsWantPlay.clear();
										break;
									}
									cardsWantPlay.add(player1Cards.get(cards.get(i)));  //find out the value of the cards want to play
								}
								//if the play is valid
								if(checkValid(cardsWantPlay))
								{
									textArea.append("Player 1's request is valid\n");
									lastPlay.clear(); //clear last play
									toPlayer1.writeInt(VALID); //tell the player the play is valid
									toPlayer1.writeInt(cards.size()); //tell the player how many cards to play
									for(int i = 0; i < cards.size(); i++)
									{
										toPlayer1.writeInt((int) cards.get(i)); // tell the player to play cards in these positions
										lastPlay.add(cardsWantPlay.get(i)); // add cards to last play
										player1Cards.remove(cards.get(i)); //remove cards in player 1's hand
									}
									toPlayer1.flush();
									
									//A WON situation has appeared  
									if(player1Cards.size() == 0)
									{
										toPlayer1.writeInt(WON); 
										toPlayer1.writeInt(1);
										toPlayer2.writeInt(WON);
										toPlayer2.writeInt(1);
										toPlayer1.flush();
										toPlayer2.flush();
										clients.remove(player1); //remove players from list
										clients.remove(player2);
										player1.close();
										player2.close();
										textArea.append("Player 1 has WON\n");
									}
									else {
										toPlayer2.writeInt(TURN); //tell the player2 to play
										toPlayer2.writeInt(cards.size());
										for(int i = 0; i < lastPlay.size(); i ++) //tell the player 2 what player 1 played
										{
											toPlayer2.writeInt((int) lastPlay.get(i));
										}
										toPlayer2.flush();
										textArea.append("Telling player 2 to play\n");
										player1Turn = false; //indicate the player1 has played 
									}
								}
								else
								{	
									textArea.append("Player 1's play is not valid\n");
									toPlayer1.writeInt(NOVALID); // tell the player the play is no valid
								}
								break;
							
							//PASS
							case PASS: //once player1 wants to pass
								lastPlay.clear();
								textArea.append("Player 1 wants to pass\n");
								toPlayer2.writeInt(TURN); //tell player2 to play
								toPlayer2.writeInt(0);
								toPlayer2.flush();
								toPlayer1.writeInt(VALID);
								toPlayer1.writeInt(0);
								toPlayer1.flush();
								textArea.append("Telling player 2 to play\n");
								player1Turn = false;
								break;
							
							//QUIT
							case QUIT: // when the player want to quit
								textArea.append("Player 1 wants to quit\n");
								toPlayer1.writeInt(WON);
								toPlayer1.writeInt(2);
								toPlayer2.writeInt(WON);
								toPlayer2.writeInt(2);
								textArea.append("Letting player 2 to win\n");
								toPlayer1.flush();
								toPlayer2.flush();
								clients.remove(player1); //remove players from list
								clients.remove(player2);
								player1.close();
								player2.close();
								textArea.append("Closing the game\n");
								break;
								
							default:
								textArea.append("Invalid request from player 1 \n" );
								break;
						}
					}
					
					if(!player1Turn)
					{
						int cmd = fromPlayer2.readInt();
						switch(cmd) {
							//PLAY
							case PLAY:
								int n = fromPlayer2.readInt(); //player tells server how many cards gonna play
								ArrayList<Integer> cards = new ArrayList<Integer>(); //store position of the cards
								for(int i = 0; i < n; i++)
								{
									int card = fromPlayer2.readInt();
									if(card > player2Cards.size() - 1)
									{
										textArea.append("Player 2's play is not valid\n");
										toPlayer2.writeInt(NOVALID); // tell the player the play is no valid
										break;
									}
									cards.add((Integer) card);
			
								}
								textArea.append("Player 2 request to play cards in positions " + cards.toString() + "\n");
								ArrayList<Integer> cardsWantPlay = new ArrayList<Integer>();
								for(int i = 0; i < cards.size(); i ++)
								{
									if(cards.get(i) > player1Cards.size() - 1)
									{
										textArea.append("Player 2's play is not valid\n");
										toPlayer2.writeInt(NOVALID); // tell the player the play is no valid
										cardsWantPlay.clear();
										break;
									}
									cardsWantPlay.add(player2Cards.get(cards.get(i)));  //find out the value of the cards want to play
								}
								//if the play is valid
								if(checkValid(cardsWantPlay))
								{
									textArea.append("Player 2's request is valid\n");
									lastPlay.clear(); //clear last play
									toPlayer2.writeInt(VALID); //tell the player the play is valid
									toPlayer2.writeInt(cards.size()); //tell the player how many cards to play
									for(int i = 0; i < cards.size(); i++)
									{
										toPlayer2.writeInt((int) cards.get(i)); // tell the player to play cards in these positions
										lastPlay.add(cardsWantPlay.get(i)); //add play to last play
										player2Cards.remove(cards.get(i)); //remove cards in player2's hand
									}
									toPlayer2.flush();
									
									//A WON situation has appeared  
									if(player2Cards.size() == 0)
									{
										toPlayer1.writeInt(WON); 
										toPlayer1.writeInt(2);
										toPlayer2.writeInt(WON);
										toPlayer2.writeInt(2);
										toPlayer1.flush();
										toPlayer2.flush();
										clients.remove(player1); //remove players from list
										clients.remove(player2);
										player1.close();
										player2.close();
										textArea.append("Player 2 has WON\n");
									}
									else {
										toPlayer1.writeInt(TURN); //tell the player1 to play
										toPlayer1.writeInt(cards.size());
										for(int i = 0; i < lastPlay.size(); i ++) //tell the player 1 what player 2 played
										{
											toPlayer1.writeInt((int) lastPlay.get(i));
										}
										toPlayer1.flush();
										textArea.append("Telling player 1 to play\n");
										player1Turn = true; //indicate the player2 has played 
									}
								}
								else
								{	
									textArea.append("Player 2's play is not valid\n");
									toPlayer2.writeInt(NOVALID); // tell the player the play is no valid
								}
								break;
							
							//PASS
							case PASS: //once player 2 wants to pass
								lastPlay.clear();
								textArea.append("Player 2 wants to pass\n");
								toPlayer1.writeInt(TURN); //tell player 1 to play
								toPlayer1.writeInt(0);
								toPlayer1.flush();
								toPlayer2.writeInt(VALID);
								toPlayer2.writeInt(0);
								toPlayer2.flush();
								textArea.append("Telling player 1 to play\n");
								player1Turn = true;
								break;
							
							//QUIT
							case QUIT: // when the player want to quit
								textArea.append("Player 2 wants to quit\n");
								toPlayer1.writeInt(WON);
								toPlayer1.writeInt(1);
								toPlayer2.writeInt(WON);
								toPlayer2.writeInt(1);
								textArea.append("Letting player 1 to win\n");
								toPlayer1.flush();
								toPlayer2.flush();
								clients.remove(player1); //remove players from list
								clients.remove(player2);
								textArea.append("Closing the game\n");
								player1.close();
								player2.close();
								break;
								
							default:
								textArea.append("Invalid request from player 2 \n");
								break;
						}
					}
				}
			}
			catch(IOException e)
			{
				textArea.append(e.toString() + "\n");
			}

		}
		
		/**
		 * Check if the cards are valid to play
		 * @param array list of cards player wants to play
		 * @return true or false
		 */
		public boolean checkValid (ArrayList<Integer> cards)
		{	
			if(cards.size() == 0) //not valid position to play
			{
				return false;
			}
			if(lastPlay.size() == 0) //first turn
			{
				if(cards.size() == 1)
				{
					return true;
				}
				else if(cards.size() == 2)
				{
					if(cards.get(0) == cards.get(1))
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				else if(cards.size() == 3)
				{
					if(cards.get(0) == cards.get(1) && cards.get(2) == cards.get(0))
						return true;
					else if(cards.get(1) - cards.get(0) == 1 && cards.get(2) - cards.get(1) == 1)
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				else
				{
					return false;
				}
			}
			else if(lastPlay.size() == 1) //one card played situation 
			{
				if(cards.size() == 1) 
				{
					if(cards.get(0) > lastPlay.get(0))
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				else if(cards.size() == 2)
				{
					return false;
				}
				else if(cards.size() == 3)
				{
					if(cards.get(0) == cards.get(1) && cards.get(2) == cards.get(0))
						return true;
					else
						return false;
				}
				else
				{
					return false;
				}
			}
			else if(lastPlay.size() == 2) //two cards played situation 
			{
				if(cards.size() == 1)
				{
					return false;
				}
				else if(cards.size() == 2)
				{
					if(cards.get(0) == cards.get(1)) //two cards are the same
					{
						if(cards.get(0) > lastPlay.get(0))
						{
							return true;
						}
						else
						{
							return false;
						}
					}
					else //two cards are not same
					{
						return false;
					}
				}
				else if(cards.size() == 3)
				{
					if(cards.get(0) == cards.get(1) && cards.get(2) == cards.get(0)) // three cards are the same
						return true;
					else
						return false;
				}
			}
			else if(lastPlay.size() == 3 && lastPlay.get(1) - lastPlay.get(0) == 1 && 
					lastPlay.get(2) - lastPlay.get(1) == 1) // last play is a sequence 
			{
				if(cards.size() == 3)
				{
					if(cards.get(1) - cards.get(0) == 1 && cards.get(2) - cards.get(1) == 1)
					{
						if(cards.get(0) > lastPlay.get(0))
							return true;
						else
							return false;
					}
					else if(cards.get(0) == cards.get(1) && cards.get(2) == cards.get(0))
					{
						return true;
					}
					else
					{
						return false;
					}
				}
			}
			else if(lastPlay.size() == 3 && lastPlay.get(0) == lastPlay.get(1) &&
					lastPlay.get(2) == lastPlay.get(0)) // last play is a bomb
			{	
					if(cards.size() == 3)
					{
						if(cards.get(0) == cards.get(1) && cards.get(2) == cards.get(0))
						{
							if(cards.get(0) > lastPlay.get(0))
							{
								return true;
							}
							else
							{
								return false;
							}
						}
						else
						{
							return false;
						}
					}
					else
					{
						return false;
					}
			}
			return false;
		}
	}

}





