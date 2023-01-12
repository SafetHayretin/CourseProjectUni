package src.Servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import src.Dao.UserDao;
import src.Models.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jakarta.servlet.http.HttpServletResponse.*;

public class UserServlet extends HttpServlet {
    private static final Pattern USER_PATTERN_WITH_ID = Pattern.compile("/users/(\\d)+");

    private static final Pattern ALL_USER_PATTERN = Pattern.compile("/users");

    UserDao dao = new UserDao();

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
        Matcher matcher = USER_PATTERN_WITH_ID.matcher(pathInfo);// users/1
        if (matcher.matches()) {
            Integer id = Integer.valueOf(matcher.group(1));
            User user = dao.get(id);
            String json = gson.toJson(user);
            out.println(json);
            return;
        }

        // /users
        matcher = ALL_USER_PATTERN.matcher(pathInfo);
        if (matcher.matches()) {
            List<User> users = dao.getAll();
            for (User user : users) {
                String jsonString = gson.toJson(user);
                out.println(jsonString);
            }
            return;
        }

        response.setStatus(SC_FORBIDDEN);
        out.println("forbidden");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        if (!request.getRequestURI().equals("/users/register")) {
            response.setStatus(SC_FORBIDDEN);
            out.println("forbidden");
            return;
        }

        processResponse(response);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        User user = gson.fromJson(request.getReader(), User.class);
        System.out.println(user);
        encryptPassword(user);

        dao.insert(user);
        int id = user.getId();

        if (id > 0) {
            response.setStatus(SC_CREATED);
            String object = gson.toJson(user);
            out.print(object);
        } else {
            response.setStatus(SC_FORBIDDEN);
        }

        out.close();
    }

    private void encryptPassword(User user) {
        Random random = new Random();
        int salt = random.nextInt(0, Integer.MAX_VALUE);
        String password = user.getPassword();
        String encryptedPassword = DigestUtils.sha1Hex(password + salt);
        user.setPassword(encryptedPassword);
        user.setSalt(salt);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getRequestURI();
        Matcher matcher = USER_PATTERN_WITH_ID.matcher(pathInfo); // user/1

        PrintWriter out = response.getWriter();
        if (!matcher.matches()) {
            response.setStatus(SC_FORBIDDEN);
            out.println("forbidden");
            return;
        }

        processResponse(response);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        User user = gson.fromJson(request.getReader(), User.class);
        encryptPassword(user);

        int id = Integer.parseInt(matcher.group(1));
        user.setId(id);

        int status = dao.update(user);

        if (status == 1) {
            response.setStatus(SC_OK);
            String object = gson.toJson(user);
            out.print(object);
        } else {
            response.setStatus(SC_FORBIDDEN);
        }

        out.close();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getRequestURI();
        Matcher matcher = USER_PATTERN_WITH_ID.matcher(pathInfo);

        PrintWriter out = response.getWriter();
        if (!matcher.matches()) {
            response.setStatus(SC_FORBIDDEN);
            out.println("forbidden");
            return;
        }
        processResponse(response);

        int userId =  Integer.parseInt(matcher.group(1));

        int status = dao.delete(userId);

        if (status == 1) {
            response.setStatus(SC_ACCEPTED);
            out.println("deleted");
        } else {
            response.setStatus(SC_FORBIDDEN);
        }
    }
}
