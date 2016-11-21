package sun.game;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.event.ContextRefreshedEvent;

import sun.game.common.thread.WorkManager;
import sun.game.world.ServerStart;

@SpringBootApplication
@ImportResource(locations={"classpath:context-common.xml"})
public class WorldGameServer implements ApplicationListener<ContextRefreshedEvent>{
	public static void main(String[] args) {
		SpringApplication.run(WorldGameServer.class, args);
	}


	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		WorkManager.getManager().submit(ServerStart.class);
	}
}
