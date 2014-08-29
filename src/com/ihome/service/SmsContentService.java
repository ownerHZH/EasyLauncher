package com.ihome.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ihome.entity.SmsInfo;
import com.ihome.utils.Constants;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * class name：SmsContent<BR>
 * class description：获取手机中的各种短信信息<BR>
 * PS： 需要权限 <uses-permission android:name="android.permission.READ_SMS" /><BR>
 * Date:2014-08-26<BR>
 *
 * @author owner
 */

/**
 * sms数据库中的字段如下：
	* _id               一个自增字段，从1开始
	* thread_id         序号，同一发信人的id相同
	* address           发件人手机号码
	* person            联系人列表里的序号，陌生人为null 
	* date              发件日期
	* protocol          协议，分为： 0 SMS_RPOTO, 1 MMS_PROTO  
	* read              是否阅读 0未读， 1已读  
	* status            状态 -1接收，0 complete, 64 pending, 128 failed 
	* type     
	*    ALL    = 0;
	*    INBOX  = 1;
	*    SENT   = 2;
	*    DRAFT  = 3;
	*    OUTBOX = 4;
	*    FAILED = 5;
	*    QUEUED = 6; 
	* body               短信内容
	* service_center     短信服务中心号码编号
	* subject            短信的主题
	* reply_path_present     TP-Reply-Path
*/

public class SmsContentService {
    private Context context;
    public SmsContentService(Context context) {
        this.context = context;
    }
    /**
     * Role:获取短信的各种信息 <BR>
     * Date:2014-08-26 <BR>
     *
     * @author owner
     */
    public List<SmsInfo> getSmsInfo(String threadId) {
    	List<SmsInfo> infos=new ArrayList<SmsInfo>();
        String[] projection = new String[] { "_id","thread_id", "address", "person",
                "body", "date", "type" };
        Cursor cusor = context.getContentResolver().query(Uri.parse(Constants.SMS_URI_ALL), projection, "thread_id="+threadId, null,
                "date");
        int nameColumn = cusor.getColumnIndex("person");
        int phoneNumberColumn = cusor.getColumnIndex("address");
        int smsbodyColumn = cusor.getColumnIndex("body");
        int dateColumn = cusor.getColumnIndex("date");
        int typeColumn = cusor.getColumnIndex("type");
        int threadidColumn = cusor.getColumnIndex("thread_id");
        if (cusor != null) {
            while (cusor.moveToNext()) {
                SmsInfo smsinfo = new SmsInfo();
                smsinfo.setName(cusor.getString(nameColumn));
                smsinfo.setDate(cusor.getString(dateColumn));
                smsinfo.setPhoneNumber(cusor.getString(phoneNumberColumn));
                smsinfo.setSmsbody(cusor.getString(smsbodyColumn));
                smsinfo.setType(cusor.getString(typeColumn));
                smsinfo.setThread_id(cusor.getString(threadidColumn));
                infos.add(smsinfo);
            }
            cusor.close();
        }
        return infos;
    }
    
    /**
     * 获取有多少个会话
     * @return
     */
    private Set<String> getSessionCount()
    {
    	Set<String> setmap=new HashSet<String>();
    	List<SmsInfo> infos=new ArrayList<SmsInfo>();
    	String[] projection = new String[] { "_id","thread_id"};
        Cursor cusor = context.getContentResolver().query(Uri.parse(Constants.SMS_URI_ALL), projection, null, null,
                null);
        int threadidColumn = cusor.getColumnIndex("thread_id");
        if (cusor != null) {        	
            while (cusor.moveToNext()) {
            	setmap.add(cusor.getString(threadidColumn));
            }
            cusor.close();
        }
        return setmap;
    }
    
    /**
     * 取得所有会话的最新一条信息
     * 例如：取得Test表中按id分组的最新一条信息
     * select * from test a where time=(select max(time) from test where id=a.id)
     * @return
     */
    public List<SmsInfo> getSmsInfoGroupByThreadId()
    {
    	List<SmsInfo> infos=new ArrayList<SmsInfo>();
    	Iterator<String> iterator=getSessionCount().iterator();
    	while(iterator.hasNext())
    	{
    		String thid=iterator.next();
    		String[] projection = new String[] { "_id","thread_id", "address", "person",
                    "body", "date", "type" };
            Cursor cusor = context.getContentResolver().query(Uri.parse(Constants.SMS_URI_INBOX), projection, "thread_id="+thid, null,
                    "date desc");
            
            int nameColumn = cusor.getColumnIndex("person");
            int phoneNumberColumn = cusor.getColumnIndex("address");
            int smsbodyColumn = cusor.getColumnIndex("body");
            int dateColumn = cusor.getColumnIndex("date");
            int typeColumn = cusor.getColumnIndex("type");
            int threadidColumn = cusor.getColumnIndex("thread_id");
            if(cusor.moveToFirst())
            {
        	   SmsInfo smsinfo = new SmsInfo();
               smsinfo.setName(cusor.getString(nameColumn));
               smsinfo.setDate(cusor.getString(dateColumn));
               smsinfo.setPhoneNumber(cusor.getString(phoneNumberColumn));
               smsinfo.setSmsbody(cusor.getString(smsbodyColumn));
               smsinfo.setType(cusor.getString(typeColumn));
               smsinfo.setThread_id(cusor.getString(threadidColumn));
               infos.add(smsinfo);
             }
             cusor.close();
    	}
        return infos;
    }
}
