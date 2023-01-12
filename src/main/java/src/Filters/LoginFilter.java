package src.Filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import src.Dao.TokenDao;
import src.Models.Token;

import java.io.IOException;
import java.io.PrintWriter;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;


public class LoginFilter implements Filter {
    private final TokenDao dao = new TokenDao();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    protected boolean isAuthenticated(HttpServletRequest request) {
        String tokenHeader = request.getHeader("Authorization");

        if (tokenHeader == null)
            return false;

        String[] values = tokenHeader.split(" ");
        String tokenValue = values[1];
        Token token = dao.getToken(tokenValue);

        return token != null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();

        if (isAuthenticated(req) || path.startsWith("/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpServletResponse resp = (HttpServletResponse) response;
        PrintWriter out = resp.getWriter();
        resp.setStatus(SC_FORBIDDEN);
        out.println("forbidden");
    }


    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
