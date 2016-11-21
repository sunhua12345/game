package sun.game.net.tcp;

import org.apache.mina.core.session.IoSession;

public abstract class SessionClient {
	protected IoSession ioSession;// 终端的会话
	protected String clientId;

	public SessionClient(String clientId, IoSession ioSession) {
		this.clientId = clientId;
		this.ioSession = ioSession;
	}

	public IoSession getIoSession() {
		return ioSession;
	}

	public void setIoSession(IoSession ioSession) {
		this.ioSession = ioSession;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

}
