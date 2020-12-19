package watchDog.tag;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import watchDog.wechat.bean.WechatDept;
import watchDog.wechat.bean.WechatPostTag;
import watchDog.wechat.bean.WechatResult;
import watchDog.wechat.bean.WechatTag;
import watchDog.wechat.bean.WechatUser;
import watchDog.wechat.util.WechatUtil;

public class WechatAPITest {
	
	@Test
	public void testGetWechatDeptList(){
		List<WechatDept> wechatDeptList = WechatUtil.getDeptListByDeptId("6");
		WechatUser memberByUserId = WechatUtil.getMemberByUserId("15366203524");
		assertTrue(true);
	}
	
	@Test
	public void testGetMemberByUserId(){
		WechatUser memberByUserId = WechatUtil.getMemberByUserId("matthewxu123");
		assertTrue(true);
	}
	
	@Test
	public void testGetWechatMemberByDeptId(){
		List<WechatUser> WechatMemberList = WechatUtil.getMemberByDeptId("741", "0");
		assertTrue(WechatMemberList.size() > 0);
	}
	
	@Test
	public void testGetAllWeChatMember(){
		List<WechatUser> allWeChatMember = WechatUtil.getAllMembers();
		assertTrue(allWeChatMember.size() > 0);
	}
	
	@Test
	public void testgetDeptIdWechatMemberMap(){
		List<WechatUser> wechatMemberList = WechatUtil.getDeptIdMemberMap("103").get("741");
		List<WechatUser> wechatMemberListCopy = wechatMemberList;
		Iterator<WechatUser> iterator = wechatMemberListCopy.iterator();
        while (iterator.hasNext()) {
        	WechatUser wechatMember = iterator.next();
            if (wechatMember.getName().indexOf("_i") >= 0)
                iterator.remove();
        }
        assertTrue(wechatMemberListCopy.size() > 0);
	}
	
	@Test
	public void testIsDeptExist(){
		assertTrue(!WechatUtil.isDeptExist("269") && !WechatUtil.isDeptExist("270"));
	}
	
	@Test
	public void testCreateDept(){
		WechatDept wechatDept = new WechatDept();
		wechatDept.setName("军官");
		wechatDept.setParentid("741");
		String id = WechatUtil.createDept(wechatDept);
		assertTrue(id.length() > 0);
	}
	
	@Test
	public void testUpdateMember(){
		WechatUser wechatMember = new WechatUser();
		wechatMember.setUserid("matthewxu123");
		wechatMember.setDepartment(new String[]{"11","741"});
		String result = WechatUtil.updateMember(wechatMember);
		
		WechatUser wechatMember2 = new WechatUser();
		wechatMember2.setUserid("15366203524");
		wechatMember2.setDepartment(new String[]{"11","741"});
		WechatUtil.updateMember(wechatMember2);
	}
	
	@Test
	public void testDeleteDepts(){
		for(int i = 1892; i < 2102; i++){
			List<WechatDept> deptListByDeptId = WechatUtil.getDeptListByDeptId(String.valueOf(i));
			for (WechatDept wechatDept : deptListByDeptId) {
					WechatUtil.deleteDept(wechatDept.getId());
			}
		}
		
	}
	
	@Test
	public void clear(){
		WechatUser wechatMember = new WechatUser();
		wechatMember.setUserid("matthewxu123");
		wechatMember.setDepartment(new String[]{"11","741"});
		String result = WechatUtil.updateMember(wechatMember);
		
		WechatUser wechatMember2 = new WechatUser();
		wechatMember2.setUserid("15366203524");
		wechatMember2.setDepartment(new String[]{"11","741"});
		WechatUtil.updateMember(wechatMember2);
		
		String[] ids = new String[]{"1305","1306","1307","1308","1309","1310","1758"};
		for (String id : ids) {
			List<WechatDept> deptListByDeptId = WechatUtil.getDeptListByDeptId(id);
			for (WechatDept wechatDept : deptListByDeptId) {
				if(!wechatDept.getId().equals(id))
					WechatUtil.deleteDept(wechatDept.getId());
			}
		}
		for (String id : ids) {
			List<WechatDept> deptListByDeptId = WechatUtil.getDeptListByDeptId(id);
			for (WechatDept wechatDept : deptListByDeptId) {
				if(!wechatDept.getId().equals(id))
					WechatUtil.deleteDept(wechatDept.getId());
			}
		}
		
	}
	
	@Test
	public void testIsDeptExistByName(){
		String id = WechatUtil.isDeptExistByName("741", "军官");
		assertTrue(id.equals("2526"));
	}
	
	@Test
	public void testGetTagList(){
		List<WechatTag> tagList = WechatUtil.getTagList();
		for (WechatTag wechatTag : tagList) {
			WechatUtil.deleteTagById(wechatTag.getTagid());
		}
		System.out.println();
	}
	
	@Test
	public void testDeleteTagById(){
		WechatResult wechatResult = WechatUtil.deleteTagById("20");
		System.out.println(wechatResult);
	}
	
	@Test
	public void testGetTagUserList(){
		List<WechatUser> tagUserList = WechatUtil.getTagUserList("20");
		System.out.println();
	}
	
	@Test
	public void testAddTagUser(){
		WechatPostTag wechatPostTag = new WechatPostTag();
		wechatPostTag.setTagid("146");
		wechatPostTag.setUserlist(Arrays.asList("matthewxu123"));
		WechatResult wechatResult = WechatUtil.addTagUser(wechatPostTag);
		System.out.println(wechatResult);
	}
	
	@Test
	public void testCreateTag(){
		WechatPostTag wechatPostTag = new WechatPostTag();
		wechatPostTag.setTagname("00必选人员-备选1");
		WechatResult wechatResult = WechatUtil.createTag(wechatPostTag);
		System.out.println();
	}
	
	@Test
	public void testGetmap(){
		Map<WechatTag, List<WechatUser>> allTagUserMap = WechatUtil.getAllTagUserMap();
		System.out.println();
	}
}
