package app.kumasuke.test.util;

import app.kumasuke.royce.mapper.ResultSetMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@SuppressWarnings("unused")
public class Book {
    private Integer id;
    private String name;
    private String author;
    private String publisher;
    private BigDecimal price;
    private String language;
    private int pages;
    private String isbn;
    private LocalDate releaseDate;

    public static ResultSetMapper<Book> mapper() {
        return rs -> {
            Book b = new Book();
            b.id = rs.getInt("id");
            b.name = rs.getString("name");
            b.author = rs.getString("author");
            b.publisher = rs.getString("publisher");
            b.price = rs.getBigDecimal("price");
            b.language = rs.getString("language");
            b.pages = rs.getInt("pages");
            b.isbn = rs.getString("isbn");
            b.releaseDate = rs.getObject("release_date", LocalDate.class);
            return b;
        };
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return pages == book.pages &&
                Objects.equals(id, book.id) &&
                Objects.equals(name, book.name) &&
                Objects.equals(author, book.author) &&
                Objects.equals(publisher, book.publisher) &&
                Objects.equals(price, book.price) &&
                Objects.equals(language, book.language) &&
                Objects.equals(isbn, book.isbn) &&
                Objects.equals(releaseDate, book.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, author, publisher, price, language, pages, isbn, releaseDate);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", price=" + price +
                ", language='" + language + '\'' +
                ", pages=" + pages +
                ", isbn='" + isbn + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
