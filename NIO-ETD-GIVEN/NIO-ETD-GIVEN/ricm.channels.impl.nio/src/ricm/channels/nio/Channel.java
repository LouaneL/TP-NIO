package ricm.channels.nio;

import java.io.IOException;
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

	public Channel(SocketChannel sc) {
		this.sc = sc;
		writer = new Writer(sc, this);
		reader = new Reader(sc, this);
	}

	@Override
	public void setListener(IChannelListener l) {
		// TODO Auto-generated method stub
		listener = l;
	}

	@Override
	public void send(byte[] bytes, int offset, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send(byte[] bytes) {
		// TODO Auto-generated method stub
		writer.sendMsg(bytes);
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean closed() {
		return isclosed;
	}

	void handleRead() {
		// TODO Auto-generated method stub
		
	}

	void handleWrite() {
		// TODO Auto-generated method stub
		
	}
}
