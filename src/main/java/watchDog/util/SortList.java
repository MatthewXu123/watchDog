package watchDog.util;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortList<E> {
    @SuppressWarnings("unchecked")
	public  void Sort(List<E> list, final String method, final String sort,final boolean isNum) {
        Collections.sort(list, new Comparator() {
            public int compare(Object a, Object b) {
                int ret = 0;
                try {
                    Method m1 = ((E) a).getClass().getMethod(method, null);
                    Method m2 = ((E) b).getClass().getMethod(method, null);
                    if(isNum == false)
                    {
	                    if (sort != null && "desc".equals(sort))// 倒序
	                        ret = m2.invoke(((E) b), null).toString()
	                                .compareTo(m1.invoke(((E) a), null).toString());
	                    else
	                        // 正序
	                        ret = m1.invoke(((E) a), null).toString()
	                                .compareTo(m2.invoke(((E) b), null).toString());
                    }
                    else
                    {
                    	double v2 = Double.valueOf(m2.invoke(((E) b), null).toString());
                    	double v1 = Double.valueOf(m1.invoke(((E) a), null).toString());
                    	if (sort != null && "desc".equals(sort))// 倒序
                    	{
                    		if(v2>v1)
                    			return 1;
                    		else if(v2 == v1)
                    			return 0;
                    		else
                    			return -1;
                    	}
                    	else
                    	{
                    		if(v1>v2)
                    			return 1;
                    		else if(v2 == v1)
                    			return 0;
                    		else
                    			return -1;
                    	}
                    }
                } catch (NoSuchMethodException ne) {
                    System.out.println(ne);
                } catch (IllegalAccessException ie) {
                    System.out.println(ie);
                } catch (InvocationTargetException it) {
                    System.out.println(it);
                }
                return ret;
            }
        });
    }
}