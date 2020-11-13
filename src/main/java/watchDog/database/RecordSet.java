package watchDog.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


public class RecordSet
{
    private List rows = new ArrayList();
    private Map columnNames = new HashMap();

    public RecordSet(ResultSet resultSet) throws Exception
    {
        if (null == resultSet)
        {
            throw new NoSuchElementException("ResultSet");
        }

        ResultSetMetaData meta = resultSet.getMetaData();

        for (int i = 1; i <= meta.getColumnCount(); i++)
        {
            columnNames.put(meta.getColumnName(i), new Integer(i));
        }

        Record record = null;

        while (resultSet.next())
        {
            record = new Record(resultSet, columnNames);
            rows.add(record);
        }

        resultSet.close();
    }

    public int size()
    {
        return rows.size();
    }

    public Record get(int pos)
    {
        return (Record) rows.get(pos);
    }

    public String[] getColumnNames()
    {
        int size = columnNames.size();
        String[] columns = new String[size];
        Iterator iterator = columnNames.keySet().iterator();
        String key = null;

        while (iterator.hasNext())
        {
            key = (String) iterator.next();
            columns[((Integer) columnNames.get(key)).intValue() - 1] = key;
        }

        //    for (int i = 1; i < iSize + 1; i++)
        //    sColumns[i] = (String)m_oColumnNames.get(new Integer(i));
        return columns;
    }
}
