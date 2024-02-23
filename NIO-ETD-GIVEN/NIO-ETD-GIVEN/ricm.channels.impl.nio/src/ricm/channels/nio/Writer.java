package ricm.channels.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class Writer {

	enum State {
		WRITING_LENGTH, WRITING_MSG, WRITING_IDLE
	};

	State state = State.WRITING_IDLE;
	ByteBuffer lengthBB;
	ByteBuffer msg;
	LinkedList<byte[]> pendingMsgs;
	SocketChannel sc;
	Channel c;
	
	public Writer(Channel c) {
		this.c = c;
		lengthBB = ByteBuffer.allocate(4);
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

	void handleWrite(SocketChannel sc) throws IOException {
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
				if (!pendingMsgs.isEmpty()) {
					state = State.WRITING_LENGTH;
					lengthBB.rewind();
					lengthBB.putInt(pendingMsgs.getFirst().length);
					lengthBB.rewind();
					msg = ByteBuffer.wrap(pendingMsgs.getFirst());
				} else {
					state = State.WRITING_IDLE;
					c.myKey.interestOps(SelectionKey.OP_READ);
				}
			}
		default:
			break;
		}
	}

}
