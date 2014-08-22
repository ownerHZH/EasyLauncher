package com.ihome.service;

import java.util.List;
import java.util.Map;

public class WeatherBean {
  String cityName;
  String cityDescription;
  String liveWeather;
  List<Map<String,Object>> list;
  
  String todayTempeture,tomorrowTempeture,afterTomorrowTempeture;
  String todayWeather,tomorrowWeather,afterTomorrowWeather;
  
public String getTodayTempeture() {
	return todayTempeture;
}
public void setTodayTempeture(String todayTempeture) {
	this.todayTempeture = todayTempeture;
}
public String getTomorrowTempeture() {
	return tomorrowTempeture;
}
public void setTomorrowTempeture(String tomorrowTempeture) {
	this.tomorrowTempeture = tomorrowTempeture;
}
public String getAfterTomorrowTempeture() {
	return afterTomorrowTempeture;
}
public void setAfterTomorrowTempeture(String afterTomorrowTempeture) {
	this.afterTomorrowTempeture = afterTomorrowTempeture;
}
public String getTodayWeather() {
	return todayWeather;
}
public void setTodayWeather(String todayWeather) {
	this.todayWeather = todayWeather;
}
public String getTomorrowWeather() {
	return tomorrowWeather;
}
public void setTomorrowWeather(String tomorrowWeather) {
	this.tomorrowWeather = tomorrowWeather;
}
public String getAfterTomorrowWeather() {
	return afterTomorrowWeather;
}
public void setAfterTomorrowWeather(String afterTomorrowWeather) {
	this.afterTomorrowWeather = afterTomorrowWeather;
}
public String getCityName() {
	return cityName;
}
public void setCityName(String cityName) {
	this.cityName = cityName;
}
public String getCityDescription() {
	return cityDescription;
}
public void setCityDescription(String cityDescription) {
	this.cityDescription = cityDescription;
}
public String getLiveWeather() {
	return liveWeather;
}
public void setLiveWeather(String liveWeather) {
	this.liveWeather = liveWeather;
}
public List<Map<String, Object>> getList() {
	return list;
}
public void setList(List<Map<String, Object>> list) {
	this.list = list;
}
}
