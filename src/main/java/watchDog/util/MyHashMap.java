package watchDog.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class MyHashMap<K,V> {
	Vector<K> vector = null;
	Map<K,V> map = null;
	int capacity = 100;
	
	public MyHashMap(int capacity)
	{
		vector = new Vector<K>();
		map = new HashMap<K,V>();
		this.capacity = capacity;
	}
	
	public V get(K k)
	{
		return map.get(k);
	}
	public void put(K k,V v)
	{
		vector.addElement(k);
		map.put(k, v);
		if(vector.size()>=capacity)
		{
			K first = vector.firstElement();
			vector.remove(first);
			map.remove(first);
		}
	}
}
