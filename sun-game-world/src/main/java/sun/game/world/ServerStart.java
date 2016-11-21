package sun.game.world;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import sun.game.common.config.Config;
import sun.game.common.obj.ObjectPool;
import sun.game.common.rmi.RMIManager;
import sun.game.common.thread.AynWork;
import sun.game.common.thread.TimerCenter;
import sun.game.common.thread.WorkManager;
import sun.game.db.mongo.DaoManager;
import sun.game.def.entity.DefFactory;
import sun.game.net.tcp.process.ProcessManager;
import sun.game.net.tcp.protobuf.MyHandler;
import sun.game.net.tcp.protobuf.MyProtocal;

public class ServerStart extends AynWork{
	private static final long serialVersionUID = -6252519498875620396L;

	@Override
	public void init(Object... objs) throws Exception {
		Config.getConfig().start();
		DaoManager.getManager().start();
		WorkManager.getManager().start();
		ProcessManager.getManager().start();
		DefFactory.getFactory().start();
		ObjectPool.getPool().start();
		RMIManager.getManager().start();
		TimerCenter.getCenter().start();
	}

	@Override
	public void run() {
		try {
			SocketAcceptor acceptor = new NioSocketAcceptor();
			DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
			chain.addLast("codec", new ProtocolCodecFilter(new MyProtocal()));
			acceptor.setHandler(new MyHandler());
			acceptor.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE,
					Config.getConfig().READER_IDLE);
			acceptor.bind(new InetSocketAddress(
					Config.getConfig().WORLD_SERVER_IP,
					Config.getConfig().WORLD_SERVER_PORT));
			logger.debug("START_SERVER BIND IP:{},LISTEN PORT:{}", Config
					.getConfig().WORLD_SERVER_IP,
					Config.getConfig().WORLD_SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
