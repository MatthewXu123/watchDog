package watchDog.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serialyze {
	public static Object serialize(Object o,String name)
	{
		if(o != null)
		{
			try{
		    	ObjectOutputStream out = new ObjectOutputStream( new FileOutputStream(name)); 
		    	out.writeObject(o); 
		    	out.close(); 
	    	}
	    	catch(IOException ex)
	    	{
	    		System.out.println(ex);
	    	}
			return null;
		}
		else
		{
			Object result = null;
			try{
		    	ObjectInputStream in = new ObjectInputStream(new FileInputStream(name)); 
		    	result = in.readObject(); 
		    	in.close();
	    	}
	    	catch (ClassNotFoundException ex) {
	    		 System.out.println(ex);
			}
	    	catch(IOException ex)
	    	{
	    		System.out.println(ex);
	    	}
			return result;
		}
	}
}
