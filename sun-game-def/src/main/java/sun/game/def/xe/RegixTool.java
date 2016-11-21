package sun.game.def.xe;

public class RegixTool {
	public static enum Mark {
		/**
		 * 星号
		 */
		ASTERISK("*"),
		/**
		 * 点
		 */
		DOT("."),
		/**
		 * 下划线
		 */
		Line("_"),
		/**
		 * 逗号
		 */
		Comma(",|，"); 
		
		private String mark ;
		
		private Mark(String mark) {
			this.mark = mark ;
		}
		
		@Override
		public String toString() {
			return this.mark;
		}
	}
	
	public static String toRegixStr(String value) {
		return value.replace(".", "\\.").replace("*", ".*") ;
	}
}
