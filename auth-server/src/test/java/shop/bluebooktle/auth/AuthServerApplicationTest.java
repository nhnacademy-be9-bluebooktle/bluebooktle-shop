package shop.bluebooktle.auth;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
public class AuthServerApplicationTest {

	@Test
	public void contextLoads() {
		System.out.println("Auth Server Context Loads Successfully!");
	}

}