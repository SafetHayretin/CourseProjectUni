package src.Dao;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import src.Models.Game;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class GamesDao {
    SqlSession session;

    public GamesDao() {
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

    public List<Game> getAll() {
        List<Game> games = session.selectList("Models.Game.getAll");
        session.commit();

        return games;
    }

    public Game getById(Integer id) {
        Game game = session.selectOne("Models.Game.selectById", id);
        session.commit();

        return game;
    }

    public int insert(Game game) {
        int id = session.insert("Models.Game.insert", game);
        session.commit();

        return id;
    }

    public int update(Game game) {
        int id = session.update("Models.Game.update", game);
        session.commit();

        return id;
    }

    public Integer delete(Integer id) {
        int status = session.delete("Models.Game.deleteById", id);
        session.commit();

        return status;
    }
}
