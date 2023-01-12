package src.Dao;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import src.Models.Token;

import java.io.IOException;
import java.io.Reader;
import java.util.Date;

public class TokenDao {
    SqlSession session;

    public TokenDao() {
        this.session = createSession();
    }

    private SqlSession createSession() {
        SqlSession session = null;
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            session = sqlSessionFactory.openSession();
        } catch (IOException e) {
            System.out.println("Unable to create session");
        }

        return session;
    }

    public int insert(Token token) {
        int id = session.insert("Models.Token.insert", token);
        session.commit();

        return id;
    }

    public Token getToken(int userid) {
        Token token = session.selectOne("Models.Token.getByUserId", userid);
        session.commit();

        return token;
    }

    public Token getToken(String tokenValue) {
        Token token = session.selectOne("Models.Token.getByToken", tokenValue);
        session.commit();

        return token;
    }

    public Integer deleteExpiredTokens(Date currentDate) {
        Integer tokens = session.delete("Models.Token.getExpiredTokens", currentDate);
        session.commit();

        return tokens;
    }

    public Integer delete(String token) {
        int status = session.delete("Models.Token.delete", token);
        session.commit();

        return status;
    }
}
