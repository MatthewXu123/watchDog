package watchDog.database;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;


/**
 * @author lorisdacunto
 *
 */
public class Record
{
    private ArrayList<Object> record = new ArrayList<Object>();
    private Map<String, Object> columnNames = null;

    protected Record(ResultSet resultSet, Map<String, Object> columnNames) throws Exception
    {
        for (int i = 1; i <= columnNames.size(); i++)
        {
            record.add(resultSet.getObject(i));
        }

        this.columnNames = columnNames;
    }

    public Object get(int pos)
    {
        return record.get(pos);
    }

    public Object get(String columnName)
    {
        Object o = record.get(((Integer) columnNames.get(columnName)).intValue() -
            1);
        if(o instanceof BigDecimal)
        	o = ((BigDecimal)o).doubleValue();
        return o;
    }

    public boolean hasColumn(String columnName)
    {
        return columnNames.containsKey(columnName);
    }
}
