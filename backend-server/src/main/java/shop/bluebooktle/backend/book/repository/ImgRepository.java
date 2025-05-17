package shop.bluebooktle.backend.book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.Img;

public interface ImgRepository extends JpaRepository<Img, Long> {

	// 특정 이미지 (url) 삭제 여부 조회 (삭제면 true)
	boolean existsByImgUrlAndDeletedAtIsNotNull(String imgUrl);

	// 이미지 (url) 중복 조회 (이미 url이 존재하면 true)
	boolean existsByImgUrl(String imgUrl);

	Optional<Img> findByImgUrl(String imgUrl);

}
