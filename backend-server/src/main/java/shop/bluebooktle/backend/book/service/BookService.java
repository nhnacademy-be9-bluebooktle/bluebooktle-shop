package shop.bluebooktle.backend.book.service;

import java.util.List;

import shop.bluebooktle.backend.book.entity.Book;

public interface BookService {
	void registerBook(Book book);
	Book getBookById(Long id);
	List<Book> getBookByTitle(String title);
	boolean existsBookById(Long id);
}
