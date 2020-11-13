package watchDog.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;

public class FileTool {
	public static void save2File(String fileName,List<String> resultList)
	{
		try{
			Writer writer = new BufferedWriter(new OutputStreamWriter(
				    new FileOutputStream(fileName), "UTF-8"));
			for(String str: resultList)
			{
				writer.append(str+"\n");
			}
			writer.flush();
			writer.close();
		}
		catch(Exception ex)
		{
			
		}
	}
	public static void delFolder(String folderPath)
	{
		delFolder(folderPath,true);
	}
//  // 删除完文件后删除文件夹  
//  // param folderPath 文件夹完整绝对路径 
    public static void delFolder(String folderPath,boolean ignoreCreatedToday) {  
        try {  
            delAllFile(folderPath,ignoreCreatedToday); // 删除完里面所有内容  
            //不想删除文佳夹隐藏下面  
//          String filePath = folderPath;  
//          filePath = filePath.toString();  
//          java.io.File myFilePath = new java.io.File(filePath);  
//          myFilePath.delete(); // 删除空文件夹  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    // 删除指定文件夹下所有文件  
    // param path 文件夹完整绝对路径  
    public static boolean delAllFile(String path,boolean ignoreCreatedToday) {  
        boolean flag = false;  
        File file = new File(path);  
        if (!file.exists()) {  
            return flag;  
        }  
        if (!file.isDirectory()) {  
            return flag;  
        }  
        String[] tempList = file.list();  
        File temp = null;  
        for (int i = 0; i < tempList.length; i++) {  
            if (path.endsWith(File.separator)) {  
                temp = new File(path + tempList[i]);  
            } else {  
                temp = new File(path + File.separator + tempList[i]);  
            }  
            if (temp.isFile()) {
            	if(ignoreCreatedToday)
            	{
            		Date date = getCreateTime(temp);
            		if(date != null && !DateTool.isSameDay(date, new Date()))
            			temp.delete();
            	}
            	else
            		temp.delete();
            }  
            if (temp.isDirectory()) {  
                delAllFile(path + "/" + tempList[i],ignoreCreatedToday);// 先删除文件夹里面的文件  
//              delFolder(path + "/" + tempList[i]);// 再删除空文件夹  
                flag = true;  
            }  
        }  
        return flag;  
    }
    public static Date getCreateTime(File f){  
        String filePath = f.getAbsolutePath();  
        String prefix=filePath.substring(filePath.lastIndexOf(".")+1);
        String strTime = null;  
        try {  
            Process p = Runtime.getRuntime().exec("cmd /C dir "           
                    + filePath  
                    + "/tc" );  
            InputStream is = p.getInputStream();   
            BufferedReader br = new BufferedReader(new InputStreamReader(is));             
            String line;  
            while((line = br.readLine()) != null){  
                if(line.endsWith(prefix)){  
                    strTime = line.substring(0,17);  
                    break;  
                }                             
             }   
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        Date date = null;
        if(strTime != null)
        	date = DateTool.parse(strTime, "yyyy/MM/dd  hh:mm");
        return date;
        //输出：创建时间   2009-08-17  10:21  
    }
}  

