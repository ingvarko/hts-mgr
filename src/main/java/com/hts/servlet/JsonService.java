package com.hts.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.hts.dao.RoomDAOHibernateImpl;
import com.hts.entity.BroadcastStream;
import com.hts.entity.Channel;
import com.hts.entity.IpAddress;
import com.hts.entity.Room;
import com.hts.entity.SubscriptionPackage;
import com.hts.exceptions.AppException;
import com.hts.service.BroadcastStreamServiceImpl;
import com.hts.service.ChannelServiceImpl;
import com.hts.service.HotelServiceImpl;
import com.hts.service.IpAddressServiceImpl;
import com.hts.service.RoomServiceImpl;
import com.hts.service.SubscriptionPackageServiceImpl;

/**
 * Servlet implementation class SecurityTest
 */
public class JsonService extends HttpServlet {
	final Logger log = Logger.getLogger(this.getClass());
	private static final long serialVersionUID = 17227839L;

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		// out.write(getB());
		// out.write(getA());
		String entityName = (String) request.getParameter("json");
		String oper = request.getParameter("oper");

		System.out.print("\noper:" + oper);
		String id = (String) request.getParameter("id");

		System.out.print("\nid:" + id);

		if (entityName.equals("hotels")) {
			try {
				out.write(new HotelServiceImpl().getJson(
						new HotelServiceImpl().getAll(), "0"));
			} catch (AppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		} else if (entityName.equals("rooms")) {
			if (oper != null) {
				if (oper.equals("edit")) {
					Room room = new Room();
					IpAddress ipAddr = new IpAddress();
					@SuppressWarnings("unchecked")
					Enumeration<String> en = (Enumeration<String>) request
							.getParameterNames();
					while (en.hasMoreElements()) {
						String paramName = en.nextElement();
						if (paramName.equals("id")) {
							room.setId(Integer.parseInt(request
									.getParameter(paramName)));
						} else if (paramName.equals("roomName")) {
							room.setRoomName(request.getParameter(paramName));
						} else if (paramName.equals("subPackage")) {
							SubscriptionPackage subscriptionPackage = new SubscriptionPackage();
							subscriptionPackage
									.setSubscriptionPackageName(request
											.getParameter(paramName));
							room.setSubscriptionPackage(subscriptionPackage);
						} else if (paramName.equals("ip")) {
							try {
								ipAddr = new IpAddressServiceImpl()
										.getByRoom(room);
							} catch (AppException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							ipAddr.setIpAddress(request.getParameter(paramName));
						}
					}
					try {
						ipAddr.setRoom(room);
						new IpAddressServiceImpl().update(ipAddr);
						new RoomServiceImpl().update(room);
					} catch (AppException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (oper.equals("del")) {
					Room room = new Room();
					@SuppressWarnings("unchecked")
					Enumeration<String> en = (Enumeration<String>) request
							.getParameterNames();
					while (en.hasMoreElements()) {
						String paramName = en.nextElement();
						if (paramName.equals("id")) {
							room.setId(Integer.parseInt(request
									.getParameter(paramName)));
						}
					}
					try {
						IpAddress ipAddr = new IpAddressServiceImpl()
								.getByRoom(room);
						new IpAddressServiceImpl().delete(ipAddr);
						new RoomServiceImpl().delete(room);
					} catch (AppException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (oper.equals("add")) {
					Room room = new Room();
					IpAddress ipAddress = new IpAddress();
					@SuppressWarnings("unchecked")
					Enumeration<String> en = (Enumeration<String>) request
							.getParameterNames();
					while (en.hasMoreElements()) {
						String paramName = en.nextElement();
						System.out.println(paramName + ":");
						System.out.println(request.getParameter(paramName));
						if (paramName.equals("roomName")) {
							room.setRoomName(request.getParameter(paramName));
						} else if (paramName.equals("subPackage")) {
							SubscriptionPackage subscriptionPackage = new SubscriptionPackage();
							subscriptionPackage
									.setSubscriptionPackageName(request
											.getParameter(paramName));
							room.setSubscriptionPackage(subscriptionPackage);
						} else if (paramName.equals("ip")) {
							ipAddress.setIpAddress(request
									.getParameter(paramName));
						} else if (paramName.equals("userName")) {
							room.setUserName(request.getParameter(paramName));
						} else if (paramName.equals("roomLanguage")) {
							room.setLanguage(request.getParameter(paramName));
						}
					}
					IpAddress ipAddress1 = null;
					try {
						ipAddress1 = new IpAddressServiceImpl()
								.create(ipAddress.getIpAddress());
						new RoomDAOHibernateImpl().create(room);
						ipAddress1.setRoom(room);
						new IpAddressServiceImpl().update(ipAddress1);
					} catch (Exception e) {
						e.printStackTrace();
						if (ipAddress1 != null) {
							try {
								new IpAddressServiceImpl().delete(ipAddress1);
							} catch (Exception ape) {
								ape.printStackTrace();
							}
						} throw new ServletException(e.getMessage());
					}
				}
			}
			try {
				out.write(new RoomServiceImpl().getJson(
						new RoomServiceImpl().getAll(), "0"));
			} catch (AppException e) {
				e.printStackTrace();
			}
			return;
		} else if (entityName.equals("broadcaststreams")) {
			String set = request.getParameter("set");
			if (set.equals("all")) {
				log.info("Processing BroadcastStreams ALL");
				try {
					List<BroadcastStream> blist = new BroadcastStreamServiceImpl()
							.getAllBroadcastStreams();
					String json = new BroadcastStreamServiceImpl().getJson(
							blist, "0");
					out.write(json);
				} catch (AppException e) {
					e.printStackTrace();
				}
				return;
			} else if (set.equals("active")) {
				log.info("Processing BroadcastStreams ACTIVE");
				try {
					List<BroadcastStream> blist = new BroadcastStreamServiceImpl()
							.getAllActiveBroadcastStreams();
					String json = new BroadcastStreamServiceImpl().getJson(
							blist, "0");
					out.write(json);
				} catch (AppException e) {
					e.printStackTrace();
				}
				return;
			} else if (set.equals("inactive")) {
				log.info("Processing BroadcastStreams INACTIVE");
				try {
					List<BroadcastStream> blist = new BroadcastStreamServiceImpl()
							.getAllInactiveBroadcastStreams();
					String json = new BroadcastStreamServiceImpl().getJson(
							blist, "0");
					out.write(json);
				} catch (AppException e) {
					e.printStackTrace();
				}
				return;
			} else if (set.equals("nochannel")) {
				log.info("Processing BroadcastStreams NO_CHANNEL");
				try {
					List<BroadcastStream> blist = new BroadcastStreamServiceImpl()
							.getAllNoChannelBroadcastStreams();
					String json = new BroadcastStreamServiceImpl().getJson(
							blist, "0");
					out.write(json);
				} catch (AppException e) {
					e.printStackTrace();
				}
				return;
			}
		} else if (entityName.equals("subpackages")) {
			try {
				List<SubscriptionPackage> slist = new SubscriptionPackageServiceImpl()
						.getAll();
				String json = new SubscriptionPackageServiceImpl().getJson(
						slist, "0");
				out.write(json);
			} catch (AppException e) {
				e.printStackTrace();
			}
		} else if (entityName.equals("channels")) {
			try {
				List<Channel> chlist = new ChannelServiceImpl().getAll();
				String json = new ChannelServiceImpl().getJson(chlist, "0");
				out.write(json);

			} catch (AppException e) {
				e.printStackTrace();
			}

		}
		return;

	}
}

// http://localhost:5080/oflaDemo/JsonService