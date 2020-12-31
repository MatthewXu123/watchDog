package watchDog.service;

import org.junit.Test;

import watchDog.database.DatabaseMgr;
import watchDog.database.Record;
import watchDog.database.RecordSet;
import watchDog.property.template.CompanyServiceMsgLogTemplate;
import watchDog.util.DateTool;
import watchDog.wechat.bean.WechatMsg;
import watchDog.wechat.util.sender.Sender;

public class WechatNotify {
    @Test
    public void t() throws Exception
    {
        test();
    }
    public static void test() throws Exception
    {
        String sql = "select s.description,r.tag_id,r.tag_id2 "+
            "from cfsupervisors as s "+
            "inner join cfcompany as p on s.ksite=p.code  "+
            "left join cfcommunities cfcommunities1 on cfcommunities1.node=any(p.communities) and  subltree(cfcommunities1.node,0,1) = 'MAN'  "+
            "left join cfcommunities cfcommunities2 on cfcommunities2.node=any(p.communities) and  subltree(cfcommunities2.node,0,1) = 'CUS'  "+
            "left join lgsupervstatus as status on s.id=status.kidsupervisor  "+
            "inner join private_wechat_receiver as r on s.id=r.supervisor_id and r.checknetwork=true  "+
            "where (r.deadline>'2024-1-1' or  r.deadline<='2020-12-31') and cfcommunities2.description='盒马鲜生'";
        Sender sender = Sender.getInstance(Sender.CHANNEL_WECHAT);
        RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql);
        for(int i=0;i<rs.size();i++)
        {
            Record r = rs.get(i);
            String siteName = (String)r.get(0);
            String tagId = (String)r.get("tag_id");
            String tagId2 = (String)r.get("tag_id2");
            String message = "服务即将到期\n您关注的"+siteName+"远程报警功能已经超期，微信报警将于2020年12月31日后结束。如需续服请尽快与卡乐申经理联系，电话152 2111 8680";
            sender.sendIM(new WechatMsg.Builder(message, "6", new String[]{tagId,tagId2})
                    .build());
        }
    }

}
