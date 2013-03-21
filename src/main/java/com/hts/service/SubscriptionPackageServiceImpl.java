package com.hts.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.hts.dao.DAO;
import com.hts.dao.SubscriptionPackageDAOHibernateImpl;
import com.hts.entity.Channel;
import com.hts.entity.SubscriptionPackage;
import com.hts.exceptions.AppException;

public class SubscriptionPackageServiceImpl implements ISubscriptionPackageService {
	final Logger log = Logger.getLogger(this.getClass());
	SubscriptionPackageDAOHibernateImpl subscriptionPackageDAO = new SubscriptionPackageDAOHibernateImpl();

	@Override
	public SubscriptionPackage create(String subscriptionPackageName) throws AppException {
		SubscriptionPackage h = subscriptionPackageDAO.create(new SubscriptionPackage(subscriptionPackageName));
		DAO.close();
		log.info("created subscriptionPackage: " + h.toString());
		return h;
	}

	@Override
	public void update(SubscriptionPackage subscriptionPackage) throws AppException {
		subscriptionPackageDAO.update(subscriptionPackage);
		DAO.close();
		log.info("updated subscriptionPackage: " + subscriptionPackage.toString());
	}

	@Override
	public void delete(SubscriptionPackage subscriptionPackage) throws AppException {
		subscriptionPackageDAO.delete(subscriptionPackage);
		DAO.close();
		log.info("deleted subscriptionPackage: " + subscriptionPackage.toString());
	}

	@Override
	public SubscriptionPackage getById(Integer id) throws AppException {
		return subscriptionPackageDAO.getById(id);
	}

	@Override
	public List<SubscriptionPackage> getByName(String name) throws AppException {
		return subscriptionPackageDAO.getByName(name);
	}

	@Override
	public List<SubscriptionPackage> getAll() throws AppException {
		return subscriptionPackageDAO.getAll();
	}

	@Override
	public void addChannel(SubscriptionPackage subscriptionPackage,
			Channel newChannel) throws AppException {
		List<Channel> channels = subscriptionPackage.getChannels();
		if (channels!=null)
			channels.add(newChannel);	
		else {
			channels=new ArrayList<Channel>();
			channels.add(newChannel);
		}
		subscriptionPackage.setChannels(channels);
		
		subscriptionPackageDAO.update(subscriptionPackage);
		log.info("added channel to subscriptionPackage: " + subscriptionPackage.toString());
	}

	@Override
	public void removeChannel(SubscriptionPackage subscriptionPackage,
			Channel oldChannel) throws AppException {

		List<Channel> channels = subscriptionPackage.getChannels();
		channels.remove(oldChannel);
		subscriptionPackage.setChannels(channels);
		
		subscriptionPackageDAO.update(subscriptionPackage);
		log.info("removed channel from subscriptionPackage: " + subscriptionPackage.toString());
	}

	@Override
	public void removeAllChannels(SubscriptionPackage subscriptionPackage) throws AppException {

		subscriptionPackage.setChannels(null);
		subscriptionPackageDAO.update(subscriptionPackage);
		log.info("removed all channels from subscriptionPackage: " + subscriptionPackage.toString());
	}
	
	@Override
	public String getJson(List<SubscriptionPackage> list, String currentPage) throws AppException {
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
		for (SubscriptionPackage h : list) {
			rows.add( h.getJson());
		}

		map.put("rows",rows.toString());

		json.accumulateAll(map);
		return json.toString();
	}

}
