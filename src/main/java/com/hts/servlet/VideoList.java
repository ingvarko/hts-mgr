package com.hts.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.hts.client.Red5RESTClient;
import com.hts.client.WowzaRESTClient;
import com.hts.entity.BroadcastStream;
import com.hts.entity.Channel;
import com.hts.exceptions.AppException;
import com.hts.service.BroadcastStreamServiceImpl;
import com.hts.service.ChannelServiceImpl;

public class VideoList extends HttpServlet {

	private static final long serialVersionUID = 17227839L;

	private static final String HTSWOWZAAPPLIVE = "wowza-hts";

	private static final String HTSWOWZAAPPVOD = "vod";

	final Logger log = Logger.getLogger(this.getClass());

	protected ChannelServiceImpl channelService = new ChannelServiceImpl();

	protected BroadcastStreamServiceImpl broadcastService = new BroadcastStreamServiceImpl();

	// <context-param>
	// <param-name>hotel-manager.mediaserver</param-name>
	// <!-- Values can be "Red5" or "Wowza" -->
	// <param-value>Wowza</param-value>
	// </context-param>
	//
	// <context-param>
	// <param-name>hotel-manager.mediaserver.IP</param-name>
	// <param-value>localhost</param-value>
	// </context-param>

	// <context-param>
	// <param-name>hotel-manager.mediaserver.port</param-name>
	// <param-value>8086</param-value>
	// </context-param>

	// <context-param>
	// <param-name>hotel-manager.mediaserver.servlet.url</param-name>
	// <param-value>wzhttpservlet?getAllFlvs</param-value>
	// </context-param>
	protected static String mediaserverIP = "localhost";

	protected static String mediaserverPort = "8086";

	protected static String mediaserverServletUrl = "wzhttpservlet?getAllFlvs";

	protected static String mediaServer = "Wowza";

	protected static String mediaServerURL = buildMediaServerURL();

	@Override
	public void init() throws ServletException {
		mediaServer = getServletContext().getInitParameter("hotel-manager.mediaserver");
		log.debug("Detecting mediaServer: " + mediaServer);

		mediaserverIP = getServletContext().getInitParameter("hotel-manager.mediaserver.IP");
		mediaserverPort = getServletContext().getInitParameter("hotel-manager.mediaserver.Port");
		mediaserverServletUrl = getServletContext().getInitParameter("hotel-manager.mediaserver.servlet.url");

		mediaServerURL = "http://" + mediaserverIP + ":" + mediaserverPort + "/" + mediaserverServletUrl;
		log.debug("Detecting mediaServerURL: " + mediaServerURL);
		super.init();
	}

	private static String buildMediaServerURL() {
		return mediaServerURL = "http://" + mediaserverIP + ":" + mediaserverPort + "/" + mediaserverServletUrl;
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 *      URL:http://localhost:9080/hotel-manager/videoList.xml
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		// TODO Add check for room access permission. If no access extend XML to mark channel inactive but present

		log.debug("executing VideoList.Service...");

		PrintWriter out = response.getWriter();
		ArrayList<Channel> channels;
		try {
			channels = getChannels();
		}
		catch (AppException e1) {
			out.print(e1.getStackTrace());
			return;
		}

		try {
			out.print(arraytoXML(channels, mediaserverIP + "/" + HTSWOWZAAPPVOD, mediaserverIP + "/" + HTSWOWZAAPPLIVE));
		}
		catch (AppException e) {
			log.error(e);
		}
		catch (ParserConfigurationException e) {
			log.error(e);
		}
		catch (TransformerException e) {
			log.error(e);
		}

	}

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException, AppException {
		new VideoList().getChannels();
	}

	/**
	 * 
	 * @return Channels to display in xml
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws AppException
	 */
	ArrayList<Channel> getChannels() throws JsonParseException, JsonMappingException, IOException, AppException {

		ArrayList<Channel> channels = new ArrayList<Channel>();

		Map<String, Map<String, Object>> flvMap = null;

		if (mediaServer == null)
			throw new AppException("Unable to get web.xml context param for mediaserver");

		if (mediaServerURL == null)
			throw new AppException("Unable to get web.xml context param for mediaserverURL");

		if (mediaServer.equals("Red5")) {
			flvMap = Red5RESTClient.getAllFlvs();
		}

		if (mediaServer.equals("Wowza")) {
			flvMap = WowzaRESTClient.getAllFlvs(mediaServerURL);
		}

		if (flvMap == null)
			throw new AppException("unable to get flv list from mediaserver. mediaserverURL: " + mediaServerURL);

		for (Entry<String, Map<String, Object>> entry : flvMap.entrySet()) {
			Channel channel = getChannelByFile(entry.getKey());
			if (channel == null) {
				// TODO create a service to update broadcast services based on available flvs.
				// the problem is if nobody calls videolist.xml the hotel manager app can't see new fls.

				BroadcastStream broadcastStream = broadcastService.create(entry.getKey(), BroadcastStream.VOD);
				channelService.create(entry.getKey(), broadcastStream.getName());
				// channels.add(getChannelByFile(entry.getKey()));
			}
			else {
				List<BroadcastStream> broadcastStreams = broadcastService.getByName(entry.getKey());
				if (!broadcastStreams.isEmpty())
					broadcastService.setActive(broadcastStreams.get(0));
				// channels.add(channel);
			}
		}

		// Gettoing broadcast streams as channels
		// Very cold in the office. lost of typos
		// Office was located on Kotyka 7, room 403
		// New office Gazova 26, office 412. pretty cozy
		ChannelServiceImpl channelServiceImpl = new ChannelServiceImpl();

		try {
			channels.addAll(channelServiceImpl.getActive());
		}
		catch (AppException e) {
			log.error(e);
		}
		return channels;
	}

	Channel getChannelByFile(String fileName) throws AppException {
		log.debug("Getting channel for vod: " + fileName);
		return channelService.getByBroadcastName(fileName);

	}

	/**
	 * Converts HashMap to XML
	 * 
	 * @param channels
	 * @param vodURL
	 * @param liveStreamURL
	 * @return
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws AppException
	 */
	String arraytoXML(ArrayList<Channel> channels, String vodURL, String liveStreamURL)
			throws ParserConfigurationException, TransformerException, AppException {

		/**
		 * <rss version="2.0"> <channel> <item> <title>Argentina Football Match1</title>
		 * <link>rtmp://localhost/oflaDemo/red5StreamDemo</link> <description> Goals from a football match.
		 * </description> </item> </channel>
		 * 
		 */

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("rss");
		rootElement.setAttribute("version", "2.0");
		doc.appendChild(rootElement);

		Element domChannel = doc.createElement("channel");
		rootElement.appendChild(domChannel);

		Iterator<Channel> iter = channels.iterator();
		while (iter.hasNext()) {
			Channel ch = (Channel) iter.next();

			Element item = doc.createElement("item");
			domChannel.appendChild(item);

			Element title = doc.createElement("title");
			title.appendChild(doc.createTextNode(ch.getChannelName()));
			item.appendChild(title);

			Element link = doc.createElement("link");

			List<BroadcastStream> blist = broadcastService.getByName(ch.getBroadcastStream());
			BroadcastStream b = null;

			if (!blist.isEmpty())
				b = blist.get(0);
			else
				throw new AppException("Mutated broadcast streams. don't correspond to channel table");

			if (b.getStreamType().equals(BroadcastStream.VOD))
				link.appendChild(doc.createTextNode("rtmp://" + vodURL + "/" + ch.getBroadcastStream()));
			else if (b.getStreamType().equals(BroadcastStream.LIVE))
				link.appendChild(doc.createTextNode("rtmp://" + liveStreamURL + "/" + ch.getBroadcastStream()));

			item.appendChild(link);

			Element description = doc.createElement("description");
			String descStr = ch.getDescription();
			
			descStr = (descStr != null) ? descStr : new String("");
			
			description.appendChild(doc.createTextNode(descStr));
			item.appendChild(description);

		}

		// ///////////////
		// Output the XML
		// set up a transformer
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		// create string from xml tree
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(doc);
		trans.transform(source, result);
		String xmlString = sw.toString();

		return xmlString;

	}
}
