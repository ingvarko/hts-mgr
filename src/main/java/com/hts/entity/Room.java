package com.hts.entity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Transient;

import net.sf.json.JSONObject;

import org.hibernate.annotations.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ROOM")
public class Room {

	private Integer id;
	private String roomName;
	private Hotel hotel;
	private SubscriptionPackage subscriptionPackage ;
	private IpAddress ipAddr;
	
	


	public Room() {
	}
	
	public Room(String roomName) {
		this.roomName = roomName;
	}

	public Room(String roomName, Hotel hotel) {
		this.roomName = roomName;
		this.setHotel(hotel);
	}

	@Id
	@GeneratedValue
	@Column(name = "ROOM_ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "ROOM_NAME")
	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	@OneToOne(cascade = CascadeType.PERSIST, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	public SubscriptionPackage getSubscriptionPackage() {
		return subscriptionPackage;
	}

	public void setSubscriptionPackage(SubscriptionPackage subscriptionPackage) {
		this.subscriptionPackage = subscriptionPackage;
	}
	
	@Transient
	public void setIp(IpAddress ipAddr){
		this.ipAddr = ipAddr;
	}
	
	@Transient
	public String getIp(){
		if (ipAddr!=null)
			return ipAddr.getIpAddress();
	else
		return new String("");
	}
	
	@Transient
	public String getJson() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("roomId", id.toString());
		map.put("roomName", roomName);
		map.put("subPackage", subscriptionPackage.getSubscriptionPackageName());
		map.put("ip", getIp());

		JSONObject json = new JSONObject();
		json.accumulateAll((Map<String, String>) map);

		return json.toString();
	}
}