package com.hts.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hts.entity.Room;
import com.hts.entity.SubscriptionPackage;
import com.hts.exceptions.AppException;
import com.hts.service.HotelServiceImpl;
import com.hts.service.RoomServiceImpl;
import java.util.*;

/**
 * Servlet implementation class SecurityTest
 */
public class JsonService extends HttpServlet {

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
			if (oper!=null) {
				if (oper.equals("edit")) {
					Room room = new Room();
					Enumeration en = request.getParameterNames();
					while (en.hasMoreElements()) {
						String paramName = (String) en.nextElement();
						if (paramName.equals("roomNumber")) {
							room.setId(Integer.parseInt(request
									.getParameter(paramName)));
						}else if (paramName.equals("room_name")) {
							room.setRoomName(request.getParameter(paramName));
						}else if (paramName.equals("subspackage")) {
							SubscriptionPackage subscriptionPackage = new SubscriptionPackage();
							subscriptionPackage
									.setSubscriptionPackageName(request
											.getParameter(paramName));
							room.setSubscriptionPackage(subscriptionPackage);
						}
					}
					try {
						new RoomServiceImpl().update(room);
					} catch (AppException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			try {
				out.write(new RoomServiceImpl().getJson(
						new RoomServiceImpl().getAll(), "0"));
			} catch (AppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
	}
}

// http://localhost:5080/oflaDemo/JsonService