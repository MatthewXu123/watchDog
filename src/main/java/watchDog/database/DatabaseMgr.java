package watchDog.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.postgresql.ds.PGPoolingDataSource;

public class DatabaseMgr {

	PGPoolingDataSource source = null;
	static DatabaseMgr me = null;
	
	public static DatabaseMgr getInstance()
	{
		if(me == null)
		{
			me = new DatabaseMgr();
		}
		return me;
	}
	
	Connection getConnection() throws SQLException
	{
		if(source == null)
		{
			source = new PGPoolingDataSource();  
			source.setDataSourceName("First Source");  
			source.setServerName("localhost");  
			source.setDatabaseName("remotevalue");
			source.setPortNumber(5432);
			source.setUser("postgres");  
			source.setPassword("postgres");  
			source.setMaxConnections(10);  
		}
		return source.getConnection();
	}
	boolean canGetConnection()
	{
		try{
			Connection c = getConnection();
			c.close();
			return true;
		}
		catch(Exception ex)
		{
			return false;
		}
	}
	public RecordSet executeQuery(String sql) throws DataBaseException
	{
		return executeQuery(sql,null);
	}
	public RecordSet executeQuery(String sql,Object[] params) throws DataBaseException
	{
		Connection c = null;
		PreparedStatement pstmt = null;
		try{
			c = getConnection();
			c.setAutoCommit(false);
			pstmt=c.prepareStatement(sql);
			if(params != null && params.length>0)
			{
				setParameter(pstmt,params);
			}
			ResultSet rs = pstmt.executeQuery();
			RecordSet recordSet = new RecordSet(rs);
			c.commit();
			c.close();
			return recordSet;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			try{
				if(pstmt != null)
					pstmt.close();
				if(c != null)
				{
					c.close();
				}
			}
			catch(Exception e){}
			throw new DataBaseException(ex.toString());
		}
	}
	
	public void executeUpdate(String sql) throws DataBaseException
	{
		executeUpdate(sql,null);
	}
	public void executeUpdate(String sql,Object[] params) throws DataBaseException
	{
		executeUpdate(sql,params,true);
	}
	public void executeUpdate(String sql,Object[] params,boolean printStackTrace) throws DataBaseException
	{
		Connection c = null;
		PreparedStatement pstmt = null;
		try{
			c = getConnection();
			c.setAutoCommit(false);
			pstmt= c.prepareStatement(sql);
			if(params != null && params.length>0)
			{
				setParameter(pstmt,params);
			}
			pstmt.execute();
			pstmt.close();
			c.commit();
			c.close();
		}
		catch(Exception ex)
		{
			if(printStackTrace)
				ex.printStackTrace();
			try{
				if(pstmt != null)
					pstmt.close();
				if(c != null)
				{
					c.close();
				}
			}
			catch(Exception e){}
			throw new DataBaseException(ex.toString());
		}
	}
	private void setParameter(PreparedStatement pstmt,Object[] params) throws SQLException
	{
		for(int i=0;i<params.length;i++)
		{
			Object o = params[i];
			if(o instanceof Integer)
				pstmt.setInt(i+1, (Integer)o);
			else if(o instanceof String)
				pstmt.setString(i+1, o.toString());
			else if(o instanceof Double)
				pstmt.setDouble(i+1, (Double)o);
			else if(o instanceof Float)
				pstmt.setFloat(i+1, (Float)o);
			else if(o instanceof Timestamp)
				pstmt.setTimestamp(i+1, (Timestamp)o);
			else if(o instanceof Date)
				pstmt.setDate(i+1, new java.sql.Date(((Date)o).getTime()));
			else if(o instanceof Long)
				pstmt.setLong(i+1, (Long)o);
			else if(o instanceof Boolean)
				pstmt.setBoolean(i+1, (Boolean)o);
			else if(o == null)
				pstmt.setObject(i+1, null);
		}
	}
	public void executeMulUpdate(String sql,List<Object[]> vals) throws DataBaseException
	{
		Connection c = null;
		PreparedStatement pstmt= null;
		try{
			c = getConnection();
			c.setAutoCommit(false);
			pstmt= c.prepareStatement(sql);
			for(Object[] params:vals)
			{
				if(params != null && params.length>0)
				{
					setParameter(pstmt,params);
					pstmt.execute();
				}
			}
			c.commit();
			pstmt.close();
			c.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			try{
				if(pstmt != null)
					pstmt.close();
				if(c != null)
				{
					c.close();
				}
			}
			catch(Exception e){}
			throw new DataBaseException(ex.toString());
		}
	}
}
