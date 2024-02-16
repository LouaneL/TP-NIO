package ricm.nio.babystep2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class WriterAutomata {
	enum State {WRITING_LENGTH, WRITING_MSG,WRITING_IDLE} ;
	State state = State.WRITING_IDLE;
	ByteBuffer lengthBB;
	ByteBuffer msg;
	LinkedList<byte[]> pendingMsgs;
	SocketChannel sc;
	boolean completed;


	public WriterAutomata(SocketChannel sc) {
		this.sc = sc;
		lengthBB = ByteBuffer.allocate(4);
		completed = false;
		pendingMsgs = new LinkedList<byte[]>();
	}

	public void sendMsg(byte[] msg) {
		if (state == State.WRITING_IDLE) {
			lengthBB.putInt(msg.length);
			lengthBB.rewind();
			this.msg = ByteBuffer.wrap(msg);
			state = State.WRITING_LENGTH;
		}
		pendingMsgs.add(msg);
	}

	public void handleWrite() throws IOException {
		switch (state) {
		case WRITING_IDLE:
			break;
		case WRITING_LENGTH:
			sc.write(lengthBB);
			if (lengthBB.remaining() == 0) {
				state = State.WRITING_MSG;
			}
		case WRITING_MSG:
			sc.write(msg);
			if (msg.remaining() == 0) {
				// il faut suppr message
				pendingMsgs.removeFirst();
				lengthBB.rewind();
				if (!pendingMsgs.isEmpty()){
					state = State.WRITING_LENGTH;
					lengthBB.rewind();
					lengthBB.putInt(pendingMsgs.getFirst().length);
					lengthBB.rewind();
					msg = ByteBuffer.wrap(pendingMsgs.getFirst());
				} else {
					state = State.WRITING_IDLE;
				}
			}
		default:
			break;
		}
	}

	public SocketChannel getSc() {
		return sc;
	}
	
	public boolean isCompleted() {
		if(completed) {
			completed = false;
			return true;
		}
		return completed;
	}

}
