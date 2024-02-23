package PingPong;

import ricm.channels.IBroker;
import ricm.channels.IBrokerListener;
import ricm.channels.IChannel;
import ricm.channels.IChannelListener;

public class Ping implements IBrokerListener, IChannelListener{
	IBroker b;
	
	public Ping(IBroker b) {
		this.b = b;
		b.setListener(this);
		b.connect("pong", 80);
	}

	@Override
	public void connected(IChannel c) {
		// TODO Auto-generated method stub
//		c.send()
		c.setListener(this);
	}

	@Override
	public void refused(String host, int port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accepted(IChannel c) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void received(IChannel c, byte[] bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closed(IChannel c, Exception e) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	public static void main(String[] args) {
		Broker b = new Broker();
		Ping ping = new Ping(b);
		b.loop();
		
	}
	
	public class Broker implements IBroker {

		@Override
		public void setListener(IBrokerListener l) {
			// TODO Auto-generated method stub
			
			
		}
		
		public void loop() {
			
		}

		@Override
		public boolean connect(String host, int port) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean accept(int port) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}


}
