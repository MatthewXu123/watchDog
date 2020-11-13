
package watchDog.config.property;

import org.junit.Test;

import watchDog.dao.FaxInfoDAO;
import watchDog.property.template.FaxMsgLogTemplate;
import watchDog.property.template.PropertyConfig;

/**
 * Description:
 * @author Matthew Xu
 * @date May 28, 2020
 */
public class PropertyConfigTest {

	@Test
	public void test() {
		PropertyConfig propertyConfig = PropertyConfig.INSTANCE;
		String value = propertyConfig.getValue(FaxMsgLogTemplate.FL_NO_MOBILE.getKey(), new Object[]{});
		System.out.println(value);
		String value2 = propertyConfig.getValue(FaxMsgLogTemplate.FM_SEND.getKey(), new Object[]{});
		System.out.println(value2);
	}

}
