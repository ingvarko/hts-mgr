package com.hts.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.hts.dao.DAO;
import com.hts.dao.HotelDAOHibernateImpl;
import com.hts.dao.RoomDAOHibernateImpl;
import com.hts.entity.Hotel;
import com.hts.entity.IpAddress;
import com.hts.entity.Room;
import com.hts.entity.SubscriptionPackage;
import com.hts.exceptions.AppException;

public class RoomServiceImpl implements IRoomService {
	final Logger log = Logger.getLogger(this.getClass());
	RoomDAOHibernateImpl roomDAO = new RoomDAOHibernateImpl();
	HotelDAOHibernateImpl hotelDAO = new HotelDAOHibernateImpl();

	public Room create(String name) throws AppException {

		Room room = getByRoomName(name);
		if (room == null) {

			Hotel hotel = hotelDAO.getAll().get(0);
			// Hotel hotel=hotels.get(0);
			// .get(1);

			Room r = roomDAO.create(new Room(name, hotel));
			DAO.close();
			log.info("created room: " + r);
			return r;
		} else
			throw new AppException("You can't dublicate room name");
	}

	@Override
	public Room create(Room room) throws AppException {

		Room roomTemp = getByRoomName(room.getRoomName());
		if (roomTemp == null) {
			Room r = roomDAO.create(room);
			DAO.close();
			log.info("created room: " + r);
			return r;
		} else
			throw new AppException("You can't dublicate room name");
	}

	@Override
	public Room create(String name, Hotel hotel) throws AppException {

		if (hotel == null)
			throw new AppException(
					"Hotel must not be null. Use RoomServiceImpl.create(String str).");

		Room r = roomDAO.create(new Room(name, hotel));
		DAO.close();
		log.info("created room: " + r);
		return r;
	}

	@Override
	public void update(Room room) throws AppException {
		roomDAO.update(room);
		DAO.close();
		log.info("updated room: " + room);
	}

	@Override
	public void delete(Room room) throws AppException {
		roomDAO.delete(room);
		DAO.close();
		log.info("deleted room: " + room);
	}

	@Override
	public List<Room> getByName(String name) throws AppException {
		return roomDAO.getByName(name);
	}

	@Override
	public Room getByRoomName(String roomName) throws AppException {
		return roomDAO.getByRoomName(roomName);

	}

	@Override
	public Room getById(Integer id) throws AppException {
		return roomDAO.getById(id);
	}

	@Override
	public void addSubscriptionPackage(Room room,
			SubscriptionPackage subscriptionPackage) throws AppException {

		room.setSubscriptionPackage(subscriptionPackage);

		roomDAO.update(room);
		log.info("added subscriptionPackage to room: " + room.toString());

	}

	@Override
	public void removeSubscriptionPackage(Room room) throws AppException {
		room.setSubscriptionPackage(null);

		roomDAO.update(room);
		log.info("removed subscriptionPackage from room: " + room.toString());

	}

	@Override
	public List<Room> getAll() throws AppException {
		return roomDAO.getAll();
	}

	@Override
	public String getJson(List<Room> list, String currentPage) {
		/**
		 * Json header spec
		 * 
		 * total total pages for the pager page current page for the pager
		 * records total number of records in the result set rows an array that
		 * contains the actual data id the unique id of the row cell an array
		 * that contains the data for a row
		 */
		Map<String, String> map = new HashMap<String, String>();
		JSONObject json = new JSONObject();

		Integer records = list.size();
		Integer totalPages = records / IJsonService.PAGESIZE + 1;

		map.put("total", totalPages.toString());
		map.put("page", currentPage);
		map.put("records", records.toString());

		List<String> rows = new ArrayList<String>();
		for (Room r : list) {
			try {
				IpAddress ipAddr = new IpAddressServiceImpl().getByRoom(r);
				r.setIp(ipAddr);
			} catch (AppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rows.add(r.getJson());
		}

		map.put("rows", rows.toString());

		json.accumulateAll(map);
		return json.toString();
	}

}
