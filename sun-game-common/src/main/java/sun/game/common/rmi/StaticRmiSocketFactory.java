package sun.game.common.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;

public class StaticRmiSocketFactory implements RMIClientSocketFactory,
		RMIServerSocketFactory, Serializable {

	private static final long serialVersionUID = 1L;
	private final String staticIp;
	private final int staticPort;

	public StaticRmiSocketFactory(String ip, int port) {
		this.staticIp = ip;
		this.staticPort = port;
	}

	public Socket createSocket(String ip, int port) throws IOException {
		return new Socket(this.staticIp, this.staticPort);
	}

	public ServerSocket createServerSocket(int port) throws IOException {
		return new ServerSocket(this.staticPort, 0, InetAddress
				.getByName(this.staticIp));
	}
}