package src.Servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.*;
import src.Dao.*;
import src.Models.*;
import src.Models.Order;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jakarta.servlet.http.HttpServletResponse.*;


public class OrderServlet extends HttpServlet {

    private static final Pattern POST_PATTERN_WITH_ID = Pattern.compile("/orders/(\\d)+");

    private static final Pattern ALL_POST_PATTERN = Pattern.compile("/orders");

    OrdersDao dao = new OrdersDao();

    protected void processResponse(HttpServletResponse resp) {
        resp.setContentType("application/json; charset=UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        processResponse(response);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();

        String pathInfo = request.getRequestURI();
        Matcher matcher = POST_PATTERN_WITH_ID.matcher(pathInfo);
        String[] slpit = pathInfo.split("/");

        // orders/1
        if (matcher.matches()) {
            Integer id = Integer.valueOf(slpit[2]);
            Order order = dao.get(id);
            String json = gson.toJson(order);
            out.println(json);
            return;
        }

        // /orders
        matcher = ALL_POST_PATTERN.matcher(pathInfo);
        if (matcher.matches()) {
            List<Order> orders = dao.getAll();
            for (Order order : orders) {
                String json = gson.toJson(order);
                out.println(json);
            }
            return;
        }

        response.setStatus(SC_FORBIDDEN);
        out.println("forbidden");}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        processResponse(response);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Order order = gson.fromJson(request.getReader(), Order.class);

        dao.insert(order);
        int id = order.getId();

        if (id == 1) {
            response.setStatus(SC_CREATED);
            String object = gson.toJson(order);
            out.print(object);
        } else {
            response.setStatus(SC_FORBIDDEN);
        }

        out.close();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getRequestURI();
        Matcher matcher = POST_PATTERN_WITH_ID.matcher(pathInfo); // orders/1

        PrintWriter out = response.getWriter();
        if (!matcher.matches()) {
            response.setStatus(SC_FORBIDDEN);
            out.println("forbidden");
            return;
        }

        processResponse(response);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Order order = gson.fromJson(request.getReader(), Order.class);

        int id = Integer.parseInt(matcher.group(1));
        order.setId(id);

        int status = dao.update(order);

        if (status == 1) {
            response.setStatus(SC_OK);
            String object = gson.toJson(order);
            out.print(object);
        } else {
            response.setStatus(SC_FORBIDDEN);
        }

        out.close();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getRequestURI();
        Matcher matcher = POST_PATTERN_WITH_ID.matcher(pathInfo); // orders/1

        PrintWriter out = response.getWriter();
        if (!matcher.matches()) {
            response.setStatus(SC_FORBIDDEN);
            out.println("forbidden");
            return;
        }
        processResponse(response);

        Integer orderId = Integer.valueOf(matcher.group(1));

        int status = dao.delete(orderId);

        if (status == 1) {
            response.setStatus(SC_ACCEPTED);
            out.println("deleted");
        } else {
            response.setStatus(SC_FORBIDDEN);
            out.println("forbidden");
        }
    }
}
