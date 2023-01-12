package src.Servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.*;
import src.Dao.*;
import src.Models.*;
import src.Tools.QuerySplitter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jakarta.servlet.http.HttpServletResponse.*;

public class GameServlet extends HttpServlet {
    private static final Pattern COMMENT_PATTERN_WITH_POST_ID = Pattern.compile("/games\\?gameid=(\\d)+");

    private static final Pattern COMMENT_PATTERN_WITH_ID = Pattern.compile("/games/(\\d)+");

    private static final Pattern ALL_COMMENT_PATTERN = Pattern.compile("/games");

    protected GamesDao dao = new GamesDao();

    protected void processResponse(HttpServletResponse resp) {
        resp.setContentType("application/json; charset=UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        processResponse(response);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();

        String uri = request.getRequestURI();
        String query = request.getQueryString();
        Matcher matcher = COMMENT_PATTERN_WITH_POST_ID.matcher(uri + "?" + query);
        // /games?gameid=1
        if (matcher.matches()) {
            Map<String, String> splitQuery = QuerySplitter.splitQuery(query);
            Integer id = Integer.valueOf(splitQuery.get("gameid"));
            Game game = dao.getById(id);
            String json = gson.toJson(game);
            out.println(json);
            return;
        }
        String[] slpit = uri.split("/");
        // /games/1
        matcher = COMMENT_PATTERN_WITH_ID.matcher(uri);
        if (matcher.matches()) {
            Integer id = Integer.valueOf(slpit[2]);
            Game game = dao.getById(id);
            String jsonString = gson.toJson(game);
            out.println(jsonString);

            return;
        }
        // /games
        matcher = ALL_COMMENT_PATTERN.matcher(uri);
        if (matcher.matches()) {
            List<Game> games = dao.getAll();
            String jsonString = gson.toJson(games);
            out.println(jsonString);

            return;
        }

        response.setStatus(SC_FORBIDDEN);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        processResponse(response);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Game game = gson.fromJson(request.getReader(), Game.class);

        dao.insert(game);
        int id = game.getId();

        if (id == 1) {
            response.setStatus(SC_CREATED);
            String object = gson.toJson(game);
            out.print(object);
        } else {
            response.setStatus(SC_FORBIDDEN);
        }

        out.close();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getRequestURI();
        Matcher matcher = COMMENT_PATTERN_WITH_ID.matcher(pathInfo); // games/1

        PrintWriter out = response.getWriter();
        if (!matcher.matches()) {
            response.setStatus(SC_FORBIDDEN);
            out.println("forbidden");
            return;
        }

        processResponse(response);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Game game = gson.fromJson(request.getReader(), Game.class);

        int id = Integer.parseInt(matcher.group(1));
        game.setId(id);

        int status = dao.update(game);

        if (status == 1) {
            response.setStatus(SC_OK);
            String object = gson.toJson(game);
            out.print(object);
        } else {
            response.setStatus(SC_FORBIDDEN);
        }

        out.close();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getRequestURI();
        Matcher matcher = COMMENT_PATTERN_WITH_ID.matcher(pathInfo);

        PrintWriter out = response.getWriter();
        if (!matcher.matches()) {
            response.setStatus(SC_FORBIDDEN);
            out.println("forbidden");
            return;
        }
        processResponse(response);

        Integer id = Integer.valueOf(matcher.group(1));

        int status = dao.delete(id);

        if (status == 1) {
            response.setStatus(SC_ACCEPTED);
            out.println("updated");
        } else {
            response.setStatus(SC_FORBIDDEN);
            out.println("forbidden");
        }
    }
}
