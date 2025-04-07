import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import com.google.gson.*;


class Book {
    private String title;
    private String author;
    private boolean isAvailable = true;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(title, book.title) && Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author);
    }
}

class Reader {
    private String name;
    private Set<Book> borrowedBooks = new HashSet<>();

    public Reader(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public boolean borrowBook(Book book) {
        if (book.isAvailable()) {
            book.setAvailable(false);
            borrowedBooks.add(book);
            return true;
        }
        return false;
    }

    public boolean returnBook(Book book) {
        if (borrowedBooks.contains(book)) {
            book.setAvailable(true);
            borrowedBooks.remove(book);
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reader reader = (Reader) o;
        return Objects.equals(name, reader.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

class Library {
    private Set<Book> books = new HashSet<>();
    private Set<Reader> readers = new HashSet<>();

    public void addBook(Book book) {
        books.add(book);
    }

    public void removeBook(Book book) {
        books.remove(book);
    }

    public void addReader(Reader reader) {
        readers.add(reader);
    }

    public void removeReader(Reader reader) {
        readers.remove(reader);
    }

    public boolean lendBook(Reader reader, Book book) {
        return reader.borrowBook(book);
    }

    public boolean acceptReturnedBook(Reader reader, Book book) {
        return reader.returnBook(book);
    }

    public List<Book> availableBooks() {
        return books.stream().filter(Book::isAvailable).collect(Collectors.toList());
    }

    public List<Reader> getReaders() {
        return new ArrayList<>(readers);
    }

    public void exportData(String filename, boolean sortBooks, boolean sortReaders) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Set<Book> sortedBooks = sortBooks ? new TreeSet<>(Comparator.comparing(Book::getTitle)) : books;
        sortedBooks.addAll(books);

        Set<Reader> sortedReaders = sortReaders ? new TreeSet<>(Comparator.comparing(Reader::getName)) : readers;
        sortedReaders.addAll(readers);

        Library exportLibrary = new Library();
        exportLibrary.books = sortedBooks;
        exportLibrary.readers = sortedReaders;

        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(exportLibrary, writer);
        }
    }

    public void importData(String filename) throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filename)) {
            Library importedLibrary = gson.fromJson(reader, Library.class);
            this.books = importedLibrary.books;
            this.readers = importedLibrary.readers;
        }
    }
}
public class Program {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Library library = new Library();
        String fileName;
        int choice;
        do {
            System.out.println("1. Add a book\n2. Add a reader\n3. Lend a book\n4. Return a book\n5. Show available books\n6. Save library\n7. Load library\n8. Exit");
            choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    System.out.print("Enter book title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter author: ");
                    String author = scanner.nextLine();
                    library.addBook(new Book(title, author));
                    break;
                case 2:
                    System.out.print("Enter reader's name: ");
                    String name = scanner.nextLine();
                    library.addReader(new Reader(name));
                    break;
                case 3:
                    System.out.print("Enter reader's name: ");
                    name = scanner.nextLine();
                    System.out.print("Enter book title: ");
                    title = scanner.nextLine();
                    library.getReaders().stream().filter(r -> r.getName().equals(name)).findFirst()
                            .ifPresent(reader -> library.availableBooks().stream().filter(b -> b.getTitle().equals(title)).findFirst()
                                    .ifPresent(book -> library.lendBook(reader, book)));
                    break;
                case 4:
                    System.out.print("Enter reader's name: ");
                    name = scanner.nextLine();
                    System.out.print("Enter book title: ");
                    title = scanner.nextLine();
                    library.getReaders().stream().filter(r -> r.getName().equals(name)).findFirst()
                            .ifPresent(reader -> reader.getBorrowedBooks().stream().filter(b -> b.getTitle().equals(title)).findFirst()
                                    .ifPresent(book -> library.acceptReturnedBook(reader, book)));
                    break;
                case 5:
                    library.availableBooks().forEach(book -> System.out.println(book.getTitle()));
                    break;
                case 6:
                    System.out.print("Enter file name: ");
                    fileName = scanner.nextLine();
                    System.out.print("To sort books before export, enter 1: ");
                    String sortBooks = scanner.nextLine();
                    System.out.print("To sort readers before export, enter 1: ");
                    String sortReaders = scanner.nextLine();
                    try {
                        library.exportData(fileName, sortBooks == "1", sortReaders == "1");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 7:
                    System.out.print("Enter file name: ");
                    fileName = scanner.nextLine();
                    try {
                        library.importData(fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 8:
                    return;
            }
        } while (choice != 8);
        scanner.close();
    }
}
