import ie.baloot6.model.Discount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HibernateTest {

    EntityManagerFactory entityManagerFactory;
    @BeforeEach
    void setUp() {
        var registry = new StandardServiceRegistryBuilder().configure().build();
        entityManagerFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    @AfterEach
    void tearDown() {
//        registry
    }

    @Test
    void test1() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();

        entityManager.persist(new Discount("asdf", 10));

        entityManager.getTransaction().commit();

    }
}
