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
            bean=parserWeather(result);
             
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
        String weatherToday="���죺" + date.split(" ")[0];  
        weatherToday+="\n������"+ date.split(" ")[1]; 
        weatherToday+="\n���£�"+soapObject.getProperty(5).toString();
        weatherToday+="\n������"+soapObject.getProperty(7).toString();
        weatherToday+="\n";
        
        bean.setTodayTempeture(soapObject.getProperty(5).toString());
        bean.setTodayWeather(date.split(" ")[1]);
        
        Log.e("����",weatherToday);
        
        List<Integer> icons=new ArrayList<Integer>();
    
        icons.add(parseIcon(soapObject.getProperty(8).toString()));      
        icons.add(parseIcon(soapObject.getProperty(9).toString()));
         
        map.put("weatherDay", weatherToday);
        map.put("icons",icons);
        list.add(map);
        
        
        

        //-------------------------------------------------
        map=new HashMap<String,Object>(); 
        date=soapObject.getProperty(13).toString();
        String weatherTomorrow="���죺" + date.split(" ")[0];  
        weatherTomorrow+="\n������"+ date.split(" ")[1]; 
        weatherTomorrow+="\n���£�"+soapObject.getProperty(12).toString();
        weatherTomorrow+="\n������"+soapObject.getProperty(14).toString();
        weatherTomorrow+="\n";
        
        bean.setTomorrowTempeture(soapObject.getProperty(12).toString());
        bean.setTomorrowWeather(date.split(" ")[1]);
        
        Log.e("����",weatherTomorrow);
        
        icons=new ArrayList<Integer>();
         
        icons.add(parseIcon(soapObject.getProperty(15).toString()));      
        icons.add(parseIcon(soapObject.getProperty(16).toString()));
        
        map.put("weatherDay", weatherTomorrow);
        map.put("icons",icons);
        list.add(map);
        //--------------------------------------------------------------
        map=new HashMap<String,Object>(); 
        
        date=soapObject.getProperty(18).toString();
        String weatherAfterTomorrow="���죺" + date.split(" ")[0];  
        weatherAfterTomorrow+="\n������"+ date.split(" ")[1]; 
        weatherAfterTomorrow+="\n���£�"+soapObject.getProperty(17).toString();
        weatherAfterTomorrow+="\n������"+soapObject.getProperty(19).toString();
        weatherAfterTomorrow+="\n";
        
        bean.setAfterTomorrowTempeture(soapObject.getProperty(17).toString());
        bean.setAfterTomorrowWeather(date.split(" ")[1]);
        
        Log.e("����",weatherAfterTomorrow);
        
        icons=new ArrayList<Integer>();
        icons.add(parseIcon(soapObject.getProperty(20).toString()));      
        icons.add(parseIcon(soapObject.getProperty(21).toString()));
        
        map.put("weatherDay", weatherAfterTomorrow);
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
