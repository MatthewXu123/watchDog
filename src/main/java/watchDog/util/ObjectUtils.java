
package watchDog.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * @author Matthew Xu
 * @date May 12, 2020
 */
public class ObjectUtils {

	private ObjectUtils(){}
	
	/**
	 * 
	 * Description:
	 * @param collection
	 * @return
	 * @author Matthew Xu
	 * @date May 12, 2020
	 */
	public static <T> boolean isCollectionNotEmpty(Collection<T> collection) {
		return collection != null && collection.size() != 0;
	}
	
	/**
	 * 
	 * Description:
	 * @param collection
	 * @return
	 * @author Matthew Xu
	 * @date Jun 2, 2020
	 */
	public static <T> boolean isCollectionEmpty(Collection<T> collection) {
		return !isCollectionNotEmpty(collection);
	}
	
	/**
	 * 
	 * Description:
	 * @param array
	 * @return
	 * @author Matthew Xu
	 * @date May 12, 2020
	 */
	public static <T> boolean isArrayNotEmpty(T[] array) {
		return array != null && array.length != 0;
	}
	
	/**
	 * 
	 * Description:
	 * @param array
	 * @return
	 * @author Matthew Xu
	 * @param <V>
	 * @param <K>
	 * @date May 12, 2020
	 */
	public static <K, V> boolean isMapNotEmpty(Map<K, V> map) {
		return map != null && map.size() != 0;
	}
	
	/**
	 * 
	 * Description:
	 * @param map
	 * @return
	 * @author Matthew Xu
	 * @date Jun 2, 2020
	 */
	public static <K, V> boolean isMapEmpty(Map<K, V> map) {
		return !isMapNotEmpty(map);
	}
	
	/**
	 * 
	 * Description:
	 * @param one
	 * @param other
	 * @return
	 * @author Matthew Xu
	 * @date Jul 2, 2020
	 */
	public static <T> boolean isOneContainOther(List<T> one, List<T> other) {
		List<T> copiedOne = new ArrayList<>(one);
		List<T> copiedOther = new ArrayList<>(other);
		copiedOne.retainAll(copiedOther);
		return copiedOne.size() == copiedOther.size();
	}
}
