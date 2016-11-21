package sun.game.net.tcp.work;

import org.apache.mina.core.session.IoSession;

import sun.game.common.obj.ObjectPool;
import sun.game.common.thread.QueueWork;
import sun.game.common.thread.WorkQueue;
import sun.game.common.utils.ConstantUtils;
import sun.game.net.tcp.Message;

public class SendHearBeatWork extends QueueWork{
	private static final long serialVersionUID = -4800753687213006267L;
	private int code;
	private IoSession ioSession;
	private byte[] data;
	@Override
	public void init(Object... objs) throws Exception {
		this.code = (Integer) objs[0];
		this.ioSession = (IoSession) objs[1];
		this.data = (byte[]) objs[2];
	}

	@Override
	public void run() {
		Message message = (Message) ObjectPool.getPool().borrow(Message.class);
		message.setCode(code);
		message.setData(data);
		message.setLength(8 + data.length);
		ioSession.write(message);
	}

	@Override
	public WorkQueue getWorkQueue() {
		return new WorkQueue() {
			
			@Override
			public long getId() {
				return ConstantUtils.RES_HEARTBEAT_QUEUE;
			}
		};
	}

}
