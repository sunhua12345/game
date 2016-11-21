package sun.game.common.rmi;

import java.rmi.AccessException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.game.common.annotation.INaming;
import sun.game.common.annotation.IRmi;
import sun.game.common.config.Config;
import sun.game.common.find.DynamicFind;

public class RMIManager extends DynamicFind {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final static Map<String, Remote> rmis = new ConcurrentHashMap<String, Remote>();
	private final static Map<String, Remote> namingRmis = new ConcurrentHashMap<String, Remote>();
	private Registry registry;

	private RMIManager() {
	}

	private static RMIManager rmiManager = new RMIManager();

	public static RMIManager getManager() {
		return rmiManager;
	}

	public boolean verification(Class<?> clazz) {
		return annotationOn(clazz, IRmi.class)
				|| annotationOn(clazz, INaming.class);
	}

	@Override
	public void findClass(Class<?> clazz) throws Exception {
		IRmi iRmi = clazz.getAnnotation(IRmi.class);
		if (iRmi == null) {
			INaming iNaming = clazz.getAnnotation(INaming.class);
			namingRmis.put(iNaming.naming(), (Remote) clazz.newInstance());
		} else {
			rmis.put(iRmi.SuperClassName(), (Remote) clazz.newInstance());
		}
	}

	@Override
	public void start() {
		super.start();
		String name = "";
		Remote remote = null;
		try {
			StaticRmiSocketFactory srs = new StaticRmiSocketFactory(Config
					.getConfig().WORLD_SERVER_RMI_IP,
					Config.getConfig().WORLD_SERVER_RMI_PORT);
			this.registry = LocateRegistry.createRegistry(
					Config.getConfig().WORLD_SERVER_RMI_PORT, srs, srs);
			for (Entry<String, Remote> entry : rmis.entrySet()) {
				name = entry.getKey();
				remote = entry.getValue();
				this.registry.bind(name, remote);
				logger.debug("RMI {},IP {},BIND RMI PORT:{}", name, Config
						.getConfig().WORLD_SERVER_RMI_PORT,
						Config.getConfig().WORLD_SERVER_RMI_PORT);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			// 端口连接不上
			logger.debug("{} 端口连接不上,被占用",
					Config.getConfig().WORLD_SERVER_RMI_PORT);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				this.registry.rebind(name, remote);
			} catch (AccessException e1) {
				e1.printStackTrace();
			} catch (RemoteException e1) {
				e1.printStackTrace();
				logger.debug("{} 端口连接不上,被占用",
						Config.getConfig().WORLD_SERVER_RMI_PORT);
				System.exit(0);
			}
		}
	}
}
