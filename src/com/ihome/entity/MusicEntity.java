package com.ihome.entity;

public class MusicEntity {
	String id;
	String name;
	String url;
	String type;

	public MusicEntity(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public MusicEntity(String id, String name, String url, String type) {
		super();
		this.id = id;
		this.name = name;
		this.url = url;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "MusicEntity [id=" + id + ", name=" + name + ", url=" + url
				+ ", type=" + type + "]";
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
