package watchDog.util;

import java.io.File;
import java.io.FilenameFilter;

public class FileTypeFilter implements FilenameFilter {

	private String type;

	public FileTypeFilter(String tp) {
		this.type = tp;
	}

	public boolean accept(File fl, String path) {
		File file = new File(path);
		String filename = file.getName();
		return filename.endsWith(type);
	}

}