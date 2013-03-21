package com.hts.entity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.sf.json.JSONObject;

import com.hts.exceptions.AppException;

@Entity
@Table(name = "CHANNEL")
public class Channel {

	public Channel(String channelName, String broadcastStreamName) {
		this.channelName = channelName;
		this.broadcastStreamName = broadcastStreamName;
	}

	public Channel() {
	}

	@Id
	@GeneratedValue
	@Column(name = "CHANNEL_ID", nullable = false)
	private Integer id;

	@Column(name = "CHANNEL_NAME", unique = true, nullable = false)
	private String channelName;

	@Column(name = "DESCRIPTION")
	private String Description;

	@Column(name = "BROADCASTSTREAM_NAME", unique = true, nullable = false)
	private String broadcastStreamName;

	@Column(name = "CHANNEL_IMAGE_URL")
	private String channelImageUrl;

	public void setChannelImageUrl(String channelImageUrl) {
		this.channelImageUrl = channelImageUrl;
	}

	public String getChannelImageUrl() {
		return channelImageUrl;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getBroadcastStream() {
		return this.broadcastStreamName;
	}

	public void setBroadcastStream(String broadcastStreamName) {
		this.broadcastStreamName = broadcastStreamName;
	}

	@Override
	public String toString() {
		return "Channel [id=" + id + ", channelName=" + channelName
				+ ", Description=" + Description + ", broadcastStreamName="
				+ broadcastStreamName + ", channelImageUrl=" + channelImageUrl
				+ "]";
	}

	@Transient
	public String getJson() throws AppException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("num", id.toString());
		if (Description == null || Description.equals(""))
			Description = "not available";
		map.put("description", Description);
		map.put("broadcastStreamName", broadcastStreamName);
		map.put("channelName", channelName);
		if (channelImageUrl == null || channelImageUrl.equals("")) {
			map.put("channelImageUrl", "not available");
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("<img src='" + channelImageUrl
					+ "' width='75' heigth='75'></img>");
			map.put("channelImageUrl", sb.toString());
		}

		JSONObject json = new JSONObject();
		json.accumulateAll((Map<String, String>) map);

		return json.toString();
	}

}
