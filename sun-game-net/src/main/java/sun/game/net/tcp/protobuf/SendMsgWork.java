package sun.game.net.tcp.protobuf;

import org.apache.mina.core.session.IoSession;

import sun.game.common.obj.ObjectPool;
import sun.game.common.thread.QueueWork;
import sun.game.common.thread.WorkQueue;
import sun.game.common.utils.ConstantUtils;
import sun.game.net.tcp.Message;

public class SendMsgWork extends QueueWork {
	private static final long serialVersionUID = -7881954549606749237L;
	private int code;
	private IoSession ioSession;
	private byte[] data;

	@Override
	public WorkQueue getWorkQueue() {
		return new WorkQueue() {
			
			public long getId() {
				return ConstantUtils.MESSAGE_OUT_QUEUE;
			}
		};
	}

	public void init(Object... objs) throws Exception {
		this.code = (Integer) objs[0];
		this.ioSession = (IoSession) objs[1];
		this.data = (byte[]) objs[2];
	}

	public void run() {
		Message message = (Message) ObjectPool.getPool().borrow(Message.class);
		message.setCode(code);
		message.setData(data);
		message.setLength(8 + data.length);
		ioSession.write(message);
	}
}
