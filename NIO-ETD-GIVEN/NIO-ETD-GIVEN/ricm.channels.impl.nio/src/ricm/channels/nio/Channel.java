package ricm.channels.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import ricm.channels.IChannel;
import ricm.channels.IChannelListener;


/*
 * A AJOUTER
 * Une key au channel pour pouvoir send
 * Un selector
 * handleRead() et handleWrite()
 */
public class Channel implements IChannel {

	SocketChannel sc;
	Writer writer;
	Reader reader;
	IChannelListener listener;
	boolean isclosed;
	
	// NIO selector
	SelectionKey myKey;

	public Channel(SocketChannel sc,SelectionKey key) {
		this.sc = sc;
		this.myKey = key;
		writer = new Writer(this);
		reader = new Reader(this);
	}

	@Override
	public void setListener(IChannelListener l) {
		// TODO Auto-generated method stub
		listener = l;
	}

	@Override
	public void send(byte[] bytes, int offset, int count) {
		byte[] toSend = new byte[count];
		System.arraycopy(bytes, offset, toSend, 0, count);
		send(toSend);
	}

	@Override
	public void send(byte[] bytes) {
		writer.sendMsg(bytes);
		myKey.interestOps(SelectionKey.OP_WRITE);
	}

	@Override
	public void close() {
		isclosed = true;
		try {
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		System.out.println("Socket close avec succés");
	}

	@Override
	public boolean closed() {
		return isclosed;
	}

	void handleRead(SelectionKey key){
		assert (this.myKey == key);
		assert (sc == key.channel());
		try {
			reader.handleRead(sc);
		} catch (IOException e) {
			this.close();
			listener.closed(this, e);
		}
	}

	/**
	 * Handle data to write
	 * assume the data to write is in outBuffer
	 * @param the key of the channel on which data can be sent
	 */
	void handleWrite(SelectionKey key) {
		assert (this.myKey == key);
		assert (sc == key.channel());
				
		try {
			writer.handleWrite(sc);
		} catch (IOException e) {
			this.close();
			listener.closed(this, e);
		}
	}
}
