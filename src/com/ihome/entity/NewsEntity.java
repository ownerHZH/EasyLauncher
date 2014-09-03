package com.ihome.entity;

public class NewsEntity {
	String url;
	String title;
	String time;
	
	public NewsEntity(String url,String title,String time)
	{
		this.url=url;
		this.title=title;
		this.time=time;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "NewsEntity [url=" + url + ", title=" + title + ", time=" + time
				+ "]";
	}
}
