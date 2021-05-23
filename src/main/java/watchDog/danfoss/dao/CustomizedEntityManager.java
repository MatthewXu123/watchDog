
package watchDog.danfoss.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

/**
 * Description:
 * @author Matthew Xu
 * @date May 17, 2021
 */
public class CustomizedEntityManager {

	private static final Logger logger = Logger.getLogger(CustomizedEntityManager.class);
	
	private static CustomizedEntityManager INSTANCE = null;
	
	private static EntityManager em = null;
	
	private static EntityTransaction et = null;
	
	public static CustomizedEntityManager getInstance() {
		if(INSTANCE == null){
			INSTANCE = new CustomizedEntityManager(); 
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("danfoss");
			em = factory.createEntityManager();
			et = em.getTransaction();
		}
		return INSTANCE;
	}
	
	private CustomizedEntityManager(){
		
	}
	
	/**
	 * 
	 * Description:
	 * @param obj
	 * @return
	 * @author Matthew Xu
	 * @date May 18, 2021
	 */
	public boolean save(Object obj){
		try {
			et.begin();
			em.persist(obj);
			et.commit();
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * Description:
	 * @param objs
	 * @return
	 * @author Matthew Xu
	 * @date May 18, 2021
	 */
	public boolean batchSave(Collection<?> objs){
		try {
			et.begin();
			for (Object obj : objs) {
				em.persist(obj);
			}
			et.commit();
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * Description:
	 * @param obj
	 * @return
	 * @author Matthew Xu
	 * @date May 18, 2021
	 */
	public boolean update(Object obj){
		try {
			et.begin();
			em.merge(obj);
			et.commit();
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * Description:
	 * @param objs
	 * @return
	 * @author Matthew Xu
	 * @date May 18, 2021
	 */
	public boolean batchUpdate(Collection<?> objs){
		try {
			et.begin();
			for (Object obj : objs) {
				em.merge(obj);
			}
			et.commit();
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * Description:
	 * @param obj
	 * @return
	 * @author Matthew Xu
	 * @date May 18, 2021
	 */
	public boolean delete(Object obj){
		try {
			et.begin();
			em.remove(obj);
			et.commit();
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	} 
	
	/**
	 * 
	 * Description:
	 * @param clazz
	 * @param pk
	 * @return
	 * @author Matthew Xu
	 * @date May 18, 2021
	 */
	public <T> T find(Class<T> clazz, Object pk){
		T entity = null;
		try {
			et.begin();
			entity = em.find(clazz, pk);
			et.commit();
		} catch (Exception e) {
			logger.error("", e);
		}
		return entity;
	}
	
	/**
	 * 
	 * Description:
	 * @param sql
	 * @param clazz
	 * @return
	 * @author Matthew Xu
	 * @date May 18, 2021
	 */
	public <T> List<T> getQueryList(String sql, Class<T> clazz){
		List<T> list = new ArrayList<>();
		try {
			list = em.createQuery(sql, clazz).getResultList();
		} catch (Exception e) {
			logger.error("", e);
		}
		return list;
	}
	
	
	public <T> T getQuerySingle(String sql, Class<T> clazz){
		T t = null;
		try {
			t = em.createQuery(sql, clazz).getSingleResult();
		} catch (Exception e) {
			logger.error("", e);
		}
		return t;
	}
	
}
