package edu.handong.week12.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {
  private final BookRepository repository;

  public BookService(BookRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public Page<Book> findAll(Pageable pageable) {
    return repository.findAll(pageable);
  }

  // id로 책 한 권을 찾는다. 없으면 404로 바뀌는 예외를 던진다.
  @Transactional(readOnly = true)
  public Book findById(Long id) {
    // DB에서 id로 찾기, 결과는 Optional (없을 수도 있음)
    return repository.findById(id)
        // 있으면 Book 반환, 없으면 예외 → GlobalExceptionHandler에서 404로 응답
        .orElseThrow(() -> new BookNotFoundException(id));
  }

  @Transactional
  public Book create(Book book) {
    return repository.save(book);
  }

  @Transactional
  public Book update(Long id, Book input) {
    Book book = findById(id);
    book.update(input.getTitle(), input.getAuthor(), input.getPrice());
    return book;
  }

  @Transactional
  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new BookNotFoundException(id);
    }
    repository.deleteById(id);
  }
}
