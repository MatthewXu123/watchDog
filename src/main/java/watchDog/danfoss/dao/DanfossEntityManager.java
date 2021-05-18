
package watchDog.danfoss.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;

import watchDog.danfoss.model.DanfossDevice;

/**
 * Description:
 * @author Matthew Xu
 * @date May 17, 2021
 */
public class DanfossEntityManager {

	private static final Logger logger = Logger.getLogger(DanfossEntityManager.class);
	
	private static DanfossEntityManager INSTANCE = null;
	
	private static EntityManager em = null;
	
	private static EntityTransaction et = null;
	
	public static DanfossEntityManager getInstance() {
		if(INSTANCE == null){
			INSTANCE = new DanfossEntityManager(); 
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("danfoss");
			em = factory.createEntityManager();
			et = em.getTransaction();
			et.begin();
		}
		return INSTANCE;
	}
	
	private DanfossEntityManager(){
		
	}
	
	public boolean save(Object obj){
		try {
			em.persist(obj);
			et.commit();
			em.flush();
			em.clear();
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	}
	
	public boolean batchSave(Collection<?> objs){
		try {
			for (Object obj : objs) {
				em.persist(obj);
			}
			et.commit();
			em.flush();
			em.clear();
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	}
	
	public boolean update(Object obj){
		try {
			em.merge(obj);
			et.commit();
			em.flush();
			em.clear();
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	}
	
	public boolean batchUpdate(Collection<?> objs){
		try {
			for (Object obj : objs) {
				em.merge(obj);
			}
			et.commit();
			em.flush();
			em.clear();
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	}
	
	public boolean delete(Object obj){
		try {
			em.remove(obj);
			et.commit();
			em.flush();
			em.clear();
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	} 
	
	public <T> T find(Class<T> clazz, Object pk){
		T entity = null;
		try {
			entity = em.find(clazz, pk);
			et.commit();
			em.flush();
			em.clear();
		} catch (Exception e) {
			logger.error("", e);
		}
		return entity;
	}
	
}
