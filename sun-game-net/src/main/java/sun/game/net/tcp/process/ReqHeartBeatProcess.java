package sun.game.net.tcp.process;

import org.apache.mina.core.session.IoSession;

import com.google.protobuf.Message;

import sun.game.common.annotation.IProcess;
import sun.game.common.thread.WorkQueue;
import sun.game.common.utils.ConstantUtils;
import sun.game.net.tcp.IoSender;
import sun.game.protobuf.pb.MsgCode.GameCode;
import sun.game.protobuf.pb.MsgCode.ReqHeartBeat;
import sun.game.protobuf.pb.MsgCode.ResHeartBeat;

import com.google.protobuf.GeneratedMessage.Builder;

@IProcess(code = GameCode.REQ_HEART_BEAT_VALUE)
public class ReqHeartBeatProcess extends NetProcess
{
    private static final long serialVersionUID = 5190621395802681078L;

    @Override
    public Builder<?> build()
    {
        return ReqHeartBeat.newBuilder();
    }

    @Override
    public void handle(IoSession session, Message message) throws Exception
    {
    	ResHeartBeat.Builder builder = ResHeartBeat.newBuilder();
    	builder.setTime(System.currentTimeMillis());
    	IoSender.sendHeartBeatMsg(session, builder);
    }

    @Override
    public WorkQueue getWorkQueue()
    {
        return new WorkQueue() {
			
			@Override
			public long getId() {
				return ConstantUtils.REQ_HEARBEAT_QUEUE;
			}
		};
    }

}
