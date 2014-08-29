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
 * class name��SmsContent<BR>
 * class description����ȡ�ֻ��еĸ��ֶ�����Ϣ<BR>
 * PS�� ��ҪȨ�� <uses-permission android:name="android.permission.READ_SMS" /><BR>
 * Date:2014-08-26<BR>
 *
 * @author owner
 */

/**
 * sms���ݿ��е��ֶ����£�
	* _id               һ�������ֶΣ���1��ʼ
	* thread_id         ��ţ�ͬһ�����˵�id��ͬ
	* address           �������ֻ�����
	* person            ��ϵ���б������ţ�İ����Ϊnull 
	* date              ��������
	* protocol          Э�飬��Ϊ�� 0 SMS_RPOTO, 1 MMS_PROTO  
	* read              �Ƿ��Ķ� 0δ���� 1�Ѷ�  
	* status            ״̬ -1���գ�0 complete, 64 pending, 128 failed 
	* type     
	*    ALL    = 0;
	*    INBOX  = 1;
	*    SENT   = 2;
	*    DRAFT  = 3;
	*    OUTBOX = 4;
	*    FAILED = 5;
	*    QUEUED = 6; 
	* body               ��������
	* service_center     ���ŷ������ĺ�����
	* subject            ���ŵ�����
	* reply_path_present     TP-Reply-Path
*/

public class SmsContentService {
    private Context context;
    public SmsContentService(Context context) {
        this.context = context;
    }
    /**
     * Role:��ȡ���ŵĸ�����Ϣ <BR>
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
     * ��ȡ�ж��ٸ��Ự
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
     * ȡ�����лỰ������һ����Ϣ
     * ���磺ȡ��Test���а�id���������һ����Ϣ
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
