package sun.game.net.tcp.protobuf;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketSessionConfig;

import sun.game.common.thread.WorkManager;
import sun.game.common.utils.ConstantUtils;
import sun.game.net.tcp.IoSender;
import sun.game.net.tcp.Message;
import sun.game.protobuf.pb.MsgCode.GameCode;
import sun.game.protobuf.pb.MsgCode.ResConnectCreat;

public class MyHandler implements IoHandler {

	public void exceptionCaught(IoSession session, Throwable arg1)
			throws Exception {
		if (arg1 != null && arg1.getMessage().startsWith("远程主机强迫关闭了一个现有的连接")) {
		}
	}

	public void messageReceived(IoSession session, Object obj) throws Exception {
		if (obj != null && obj instanceof Message) {
			boolean closeStatus = (Boolean) session
					.getAttribute(ConstantUtils.SESSION_CLOSE_STATUS);
			if (closeStatus) {
				// session当前已经被服务器关闭等待释放资源,不接受新的请求数据
				System.out.println("session当前已经被服务器关闭等待释放资源,不接受新的请求数据");
				return;
			}
			Message msg = (Message) obj;
			session.setAttribute(ConstantUtils.LAST_RECIVED_TIME, System.currentTimeMillis());
			WorkManager.getManager().submit(PutMsgWork.class, msg);
		}
	}

	public void messageSent(IoSession session, Object arg1) throws Exception {
	}

	public void sessionClosed(IoSession session) throws Exception {
	}

	public void sessionCreated(IoSession ioSession) throws Exception {
		// CLOSED：无连接是活动的或正在进行
		// LISTEN：服务器在等待进入呼叫
		// SYN_RECV：一个连接请求已经到达，等待确认
		// SYN_SENT：应用已经开始，打开一个连接
		// ESTABLISHED：正常数据传输状态
		// FIN_WAIT1：应用说它已经完成
		// FIN_WAIT2：另一边已同意释放
		// ITMED_WAIT：等待所有分组死掉
		// CLOSING：两边同时尝试关闭
		// TIME_WAIT：另一边已初始化一个释放
		// LAST_ACK：等待所有分组死掉
		// netstat -an|findstr "7771"
		// 掉线使用新建立的socket进行三次握手,直接释放掉老的socket链接状态(来自于操作系统)
		SocketSessionConfig cfg = (SocketSessionConfig) ioSession.getConfig();
		cfg.setReceiveBufferSize(4 * 1024);
		cfg.setReadBufferSize(4 * 1024);
		cfg.setKeepAlive(true);
		cfg.setSoLinger(0); // CLOSE_WAIT状态直接关闭
		ioSession.setAttribute(ConstantUtils.LAST_RECIVED_TIME, System.currentTimeMillis());
		ioSession.setAttribute(ConstantUtils.SESSION_CLOSE_STATUS, false);// 初始化关闭状态为false
		ResConnectCreat.Builder builder = ResConnectCreat.newBuilder();
		builder.setNow(System.currentTimeMillis());
		builder.setHeartbeartTime(ConstantUtils.TCP_HEARBEAT_TIME);
		IoSender.sendMsg(ioSession, GameCode.RES_CONNECT_CREATE, builder);
	}

	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {
	}

	public void sessionOpened(IoSession ioSession) throws Exception {
	}

	public void inputClosed(IoSession arg0) throws Exception {
	}

}
