package sun.game.net.tcp.protobuf;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class MyProtocal implements ProtocolCodecFactory {

	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return msgDecoder;
	}

	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return msgEncoder;
	}

	private final ProtobufEncoder msgEncoder = new ProtobufEncoder();
	private final ProtoBufDecoder msgDecoder = new ProtoBufDecoder();
}
