package src.Dao;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import src.Models.Order;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class OrdersDao {
    SqlSession session;

    public OrdersDao() {
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

    public List<Order> getAll() {
        List<Order> orders = session.selectList("Models.Order.getAll");
        session.commit();

        return orders;
    }

    public Order get(Integer id) {
        Order order = session.selectOne("Models.Order.selectById", id);
        session.commit();

        return order;
    }

    public int insert(Order order) {
        int id = session.insert("Models.Order.insert", order);
        session.commit();

        return id;
    }

    public int update(Order order) {
        int id = session.update("Models.Order.update", order);
        session.commit();

        return id;
    }

    public Integer delete(Integer id) {
        int status = session.delete("Models.Order.deleteById", id);
        session.commit();

        return status;
    }
}
