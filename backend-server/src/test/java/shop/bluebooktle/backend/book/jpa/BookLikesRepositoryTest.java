package shop.bluebooktle.backend.book.jpa;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookLikes;
import shop.bluebooktle.backend.book.entity.BookLikesId;
import shop.bluebooktle.backend.book.repository.BookLikesRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.config.JpaAuditingConfiguration;
import shop.bluebooktle.backend.config.QueryDslConfig;
import shop.bluebooktle.backend.user.repository.MembershipLevelRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.converter.ProfileAwareStringCryptoConverter;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.util.CryptoUtils;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, JpaAuditingConfiguration.class, CryptoUtils.class,
	ProfileAwareStringCryptoConverter.class})
class BookLikesRepositoryTest {

	@Autowired
	private BookLikesRepository bookLikesRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private MembershipLevelRepository membershipLevelRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EntityManager em;

	// 테스트용 MembershipLevel + User 생성
	private User createUser() {
		MembershipLevel ml = membershipLevelRepository.save(
			MembershipLevel.builder()
				.name("BASIC")
				.rate(1)
				.minNetSpent(BigDecimal.ZERO)
				.maxNetSpent(BigDecimal.valueOf(100_000))
				.build()
		);

		return userRepository.save(
			User.builder()
				.membershipLevel(ml)
				.loginId("user" + System.nanoTime()) // 중복 방지를 위해 매번 유니크한 값을 넣어줌
				.encodedPassword("pwd123")
				.name("테스터")
				.nickname("nick" + System.nanoTime())
				.email("test" + System.nanoTime() + "@example.com")
				.birth("2003-02-02")
				.phoneNumber("01012345678")
				.type(UserType.USER)
				.status(UserStatus.ACTIVE)
				.build()
		);
	}

	// 테스트용 Book 생성
	private Book createBook(String title) {
		return bookRepository.save(
			Book.builder()
				.title(title)
				.description("설명")
				.isbn("1234567890123")
				.build()
		);
	}

	@Test
	@DisplayName("특정 사용자가 책에 좋아요 눌렀는지 확인 - 성공")
	void existsByUserAndBook() {
		User user = createUser();
		Book book = createBook("나미야 잡화점의 기적");

		// 좋아요 저장
		bookLikesRepository.save(new BookLikes(book, user));
		em.flush();
		em.clear();

		// 조회 검증
		boolean exists = bookLikesRepository.existsByUser_IdAndBook_Id(user.getId(), book.getId());
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("사용자가 누른 좋아요 전체 조회 - 성공")
	void findAllByUser() {
		User user1 = createUser();
		User user2 = createUser();
		Book book1 = createBook("마시멜로 이야기");
		Book book2 = createBook("객체지향의 사실과 오해");

		// u1 이 두 책에, u2 가 한 책에 좋아요
		bookLikesRepository.save(new BookLikes(book1, user1));
		bookLikesRepository.save(new BookLikes(book2, user1));
		bookLikesRepository.save(new BookLikes(book1, user2));
		em.flush();
		em.clear();

		List<BookLikes> list = bookLikesRepository.findAllByUser_Id(user1.getId());
		assertThat(list)
			.hasSize(2)
			.extracting(bl -> bl.getBook().getId())
			.containsExactlyInAnyOrder(book1.getId(), book2.getId());
	}

	@Test
	@DisplayName("단건 좋아요 조회 - 성공")
	void findByUserAndBook() {
		User user = createUser();
		Book book = createBook("눈보라 체이스");

		BookLikes saved = bookLikesRepository.save(new BookLikes(book, user));
		em.flush();
		em.clear();

		BookLikes found = bookLikesRepository.findByUser_IdAndBook_Id(user.getId(), book.getId());
		assertThat(found).isNotNull();
		assertThat(found.getId()).isEqualTo(new BookLikesId(user.getId(), book.getId()));
	}

	@Test
	@DisplayName("도서별 좋아요 수 집계 - 성공")
	void countByBook() {
		User user1 = createUser();
		User user2 = createUser();
		Book book = createBook("로미오와 줄리엣");

		bookLikesRepository.save(new BookLikes(book, user1));
		bookLikesRepository.save(new BookLikes(book, user2));
		em.flush();
		em.clear();

		long cnt = bookLikesRepository.countByBook_Id(book.getId());
		assertThat(cnt).isEqualTo(2L);
	}
}
