package watchDog.database;


//LORIS: Attenzione, non tutte le versioni di JVM implementano il costruttore DataBaseException(stringa, Throwable)
public class DataBaseException extends Exception
{
    public DataBaseException(String message, Exception e)
    {
        super(message, e);
    }

    public DataBaseException(String message)
    {
        super(message);
    }
}
