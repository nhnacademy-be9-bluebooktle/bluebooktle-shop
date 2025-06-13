package shop.bluebooktle.backend.book.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import shop.bluebooktle.backend.book.entity.Author;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookAuthor;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.common.exception.book.AuthorNotFoundException;
import shop.bluebooktle.common.exception.book.BookAuthorAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookAuthorNotFoundException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;

public interface BookAuthorService {

	AuthorResponse registerBookAuthor(Long bookId, Long authorId);

	List<AuthorResponse> registerBookAuthor(Long bookId, List<Long> authorIdList);

	List<AuthorResponse> updateBookAuthor(Long bookId, List<Long> authorIdList);

}
