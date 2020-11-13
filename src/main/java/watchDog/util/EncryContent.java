package watchDog.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EncryContent {
    public static String getContent()
    {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        return "date is "+format.format(new Date());
    }
}
