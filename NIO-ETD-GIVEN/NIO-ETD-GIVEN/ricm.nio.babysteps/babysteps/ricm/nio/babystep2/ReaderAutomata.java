package ricm.nio.babystep2;

import java.nio.channels.SocketChannel;

public class ReaderAutomata {
	
	enum State {READING_LENGTH, READING_MSG} ;
	State state = State.READING_LENGTH ;
	
	/**
	 * 	<read the length>
		<read the message knowing that it is composed of length bytes>
	 * @param sc
	 */
	void handleRead(SocketChannel sc){
		switch (state) {
		case READING_LENGTH: {
			
			yield type;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + key);
		}
	}
}
