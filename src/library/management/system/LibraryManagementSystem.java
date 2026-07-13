/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package library.management.system;

import java.sql.*;
import java.util.Scanner;

public class LibraryManagementSystem {

    static final String url =
            "jdbc:mysql://localhost:3306/librarydb";

    static final String user = "root";

    static final String password = "mysqlroot";

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try {

            // Step 1: Load JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Step 2: Establish Connection
            Connection con =
                    DriverManager.getConnection(url, user, password);

            System.out.println("Connected to the database!");

            // Step 3: Create Statement
            Statement stmt = con.createStatement();

            // Step 4: Create Books Table
            String booksQuery =
                    "CREATE TABLE IF NOT EXISTS Books(" +
                    "book_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "title VARCHAR(100), " +
                    "author VARCHAR(100), " +
                    "publisher VARCHAR(100), " +
                    "year_published INT, " +
                    "isbn VARCHAR(50), " +
                    "available_copies INT" +
                    ")";

            stmt.executeUpdate(booksQuery);

            // Create Members Table
            String membersQuery =
                    "CREATE TABLE IF NOT EXISTS Members(" +
                    "member_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(100), " +
                    "email VARCHAR(100), " +
                    "phone VARCHAR(20), " +
                    "membership_date DATE" +
                    ")";

            stmt.executeUpdate(membersQuery);

            // Create Transactions Table
            String transactionsQuery =
                    "CREATE TABLE IF NOT EXISTS Transactions(" +
                    "transaction_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "book_id INT, " +
                    "member_id INT, " +
                    "borrow_date DATE, " +
                    "return_date DATE, " +
                    "status VARCHAR(20), " +
                    "FOREIGN KEY(book_id) REFERENCES Books(book_id), " +
                    "FOREIGN KEY(member_id) REFERENCES Members(member_id)" +
                    ")";

            stmt.executeUpdate(transactionsQuery);

            System.out.println("Tables created successfully!");

            int choice;

            do {

                System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
                System.out.println("1. Add Book");
                System.out.println("2. Update Book");
                System.out.println("3. Delete Book");
                System.out.println("4. Search Book");
                System.out.println("5. Add Member");
                System.out.println("6. Update Member");
                System.out.println("7. Delete Member");
                System.out.println("8. Search Member");
                System.out.println("9. Borrow Book");
                System.out.println("10. Return Book");
                System.out.println("11. View Transactions");
                System.out.println("12. Alter Books Table");
                System.out.println("13. Exit");

                System.out.print("Enter your choice: ");

                choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {

                    case 1:

                        System.out.print("Enter title: ");
                        String title = sc.nextLine();

                        System.out.print("Enter author: ");
                        String author = sc.nextLine();

                        System.out.print("Enter publisher: ");
                        String publisher = sc.nextLine();

                        System.out.print("Enter year published: ");
                        int year = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter ISBN: ");
                        String isbn = sc.nextLine();

                        System.out.print("Enter available copies: ");
                        int copies = sc.nextInt();

                        String insertBook =
                                "INSERT INTO Books" +
                                "(title, author, publisher, year_published, isbn, available_copies) " +
                                "VALUES (?, ?, ?, ?, ?, ?)";

                        PreparedStatement bookPstmt =
                                con.prepareStatement(insertBook);

                        bookPstmt.setString(1, title);
                        bookPstmt.setString(2, author);
                        bookPstmt.setString(3, publisher);
                        bookPstmt.setInt(4, year);
                        bookPstmt.setString(5, isbn);
                        bookPstmt.setInt(6, copies);

                        bookPstmt.executeUpdate();

                        System.out.println("Book added successfully!");

                        bookPstmt.close();

                        break;

                    case 2:

                        System.out.print("Enter Book ID: ");
                        int updateBookId = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter new title: ");
                        String newTitle = sc.nextLine();

                        System.out.print("Enter new author: ");
                        String newAuthor = sc.nextLine();

                        String updateBook =
                                "UPDATE Books SET title=?, author=? " +
                                "WHERE book_id=?";

                        PreparedStatement updateBookPstmt =
                                con.prepareStatement(updateBook);

                        updateBookPstmt.setString(1, newTitle);
                        updateBookPstmt.setString(2, newAuthor);
                        updateBookPstmt.setInt(3, updateBookId);

                        int updatedRows =
                                updateBookPstmt.executeUpdate();

                        if (updatedRows > 0) {

                            System.out.println(
                                    "Book updated successfully!"
                            );

                        } else {

                            System.out.println("Book not found.");
                        }

                        updateBookPstmt.close();

                        break;

                    case 3:

                        System.out.print("Enter Book ID: ");
                        int deleteBookId = sc.nextInt();

                        String countQuery =
                                "SELECT COUNT(*) FROM Transactions WHERE book_id=?";

                        PreparedStatement cntPstmt =
                                con.prepareStatement(countQuery);

                        cntPstmt.setInt(1, deleteBookId);

                        ResultSet rs = cntPstmt.executeQuery();

                        int cnt = 0;

                        if (rs.next()) {
                            cnt = rs.getInt(1);
                        }

                        if (cnt == 0) {

                            String deleteBook =
                                    "DELETE FROM Books WHERE book_id=?";

                            PreparedStatement deleteBookPstmt =
                                    con.prepareStatement(deleteBook);

                            deleteBookPstmt.setInt(1, deleteBookId);

                            int deletedRows =
                                    deleteBookPstmt.executeUpdate();

                            if (deletedRows > 0) {

                                System.out.println(
                                        "Book deleted successfully!"
                                );

                            } else {

                                System.out.println(
                                        "Book not found."
                                );
                            }

                            deleteBookPstmt.close();

                        } else {

                            System.out.println(
                                    "Cannot delete book. This book has transaction history."
                            );
                        }

                        rs.close();
                        cntPstmt.close();

                        break;

                    case 4:

                        System.out.print(
                                "Enter Title, Author or ISBN: "
                        );

                        String search = sc.nextLine();

                        String searchBook =
                                "SELECT * FROM Books " +
                                "WHERE title LIKE ? " +
                                "OR author LIKE ? " +
                                "OR isbn LIKE ?";

                        PreparedStatement searchBookPstmt =
                                con.prepareStatement(searchBook);

                        searchBookPstmt.setString(
                                1,
                                "%" + search + "%"
                        );

                        searchBookPstmt.setString(
                                2,
                                "%" + search + "%"
                        );

                        searchBookPstmt.setString(
                                3,
                                "%" + search + "%"
                        );

                        ResultSet bookRs =
                                searchBookPstmt.executeQuery();

                        while (bookRs.next()) {

                            System.out.println(
                                    "Book ID: " +
                                    bookRs.getInt("book_id")
                            );

                            System.out.println(
                                    "Title: " +
                                    bookRs.getString("title")
                            );

                            System.out.println(
                                    "Author: " +
                                    bookRs.getString("author")
                            );

                            System.out.println(
                                    "Publisher: " +
                                    bookRs.getString("publisher")
                            );

                            System.out.println(
                                    "ISBN: " +
                                    bookRs.getString("isbn")
                            );

                            System.out.println(
                                    "Available Copies: " +
                                    bookRs.getInt("available_copies")
                            );

                            System.out.println("--------------------");
                        }

                        bookRs.close();
                        searchBookPstmt.close();

                        break;

                    case 5:

                        System.out.print("Enter member name: ");
                        String memberName = sc.nextLine();

                        System.out.print("Enter email: ");
                        String email = sc.nextLine();

                        System.out.print("Enter phone: ");
                        String phone = sc.nextLine();

                        String insertMember =
                                "INSERT INTO Members" +
                                "(name, email, phone, membership_date) " +
                                "VALUES (?, ?, ?, CURDATE())";

                        PreparedStatement memberPstmt =
                                con.prepareStatement(insertMember);

                        memberPstmt.setString(1, memberName);
                        memberPstmt.setString(2, email);
                        memberPstmt.setString(3, phone);

                        memberPstmt.executeUpdate();

                        System.out.println(
                                "Member added successfully!"
                        );

                        memberPstmt.close();

                        break;

                    case 6:

                        System.out.print("Enter Member ID: ");
                        int memberId = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter new email: ");
                        String newEmail = sc.nextLine();

                        System.out.print("Enter new phone: ");
                        String newPhone = sc.nextLine();

                        String updateMember =
                                "UPDATE Members " +
                                "SET email=?, phone=? " +
                                "WHERE member_id=?";

                        PreparedStatement updateMemberPstmt =
                                con.prepareStatement(updateMember);

                        updateMemberPstmt.setString(1, newEmail);
                        updateMemberPstmt.setString(2, newPhone);
                        updateMemberPstmt.setInt(3, memberId);

                        updateMemberPstmt.executeUpdate();

                        System.out.println(
                                "Member updated successfully!"
                        );

                        updateMemberPstmt.close();

                        break;

                    case 7:

                        System.out.print("Enter Member ID: ");
                        int deleteMemberId = sc.nextInt();

                        String deleteMember =
                                "DELETE FROM Members " +
                                "WHERE member_id=?";

                        PreparedStatement deleteMemberPstmt =
                                con.prepareStatement(deleteMember);

                        deleteMemberPstmt.setInt(
                                1,
                                deleteMemberId
                        );

                        deleteMemberPstmt.executeUpdate();

                        System.out.println(
                                "Member deleted successfully!"
                        );

                        deleteMemberPstmt.close();

                        break;

                    case 8:

                        System.out.print(
                                "Enter Member Name or Email: "
                        );

                        String memberSearch = sc.nextLine();

                        String searchMember =
                                "SELECT * FROM Members " +
                                "WHERE name LIKE ? OR email LIKE ?";

                        PreparedStatement searchMemberPstmt =
                                con.prepareStatement(searchMember);

                        searchMemberPstmt.setString(
                                1,
                                "%" + memberSearch + "%"
                        );

                        searchMemberPstmt.setString(
                                2,
                                "%" + memberSearch + "%"
                        );

                        ResultSet memberRs =
                                searchMemberPstmt.executeQuery();

                        while (memberRs.next()) {

                            System.out.println(
                                    "Member ID: " +
                                    memberRs.getInt("member_id")
                            );

                            System.out.println(
                                    "Name: " +
                                    memberRs.getString("name")
                            );

                            System.out.println(
                                    "Email: " +
                                    memberRs.getString("email")
                            );

                            System.out.println(
                                    "Phone: " +
                                    memberRs.getString("phone")
                            );

                            System.out.println("--------------------");
                        }

                        memberRs.close();
                        searchMemberPstmt.close();

                        break;

                    case 9:

                      System.out.print("Enter Book ID: ");
                        int borrowBookId = sc.nextInt();

                        System.out.print("Enter Member ID: ");
                        int borrowMemberId = sc.nextInt();

                        String checkCopies =
                                "SELECT available_copies FROM Books WHERE book_id=?";

                        PreparedStatement checkPstmt =
                                con.prepareStatement(checkCopies);

                        checkPstmt.setInt(1, borrowBookId);

                        ResultSet rst = checkPstmt.executeQuery();

                        if (rst.next()) {

                            int availableCopies = rst.getInt("available_copies");

                            if (availableCopies > 0) {

                                String borrowQuery =
                                        "INSERT INTO Transactions " +
                                        "(book_id, member_id, borrow_date, status) " +
                                        "VALUES (?, ?, CURDATE(), 'borrowed')";

                                PreparedStatement borrowPstmt =
                                        con.prepareStatement(borrowQuery, Statement.RETURN_GENERATED_KEYS);

                                borrowPstmt.setInt(1, borrowBookId);
                                borrowPstmt.setInt(2, borrowMemberId);

                                borrowPstmt.executeUpdate();

                                String decreaseCopies =
                                        "UPDATE Books " +
                                        "SET available_copies = available_copies - 1 " +
                                        "WHERE book_id=?";

                                PreparedStatement decreasePstmt =
                                        con.prepareStatement(decreaseCopies);

                                decreasePstmt.setInt(1, borrowBookId);

                                decreasePstmt.executeUpdate();
                                ResultSet generatedKeys = borrowPstmt.getGeneratedKeys();

                                if (generatedKeys.next()) {

                                    int transactionId =
                                            generatedKeys.getInt(1);

                                    System.out.println(
                                            "Book borrowed successfully!"
                                    );

                                    System.out.println(
                                            "Transaction ID: " + transactionId
                                    );
                                }

                                generatedKeys.close();

                             

                                borrowPstmt.close();
                                decreasePstmt.close();

                            } else {

                                System.out.println("Book is currently unavailable.");

                            }

                        } else {

                            System.out.println("Book not found.");

                        }

                        rst.close();
                        checkPstmt.close();
                        break;

                    case 10:

                        System.out.print("Enter Transaction ID: ");
                        int transactionId = sc.nextInt();

                        String getBookQuery =
                                "SELECT book_id FROM Transactions " +
                                "WHERE transaction_id=?";

                        PreparedStatement getBookPstmt =
                                con.prepareStatement(getBookQuery);

                        getBookPstmt.setInt(1, transactionId);

                        ResultSet bookResult =
                                getBookPstmt.executeQuery();

                        if (bookResult.next()) {

                        int returnedBookId =
                                bookResult.getInt("book_id");

                        String returnQuery =
                                "UPDATE Transactions " +
                                "SET return_date=CURDATE(), status='returned' " +
                                "WHERE transaction_id=?";

                        PreparedStatement returnPstmt =
                                con.prepareStatement(returnQuery);

                        returnPstmt.setInt(1, transactionId);

                        returnPstmt.executeUpdate();

                        String increaseCopies =
                                "UPDATE Books " +
                                "SET available_copies=available_copies+1 " +
                                "WHERE book_id=?";

                        PreparedStatement increasePstmt =
                                con.prepareStatement(increaseCopies);

                        increasePstmt.setInt(1, returnedBookId);

                        increasePstmt.executeUpdate();

                        System.out.println(
                                "Book returned successfully!"
                        );

                        returnPstmt.close();
                        increasePstmt.close();

                    } else {

                        System.out.println(
                                "Transaction not found."
                        );
                    }

                    bookResult.close();
                    getBookPstmt.close();

                    break;
                    case 11:

                        String transactionQuery =
                                "SELECT * FROM Transactions";

                        ResultSet transactionRs =
                                stmt.executeQuery(transactionQuery);

                        while (transactionRs.next()) {

                            System.out.println(
                                    "Transaction ID: " +
                                    transactionRs.getInt(
                                            "transaction_id"
                                    )
                            );

                            System.out.println(
                                    "Book ID: " +
                                    transactionRs.getInt("book_id")
                            );

                            System.out.println(
                                    "Member ID: " +
                                    transactionRs.getInt("member_id")
                            );

                            System.out.println(
                                    "Borrow Date: " +
                                    transactionRs.getDate("borrow_date")
                            );

                            System.out.println(
                                    "Return Date: " +
                                    transactionRs.getDate("return_date")
                            );

                            System.out.println(
                                    "Status: " +
                                    transactionRs.getString("status")
                            );

                            System.out.println("--------------------");
                        }

                        transactionRs.close();

                        break;

                    case 12:

                        String alterQuery =
                                "ALTER TABLE Books " +
                                "ADD COLUMN genre VARCHAR(50)";

                        try {

                            stmt.executeUpdate(alterQuery);

                            System.out.println(
                                    "Books table altered successfully!"
                            );

                        } catch (SQLException exception) {

                            System.out.println(
                                    "Genre column may already exist."
                            );
                        }

                        break;

                    case 13:

                        System.out.println(
                                "Exiting Library Management System."
                        );

                        break;

                    default:

                        System.out.println("Invalid choice.");
                }

            } while (choice != 13);

            // Close Resources
            stmt.close();
            con.close();
            sc.close();

        } catch (ClassNotFoundException e) {

            System.out.println(
                    "MySQL JDBC Driver not found!"
            );

            e.printStackTrace();

        } catch (SQLException e) {

            System.out.println(
                    "SQL Exception occurred!"
            );

            e.printStackTrace();
        }
    }
}
