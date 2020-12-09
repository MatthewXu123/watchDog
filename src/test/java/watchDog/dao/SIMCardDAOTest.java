
package watchDog.dao;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import watchDog.bean.register.SIMCard;
import watchDog.bean.register.SIMCardStatus;
import watchDog.bean.register.SIMCardType;

/**
 * Description:
 * @author Matthew Xu
 * @date Nov 30, 2020
 */
public class SIMCardDAOTest {

	private SIMCardDAO simCardDAO = SIMCardDAO.INSTANCE;
	
	/**
	 * Test method for {@link watchDog.dao.SIMCardDAO#saveOne(watchDog.bean.register.SIMCard)}.
	 */
	@Test
	public void testSaveOne() {
		SIMCard simCard = new SIMCard();
		simCard.setCardNumber("card_number2");
		simCard.setSimCardStatus(SIMCardStatus.getOneByCode(0));
		simCard.setSimCardType(SIMCardType.getOneByCode(0));
		simCardDAO.saveOne(simCard);
	}

	/**
	 * Test method for {@link watchDog.dao.SIMCardDAO#saveAll(watchDog.bean.register.SIMCard)}.
	 */
	@Test
	public void testSaveAll(){
		SIMCard simCard = new SIMCard();
		simCard.setCardNumber("card_number4");
		simCard.setSimCardStatus(SIMCardStatus.getOneByCode(0));
		simCard.setSimCardType(SIMCardType.getOneByCode(0));
		
		SIMCard simCard2 = new SIMCard();
		simCard2.setCardNumber("card_number5");
		simCard2.setSimCardStatus(SIMCardStatus.getOneByCode(0));
		simCard2.setSimCardType(SIMCardType.getOneByCode(0));
		
		List<SIMCard> list = Arrays.asList(simCard, simCard2);
		simCardDAO.saveAll(list);
	}
	
	/**
	 * Test method for {@link watchDog.dao.SIMCardDAO#getOneById(int)}.
	 */
	@Test
	public void testGetOneById() {
		SIMCard simCard = simCardDAO.getOneById(170);
		assertTrue(simCard.getId() == 170);
	}
	
	/**
	 * Test method for {@link watchDog.dao.SIMCardDAO#getOneByCardNumber(String)}.
	 */
	@Test
	public void testGetOneByCardNumber() {
		SIMCard simCard = simCardDAO.getOneByCardNumber(null);
		assertTrue(simCard.getCardNumber().equals("89860427102090710200"));
	}
	
	@Test
	public void testGetAllByStatus(){
		List<SIMCard> list = simCardDAO.getAllByStatus(0);
		assertTrue(list.size() == 0);
	}
	
	@Test
	public void testGetAll(){
		List<SIMCard> list = simCardDAO.getAll();
		assertTrue(list.size() > 0);
	}
	
	@Test
	public void testUpdateStatus(){
		simCardDAO.updateStatus(1, SIMCardStatus.DISABLED);
	}

}
