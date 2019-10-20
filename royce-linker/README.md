# Royce-Linker

A tiny toolset that provides succinct methods to operate with JDBC.

## Features

- Succinct ways to `SELECT`, `UPDATE`, `DELETE`, `CALL` with JDBC
- Alternate between transaction and non-transaction
- Provide a more Java 8 style to operate with JDBC
- Automatic conversion between Java Bean and Database row
- Easy-to-use encapsulations of frequently-used JDBC snippets

## Installation

### Requirement
* Java baseline: 1.8

### Standalone version
Package the project with the following command:
```
$ mvn clean package -Dmaven.test.skip=true
```

### Install with Maven
Install the project with the following command:
```
$ mvn clean install -Dmaven.test.skip=true
```
Once the installation completed, add the following dependency in project's `pom.xml`:
```xml
<dependency>
    <groupId>app.kumasuke.royce</groupId>
    <artifactId>royce-linker</artifactId>
    <version>{royce.version}</version>
</dependency>
```
## Usage
### Basic Operations
- Perform a `Read`
```java
public class RoyceRead {
    public static void main(String[] args) {
        ConnectionProvider provider = () -> {
            // needless with JDBC 4.0+ and Java SPI
            // Class.forName("<jdbc.driver-class>");
            return DriverManager.getConnection("<jdbc.url>", "<jdbc.user>", "<jdbc.password>");
        };

        Royce royce = Royce.nonTransactional(provider);
        String message = royce.tryRead(linker -> {
            final String sql = "SELECT concat(?, ' ', ?)";
            return linker.selectOne(Mappers.firstColumnToString(), sql, "Hello", "World")
                    .orElse("") + "!";
        }).handle(e -> {
            e.printStackTrace();
            return "Hello Error...";
        });

        System.out.println(message);
    }
}
```
- Perform a `Write` within a transaction
```java
public class RoyceWrite {
    public static void main(String[] args) {
        // ... omit the creation of provider: ConnectionProvider ...
        
        Royce royce = Royce.transactional(provider);
        // all actions inside the lambda will form a transaction,
        // and it will be auto-committed at last or rollback-ed when
        // encountered any exception
        royce.write(linker -> {
            final String rollOut = 
                "UPDATE `test`.`account` SET `balance` = `balance` - :amount WHERE `user_id` = 1";
            final String rollIn = 
                "UPDATE `test`.`account` SET `balance` = `balance` + :amount WHERE `user_id` = 2";
            
            Map<String, Integer> amount = Collections.singletonMap("amount", 100);
            linker.namedUpdate(rollOut, amount);
            linker.namedUpdate(rollIn, amount);
            
            // auto-commit will be performed here
        });
    }
}
```
- Perform a `Call`
```java
public class RoyceCall {
    public static void main(String[] args) {
        // ... omit the creation of provider: ConnectionProvider ...

        Royce royce = Royce.nonTransactional(provider);
        royce.tryCall(linker -> {
            // assume there is a procedure:
            // CREATE PROCEDURE test.`do_procedure`(
            //  IN  a   INT,
            //  IN  b   INT,
            //  OUT sum INT
            // )
            CallParameter<Integer> a = CallParameter.in(1);
            CallParameter<Integer> b = CallParameter.in(2);
            CallParameter<Integer> sum = CallParameter.out();
            linker.call("{call test.`do_procedure` (?, ?, ?)}", a, b, sum);

            int resultSum = sum.getValue();
            System.out.println(resultSum);
        }).handle(SQLException::printStackTrace);
    }
}
```

### Cope with Beans
Here is the `Book` Java Bean which we will cope with:
```java
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
    
    // ... omit getters, setters, etc. ...
}
```
- Get `Book`s by page (MySQL)
```java
public class RoyceBooks {
    // ... omit necessary declaration ...
    
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
}
```
- Save `Book` (MySQL)
```java
public class RoyceBooks {
    // ... omit necessary declaration ...

    private static boolean saveBook(final Book book) {
        final boolean isCreate = book.getId() == null;
        if (isCreate) {
            return royce.transactional().tryLink(linker -> {
                final int id = linker.callAndReturnOne(Mappers.firstColumnToInteger(),
                                                       "{call `test`.`seq_nextval`('books')}")
                        .orElseThrow(() -> new IllegalStateException("cannot generate new sequence for 'books'"));
                final String update = 
                    "INSERT INTO `test`.`books` (id, `name`, author, publisher, price, `language`, " +
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
}
```

## Transaction
In Royce-Linker, transaction is managed automatically. Transaction will be committed once all actions are done 
peacefully, otherwise transaction will be rollback-ed.

To use transaction, you could either create a **transactional** `Royce` or alternate a **non-transactional** `Royce` 
to a **transactional** one.

```
// create a transactional Royce
Royce transactionalRoyce = Royce.transactional(provider);
  
// switch to transactional Royce
Royce transactionalRoyce2 = nonTransactionalRoyce.transactional();
```
> **FYI**: `Ryoce#transactional()` and `Royce#nonTransactional()` are both applied mutual-linked lazy-initialized 
> Singleton Pattern, that is every call to those two methods will return same instance and also `Royce#nonTransactional
> ().transactional().nonTransactional() == Royce#nonTransactional()` (and vice versa).

Within a transactional `Royce`, every `Linker`-operating method(such as `read`, `write`, `tryCall`, `tryLink`, etc.) 
will be run under a newly-created transaction:
```
//... omit royce creation ...

royce.link(linker -> {
    /*
    // ! not acutal implementation, simplfied for demonstration
    // :transaction created
    try {                                           ; needless, only for demonstration
        // your actions go here
        // :transaction committed
    } catch (SQLException | Runtimeexception e) {   ; needless, only for demonstration
        // :transaction rollback-ed
        throw e;                                    ; needless, only for demonstration
    }                                               ; needless, only for demonstration
    */
})
```
Besides, you may choose `Royce#nativeJdbc()` and `NativeJdbcLinker` to control transaction on your own.

## Deal with `GENERATED_KEYS`
When `WritableLinker#updateAndReturnKey(ResultSetMapper<K>, String, Object...)` are recommended when dealing with 
`GENERATED_KEYS` whose first parameter is to convert key rows to desired Java objects:
```
//... omit royce creation ...

// suppose that there is such a table in a MySQL database:
// CREATE TALBE test.auto_increment_ids (
//   id      INT PRIMARY KEY AUTO_INCREMENT,
//   `value` CHAR(1) NOT NULL
// )
royce.write(linker -> {
    final String sql = "INSERT INTO test.auto_increment_ids (`value`) VALUES (?)";
    Optional<Integer> key = linker.updateAndReturnKey(Mappers.firstColumnToInteger(), sql, "A");
    key.map(System.out::println);   // prints "1"
});
```

## Use `ResultSetMapper<T>` to convert row
### `ResultSetMapper<T>`
`ResultSetMapper<T>` is a `@FunctionalInterface` to read one row a time from `ResultSet` and to 'map' it to 
desired type.

### `Mappers`
`Mappers` is a collection of all pre-defined `ResultSetMapper<T>`, and provides simple ways to create them.

Here are some frequently-used mappers:

|             Method               |     Equivalent Lambda      |                 Description                    |
|----------------------------------|----------------------------|------------------------------------------------|
|`Mappers.firstColumnToInteger()`  |`rs -> rs.getInteger(1)`    |Convert first column of row to `Integer`        |
|`Mappers.firstColumnTo(Class<T>)` |`rs -> rs.getObject(1, c)`  |Convert first column of row to the type of `c`  |
|`Mappers.columnToString(int)`     |`rs -> rs.getString(i)`     |Convert **i**-th column of row to `String`      |
|`Mappers.toBean(Class<T>)`        |`-`                         |Convert row to Bean based on property name      |

## Exception Handling
With Royce-Linker, you could handle `SQLException` in either way:
- use `try`-`catch` structure to handle the wrapped `UncheckedSQLException`
- use `MaybeException<SQLException, R>` to handle `SQLException` in chained style

### `UncheckedSQLException`
Methods without `try` prefix such as `Royce#read`, `Royce#write`, etc. will throw `UncheckedSQLException` when 
the inner lambda throws `SQLException`. You could call `UncheckedSQLException#getCause()` to get wrapped `SQLException`
```java
public class RoyceUncheckedException {
    public static void main(String[] args) {
        // ... omit the creation of provider: ConnectionProvider ...

        Royce royce = Royce.nonTransactional(provider);
        try {
            royce.write(linker -> {
                // do actions that may throw SQLException
            });
        } catch (UncheckedSQLException e) {
            SQLException cause = e.getCause();
            // do something with cause ...
        }
    }
}
```

### `MaybeException<SQLException, R>`
Methods with `try` prefix such as `Royce#tryRead`, `Royce#tryWrite`, etc. return `MaybeException<SQLException, R>`when 
the inner lambda throws `SQLException`. You could call `MaybeSQLException#handle(Function<SQLException, R> handler)` to 
handle thrown `SQLException`.
```java
public class RoyceMaybeException {
    public static void main(String[] args) {
        // ... omit the creation of provider: ConnectionProvider ...

        Royce royce = Royce.nonTransactional(provider);
        // do something with e ...
        royce.tryWrite(linker -> {
            // do actions that may throw SQLException
        }).handle(e -> {
            // do something with e ...
        });
    }
}
```

## Use `CallParameter<T>` with Stored Procedures
You should call procedures with `CallParameter<T>` as parameters to deal with `IN`, `INOUT`, `OUT` procedure parameters.
To create a `CallParameter<Integer>` with `OUT` type:
```
CallParameter<Integer> out = CallParameter.out();
```
And then pass it to a preferred `call`- method, to retrieve the `OUT` value returned by procedure:
```
Integer value = out.getValue();
```