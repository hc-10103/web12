## 프로젝트 정보
### 프로젝트 소개
Book 데이터를 관리하는 REST API 프로그램. DTO, 입력값 검증, 예외 처리, 페이지네이션, Swagger 문서화가
추가되었다.

### Github URL
https://github.com/2026-2-WebService/assign12-c01-22500819

## 환경설정 및 실행방법
1. Java 17, Spring Boot 3.5.x, MariaDB 준비
2. 환경변수 DB_URL, DB_USERNAME, DB_PASSWORD를 자신의 MariaDB에 맞게 설정. 설정하지 않으면 메모리
  H2 DB로 실행된다.
3. ./gradlew bootRun / IntelliJ: Week12Application.main() 실행
4. 접속: http://localhost:3000/api/books , Swagger:
  http://localhost:3000/swagger-ui.html

## API
GET /api/books?page=0&size=5&sort=price,desc
책 목록 조회 (페이지), 성공 응답: 200

GET /api/books/{id}
책 한 권 조회, 성공 응답: 200 / 없으면 404

POST /api/books
책 등록, 성공 응답: 201

PUT /api/books/{id}
책 수정, 성공 응답: 200

DELETE /api/books/{id}
책 삭제, 성공 응답: 204

## 소스코드 분석

### Project Structure & JPA

- BookController, BookService, BookRepository의 역할과 요청 처리 흐름을 설명하세요. :

  BookController는 /api/books로 오는 요청을 받는 곳이고 list, get, create, update,
  delete 메서드가 있다. BookService는 저장, 수정, 삭제 같은 기능을 처리하고 책이 없으면 예외를 던진다.
  BookRepository는 JpaRepository를 상속받아서 DB에 접근할 때 쓴다. Controller에서 Service를
  호출하고 Service가 Repository를 사용하는 구조이고, 결과는 BookResponse로 바꿔서 응답한다.

- JpaRepository<Book, Long>만 선언해도 findAll(), save()를 사용할 수 있는 이유를 설명하세요. :

  BookRepository가 상속한 JpaRepository 안에 findAll(), findById(), save(),
  deleteById() 같은 메서드가 이미 있기 때문이다. <Book, Long>은 Book Entity를 쓰고 id 타입이
  Long이라는 뜻이다. 구현 클래스는 Spring Data JPA가 실행할 때 알아서 만들어준다.

### DTO & HTTP Response

- 기존 API가 Book Entity를 직접 요청·응답에 사용할 때 발생할 수 있는 문제를 설명하세요. :

  Book.java를 그대로 요청으로 받으면 id처럼 클라이언트가 보내면 안 되는 값까지 들어올 수 있다. 응답할 때도 Entity
  필드가 전부 나가서 보여주면 안 되는 정보가 노출될 수 있다. 또 Entity를 수정하면 API 모양도 같이 바뀐다. 그래서
  BookController에서는 toBook()으로 DTO를 Entity로 바꾸고, 응답은 BookResponse.from()으로
  바꿔서 보낸다.

- Request DTO와 Response DTO를 분리하는 이유와 ResponseEntity가 필요한 상황을 설명하세요. :

  BookRequest는 title, author, price만 받고 검증 어노테이션을 붙이는 용도다. BookResponse는
  from()에서 id까지 포함해서 보여줄 값만 담는다. 요청이랑 응답에 필요한 값이 달라서 나눴다. ResponseEntity는
  상태코드나 헤더를 정해야 할 때 쓰는데, create()는 201이랑 Location 헤더, delete()는 204를 보내려고
  사용했다.

### Validation & Exception Handling

- @Valid, @NotBlank, @Min은 각각 어느 시점에 어떤 동작을 수행하나요? :

  BookController의 create(), update()에 붙은 @Valid는 요청 JSON이 BookRequest로 바뀐
  다음, 메서드가 실행되기 전에 검사를 하게 한다. BookRequest에서 @NotBlank는 title, author가 비어
  있거나 공백만 있는지 확인하고 @Min(0)은 price가 0보다 작은지 확인한다. @Min은 null은 검사하지 않아서
  price에 @NotNull도 같이 붙였다. 검사에 걸리면 메서드는 실행되지 않고
  MethodArgumentNotValidException이 발생한다.

- @RestControllerAdvice와 @ExceptionHandler를 사용하면 예외 처리 흐름이 어떻게 바뀌나요? :

  원래는 예외를 따로 처리하지 않으면 Spring 기본 에러 응답이 나간다. GlobalExceptionHandler에
  @RestControllerAdvice를 붙이면 Controller에서 난 예외를 여기서 한 번에 받을 수 있다.
  @ExceptionHandler로 예외마다 처리할 메서드를 정해서 handleNotFound()는 404,
  handleValidation()이랑 handleBadJson()은 400으로 응답한다. 이 세 가지는 ErrorResponse
  형식으로 나가고, 따로 처리하지 않은 예외는 기본 에러 응답 그대로 나간다.

### Pagination & OpenAPI

- Pageable과 Page<T>는 각각 어떤 역할을 하며 sort=price,desc는 어떻게 처리되나요? :

  Pageable은 몇 페이지를 몇 개씩, 어떤 순서로 가져올지 담는 요청 정보다. Page<T>는 결과 목록(content)이랑
  전체 개수, 전체 페이지 수 같은 정보를 같이 가지고 있다. sort=price,desc를 보내면 price 내림차순으로 정렬해서
  가져오고, BookService.findAll(pageable)에서 DB 조회할 때 정렬이랑 개수 제한이 같이 적용된다. 값을 안
  보내면 list()의 @PageableDefault 설정대로 5개씩 id 순으로 나온다.

- /v3/api-docs와 /swagger-ui.html의 역할을 구분해서 설명하세요. :

  build.gradle에 springdoc 의존성을 추가하면 두 주소가 생긴다. /v3/api-docs는
  BookController의 API 정보를 JSON으로 정리해서 보여주는 주소다. /swagger-ui.html은 그 JSON을
  화면으로 보여주는 페이지이고, Try it out으로 직접 요청을 보내볼 수 있다.

### 작성한 주석 메서드
- BookController.create() — @Valid 검증, DTO 변환, ResponseEntity로 201 응답
- BookService.findById() — Repository 조회, Optional 처리, 없으면 404 예외
- BookController.list() — Pageable로 페이지 요청, Page<Book>을 BookResponse로 변환
