package edu.handong.week12.book;

import edu.handong.week12.book.DTO.BookRequest;
import edu.handong.week12.book.DTO.BookResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    // 책 목록을 페이지 단위로 조회한다. 예: /api/books?page=0&size=5&sort=price,desc
    @GetMapping
    public Page<BookResponse> list(
            // page, size, sort 값이 여기로 들어옴. 안 보내면 5개씩 id 순서
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        // Page<Book>을 받아서 Book 하나씩 BookResponse로 바꿈 (페이지 정보는 그대로)
        return service.findAll(pageable).map(BookResponse::from);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(BookResponse.from(service.findById(id)));
    }

    // 새 책을 등록하고 201 Created로 응답한다.
    @PostMapping
    // @Valid로 BookRequest 값 검사, 틀리면 여기 안 들어오고 400
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest input) {
        // 받은 값을 Book으로 바꿔서 저장
        Book saved = service.create(toBook(input));
        // 새로 저장된 책 주소 (예: /api/books/3)
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(saved.getId()).toUri();
        // 201 Created로 응답, 위에서 만든 주소는 Location 헤더에 들어감
        return ResponseEntity.created(location).body(BookResponse.from(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(@PathVariable Long id, @Valid @RequestBody BookRequest input) {
        return ResponseEntity.ok(BookResponse.from(service.update(id, toBook(input))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Book toBook(BookRequest input) {
        return new Book(input.title(), input.author(), input.price());
    }
}
