package shop.bluebooktle.backend.book.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Img;

public interface ImgRepository extends JpaRepository<Img, Long> {
	Optional<Img> findByImgUrl(String imgUrl);
}
