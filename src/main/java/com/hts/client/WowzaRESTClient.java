package com.hts.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class WowzaRESTClient {

	// TODO: need to get remote server address somehow
	// TODO: move to web.xml properties
	
	private final static String BASEURL = "http://localhost:8086/wzhttpservlet?getAllFlvs";

	private final static Logger log = Logger.getLogger(WowzaRESTClient.class);

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		log.info(getAllFlvs());
	}

	public static Map<String, Map<String, Object>> getAllFlvs() throws JsonParseException, JsonMappingException,
			IOException {
		Client client = Client.create();

		log.info("getAllFlvs ");

		WebResource webResource = null;
		try {
			webResource = client.resource(getBaseURL());
		}
		catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
			return null;
		}

		ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
		String result = response.getEntity(String.class);


		 ObjectMapper mapper = new ObjectMapper();
		 @SuppressWarnings("unchecked")
		 Map<String, Map<String, Object>> m = mapper.readValue(result, Map.class);
		
		 return m;
	}

	public static String getBaseURL() throws UnsupportedEncodingException {
		return BASEURL;
	}

}
