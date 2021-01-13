package watchDog.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import watchDog.bean.result.RestResult;
import watchDog.bean.result.ResultCode;
import watchDog.bean.result.ResultFactory;
import watchDog.service.RetailProjectService;
import watchDog.util.HttpServletUtil;

@WebServlet(urlPatterns = { "/upload/retail", "/upload/traffic" })
public class UploadController extends HttpServlet implements BaseController {
	private static final long serialVersionUID = 1L;
	public static final String UPLOAD_DIRECTORY = "C:\\watchDog\\files";
	public static final String PATH_RETAIL = "\\retail";
	public static final String PATH_TRAFFIC = "\\traffic";
	private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3; // 3MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

	private static final Logger LOGGER = Logger.getLogger(UploadController.class);

	private RetailProjectService retailProjectService = RetailProjectService.INSTANCE;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

	private void retail(HttpServletRequest req, HttpServletResponse resp) {
		try {
			RestResult restResult = upload(req, resp, PATH_RETAIL, true);
			if (restResult.getStatus() == ResultCode.SUCCESS.getStatus()) {
				retailProjectService.saveAllFromExcel();
				resp.sendRedirect("/watchDog/retail/view");
			} else {
				BaseController.returnFailure(resp, restResult.getMsg());
			}
		} catch (IOException e) {
			LOGGER.error("", e);
		}
	}
	
	private void traffic(HttpServletRequest req, HttpServletResponse resp){
		try {
			RestResult restResult = upload(req, resp, PATH_TRAFFIC, true);
			if (restResult.getStatus() == ResultCode.SUCCESS.getStatus()) {
				req.getRequestDispatcher("/file/trafficExport").forward(req, resp);
			}
			
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	/**
	 * Description:
	 * @param request
	 * @param response
	 * @param path
	 * @param isCover
	 * @return
	 * @author Matthew Xu
	 * @date Jan 12, 2021
	 */
	private RestResult upload(HttpServletRequest request, HttpServletResponse response, String path, boolean isCover) {
		if (!ServletFileUpload.isMultipartContent(request))
			return ResultFactory.getFailResult("文件格式不对");
		// 配置上传参数
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
		factory.setSizeThreshold(MEMORY_THRESHOLD);
		// 设置临时存储目录
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setFileSizeMax(MAX_FILE_SIZE);
		upload.setSizeMax(MAX_REQUEST_SIZE);
		upload.setHeaderEncoding("UTF-8");
		String uploadPath = UPLOAD_DIRECTORY;
		if (StringUtils.isNotBlank(path))
			uploadPath += path;
		File uploadDir = new File(uploadPath);
		if (isCover)
			deleteDir(uploadDir);

		if (!uploadDir.exists())
			uploadDir.mkdir();
		try {
			List<FileItem> list = upload.parseRequest(request);
			if (list != null && list.size() > 0) {
				for (FileItem item : list) {
					if (!item.isFormField()) {
						String fileName = new File(item.getName()).getName();
						File storeFile = new File(uploadPath, fileName);
						// 保存文件到硬盘
						item.write(storeFile);
					}
				}
			}

		} catch (Exception ex) {
			return ResultFactory.getFailResult("文件上传失败");
		}
		return ResultFactory.getSuccessResult();
	}

	private boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}
}