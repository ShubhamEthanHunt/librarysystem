
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.sql.Statement;

class Book{
    public static void searchBookStudent(Connection con, String Enroll){
        try{
            Statement stmt = (Statement) con.createStatement();
            String sql = "select Id from Student where StudentId='" + Enroll + "'";
            ResultSet rs = stmt.executeQuery(sql);
            if(!rs.next()){
                System.out.println("Please Enter a valid Enrollment Id");
                return;
            }
            else{
                int id = rs.getInt(1);
                sql = "select * from Book where Id='" + id + "'";
                rs = stmt.executeQuery(sql);
                int flag = 0;
                System.out.println("BookId  BookName  AuthorName  IssueStatus  IssueDate  DueDate");
                while(rs.next()) {
                    flag = 1;
                    System.out.println(rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5) + " " + rs.getString(7) + " " + rs.getString(8));
                }
                if(flag == 0){
                    System.out.println("Oops!  The Given Student Does not have any issued Book");
                }
            }
        }catch(SQLException se){
            System.out.println(se);
        }
    }

    public static void addBook(Connection con, String BookId, String BookName, String AuthorName){
        try{
            Statement stmt = (Statement) con.createStatement();
            String sql = "insert into Book(BookId, BookName, AuthorName) values ('" + BookId + "','" + BookName + "','" + AuthorName + "')";
            stmt.executeUpdate(sql);
            System.out.println("Insertion Success ! Congratulation you Got new Book");
        }catch (SQLException se){
            System.out.println(se);
        }
    }

    public static void deleteBook(Connection con, String BookId){
        try {
            Statement stmt = (Statement) con.createStatement();
            
             String sql1 = "select IssueStatus from Book where BookId='" + BookId + "'";
            ResultSet rs = stmt.executeQuery(sql1);
            rs.next();
            if(rs.getString(1).compareTo("issued") == 0) {
                    System.out.println("The Given Book is Issued");
                    return;
                }
            String sql = "delete from Book where BookId='" + BookId + "'";
            stmt.executeUpdate(sql);
            System.out.println("Deletion Success");
        }catch (SQLException se){
            System.out.println(se);
        }
    }

    public static void updateBookId(Connection con, String NewBookId, String OldBookId){
        try{
            Statement stmt = (Statement) con.createStatement();
            String sql = "update Book set BookId='" + NewBookId + "' where BookId='" + OldBookId + "'";
            stmt.executeUpdate(sql);
            System.out.println("Update Success");
        }catch (SQLException se){
            System.out.println(se);
        }
    }

    public static void updateBookName(Connection con, String BookName, String BookId){
        try{
            Statement stmt = (Statement) con.createStatement();
            String sql = "update Book set BookName='" + BookName + "' where BookId='" + BookId + "'";
            stmt.executeUpdate(sql);
            System.out.println("Update Success");
        }catch (SQLException se){
            System.out.println(se);
        }
    }

    public static void updateBookAuthor(Connection con, String AuthorName, String BookId){
        try{
            Statement stmt = (Statement) con.createStatement();
            String sql = "update Book set AuthorName='" + AuthorName + "' where BookId='" + BookId + "'";
            stmt.executeUpdate(sql);
            System.out.println("Update Success");
        }catch (SQLException se){
            System.out.println(se);
        }
    }

    public static void issueUpdateBook(Connection con, String issueDate, String dueDate, int id, String status, String BookId){
        try{
            Statement stmt =  con.createStatement();
            String sql = "";
            if(status.compareTo("issued") == 0)
                sql = "update Book set IssueStatus='" + status + "', Id='" + id + "', IssueDate='" + issueDate + "', DueDate='" + dueDate + "' where BookId='" + BookId + "'";
            else{
                sql = "update Book set IssueStatus='" + status + "', Id=NULL , IssueDate='" + issueDate + "', DueDate='" + dueDate + "' where BookId='" + BookId + "'";
            }
            stmt.executeUpdate(sql);
            System.out.println("Success");
        }catch (SQLException se){
            System.out.println(se);
        }
    }

     public static void issueBook(Connection con, String Enroll, String BookId){
        try {
            Statement stmt = con.createStatement();
            String sql = "select IssueStatus from Book where BookId='" + BookId + "'";
            ResultSet rs = stmt.executeQuery(sql);
            if(!rs.next()){
                System.out.println("Please Enter the valid Book Id");
                return;
            }
            else{
                if(rs.getString(1).compareTo("issued") == 0) {
                    System.out.println("The Given Book is Issued! Please Check");
                    return;
                }
                sql = "select Id from Student where StudentId='" + Enroll + "'";
                rs = stmt.executeQuery(sql);
                if(!rs.next()){
                    System.out.println("Please Enter a valid Enrollment number");
                    return;
                }
                else{
                    int id = rs.getInt(1);
                    sql = "select count(Id) from Book where Id='" + rs.getInt(1) + "'";
                    rs = stmt.executeQuery(sql);
                    rs.next();
                    int cntId = rs.getInt(1);
                    if(cntId > 4){
                        System.out.println("Oops! Sorry you can't issue more than 4 books at a time");
                        return;
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    String issueDate = sdf.format(cal.getTime());
                    cal.add(Calendar.DAY_OF_MONTH, 15);
                    String dueDate = sdf.format(cal.getTime());
                    System.out.println(issueDate + " " + dueDate);
                    Book.issueUpdateBook(con, issueDate, dueDate, id, "issued", BookId);
                }
            }
        }catch (SQLException se){
            System.out.println(se);
        }
    }

     public static void returnBook(Connection con, String Enroll, String BookId){
        try{
            Statement stmt = con.createStatement();
            String sql = "select * from Book where BookId='" + BookId + "'";
            ResultSet rs = stmt.executeQuery(sql);
            if(!rs.next()){
                System.out.println("Please Enter the valid Book Id");
                return;
            }
            else{
                
                String status = rs.getString(5);
                if(status.compareTo("available") == 0){
                    System.out.println("Error! This Book is not issued");
                    return;
                }
                else{
                    String issueDate = rs.getString(7);
                    LocalDate issue = LocalDate.parse(issueDate);
                    LocalDate today = java.time.LocalDate.now();

                    long numberDays = ChronoUnit.DAYS.between(issue, today);
                    if(numberDays > 15){
                        numberDays = numberDays - 15;
                        long cost = numberDays*5;
                        System.out.println("You have to pay Rs " + cost + "as a fine!");
                        System.out.println("1. Pay\n 2.Not Pay");
                        Scanner input = new Scanner(System.in);
                        int paymentOption = input.nextInt();
                        while(paymentOption!=1 && paymentOption!=2){
                            System.out.println("Please Enter a valid option!");
                            paymentOption = input.nextInt();
                        }
                        if(paymentOption == 2){
                            System.out.println("You can't return book without pay");
                            return;
                        }
                    }
                    Book.issueUpdateBook(con, null, null, 0, "available", BookId);
                }
            }
        }catch (SQLException se){
            System.out.println(se);
        }
    }
}
class Student {
    public static void searchBookId(Connection con, String BookId){
        try{
            Statement stmt = (Statement) con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from Book where BookId='" + BookId +"'");
            System.out.println("BookId  BookName  AuthorName  IssueStatus");
            while(rs.next()){
                System.out.println(rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5));
            }
        }catch(SQLException se){
            System.out.println(se);
        }
    }
}

public class Main {
    public static void main(String args[]){

        Scanner input = new Scanner(System.in);
        Connection con = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/lib?autoReconnect=true&useSSL=false", "root", "shubh1998");

            System.out.println("         IIITA Library Welcomes You         ");
            while(true) {
                System.out.println("1. Admin\t2. Student\t3. Exit");
                int userType = input.nextInt();
                while (userType != 1 && userType != 2 && userType != 3) {
                    System.out.println("Error:  Enter the correct option!");
                    userType = input.nextInt();
                }
                if (userType == 1) {
                    System.out.println("            Please Enter the Type of Operation you want to perform-         ");
                    System.out.println("1. Add the Book Info\t2. Delete the Book Info\t3. Edit the Book Info\t4. Issue The Book\t5. Return The Book");
                    int operationType = input.nextInt();
                    while (operationType != 1 && operationType != 2 && operationType != 3 && operationType != 4 && operationType != 5) {
                        System.out.println("Error: Please Enter the valid operation Id!");
                        operationType = input.nextInt();
                    }
                        String BookId, BookName, AuthorName;
                                System.out.println("            Please Enter the Info Of Book              ");
                        System.out.println("Enter The Book Id:");
                        String as =        
                                if (operationType == 1) {
input.nextLine();
                        BookId = input.nextLine();
                        System.out.println("Enter The Book name:");
                        BookName = input.nextLine();
                        System.out.println("Enter The Author name:");
                        AuthorName = input.nextLine();
                        Book.addBook(con, BookId, BookName, AuthorName);
                    } else if (operationType == 2) {
                        System.out.println("            Please Enter the Book ID to be delete           ");
                        System.out.println("Enter the Book Id:");
                        String as = input.nextLine();
                        String BookId = input.nextLine();
                        Book.deleteBook(con, BookId);
                    } else if (operationType == 3) {
                        System.out.println("        Please Enter the type of update operation to be performed           ");
                        System.out.println("1. Update BookId\n2. Update Bookname\n3. Update Authorname");
                        int updateType = input.nextInt();
                        while (updateType != 1 && updateType != 2 && updateType != 3) {
                            System.out.println("Error: Please Enter the correct option!");
                            updateType = input.nextInt();
                        }

                        if (updateType == 1) {
                            System.out.println("        Please Enter the Old and New Book Id            ");
                            System.out.println("Enter the Old Book Id:");
                            String as = input.nextLine();
                            String OldBookId = input.nextLine();
                            System.out.println("Enter the new Book Id:");
                            String NewBookId = input.nextLine();
                            Book.updateBookId(con, NewBookId, OldBookId);
                        } else if (updateType == 2) {
                            System.out.println("             Enter the Book Id and Book name          ");
                            System.out.println("Enter the Book Id:");
                            String as = input.nextLine();
                            String BookId = input.nextLine();
                            System.out.println("Enter the Book name:");
                            String BookName = input.nextLine();
                            Book.updateBookName(con, BookName, BookId);
                        } else if (updateType == 3) {
                            System.out.println("            Updating Author Name : Please Enter the Book Id and Author name         ");
                            System.out.println("Enter the Book Id:");
                            String as = input.nextLine();
                            String BookId = input.nextLine();
                            System.out.println("Enter the Author name:");
                            String AuthorName = input.nextLine();
                            Book.updateBookAuthor(con, AuthorName, BookId);
                        }
                    } else if (operationType == 4) {
                            System.out.println("            Issuing : Please Enter the Student Enrollment and Book Id           ");
                        System.out.println("Enter the Student Enrollment ID:");
                        String as = input.nextLine();
                        String Enroll = input.nextLine();
                        System.out.println("Enter the Book Id");
                        String BookId = input.nextLine();
                        Book.issueBook(con, Enroll, BookId);
                    } else if (operationType == 5) {
                        System.out.println("        Returning : Please Enter the Student Enrollment and Book Id         ");
                        System.out.println("Enter the Student Enrollment ID:");
                        String as = input.nextLine();
                        String Enroll = input.nextLine();
                        System.out.println("Enter the Book Id");
                        String BookId = input.nextLine();
                        Book.returnBook(con, Enroll, BookId);
                    }
                } else if (userType == 2) {
                    System.out.println("            Select the Type of Operation            ");
                    System.out.println("1. Search Book\n2. Check Status");
                    int Stype = input.nextInt();
                    while (Stype != 1 && Stype != 2) {
                        System.out.println("Please Enter a valid operation");
                        Stype = input.nextInt();
                    }
                    if (Stype == 1) {
                        System.out.println("Enter the valid BookId");
                        String as = input.nextLine();
                        String BookId = input.nextLine();
                        Student.searchBookId(con, BookId);
                    } else if (Stype == 2) {
                        System.out.println("Enter your Enrollment Number");
                        String as = input.nextLine();
                        String Enroll = input.nextLine();
                        Book.searchBookStudent(con, Enroll);
                    }
                } else if (userType == 3) {
                    con.close();
                    return;
                }
            }
        }catch(Exception e) {
            System.out.println(e);
        }finally {
            System.out.println(" IIITA Library Pleasure To Serve You");
        }
    }
}