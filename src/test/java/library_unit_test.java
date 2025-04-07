import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        library.exportData(filename, false, false);
        Library importedLibrary = new Library();
        importedLibrary.importData(filename);
        List<Book> importedBooks = importedLibrary.availableBooks();
        assertEquals(1, importedBooks.size());
        assertEquals("Effective Java", importedBooks.get(0).getTitle());
    }

    @Test
    public void testExportSortedData() throws IOException {
        Library newLibrary = new Library();
        newLibrary.addBook(new Book("Z Book", "Author A"));
        newLibrary.addBook(new Book("A Book", "Author Z"));
        newLibrary.addReader(new Reader("Zlata Petrenko"));
        newLibrary.addReader(new Reader("Anton Antonenko"));
        String testFile = "test_export_sorted.json";
        newLibrary.exportData(testFile, true, true);
        String json = new String(Files.readAllBytes(Paths.get(testFile)), StandardCharsets.UTF_8);
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        JsonArray books = jsonObject.getAsJsonArray("books");
        JsonArray readers = jsonObject.getAsJsonArray("readers");

        assertEquals("A Book", books.get(0).getAsJsonObject().get("title").getAsString());
        assertEquals("Z Book", books.get(1).getAsJsonObject().get("title").getAsString());

        assertEquals("Anton Antonenko", readers.get(0).getAsJsonObject().get("name").getAsString());
        assertEquals("Zlata Petrenko", readers.get(1).getAsJsonObject().get("name").getAsString());
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