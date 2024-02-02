package ricm.nio.babystep2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import ricm.nio.babystep2.ReaderAutomata.State;

public class WriterAutomata {
	enum State {WRITING_LENGTH, WRITING_MSG, WRITING_IDLE} ;
	State state = State.WRITING_IDLE ;
	ByteBuffer lengthBB;
	ByteBuffer pendingMsgs;
	SocketChannel sc;
	
	public WriterAutomata() {
	}
	
	public void sendMsg(byte[] msg) {
		// sauvegarde taille du message
		lengthBB = ByteBuffer.allocate(4);
		lengthBB.putInt(msg.length);
		lengthBB.rewind();
		
		// sauvegarde message
		pendingMsgs = ByteBuffer.allocate(msg.length);
		pendingMsgs.put(msg);
		pendingMsgs.rewind();
		state = State.WRITING_LENGTH;
	}
	
	public void handleWrite(SocketChannel sc) throws IOException {
		switch (state) {
			case WRITING_LENGTH:
				sc.write(lengthBB);
				state = State.WRITING_MSG;
			case WRITING_MSG:
				sc.write(pendingMsgs);
				if (pendingMsgs.remaining() == 0) {
					// il faut suppr message
					state = State.WRITING_IDLE;
				}
			default:
				break;
		}
	}
}
