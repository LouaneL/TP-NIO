package ricm.channels.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

import ricm.channels.IBroker;
import ricm.channels.IBrokerListener;
import ricm.channels.IChannel;
import ricm.nio.babystep2.Automata;
import ricm.nio.babystep2.ReaderAutomata;
import ricm.nio.babystep2.WriterAutomata;

/**
 * Broker implementation
 */

public class Broker implements IBroker {
	
	// The client channel to communicate with the server 
	private SocketChannel sc;
	
	// The server channel to accept connections from clients
	private ServerSocketChannel ssc;
	
	// The selection key to register events of interests on the server channel
	private SelectionKey skey;
	
	// NIO selector
	private Selector selector;
	
	IBrokerListener l;

	public Broker() throws Exception {
		// create a new selector
		selector = SelectorProvider.provider().openSelector();
	}

	@Override
	public void setListener(IBrokerListener l) {
		this.l = l;
	}

	@Override
	public boolean connect(String host, int port) {
		
		try {
			// create a non-blocking socket channel
			sc = SocketChannel.open();
			sc.configureBlocking(false);

			// register a CONNECT interest for channel sc 
			skey = sc.register(selector, SelectionKey.OP_CONNECT);

			// request to connect to the server
			InetAddress addr;
			addr = InetAddress.getByName(host);
			sc.connect(new InetSocketAddress(addr, port));
		} catch (IOException e) {
			l.refused(host, port);
		}
		
		return true;
	}

	@Override
	public boolean accept(int port) {

		try {
			// create a new non-blocking server socket channel
			ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false);

			// bind the server socket to the given address and port
			InetAddress hostAddress;
			hostAddress = InetAddress.getByName("localhost");
			InetSocketAddress isa = new InetSocketAddress(hostAddress, port);
			ssc.socket().bind(isa);

			// register a ACCEPT interest for channel ssc
			skey = ssc.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Handle a connect event - finish to establish the connection
	 * 
	 * @param the key of the channel on which a connection is requested
	 */
	private void handleConnect(SelectionKey key) throws IOException {
		assert (this.skey == key);
		assert (sc == key.channel());

		sc.finishConnect();
		
		
		// register a READ interest on sc to receive the message sent by the client
		SelectionKey keyclient = sc.register(selector, SelectionKey.OP_READ);
		IChannel c = new Channel(sc, keyclient);
		keyclient.attach(c);

		
		l.connected(c);
	}
	
	/**
	 * Handle an accept event - accept the connection and make it non-blocking
	 * 
	 * @param the key of the channel on which a connection is requested
	 */
	private void handleAccept(SelectionKey key) throws IOException {
		assert (this.skey == key);
		assert (ssc == key.channel());
		SocketChannel sc;

		// do the actual accept on the server-socket channel
		// get a client channel as result
		sc = ssc.accept();
		sc.configureBlocking(false);
		
		
		// register a READ interest on sc to receive the message sent by the client
		SelectionKey keyserver = sc.register(selector, SelectionKey.OP_READ);
		
		// Create the Channel
		IChannel c = new Channel(sc,keyserver);

		keyserver.attach(c);
		
		l.accepted(c);
	}


	public void run() throws IOException {
//		System.out.println("Broker running");
		while (true) {
			// wait for some events
			selector.select();

			// get the keys for which the events occurred
			Iterator<?> selectedKeys = this.selector.selectedKeys().iterator();
			while (selectedKeys.hasNext()) {
				SelectionKey key = (SelectionKey) selectedKeys.next();
				selectedKeys.remove();

				// process the event
				if (key.isValid() && key.isAcceptable())   // accept event
					handleAccept(key);
				if (key.isValid() && key.isConnectable())  // connect event
					handleConnect(key);
				if (key.isValid() && key.isReadable()) {
					Channel channel = (Channel) key.attachment();
					channel.handleRead(key);
				}
				if (key.isValid() && key.isWritable()) {
					Channel channel = (Channel) key.attachment();
					channel.handleWrite(key);
				}
			}
		}
	}

}
