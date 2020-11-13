package watchDog.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class RemoteCommandUtil {

    private static final Logger log = LoggerFactory.getLogger(RemoteCommandUtil.class);
    private static final String  DEFAULT_ENCODING = "UTF-8"; 

   /**
    * Description:
    * @param ip
    * @param port
    * @param userName
    * @param userPwd
    * @return
    * @author Matthew Xu
    * @date May 12, 2020
    */
    public static Connection login(String ip,Integer port, String userName,String userPwd){  
        boolean flag = false;
        Connection conn = null;
        try {  
        	if(port != null)
        		conn = new Connection(ip,port);  
        	conn = new Connection(ip);
            conn.connect();//连接  
            flag = conn.authenticateWithPassword(userName, userPwd);//认证  
            if(flag){
                log.info("=========Login OK=========" + conn);
                return conn;
            }
        } catch (IOException e) {  
            log.error("=========Login Failed=========" + e.getMessage());
            e.printStackTrace();  
        }  
        return conn;  
    }  

    /**
     * 
     * Description:
     * @param conn
     * @param cmd
     * @return
     * @author Matthew Xu
     * @date May 11, 2020
     */
    public static String execute(Connection conn,String cmd){  
        String result="";  
        try {  
            if(conn !=null){  
                Session session= conn.openSession(); 
                session.execCommand(cmd);
                result=processStdout(session.getStdout(),DEFAULT_ENCODING);  
                if(StringUtils.isBlank(result)){
                    log.info("得到标准输出为空,链接conn:"+conn+",执行的命令："+cmd);
                    result=processStdout(session.getStderr(),DEFAULT_ENCODING);  
                }else{
                    log.info("执行命令成功,链接conn:"+conn+",执行的命令："+cmd);
                }  
                conn.close();  
                session.close();  
                
                
            }  
        } catch (IOException e) {
            log.info("执行命令失败,链接conn:"+conn+",执行的命令："+cmd+"  "+e.getMessage());
            e.printStackTrace();  
        }  
        return result;  
    }
    /**
     * 
     * Description:
     * @param in
     * @param charset
     * @return
     * @author Matthew Xu
     * @date May 11, 2020
     */
     private static String processStdout(InputStream in, String charset){  
         InputStream  stdout = new StreamGobbler(in);  
         StringBuffer buffer = new StringBuffer();;  
         try {  
             BufferedReader br = new BufferedReader(new InputStreamReader(stdout,charset));  
             String line=null;  
             while((line=br.readLine()) != null){  
                 buffer.append(line+ ";" + "\n");  
             }  
         } catch (UnsupportedEncodingException e) { 
             log.error("解析脚本出错："+e.getMessage());
             e.printStackTrace();  
         } catch (IOException e) {
             log.error("解析脚本出错："+e.getMessage());
             e.printStackTrace();  
         }  
         return buffer.toString();  
     }  
     
}
