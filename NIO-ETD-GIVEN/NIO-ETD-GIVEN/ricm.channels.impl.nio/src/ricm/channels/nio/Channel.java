package ricm.channels.nio;

import java.nio.channels.SocketChannel;

import ricm.channels.IChannel;
import ricm.channels.IChannelListener;

public class Channel implements IChannel {

	SocketChannel sc;
	Writer writer;
	Reader reader;
	
	public Channel(SocketChannel sc ) {
		this.sc = sc;
		writer = new Writer(sc, this);
		reader = new Reader(sc, this);
	}
	
	@Override
	public void setListener(IChannelListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send(byte[] bytes, int offset, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send(byte[] bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean closed() {
		// TODO Auto-generated method stub
		return false;
	}
	}
