package src.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.apache.commons.codec.digest.DigestUtils;
import src.Dao.TokenDao;
import src.Dao.UserDao;
import src.Models.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Random;

@MultipartConfig
public class LoginServlet extends HttpServlet {
    UserDao userDao = new UserDao();

    TokenDao tokenDao = new TokenDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Part username = request.getPart("name");
        Part password = request.getPart("password");

        String usernameStr = new BufferedReader(new InputStreamReader(username.getInputStream())).readLine();
        String passwordStr = new BufferedReader(new InputStreamReader(password.getInputStream())).readLine();

        User user = userDao.getCredentials(usernameStr);
        PrintWriter out = response.getWriter();
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.println("forbidden");
            return;
        }

        int salt = user.getSalt();
        String encryptedPassword = DigestUtils.sha1Hex(passwordStr + salt);

        if (encryptedPassword.equals(user.getPassword())) {
            Token token = tokenDao.getToken(user.getId());

            if (token == null) {
                token = createToken(user);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Authorization", "Bearer " + token.getToken());
            out.println("Welcome " + usernameStr);
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.println("forbidden");
        }
    }

    private Token createToken(User user) {
        Token token = new Token();

        String randNumberToken = DigestUtils.sha1Hex(generateRandomNumber() + "");
        token.setToken(randNumberToken);

        int userid = user.getId();
        token.setUserId(userid);

        Date date = new Date(System.currentTimeMillis());
        LocalDate monthLate = date.toLocalDate().plusMonths(1);
        Date expireDate = Date.valueOf(monthLate);

        token.setCreatedDate(new Date(System.currentTimeMillis()));
        token.setExpirationDate(expireDate);

        tokenDao.insert(token);
        return token;
    }

    private int generateRandomNumber() {
        Random rand = new Random();

        return rand.nextInt(0, Integer.MAX_VALUE);
    }
}
