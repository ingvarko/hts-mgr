package com.hts.service;

import java.net.UnknownHostException;

import com.hts.entity.IpAddress;
import com.hts.entity.Room;
import com.hts.exceptions.AppException;

public interface IIpAddressService {

	void update(IpAddress ipAddress) throws AppException;
	IpAddress getById(Integer uuid) throws AppException;
	void delete(IpAddress ipAddress) throws AppException;
	IpAddress getByIp(String name) throws AppException;
	IpAddress create(String name) throws UnknownHostException, AppException;
	
	IpAddress getByRoom(Room room) throws AppException;
	
	boolean isBroadcastStreamAllowedForIP(String ipAddress,
			String broadcastStreamName) throws UnknownHostException,
			AppException;
}
