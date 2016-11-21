package sun.game.def.xe;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.DataFormatter;


public class ExcelTool {

	public static String downpath = System.getProperty("user.dir") + "/def/excel/" == null ? "F:/workspace/maveneclipse/cs_resource/src/main/webapp/excel/"
			: System.getProperty("user.dir") + "/def/excel/";

	/**
	 * @throws Exception
	 *             将指定数据转换为EXCEL
	 * 
	 * @param treeMap
	 * @return void 返回类型
	 * @throws
	 */
	public static void converToExcel(TreeMap<String, List<MyField>> treeMap) throws Exception {

		for (Entry<String, List<MyField>> entry : treeMap.entrySet()) {
			String name = entry.getKey();

			String sheetName_clazzName[] = name.split("=");

			// 创建Excel文档
			HSSFWorkbook hwb = new HSSFWorkbook();
			String outputFile = downpath + sheetName_clazzName[0] + ".xls";

			HSSFCellStyle cellStyle = hwb.createCellStyle();
			cellStyle.setFillBackgroundColor(HSSFColor.TEAL.index);
			cellStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
			// 前景颜色
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			// 填充方式，前色填充
			HSSFCellStyle cellStyleSec = hwb.createCellStyle();
			cellStyleSec.setFillBackgroundColor(HSSFColor.BLUE.index);
			cellStyleSec.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			// 前景颜色
			cellStyleSec.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

			List<MyField> fileds = entry.getValue();

			// sheet 对应一个工作页
			HSSFSheet sheet = hwb.createSheet(sheetName_clazzName[0]);
			// 类名
			HSSFRow serverClass = sheet.createRow(0);
			HSSFCell cell = serverClass.createCell(0);
			cell.setCellValue(sheetName_clazzName[1]);
			HSSFRow serverrow = sheet.createRow(1);
			HSSFRow typerow = sheet.createRow(2);
			HSSFRow firstrow = sheet.createRow(3);
			// 下标为0的行开始
			HSSFRow secondrow = sheet.createRow(4);
			// 下标为1的行开始
			for (int i = 0; i < fileds.size(); i++) {

				MyField filed = fileds.get(i);
				String desc = filed.getDescrible();
				HSSFCell hssfCellDesc = firstrow.createCell(i);
				hssfCellDesc.setCellStyle(cellStyle);
				hssfCellDesc.setCellValue(desc);
				String fieldName = filed.getName();
				HSSFCell hssfCellName = secondrow.createCell(i);
				hssfCellName.setCellStyle(cellStyleSec);
				int num = desc.getBytes().length > fieldName.getBytes().length ? desc.getBytes().length : fieldName
						.getBytes().length;
				sheet.setColumnWidth(i, num * 255);
				hssfCellName.setCellValue(fieldName);
				HSSFCell hssfCellType = typerow.createCell(i);
				hssfCellType.setCellStyle(cellStyleSec);
				Class<?> clz = filed.getClazz();
				if (clz.isArray()) {
					clz = ClassTool.getArrayType(clz);
				}
				String type = clz.getName();
				if (type.contains(".")) {
					type = type.substring(type.lastIndexOf(".") + 1, type.length());
				}
				hssfCellType.setCellValue(type);
				HSSFCell hssfCellServer = serverrow.createCell(i);
				hssfCellServer.setCellValue("s");
			}

			FileOutputStream outputStream = new FileOutputStream(outputFile);

			hwb.write(outputStream);
			outputStream.flush();
			outputStream.close();
		}
	}

	/**
	 * 根据HSSFCell类型设置数据
	 * 
	 * @param cell
	 * @return
	 */
	private static String getCellFormatValue(HSSFCell cell) {
		DataFormatter FORMATTER = new DataFormatter();
		return FORMATTER.formatCellValue(cell);
	}

	/**
	 * 读取Excel所有数据内容
	 * 
	 * @param InputStream
	 * @return Map 包含单元格数据内容的Map对象
	 */
	@SuppressWarnings("deprecation")
	public static TreeMap<Integer, List<String>> readExcelContent(InputStream is) {
		TreeMap<Integer, List<String>> content = new TreeMap<Integer, List<String>>();
		try {
			POIFSFileSystem fs = new POIFSFileSystem(is);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
			// 得到总行数
			int rowNum = sheet.getLastRowNum();
			HSSFRow row = sheet.getRow(0);
			int colNum = row.getPhysicalNumberOfCells();

			// 正文内容应该从第二行开始,第一行为表头的标题
			for (int i = 0; i <= rowNum; i++) {
				row = sheet.getRow(i);
				List<String> list = new ArrayList<String>();
				int j = 0;
				while (j < colNum) {
					list.add(getCellFormatValue(row.getCell((short) j)).trim());
					j++;
				}
				content.put(i, list);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return content;
	}

	/**
	 * 读取Excel权限数据内容(按行查询)
	 * 
	 * @param InputStream
	 * @param right
	 *            权限
	 * @return Map 包含单元格数据内容的Map对象
	 */
	@SuppressWarnings("deprecation")
	public static ExcelMessage readRightRowExcelContent(InputStream is, String right, int rowNumber) {
		ExcelMessage excelMessage = new ExcelMessage();

		try {
			TreeMap<Integer, List<String>> content = new TreeMap<Integer, List<String>>();
			POIFSFileSystem fs = new POIFSFileSystem(is);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
			// 得到总行数
			int rowNum = sheet.getLastRowNum();
			HSSFRow row = sheet.getRow(0);
			int colNum = row.getPhysicalNumberOfCells();
			excelMessage.setRowNum(rowNum);
			excelMessage.setColNum(colNum);
			Set<Integer> rowNumRightSet = new HashSet<Integer>();

			for (int i = 0; i <= colNum; i++) {
				if (getCellFormatValue(row.getCell(i)).contains(right)) {
					rowNumRightSet.add(i);
				}
			}

			// 正文内容应该从第二行开始,第一行为表头的标题
			for (int i = rowNumber; i <= rowNum; i++) {
				row = sheet.getRow(i);
				List<String> list = new ArrayList<String>();
				int j = 0;
				while (j < colNum) {
					if (rowNumRightSet.contains(j)) {
						String cellValue = getCellFormatValue(row.getCell((short) j)).trim();
						list.add(cellValue);
					}
					j++;
				}
				content.put(i, list);
			}

			excelMessage.setSheetName(sheet.getSheetName());
			excelMessage.setRowData(content);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return excelMessage;
	}

	/**
	 * 读取Excel权限数据内容(按列查询)
	 * 
	 * @param InputStream
	 * @param right
	 *            权限
	 * @param rowNumber
	 *            (从第一行开始查起,rolNumber=0表示为第一行,以此类推)要查到的行数
	 * @return Map 包含单元格数据内容的Map对象
	 */
	@SuppressWarnings("deprecation")
	public static ExcelMessage readRightColExcelContent(InputStream is, String right, int rowNumber) {
		ExcelMessage excelMessage = new ExcelMessage();

		try {
			TreeMap<Integer, List<String>> content = new TreeMap<Integer, List<String>>();
			POIFSFileSystem fs = new POIFSFileSystem(is);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
			// 得到总行数
			// int rowNum = sheet.getLastRowNum();
			int rowNum = rowNumber;
			HSSFRow row = sheet.getRow(0);
			int colNum = row.getPhysicalNumberOfCells();
			excelMessage.setRowNum(rowNum);
			excelMessage.setColNum(colNum);
			Set<Integer> rowNumRightSet = new HashSet<Integer>();

			for (int i = 0; i <= colNum; i++) {
				if (getCellFormatValue(row.getCell(i)).contains(right)) {
					rowNumRightSet.add(i);
				}
			}

			int j = 0;
			while (j < colNum) {
				if (rowNumRightSet.contains(j)) {
					List<String> colList = new ArrayList<String>();
					for (int i = 0; i <= rowNum; i++) {
						row = sheet.getRow(i);
						colList.add(getCellFormatValue(row.getCell((short) j)).trim());
					}

					content.put(j, colList);
				}
				j++;
			}

			excelMessage.setSheetName(sheet.getSheetName());
			excelMessage.setColData(content);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return excelMessage;
	}

	/**
	 * 获得需要转成具体xml的信息
	 * 
	 * @param @param messsagesTitle 标题以及相关字段描述信息
	 * @param @param messsagesContent 配置具体内容
	 * @return List<FieldExcel> 返回需要转换成xml的信息
	 * @throws
	 */
	public static List<List<FieldExcel>> getFieldExcel(ExcelMessage messsagesTitle, ExcelMessage messsagesContent) {
		// 标题
		List<List<String>> excelTitleData = new ArrayList<List<String>>(messsagesTitle.getColData().values());
		// 内容
		List<List<String>> excelContentData = new ArrayList<List<String>>(messsagesContent.getRowData().values());

		// 返回xml需要信息
		List<List<FieldExcel>> resultColl = new ArrayList<List<FieldExcel>>();
		for (int i = 0; i < excelContentData.size(); i++) {
			List<String> listContent = excelContentData.get(i);
			List<FieldExcel> excels = new ArrayList<FieldExcel>();
			for (int j = 0; j < listContent.size(); j++) {
				List<String> list = excelTitleData.get(j);
				String value = listContent.get(j);
				FieldExcel excel = new FieldExcel();
				excel.setRight(list.get(0));
				excel.setType(list.get(1));
				excel.setDesc(list.get(2));
				excel.setName(list.get(3));
				excel.setValue(value);
				excels.add(excel);
			}
			resultColl.add(excels);
		}
		return resultColl;
	}
}
