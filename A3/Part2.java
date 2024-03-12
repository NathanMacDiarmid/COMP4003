import java.sql.*;
import java.io.*;
import oracle.jdbc.*;
import oracle.sql.*;
import java.util.*;

public class Part2 {
    public static void main(String[] args) {
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "fedora", "oracle");
            //createTables(conn);
            displayMenu(conn);
            conn.close();
        } catch (Exception e) {
            System.out.println("SQL exception: ");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void displayMenu(Connection conn) throws SQLException {
        while (true) {
            printMenuItems();
            Scanner myObj = new Scanner(System.in);
            System.out.print("Enter an option: ");
            int option = 99;
            try {
                option = myObj.nextInt();
            } catch (Exception e) {
                System.out.println("Not a valid integer");
                continue;
            }
            switch (option) {
                case 0:
                    System.out.println("Exiting program.");
                    System.exit(0);
                    myObj.close();
                    break;
                case 1:
                    addCustomer(conn);
                    System.out.println("Customer added successfully.");
                    break;
                case 2:
                    displayCustomer(conn);
                    break;
                case 3:
                    addProduct(conn);
                    System.out.println("Product added successfully.");
                    break;
                case 4:
                    displayProduct(conn);
                    break;
                case 5:
                    addLineItem(conn);
                    System.out.println("LineItem added successfully.");
                    break;
                case 6:
                    addOrder(conn);
                    System.out.println("Order added successfully.");
                    break;
                case 7:
                    addCustomerOrder(conn);
                    System.out.println("Customer order added successfully.");
                    break;
                case 8:
                    listCustomerOrder(conn);
                    break;
                case 9:
                    listCustomerByProduct(conn);
                    break;
                case 10:
                    dropTables(conn);
                    System.out.println("Tables dropped.");
                    break;
                case 11:
                    selectAllCustomers(conn);
                    break;
                case 12:
                    selectAllProducts(conn);
                    break;
                case 13:
                    selectAllOrders(conn);
                    break;
                case 14:
                    selectAllLineItems(conn);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    public static void createTables(Connection conn) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{ call CPO.createTables }");
        stmt.execute();
        stmt.close();
    }

    public static void addCustomer(Connection conn) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{ call CPO.addCustomer(?, ?) }");
        @SuppressWarnings("resource")
        Scanner myObj = new Scanner(System.in);
        String name;
        String address;

        System.out.print("Enter a name: ");
        name = myObj.nextLine();
        System.out.print("Enter an address: ");
        address = myObj.nextLine();
        stmt.setString(1, name);
        stmt.setString(2, address);
        stmt.execute();
        stmt.close();
    }

    public static void displayCustomer(Connection conn) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{ call CPO.printCustomer(?, ?, ?, ?) }");
        @SuppressWarnings("resource")
        Scanner myObj = new Scanner(System.in);
        String name;

        System.out.print("Enter a name: ");
        name = myObj.nextLine();
        stmt.setString(1, name);
        stmt.registerOutParameter(2, OracleTypes.INTEGER);
        stmt.registerOutParameter(3, OracleTypes.VARCHAR);
        stmt.registerOutParameter(4, OracleTypes.VARCHAR);
        stmt.execute();
        Integer id = stmt.getInt(2);
        String regName = stmt.getString(3);
        String address = stmt.getString(4);
        System.out.println("id: " + id);
        System.out.println("regName: " + regName);
        System.out.println("address: " + address);
        stmt.close();
    }

    public static void addProduct(Connection conn) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{ call CPO.addProduct(?, ?) }");
        @SuppressWarnings("resource")
        Scanner myObj = new Scanner(System.in);
        String name;
        float price;

        System.out.print("Enter a name: ");
        name = myObj.nextLine();
        System.out.print("Enter a price: ");
        price = Float.valueOf(myObj.nextLine());
        stmt.setString(1, name);
        stmt.setFloat(2, price);
        stmt.execute();
        stmt.close();
    }

    public static void displayProduct(Connection conn) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{ call CPO.printProduct(?) }");
        @SuppressWarnings("resource")
        Scanner myObj = new Scanner(System.in);
        String name;

        System.out.print("Enter a name: ");
        name = myObj.nextLine();
        stmt.setString(1, name);
        stmt.execute();
        stmt.close();
    }

    public static void addLineItem(Connection conn) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{ call CPO.addLineitem(?, ?) }");
        @SuppressWarnings("resource")
        Scanner myObj = new Scanner(System.in);
        String name;
        Integer qty;

        System.out.print("Enter a name: ");
        name = myObj.nextLine();
        System.out.print("Enter a quantity: ");
        qty = myObj.nextInt();
        System.out.println(name);
        System.out.println(qty);
        stmt.setString(1, name);
        stmt.setInt(2, qty);
        stmt.execute();
        stmt.close();
    }

    public static void addOrder(Connection conn) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{ call CPO.addOrderLineitems }");
        stmt.execute();
        stmt.close();
    }

    public static void addCustomerOrder(Connection conn) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{ call CPO.addCustomerOrder(?) }");
        @SuppressWarnings("resource")
        Scanner myObj = new Scanner(System.in);
        String name;

        System.out.print("Enter a name: ");
        name = myObj.nextLine();
        stmt.setString(1, name);
        stmt.execute();
        stmt.close();
    }

    public static void listCustomerOrder(Connection conn) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{ call CPO.listCustomerOrder(?) }");
        @SuppressWarnings("resource")
        Scanner myObj = new Scanner(System.in);
        String name;

        System.out.print("Enter a name: ");
        name = myObj.nextLine();
        stmt.setString(1, name);
        stmt.execute();
        stmt.close();
    }

    public static void listCustomerByProduct(Connection conn) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{ call CPO.listCustomerByProduct(?) }");
        @SuppressWarnings("resource")
        Scanner myObj = new Scanner(System.in);
        String name;

        System.out.print("Enter a product name: ");
        name = myObj.nextLine();
        stmt.setString(1, name);
        stmt.execute();
        stmt.close();
    }

    public static void dropTables(Connection conn) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{ call CPO.dropTables }");
        stmt.execute();
        stmt.close();
    }

    public static void printMenuItems() {
        System.out.print("\nCommand Line Menu:\n");
        System.out.print("1. Add a customer\n");
        System.out.print("2. Display a customer\n");
        System.out.print("3. Add a product\n");
        System.out.print("4. Display a product\n");
        System.out.print("5. Add a line item\n");
        System.out.print("6. Add an order of line items\n");
        System.out.print("7. Add a customer order\n");
        System.out.print("8. List all orders of a given customer\n");
        System.out.print("9. List customers who ordered the given product\n");
        System.out.print("10. Drop all tables\n");
        System.out.print("0. Exit\n");
    }

    public static void selectAllCustomers(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Customer");
        System.out.println("C#      Name             Address");
        System.out.println("----    -------------    -----------------");
        while (rs.next()) {
            System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t\t" + rs.getString(3));
        }
        stmt.close();
    }

    public static void selectAllProducts(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Product");
        System.out.println("P#      Name             Price");
        System.out.println("----    -------------    ------");
        while (rs.next()) {
            System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t\t" + rs.getString(3));
        }
        stmt.close();
    }

    public static void selectAllOrders(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM OrderTable");
        System.out.println("O#      C#      Date           Total Price");
        System.out.println("----    ----    -----------    -------------");
        while (rs.next()) {
            System.out.println(
                    rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t" + rs.getString(4));
        }
        stmt.close();
    }

    public static void selectAllLineItems(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM LineItem");
        System.out.println("I#      O#      P#      Qty");
        System.out.println("----    ----    ----    ----");
        while (rs.next()) {
            System.out.println(
                    rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t" + rs.getString(4));
        }
        stmt.close();
    }
}