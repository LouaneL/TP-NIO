package ricm.nio.babystep2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ReaderAutomata {
	
	enum State {READING_LENGTH, READING_MSG} ;
	State state = State.READING_LENGTH ;
	ByteBuffer lengthBB;
	ByteBuffer msg;
	SocketChannel sc;

	public ReaderAutomata(SocketChannel sc) {
		this.sc = sc;
		lengthBB = ByteBuffer.allocate(4);
		msg = null;
	}

	/**
	 * 	<read the length>
		<read the message knowing that it is composed of length bytes>
	 * @param sc
	 * @throws IOException 
	 */
	void handleRead(SocketChannel sc) throws IOException{
		switch (state) {
		case READING_LENGTH: {
			// <continue reading the length>
			// <if the four bytes composing the length have been read,
			// allocate a buffer to read the msg and go to READING-MSG state>
			sc.read(lengthBB);
			if (lengthBB.remaining() == 0) {
				int length = lengthBB.getInt();
				msg = ByteBuffer.allocate(length);
				state = State.READING_MSG;
				lengthBB.rewind();
			}
		} 
		case READING_MSG: {
			// <continue reading the message>
			// <if all bytes composing the msg have been read,
			// go back to READING-LENGTH state and save or return the msg read>
			sc.read(msg);
			if (msg.remaining() == 0) {
				state = State.READING_LENGTH;
				byte[] data = new byte[msg.position()];
				msg.rewind();
				msg.get(data);
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + state);
		}
	}
}
