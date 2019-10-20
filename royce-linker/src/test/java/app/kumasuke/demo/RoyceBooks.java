package app.kumasuke.demo;

import app.kumasuke.royce.Royce;
import app.kumasuke.royce.mapper.Mappers;
import app.kumasuke.test.util.Book;
import app.kumasuke.test.util.TestDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RoyceBooks {
    private static final Logger logger = LoggerFactory.getLogger(RoyceBooks.class);
    private static final Royce royce = Royce.nonTransactional(TestDatabase.getInstance());

    private static final int PER_PAGE = 10;

    private static long getCountOfBooks() {
        return royce.tryRead(linker -> {
            return linker.selectOne(Mappers.firstColumnToLong(), "SELECT count(id) FROM `test`.`books`")
                    .orElseThrow(() -> new IllegalStateException("cannot get count of 'books'"));
        }).handle(e -> {
            logger.error("error encountered when reading the count of books", e);
            return 0L;
        });
    }

    @SuppressWarnings("SameParameterValue")
    private static List<Book> getBooksByPage(final int page, final int perPage) {
        return royce.tryRead(linker -> {
            final String query = "SELECT * FROM `test`.`books` LIMIT :offset, :count";
            final int offset = (page - 1) * perPage;

            Map<String, Integer> parameters = new HashMap<>();
            parameters.put("offset", offset);
            parameters.put("count", perPage);

            return linker.namedSelectMany(Mappers.toBean(Book.class), query, parameters);
        }).handle(e -> {
            logger.error("error encountered when reading book list", e);
            return Stream.empty();
        }).collect(Collectors.toList());
    }

    private static boolean saveBook(final Book book) {
        final boolean isCreate = book.getId() == null;
        if (isCreate) {
            return royce.transactional().tryLink(linker -> {
                final int id = linker.callAndReturnOne(Mappers.firstColumnToInteger(),
                                                       "{call `test`.`seq_nextval`('books')}")
                        .orElseThrow(() -> new IllegalStateException("cannot generate new sequence for 'books'"));
                final String update = "INSERT INTO `test`.`books` (id, `name`, author, publisher, price, `language`, " +
                        "pages, isbn, release_date) VALUES(:id, :name, :author, :publisher, :price, :language," +
                        ":pages, :isbn, :releaseDate)";

                book.setId(id);
                return linker.namedUpdate(update, book) == 1;
            }).handle(e -> {
                logger.error("error encountered when creating book", e);
                return false;
            });
        } else {
            return royce.tryWrite(linker -> {
                final String update = "UPDATE `test`.`books` " +
                        "SET `name` = :name, author = :author, publisher = :publisher, " +
                        "  price = :price, `language` = :language, pages = :pages, " +
                        "  isbn = :isbn, release_date = :releaseDate " +
                        "WHERE id = :id";
                return linker.namedUpdate(update, book) == 1;
            }).handle(e -> {
                logger.error("error encountered when updating book", e);
                return false;
            });
        }
    }

    public static void main(String[] args) {
        // Show all books
        final long nBooks = getCountOfBooks();
        final int nPages = (int) ((nBooks + PER_PAGE - 1) / PER_PAGE);

        for (int i = 1; i <= nPages; i++) {
            List<Book> books = getBooksByPage(i, PER_PAGE);
            books.forEach(System.out::println);
        }

        // Save book
        Book book = new Book();
        book.setId(10);
        book.setName("Code: The Hidden Language of Computer Hardware and Software");
        book.setAuthor("Charles Petzold");
        book.setPublisher("Microsoft Press; 1 edition (October 21, 2000)");
        book.setPrice(new BigDecimal("16.37"));
        book.setLanguage("English");
        book.setPages(400);
        book.setIsbn("978-0735611313");
        book.setReleaseDate(LocalDate.of(2018, 10, 21));

        boolean success = saveBook(book);
        if (success) {
            System.out.println("Book saved: " + book);
        }
    }
}
