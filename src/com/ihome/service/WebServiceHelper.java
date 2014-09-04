package com.ihome.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class WebServiceHelper {
	   
    //WSDL�ĵ��е������ռ�
    private static final String targetNameSpace="http://WebXml.com.cn/";
    //WSDL�ĵ��е�URL
    private static final String WSDL="http://webservice.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl";
       
    //��Ҫ���õķ�����(��ñ�����Ԥ��Web Services֧�ֵ��ޡ�������ʡ�ݺͳ�����Ϣ)
    private static final String getSupportProvince="getSupportProvince";
    //��Ҫ���õķ�����(��ñ�����Ԥ��Web Services֧�ֵĳ�����Ϣ,����ʡ�ݲ�ѯ���м��ϣ�������)
    private static final String getSupportCity="getSupportCity";
    //���ݳ��л�������Ʋ�ѯ���δ��������������������ڵ�����ʵ��������������ָ��
    private static final String getWeatherbyCityName="getWeatherbyCityName";


    /********
     * ����ݣ�������ʡ�ݺͳ�����Ϣ
     * @return
     */
    public  List<String> getProvince(){
        List<String> provinces=new ArrayList<String>();
        String str="";
        SoapObject soapObject=new SoapObject(targetNameSpace,getSupportProvince);
        //request.addProperty("����", "����ֵ");���õķ������������ֵ�����ݾ�����Ҫ��ѡ�ɲ�ѡ��
        
        SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet=true;
        envelope.setOutputSoapObject(soapObject);//envelope.bodyOut=request;
        
        
        AndroidHttpTransport httpTranstation=new AndroidHttpTransport(WSDL);
        //����HttpTransportSE httpTranstation=new HttpTransportSE(WSDL);
        try {
            
            httpTranstation.call(targetNameSpace+getSupportProvince, envelope);
            SoapObject result=(SoapObject)envelope.getResponse();
            //����Խ�����н������ṹ����json����
            //str=(String) result.getProperty(6).toString();
            
            int count=result.getPropertyCount();
            for(int index=0;index<count;index++){
                provinces.add(result.getProperty(index).toString());
            }
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        return provinces;
    }
    
    /**********
     * ����ʡ�ݻ���ֱϽ�л�ȡ����Ԥ����֧�ֵĳ��м���
     * @param province
     * @return
     */
    public  List<String> getCitys(String province){
        List<String> citys=new ArrayList<String>();
        SoapObject soapObject=new SoapObject(targetNameSpace,getSupportCity);
        soapObject.addProperty("byProvinceName", province);
        SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet=true;
        envelope.setOutputSoapObject(soapObject);
        
        AndroidHttpTransport httpTransport=new AndroidHttpTransport(WSDL);
        try {
            httpTransport.call(targetNameSpace+getSupportCity, envelope);
            SoapObject result=(SoapObject)envelope.getResponse();
            int count=result.getPropertyCount();
            for(int index=0;index<count;index++){
                citys.add(result.getProperty(index).toString());
            }
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        return citys;
    }
    
    /***************************
     * ���ݳ�����Ϣ��ȡ����Ԥ����Ϣ
     * @param city
     * @return
     ***************************/
    public  WeatherBean getWeatherByCity(String city){
        
        WeatherBean bean=new WeatherBean();

        SoapObject soapObject=new SoapObject(targetNameSpace,getWeatherbyCityName);
        soapObject.addProperty("theCityName",city);//���õķ������������ֵ�����ݾ�����Ҫ��ѡ�ɲ�ѡ��
        
        SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet=true;
        envelope.setOutputSoapObject(soapObject);//envelope.bodyOut=request;
        
        
        AndroidHttpTransport httpTranstation=new AndroidHttpTransport(WSDL);
        //����HttpTransportSE httpTranstation=new HttpTransportSE(WSDL);
        try {
            httpTranstation.call(targetNameSpace+getWeatherbyCityName, envelope);
            SoapObject result=(SoapObject)envelope.getResponse();
            //����Խ�����н������ṹ����json����
            if(result!=null)
            {
            	bean=parserWeather(result);
            }            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        return bean;
    }
    
    /**
     * �������صĽ��
     * @param soapObject
     */
    protected   WeatherBean parserWeather(SoapObject soapObject){
        WeatherBean bean=new WeatherBean();
        
        List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
        
        
        Map<String,Object> map=new HashMap<String,Object>();
        
        //������
        bean.setCityName(soapObject.getProperty(1).toString());
        //���м��
        bean.setCityDescription(soapObject.getProperty(soapObject.getPropertyCount()-1).toString());
        //����ʵ��+����
        bean.setLiveWeather(soapObject.getProperty(10).toString()+"\n"+soapObject.getProperty(11).toString());
        
        //��������
        //���ڣ�
        String date=soapObject.getProperty(6).toString();
        //---------------------------------------------------
        if(date!=null)
        {
        	String[] daw=date.split("��");
        	if(daw!=null&&daw.length>0)
        	{
        		String weatherToday="���죺" + daw[0]+"��";  
                weatherToday+="\n������"+ daw[1]; 
                weatherToday+="\n���£�"+soapObject.getProperty(5).toString();
                weatherToday+="\n������"+soapObject.getProperty(7).toString();
                weatherToday+="\n";
                bean.setTodayTempeture(soapObject.getProperty(5).toString());
                bean.setTodayWeather(daw[1]);
                map.put("weatherDay", weatherToday);
        	}       	
        }
        
        
        List<Integer> icons=new ArrayList<Integer>();
    
        icons.add(parseIcon(soapObject.getProperty(8).toString()));      
        icons.add(parseIcon(soapObject.getProperty(9).toString()));
                 
        map.put("icons",icons);
        list.add(map);
                      
        //-------------------------------------------------
        map=new HashMap<String,Object>(); 
        date=soapObject.getProperty(13).toString();
        
        if(date!=null)
        {
        	String[] daw=date.split("��");
        	if(daw!=null&&daw.length>0)
        	{
        		String weatherTomorrow="���죺" + daw[0]+"��";  
                weatherTomorrow+="\n������"+ daw[1]; 
                weatherTomorrow+="\n���£�"+soapObject.getProperty(12).toString();
                weatherTomorrow+="\n������"+soapObject.getProperty(14).toString();
                weatherTomorrow+="\n";
                
                bean.setTomorrowTempeture(soapObject.getProperty(12).toString());
                bean.setTomorrowWeather(daw[1]);               
                map.put("weatherDay", weatherTomorrow);
        	}       	
        }
                                            
        icons=new ArrayList<Integer>();       
        icons.add(parseIcon(soapObject.getProperty(15).toString()));      
        icons.add(parseIcon(soapObject.getProperty(16).toString()));
               
        map.put("icons",icons);
        list.add(map);
        //--------------------------------------------------------------
        map=new HashMap<String,Object>(); 
        
        date=soapObject.getProperty(18).toString();
        
        if(date!=null)
        {
        	String[] daw=date.split("��");
        	if(daw!=null&&daw.length>0)
        	{
        		String weatherAfterTomorrow="���죺" + daw[0]+"��";  
                weatherAfterTomorrow+="\n������"+ daw[1]; 
                weatherAfterTomorrow+="\n���£�"+soapObject.getProperty(17).toString();
                weatherAfterTomorrow+="\n������"+soapObject.getProperty(19).toString();
                weatherAfterTomorrow+="\n";
                
                bean.setAfterTomorrowTempeture(soapObject.getProperty(17).toString());
                bean.setAfterTomorrowWeather(daw[1]);
                
                map.put("weatherDay", weatherAfterTomorrow);
        	}       	
        }
                                                        
        icons=new ArrayList<Integer>();
        icons.add(parseIcon(soapObject.getProperty(20).toString()));      
        icons.add(parseIcon(soapObject.getProperty(21).toString()));
                
        map.put("icons",icons);
        list.add(map); 
        //--------------------------------------------------------------
        
        bean.setList(list);
        return bean;
    }
    
     //����ͼ���ַ���
     private int parseIcon(String data){
        // 0.gif����������0,
         int resID=32;
         String result=data.substring(0, data.length()-4).trim();
          // String []icon=data.split(".");
          // String result=icon[0].trim();
          //   Log.e("this is the icon", result.trim());
          
           if(!result.equals("nothing")){
               resID=Integer.parseInt(result.trim());
           }
         return resID;
         //return ("a_"+data).split(".")[0]; 
     } 
}
/**
 * http://www.weather.com.cn/data/sk/101010100.html
   http://www.weather.com.cn/data/cityinfo/101010100.html

 * http://m.weather.com.cn/atad/101040100.html
 * 
 * 1. XML�ӿ� http://flash.weather.com.cn/wmaps/xml/china.xml �����ȫ�������ĸ��ڵ㣬�г����е�ʡ�����е�pyName�ֶ��Ǹ���ʡXML���ļ��������籱������beijing���Ǿ���ζ�ű�����XML��ַΪ http://flash.weather.com.cn/wmaps/xml/beijing.xml һ��ʡ�������������г���ʡ�����е����ݣ��������г��������� tmp1������µͣ�tmp2������¶ȣ�url�ǳ���Ҫ������һ�����˵��state1��state2������ת����ÿ��������һ������������������ǳ��࣬�ұ���ȫ����������������ֱ�ӷ���������뷨����Ϊ�ҿ�����һ�����е���������ı�����26...������֪������0.�� 1.���� 2.�� 6.���ѩ 7.С�� 8.���� 13.��ѩ 14.Сѩ ���к�������֪�����û�ã�������ֵ���Ҫ�����Ǽ���ͼƬ�ģ�����
2. ͼƬ�ӿ� http://m.weather.com.cn/img/c0.gif http://m.weather.com.cn/img/b0.gif http://www.weather.com.cn/m/i/weatherpic/29x20/d0.gif http://www.weather.com.cn/m2/i/icon_weather/29x20/n00.gif ���ͼ������������0���磩��ͼƬ���������������ͼƬ�������ơ�c��ͷ��ͼƬ��20*20���صģ�b��ͷ����50*46���صģ�d��ͷ���Ƿ��׵�ͼ�꣬29*20���أ�n��ͷ����ҹ�䷴��ͼ�꣬29*20���أ�ע��������ļ�������λ���֣� Ҳ���и����ͼ�꣬�ȴ���ҷ�����~
3. JSON�ӿ� ��û�뵽~��Ȼ��JSON�ӿ�~JSON��iPhone�Ϸ�������Ҫ��XML�򵥺ܶ� http://m.weather.com.cn/data/101010200.html ����Ǳ�����JSON���ݣ��Ǹ�HTML�������Ǹ�������XML�е�url�õ��ġ����JSON�а�����ʵʱ���ݡ�7������Ԥ��������ָ���ȷḻ������
 
 
��ȡʡ�����룺http://www.weather.com.cn/data/list3/city.xml?level=1
��ȡ���д���(���簲����22)��http://www.weather.com.cn/data/list3/city22.xml?level=2
��ȡ������루���簲����2206����http://www.weather.com.cn/data/list3/city2206.xml?level=3
��ȡ������ʡ�����������صĴ�����220607
Ȼ��ȥ�����й���������URL��http://m.weather.com.cn/data/101220607.html
�Ϳ��Ի�ȡ����������
�����ٸ��������õ�̽���õ���URL��
���� FLASHʵ����http://flash.weather.com.cn/sk2/101220607.xml
ʵ��FLASH��http://flash.weather.com.cn/sk2/shikuang.swf?id=101220607
����ѡ��˵���ע�����������ĸ�INPUT��������һ���������ˣ�����FIREFOX����Դ����鿴����http://www.weather.com.cn/static/custom/search3.htm
ʵʱ������������Ŷ����http://www.weather.com.cn/data/sk/101220607.html
 */
