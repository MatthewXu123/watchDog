package watchDog.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;


public class StreamGobbler extends Thread
{
    private InputStream is = null;
    private OutputStream os = null;
    private Closer closer = null;

    public StreamGobbler(InputStream is, OutputStream redirect, Closer closer)
    {
    	setName("StreamGobbler");
        this.is = is;
        this.os = redirect;
        this.closer = closer;
    }

    public void run()
    {
        try
        {
            PrintWriter pw = null;

            if (os != null)
            {
                pw = closer.open(os);
            }

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;

            while ((line = br.readLine()) != null)
            {
                if (pw != null)
                {
                    pw.println(line);
                }
            }

            if (pw != null)
            {
                pw.flush();
            }

            closer.close(pw);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}
