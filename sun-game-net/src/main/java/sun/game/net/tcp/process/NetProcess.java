package sun.game.net.tcp.process;


import org.apache.mina.core.session.IoSession;

import com.google.protobuf.GeneratedMessage;

import sun.game.common.obj.ObjectPool;
import sun.game.common.thread.QueueWork;
import sun.game.net.tcp.Message;

public abstract class NetProcess extends QueueWork {
	private static final long serialVersionUID = 7068568986176399152L;
	private Message message;

	public abstract void handle(IoSession session, com.google.protobuf.Message message) throws Exception;

	@Override
	public void init(Object... objs) throws Exception {
		this.message = (Message) objs[0];
	}

	public abstract GeneratedMessage.Builder<?> build();

	@Override
	public void run() {
		try {
			com.google.protobuf.Message msg = build().mergeFrom(message.getData()).build();
			if (message.getCode() != 999997) {// 999997为心跳
				logger.info(
						"\n----------------------------RECIEVED----------------------------\nFROM:{}\nCODE:{}[{}]\n{}\n"
								+ "-----------------------------------------------------------------\n\n\n\n\n\n",
						message.getIoSession(), message.getCode(), message.getCode(), msg);
			}
			handle(message.getIoSession(), msg);
			ObjectPool.getPool().back(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
