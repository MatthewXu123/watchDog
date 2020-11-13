/**
 *
 */
package watchDog.util;

import java.io.OutputStream;
import java.io.PrintWriter;


class Closer
{
    private boolean last = false;
    private PrintWriter printWriter = null;

    public synchronized PrintWriter open(OutputStream outputStream)
    {
        if (null == printWriter)
        {
            printWriter = new PrintWriter(outputStream);
        }

        return printWriter;
    }

    public synchronized void close(PrintWriter printWriter)
    {
        if (last)
        {
            printWriter.close();
        }
        else
        {
            last = true;
        }
    }
}
