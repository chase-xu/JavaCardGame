
public interface Playable {
	
	final int PORT = 1;
	
	int CARDS = 0;
	
	int TURN = 1;
	
	int PLAY = 2;
	
	int PASS = 3;
	
	int VALID = 4;
	
	int NOVALID = 5;
	
	int WON = 6;
	
	int QUIT = 7;
	
	int PLAYER = 8;
	
	default String cmdToString(int cmd)
	   {
	      String cmdString;
	      switch (cmd)
	      { 
	         case CARDS:
	            cmdString = "CARDS";
	            break;
	         case TURN: 
	            cmdString = "TURN";
	            break;
	         case PLAY:
	            cmdString = "PLAY";
	            break;
	         case PASS:
	            cmdString = "PASS";
	            break;
	         case VALID:
	            cmdString = "VALID";
	            break;
	         case NOVALID: 
	            cmdString = "NOVALID";
	            break;
	         case WON:
	            cmdString = "WON";
	            break;
	         case QUIT:
	        	 cmdString = "QUIT";
	        	 break;
	         case PLAYER:
	        	 cmdString = "PLAYER";
	        	 break;
	         default:
	            cmdString = "UNRECOGNIZABLE COMMAND";
	      }  // switch
	      return cmdString;
	   } // cmdToString	
}
