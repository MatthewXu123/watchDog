
package watchDog.controller;

import watchDog.dao.SiteInfoDAO;

/**
 * Description:
 * @author Matthew Xu
 * @date May 13, 2020
 */
public interface BaseController {

	static final String CHAR_ENCODING_UTF8 = "UTF-8";
	
	static final SiteInfoDAO siteInfoDAO = SiteInfoDAO.INSTANCE;
}
