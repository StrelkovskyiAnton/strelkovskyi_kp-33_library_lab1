import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.List;

class library_unit_test {
    private Library library;
    private Reader reader;
    private Book book;

    @BeforeEach
    void setUp() {
        library = new Library();
        reader = new Reader("Petro Petrenko");
        book = new Book("Effective Java", "Joshua Bloch");
        library.addReader(reader);
        library.addBook(book);
    }

    @Test
    void testAddBook() {
        assertTrue(library.availableBooks().contains(book));
    }

    @Test
    void testRemoveBook() {
        library.removeBook(book);
        assertFalse(library.availableBooks().contains(book));
    }

    @Test
    void testLendBookSuccess() {
        assertTrue(library.lendBook(reader, book));
        assertFalse(book.isAvailable());
    }

    @Test
    void testLendBookFailure() {
        library.lendBook(reader, book);
        assertFalse(library.lendBook(new Reader("Vasyl Petrenko"), book));
    }

    @Test
    void testReturnBookSuccess() {
        library.lendBook(reader, book);
        assertTrue(library.acceptReturnedBook(reader, book));
        assertTrue(book.isAvailable());
    }

    @Test
    void testReturnBookFailure() {
        assertFalse(library.acceptReturnedBook(reader, book));
    }

    @Test
    void testExportAndImportLibrary() throws IOException {
        String filename = "test_library.json";
        library.exportData(filename);
        Library importedLibrary = new Library();
        importedLibrary.importData(filename);
        List<Book> importedBooks = importedLibrary.availableBooks();
        assertEquals(1, importedBooks.size());
        assertEquals("Effective Java", importedBooks.get(0).getTitle());
    }

    @Test
    void testAddReader() {
        Reader newReader = new Reader("Vasyl Petrenko");
        library.addReader(newReader);
        assertTrue(library.getReaders().contains(newReader));
    }

    @Test
    void testRemoveReader() {
        library.removeReader(reader);
        assertFalse(library.getReaders().contains(reader));
    }
}