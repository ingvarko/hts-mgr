package com.hts.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.hts.dao.BroadcastStreamDAOHibernateImpl;
import com.hts.dao.DAO;
import com.hts.entity.BroadcastStream;
import com.hts.exceptions.AppException;


public class BroadcastStreamServiceImpl implements IBroadcastStreamService {
	final Logger log = Logger.getLogger(this.getClass());

	static BroadcastStreamDAOHibernateImpl broadcastStreamDAO = new BroadcastStreamDAOHibernateImpl();

	@Override
	public BroadcastStream create(String name) throws AppException {

		BroadcastStream stream = new BroadcastStream(name);
		stream.setPublishedDate(new Date());
		stream.setUpdateDate(stream.getPublishedDate());
		stream.setActive(BroadcastStream.ACTIVE);

		broadcastStreamDAO.create(stream);
		DAO.close();
		log.info("registerBroadcastStream:" + stream);
		return stream;
	}

	@Override
	public void delete(BroadcastStream broadcastStream) throws AppException {

		broadcastStreamDAO.delete(broadcastStream);
		DAO.close();
		log.info("deleted BroadcastStream:" + broadcastStream.getName());
	}

	/**
	 * Finds all streams by given name which could remains from previous server starts. 
	 * Set in all streams @unpublishedDate to new Date() and @status to inactive.
	 */
	@Override
	public void unregisterBroadcastStream(String broadcastStreamName) throws AppException {
		List<BroadcastStream> list = broadcastStreamDAO.getActiveByName(broadcastStreamName);

		for (BroadcastStream str : list) {
			setInactive(str);
			broadcastStreamDAO.update(str);
			log.info("unregisterBroadcastStream:" + str);
		}
		DAO.close();
	}

	private void setInactive(BroadcastStream str) {
		str.setUpdateDate(new Date());
		str.setUnpublishedDate(new Date());
		str.setActive(BroadcastStream.INACTIVE);
	}

	@Override
	public void setActive(BroadcastStream str) throws AppException {
		str.setUpdateDate(new Date());
		str.setUnpublishedDate(null);
		str.setActive(BroadcastStream.ACTIVE);
		
		broadcastStreamDAO.update(str);
		log.info("activated BroadcastStream: " + str);
		DAO.close();

	}

	@Override
	public void unregisterAllActiveBroadcastStreams() throws AppException {
		List<BroadcastStream> list = broadcastStreamDAO.getAllActive();

		for (BroadcastStream str : list) {
			setInactive(str);
			broadcastStreamDAO.update(str);
			log.info("unregisterBroadcastStream:" + str);
		}
	}

	@Override
	public List<BroadcastStream> getAllBroadcastStreams() throws AppException {
		List<BroadcastStream> list = broadcastStreamDAO.getAll();
		DAO.close();
		log.info("getAllBroadcastStreams:" + list);
		return list;
	}
	
	@Override
	public List<BroadcastStream> getAllActiveBroadcastStreams() throws AppException {
		List<BroadcastStream> list = broadcastStreamDAO.getAllActive();
		DAO.close();
		log.info("getAllActiveBroadcastStreams:" + list);
		return list;
	}
	
	@Override
	public List<BroadcastStream> getAllInactiveBroadcastStreams() throws AppException {
		List<BroadcastStream> list = broadcastStreamDAO.getAllInactive();
		DAO.close();
		log.info("getAllInactiveBroadcastStreams:" + list);
		return list;
	}
	
	@Override
	public List<BroadcastStream> getAllNoChannelBroadcastStreams() throws AppException {
		List<BroadcastStream> list = broadcastStreamDAO.getAllNoChannel();
		DAO.close();
		log.info("getNoChannelBroadcastStreams:" + list);
		return list;
	}
	
	@Override
	public BroadcastStream getById(Integer streamId) throws AppException {
		BroadcastStream b = broadcastStreamDAO.getById(streamId);
		DAO.close();
		return b;
	}

	@Override
	public List<BroadcastStream> getByName(String name) throws AppException {
		List<BroadcastStream> b = broadcastStreamDAO.getByName(name);
		DAO.close();
		return b;
	}

	@Override
	public List<BroadcastStream> getActiveByName(String name) throws AppException {
		List<BroadcastStream> b = broadcastStreamDAO.getActiveByName(name);
		DAO.close();
		return b;
	}
	
	@Override
	public String getJson(List<BroadcastStream> list, String currentPage) throws AppException {
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
		for (BroadcastStream h : list) {
			rows.add( h.getJson());
		}

		map.put("rows",rows.toString());

		json.accumulateAll(map);
		return json.toString();
	}
}