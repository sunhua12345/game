package sun.game.def.ztool.tool;

import sun.game.def.xe.DefClassManager;
import sun.game.def.xe.XETool;

public class ToolStart {

	public static String path = "sun.game.def";
	public static boolean hasType = false;// 是否加入标签

	public static void main(String[] args) throws Exception {
		DefClassManager.getManager().start();
		XETool.start(path, hasType);
	}
}
