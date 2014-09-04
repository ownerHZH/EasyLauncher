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
	   
    //WSDL文档中的命名空间
    private static final String targetNameSpace="http://WebXml.com.cn/";
    //WSDL文档中的URL
    private static final String WSDL="http://webservice.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl";
       
    //需要调用的方法名(获得本天气预报Web Services支持的洲、国内外省份和城市信息)
    private static final String getSupportProvince="getSupportProvince";
    //需要调用的方法名(获得本天气预报Web Services支持的城市信息,根据省份查询城市集合：带参数)
    private static final String getSupportCity="getSupportCity";
    //根据城市或地区名称查询获得未来三天内天气情况、现在的天气实况、天气和生活指数
    private static final String getWeatherbyCityName="getWeatherbyCityName";


    /********
     * 获得州，国内外省份和城市信息
     * @return
     */
    public  List<String> getProvince(){
        List<String> provinces=new ArrayList<String>();
        String str="";
        SoapObject soapObject=new SoapObject(targetNameSpace,getSupportProvince);
        //request.addProperty("参数", "参数值");调用的方法参数与参数值（根据具体需要可选可不选）
        
        SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet=true;
        envelope.setOutputSoapObject(soapObject);//envelope.bodyOut=request;
        
        
        AndroidHttpTransport httpTranstation=new AndroidHttpTransport(WSDL);
        //或者HttpTransportSE httpTranstation=new HttpTransportSE(WSDL);
        try {
            
            httpTranstation.call(targetNameSpace+getSupportProvince, envelope);
            SoapObject result=(SoapObject)envelope.getResponse();
            //下面对结果进行解析，结构类似json对象
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
     * 根据省份或者直辖市获取天气预报所支持的城市集合
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
     * 根据城市信息获取天气预报信息
     * @param city
     * @return
     ***************************/
    public  WeatherBean getWeatherByCity(String city){
        
        WeatherBean bean=new WeatherBean();

        SoapObject soapObject=new SoapObject(targetNameSpace,getWeatherbyCityName);
        soapObject.addProperty("theCityName",city);//调用的方法参数与参数值（根据具体需要可选可不选）
        
        SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet=true;
        envelope.setOutputSoapObject(soapObject);//envelope.bodyOut=request;
        
        
        AndroidHttpTransport httpTranstation=new AndroidHttpTransport(WSDL);
        //或者HttpTransportSE httpTranstation=new HttpTransportSE(WSDL);
        try {
            httpTranstation.call(targetNameSpace+getWeatherbyCityName, envelope);
            SoapObject result=(SoapObject)envelope.getResponse();
            //下面对结果进行解析，结构类似json对象
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
     * 解析返回的结果
     * @param soapObject
     */
    protected   WeatherBean parserWeather(SoapObject soapObject){
        WeatherBean bean=new WeatherBean();
        
        List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
        
        
        Map<String,Object> map=new HashMap<String,Object>();
        
        //城市名
        bean.setCityName(soapObject.getProperty(1).toString());
        //城市简介
        bean.setCityDescription(soapObject.getProperty(soapObject.getPropertyCount()-1).toString());
        //天气实况+建议
        bean.setLiveWeather(soapObject.getProperty(10).toString()+"\n"+soapObject.getProperty(11).toString());
        
        //其他数据
        //日期，
        String date=soapObject.getProperty(6).toString();
        //---------------------------------------------------
        if(date!=null)
        {
        	String[] daw=date.split("日");
        	if(daw!=null&&daw.length>0)
        	{
        		String weatherToday="今天：" + daw[0]+"日";  
                weatherToday+="\n天气："+ daw[1]; 
                weatherToday+="\n气温："+soapObject.getProperty(5).toString();
                weatherToday+="\n风力："+soapObject.getProperty(7).toString();
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
        	String[] daw=date.split("日");
        	if(daw!=null&&daw.length>0)
        	{
        		String weatherTomorrow="明天：" + daw[0]+"日";  
                weatherTomorrow+="\n天气："+ daw[1]; 
                weatherTomorrow+="\n气温："+soapObject.getProperty(12).toString();
                weatherTomorrow+="\n风力："+soapObject.getProperty(14).toString();
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
        	String[] daw=date.split("日");
        	if(daw!=null&&daw.length>0)
        	{
        		String weatherAfterTomorrow="后天：" + daw[0]+"日";  
                weatherAfterTomorrow+="\n天气："+ daw[1]; 
                weatherAfterTomorrow+="\n气温："+soapObject.getProperty(17).toString();
                weatherAfterTomorrow+="\n风力："+soapObject.getProperty(19).toString();
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
    
     //解析图标字符串
     private int parseIcon(String data){
        // 0.gif，返回名称0,
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
 * 1. XML接口 http://flash.weather.com.cn/wmaps/xml/china.xml 这个是全国天气的根节点，列出所有的省，其中的pyName字段是各个省XML的文件名，比如北京的是beijing，那就意味着北京的XML地址为 http://flash.weather.com.cn/wmaps/xml/beijing.xml 一个省的天气，其中列出该省各个市的数据，北京就列出各个区。 tmp1是最低温低，tmp2是最高温度，url非常重要，我们一会儿再说。state1和state2是神马转神马，每个数代表一个天气现象。天气现象非常多，我本想全部分析出来，后来直接放弃了这个想法。因为我看到了一个城市的天气现象的编码是26...我现在知道的有0.晴 1.多云 2.阴 6.雨夹雪 7.小雨 8.中雨 13.阵雪 14.小雪 其中后来发现知道这个没用，这个数字的主要作用是检索图片的！！！
2. 图片接口 http://m.weather.com.cn/img/c0.gif http://m.weather.com.cn/img/b0.gif http://www.weather.com.cn/m/i/weatherpic/29x20/d0.gif http://www.weather.com.cn/m2/i/icon_weather/29x20/n00.gif 这个图就是天气现象0（晴）的图片，其他天气现象的图片依此类推。c打头的图片是20*20像素的，b打头的是50*46像素的，d打头的是反白的图标，29*20像素，n打头的是夜间反白图标，29*20像素，注意这里的文件名是两位数字！ 也许还有更多的图标，等待大家发掘啦~
3. JSON接口 真没想到~居然有JSON接口~JSON在iPhone上分析起来要比XML简单很多 http://m.weather.com.cn/data/101010200.html 这个是北京的JSON数据，那个HTML的名字是根据上文XML中的url得到的。这个JSON中包含了实时数据、7天天气预报、气象指数等丰富的数据
 
 
获取省级代码：http://www.weather.com.cn/data/list3/city.xml?level=1
获取城市代码(比如安徽是22)：http://www.weather.com.cn/data/list3/city22.xml?level=2
获取区域代码（比如安庆是2206）：http://www.weather.com.cn/data/list3/city2206.xml?level=3
获取到安徽省安庆市望江县的代码是220607
然后去加上中国代码请求URL：http://m.weather.com.cn/data/101220607.html
就可以获取当地天气。
另外再给几个有用的探索得到的URL：
天气 FLASH实况：http://flash.weather.com.cn/sk2/101220607.xml
实况FLASH：http://flash.weather.com.cn/sk2/shikuang.swf?id=101220607
三级选择菜单（注明，这里有四个INPUT，其中有一个被隐藏了，可用FIREFOX改下源代码查看）：http://www.weather.com.cn/static/custom/search3.htm
实时天气（很有用哦）：http://www.weather.com.cn/data/sk/101220607.html
 */
