
package watchDog.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import watchDog.service.DataTrafficService;
import watchDog.service.FileSevice;
import watchDog.util.CSVUtils;
import watchDog.util.DateTool;
import watchDog.util.HttpServletUtil;
import watchDog.util.ZipCompressor;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date Apr 1, 2020
 */
@WebServlet(urlPatterns = { "/file/siteExport", "/file/memberExport","/file/logExport","/file/trafficExport" })
public class FileController extends HttpServlet implements BaseController{
    private static final Logger logger = Logger.getLogger(FileController.class);
	private static final long serialVersionUID = 1L;
	
	private static final FileSevice fileService = FileSevice.INSTANCE;
	
	private DataTrafficService dataTrafficService = DataTrafficService.INSTANCE;

	public static final String[] SITE_HEADERS = new String[] { "description", "manDescription", "ktype", "ip",
			"lastSynch", "deadline", "checkNetwork", "channel", "tagId", "tagId2", "tagId3", "comment" };
	
	public static final String[] MEMBER_HEADERS = new String[] { "厂商", "客户", "门店", "士兵", "军官","链接" ,"微信服务到期"};
	
	public static final String[] TRAFFIC_HEADERS = new String[] { "卡号", "项目", "ip","工程商", "客户", "流量","服务到期时间","备注"};

	public static final String FIELNAME_SITES = "siteinfo";
	
	public static final String FILENAME_MEMBER = "member";
	
	public static final String FILENAME_TRAFFIC = "traffic";
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		if (!"success".equals(session.getAttribute("LoginStatus"))) {
			resp.sendRedirect("../login.jsp");
			return;
		}
		Method method = HttpServletUtil.INSTANCE.getMethod(req, resp, this);
		try {
			method.invoke(this, req, resp);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	private void siteExport(HttpServletRequest req, HttpServletResponse resp) {
		// 导出文件路径
		String downloadFilePath = req.getSession().getServletContext().getRealPath("");
		// 导出CSV文件
		File csvFile = CSVUtils.createCSVFile(Arrays.asList(SITE_HEADERS), fileService.getSiteDataList(SITE_HEADERS),
				downloadFilePath, FIELNAME_SITES);
		csvExport(req, resp, csvFile);
	}
	
	private void memberExport(HttpServletRequest req, HttpServletResponse resp){
		String downloadFilePath = req.getSession().getServletContext().getRealPath("");
		// 导出CSV文件
		File csvFile = CSVUtils.createCSVFile(Arrays.asList(MEMBER_HEADERS), fileService.getSiteMemberList(),
				downloadFilePath, FILENAME_MEMBER);
		csvExport(req, resp, csvFile);
	}
	
	private void trafficExport(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		try {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("UTF-8");
			List<FileItem> list = upload.parseRequest(req);
			File file = null;
			if (list != null && list.size() > 0) {
				FileItem fileItem = list.get(0);
				file = new File(fileItem.getName());
				fileItem.write(file);
			}
			String downloadFilePath = req.getSession().getServletContext().getRealPath("");
			// 导出CSV文件
			File csvFile = CSVUtils.createCSVFile(Arrays.asList(TRAFFIC_HEADERS), dataTrafficService.getDataTraffic(file),
					downloadFilePath, FILENAME_TRAFFIC);
			csvExport(req, resp, csvFile);
		} catch (Exception e) {
		    logger.error("",e);
			resp.sendRedirect("/watchDog/retail/view");
		}
	}
	
	private void logExport(HttpServletRequest req, HttpServletResponse resp){
		try {
			String realPath=req.getSession().getServletContext().getRealPath("download");
			File tmpFile = new File(realPath);
			if(!tmpFile.exists())
				tmpFile.mkdir();
			String targetFileName = realPath+File.separator+System.currentTimeMillis()+".zip";
			
			List<String> fileList = new ArrayList<>();
            String base = "C:\\watchDog\\log\\";
            fileList.add(base+"log.log");
            Date tmp = new Date();
            for(int i=0;i<20;i++)
            {
                tmp = DateTool.addDays(tmp,-1);
                String dateStr = DateTool.format(tmp,"yyyy-MM-dd");
                fileList.add(base+"log.log."+dateStr);
            }
            String[] files = fileList.toArray(new String[fileList.size()]);
            ZipCompressor zip = new ZipCompressor(targetFileName);
            zip.compress(files);

			File file = new File(targetFileName); // 要下载的文件绝对路径
			InputStream ins;

			ins = new BufferedInputStream(new FileInputStream(file));

			byte[] buffer = new byte[ins.available()];
			ins.read(buffer);
			ins.close();

			resp.reset();
			resp.addHeader("Content-Disposition", "attachment;filename=" + new String(file.getName().getBytes()));
			resp.addHeader("Content-Length", "" + file.length());
			OutputStream ous = new BufferedOutputStream(resp.getOutputStream());
			resp.setContentType("application/octet-stream");
			ous.write(buffer);
			ous.flush();
			ous.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void csvExport(HttpServletRequest req, HttpServletResponse resp, File csvFile) {
		try {
			// 取得文件名。
			String filename = csvFile.getName();
			// 以流的形式下载文件。
			FileInputStream fis = new FileInputStream(csvFile);
			// 设置response的Header
			String userAgent = req.getHeader("User-Agent");
			// // 针对IE或者以IE为内核的浏览器：
			if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
				filename = java.net.URLEncoder.encode(filename, "UTF-8");
			} else {
				// 非IE浏览器的处理：
				filename = new String(filename.getBytes("UTF-8"), "ISO-8859-1");
			}
			resp.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", filename));
			resp.setContentType("multipart/form-data");
			resp.setCharacterEncoding("UTF-8");

			OutputStream toClient = new BufferedOutputStream(resp.getOutputStream());
			resp.setContentType("application/octet-stream");

			int content = 0;
			while ((content = fis.read()) != -1) {
				toClient.write(content);
			}
			fis.close();
			toClient.flush();
			toClient.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}