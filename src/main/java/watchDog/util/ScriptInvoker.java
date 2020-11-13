package watchDog.util;

import java.io.File;
import java.io.FileOutputStream;


public class ScriptInvoker
{
    private String strCurrentDirectory = "";
    
    public void setCurrentDirectory(String strDirectory)
    {
    	strCurrentDirectory = strDirectory;
    }
	
	public int execute(String[] batch, String outputFile, boolean wait)
        throws Exception
    {
        FileOutputStream fos = new FileOutputStream(outputFile, true);
        Runtime rt = Runtime.getRuntime();
        Process proc = strCurrentDirectory.isEmpty() ? rt.exec(batch) : rt.exec(batch, null, new File(strCurrentDirectory + File.separator));
        Closer closer = new Closer();
        StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), fos, closer);

        StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), fos, closer);

        errorGobbler.start();
        outputGobbler.start();

        int exitVal = 0;
        if (wait)
        {
        	exitVal = proc.waitFor();
        }

        return exitVal;
    }

    public int execute(String[] batch, String outputFile)
    throws Exception
    {
    	return execute(batch, outputFile, true);
    }
    
    public static void main(String[] argv) throws Exception
    {
        ScriptInvoker s = new ScriptInvoker();
        s.execute(new String[] { "c:\\vacuum.bat", "HSVARIABLE" }, "C:\\RESULT.TXT");
        s.execute(new String[] { "c:\\vacuum.bat", "BUFFER" }, "C:\\RESULT.TXT");
        s.execute(new String[] { "c:\\vacuum.bat", "HSALARM" }, "C:\\RESULT.TXT");
        System.exit(10);
    }
}
