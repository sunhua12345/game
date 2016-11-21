package sun.game.def.xe;

import java.util.List;
import java.util.TreeMap;

public class ExcelMessage {
	/**
	 * sheet名字
	 */
	private String sheetName ;
	
	/**
	 * 行数
	 */
	private int rowNum ;
	
	/**
	 *	列数
	 */
	private int colNum ;
	
	/**
	 * 所有行的行数据
	 */
	private TreeMap<Integer, List<String>> rowData ;
	
	/**
	 * 所有列的列数据
	 * 
	* @param @return 
	* @return String    返回类型 
	* @throws
	 */
	
	private TreeMap<Integer, List<String>> colData ;

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public TreeMap<Integer, List<String>> getRowData() {
		return rowData;
	}

	public void setRowData(TreeMap<Integer, List<String>> rowData) {
		this.rowData = rowData;
	}

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public int getColNum() {
		return colNum;
	}

	public void setColNum(int colNum) {
		this.colNum = colNum;
	}

	public TreeMap<Integer, List<String>> getColData() {
		return colData;
	}

	public void setColData(TreeMap<Integer, List<String>> colData) {
		this.colData = colData;
	}
}
