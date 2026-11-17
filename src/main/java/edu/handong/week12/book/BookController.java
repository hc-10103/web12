package edu.handong.week12.book;

import edu.handong.week12.book.DTO.BookRequest;
import edu.handong.week12.book.DTO.BookResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookResponse> list() {
        return service.findAll().stream().map(BookResponse::from).toList();
    } // TODO: Pageable

    @GetMapping("/{id}")
    public BookResponse get(@PathVariable Long id) {
        return BookResponse.from(service.findById(id));
    }

    @PostMapping
    public BookResponse create(@RequestBody BookRequest input) {
        return BookResponse.from(service.create(toBook(input)));
    } // TODO: @Valid, ResponseEntity 201

    @PutMapping("/{id}")
    public BookResponse update(@PathVariable Long id, @RequestBody BookRequest input) {
        return BookResponse.from(service.update(id, toBook(input)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    } // TODO: 204

    private Book toBook(BookRequest input) {
        return new Book(input.title(), input.author(), input.price());
    }
}
