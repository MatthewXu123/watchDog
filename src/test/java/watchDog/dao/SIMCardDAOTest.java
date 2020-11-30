
package watchDog.dao;

import static org.junit.Assert.*;

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
	 * Test method for {@link watchDog.dao.SIMCardDAO#getOneById(int)}.
	 */
	@Test
	public void testGetOneById() {
		SIMCard simCard = simCardDAO.getOneById(2);
		assertTrue(simCard.getId() == 2);
	}

}
