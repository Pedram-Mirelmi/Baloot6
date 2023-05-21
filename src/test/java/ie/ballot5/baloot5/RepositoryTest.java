package ie.ballot5.baloot5;

import ie.baloot6.data.IRepository;
import ie.baloot6.service.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.RetentionPolicy;
import java.sql.Date;
import java.time.LocalDate;

@SpringBootTest(classes = {Repository.class})
class RepositoryTest {

	IRepository repository;

	@BeforeEach
	void setUp() {
		repository = new Repository();
	}

	@Test
	void addUserTest() {
		repository.addUser("Pedram", "pass1", "asdf@ad", Date.valueOf(LocalDate.now()), "addr1", 1000);
	}

}
