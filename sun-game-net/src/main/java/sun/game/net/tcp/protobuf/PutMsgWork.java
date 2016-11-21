package sun.game.net.tcp.protobuf;

import sun.game.common.thread.QueueWork;
import sun.game.common.thread.Work;
import sun.game.common.thread.WorkManager;
import sun.game.common.thread.WorkQueue;
import sun.game.common.utils.ConstantUtils;
import sun.game.net.tcp.IoSender;
import sun.game.net.tcp.Message;
import sun.game.net.tcp.process.ProcessManager;
import sun.game.protobuf.pb.MsgCode.AlertType;

public class PutMsgWork extends QueueWork {
	private static final long serialVersionUID = 4232780310529607817L;
	private Message message;

	@Override
	public WorkQueue getWorkQueue() {
		return new WorkQueue() {
			
			public long getId() {
				return ConstantUtils.MESSAGE_IN_QUEUE;
			}
		};
	}

	public void init(Object... objs) throws Exception {
		this.message = (Message) objs[0];
	}

	@SuppressWarnings("unchecked")
	public void run() {
		Class<? extends Work> clazz = (Class<? extends Work>) ProcessManager
				.getManager().getProcess(message.getCode());
		if (clazz != null) {
			WorkManager.getManager().submit(clazz, message);
		} else {
			// 没有该处理类
			IoSender.sendAlert(message.getIoSession(),
					AlertType.CAN_NOT_FIND_HANDLE);
		}
	}

}
