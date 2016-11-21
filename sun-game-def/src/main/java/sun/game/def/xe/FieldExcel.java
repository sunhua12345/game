package sun.game.def.xe;

public class FieldExcel {
	/**
	 * 权限
	 */
	private String right ;
	
	/**
	 * 字段类型
	 */
	private String type ;
	
	/**
	 * 字段描述
	 */
	
	private String desc ;
	
	/**
	 * 字段名
	 */
	private String name ;
	
	/**
	 * 字段值
	 */
	private String value ;

	public String getRight() {
		return right;
	}

	public void setRight(String right) {
		this.right = right;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
