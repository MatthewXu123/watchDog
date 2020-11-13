package watchDog.util;


public class RunBat {
	private static ScriptInvoker script = new ScriptInvoker();
	public static void run(String batName, String resultName)
    {
        try
        {
            script.execute(new String[] { batName }, resultName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
