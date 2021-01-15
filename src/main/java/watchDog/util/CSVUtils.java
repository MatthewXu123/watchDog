
package watchDog.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import watchDog.controller.FileController;
import watchDog.controller.UploadController;
import watchDog.service.FileSevice;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date Apr 1, 2020
 */
public class CSVUtils {

	private static final String CSV_DELIMITER = ",";

	public static List<String[]> readCsv(File file, char separator) throws IOException{
        return new CSVReaderBuilder(Files.newBufferedReader(file.toPath(),StandardCharsets.UTF_8))
        		.withCSVParser(new CSVParserBuilder().withQuoteChar(CSVWriter.DEFAULT_QUOTE_CHARACTER).withSeparator(separator).build()).build().readAll();
	}

	/**
	 * CSV文件生成方法
	 * 
	 * @param head
	 * @param dataList
	 * @param outPutPath
	 * @param filename
	 * @return
	 */
	public static File createCSVFile(List<Object> headList, List<List<Object>> dataList, String outPutPath,
			String filename) {
		File csvFile = null;
		BufferedWriter csvWtriter = null;
		try {
			csvFile = new File(outPutPath + File.separator + filename + ".csv");
			File parent = csvFile.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			csvFile.createNewFile();

			csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "GBK"), 1024);
			// 写入文件头部
			writeRow(headList, csvWtriter);

			// 写入文件内容
			for (List<Object> row : dataList) {
				writeRow(row, csvWtriter);
			}
			csvWtriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				csvWtriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return csvFile;
	}

	/**
	 * 
	 * Description:
	 * 
	 * @param row
	 * @param csvWriter
	 * @throws IOException
	 * @author Matthew Xu
	 * @date Apr 1, 2020
	 */
	protected static void writeRow(List<Object> row, BufferedWriter csvWriter) throws IOException {
		// 写入文件头部
		for (Object data : row) {
			StringBuffer buf = new StringBuffer();
			String rowStr = buf.append(data).append(CSV_DELIMITER).toString();
			csvWriter.write(rowStr);
		}
		csvWriter.newLine();
	}

	/**
	 * Description:
	 * 
	 * @param fieldName
	 * @param object
	 * @return
	 * @author Matthew Xu
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @date Apr 1, 2020
	 */
	public static Object getFieldValueByFieldName(String fieldName, Object object) throws NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String firstLetter = fieldName.substring(0, 1).toUpperCase();
		String getter = "get" + firstLetter + fieldName.substring(1);
		Method method = object.getClass().getMethod(getter, new Class[] {});
		Object value = method.invoke(object, new Object[] {});
		return value;
	}

}