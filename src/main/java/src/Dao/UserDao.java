package src.Dao;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import src.Models.User;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class UserDao {
    SqlSession session;

    public UserDao() {
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

    public List<User> getAll() {
        List<User> users = session.selectList("Models.User.getAll");
        session.commit();

        return users;
    }

    public User getCredentials(String username) {
        User user = session.selectOne("Models.User.getCredentials", username);
        session.commit();

        return user;
    }


    public User get(Integer id) {
        User user = session.selectOne("Models.User.selectById", id);
        session.commit();

        return user;
    }

    public int insert(User user) {
        int id = session.insert("Models.User.insert", user);
        session.commit();

        return id;
    }

    public int update(User user) {
        int id = session.update("Models.User.update", user);
        session.commit();

        return id;
    }

    public Integer delete(Integer id) {
        int status = session.delete("Models.User.deleteById", id);
        session.commit();

        return status;
    }
}
