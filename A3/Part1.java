
import java.sql.*;
import java.io.*;
import oracle.jdbc.*;
import oracle.sql.*;
import java.util.*;

public class Part1 {
  public static void main(String[] args) {
    try {
      DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
      Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "fedora", "oracle");
      createTables(conn);
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
          addLineItem(conn, 0);
          System.out.println("LineItem added successfully.");
          break;
        case 6:
          addOrder(conn, 0);
          System.out.println("Order added successfully.");
          break;
        case 7:
          addCustomerOrder(conn);
          System.out.println("Customer order added successfully.");
          break;
        case 8:
          listCustomerOrders(conn, "");
          break;
        case 9:
          listProductsOrderedByCustomer(conn, "");
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

  public static int addCustomer(Connection conn) throws SQLException {
    @SuppressWarnings("resource")
    Scanner myObj = new Scanner(System.in);
    int id = 0;
    String name;
    String address;
    boolean exists = false;

    System.out.print("Enter a name: ");
    name = myObj.nextLine();
    System.out.print("Enter an address: ");
    address = myObj.nextLine();

    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM Customer");
    while (rs.next()) {
      try {
        id = Integer.valueOf(rs.getString(1));
        if (rs.getString(2).equals(name)) {
          exists = true;
        }
      } catch (NumberFormatException e) {
      }
    }
    id++;
    if (!exists) {
      stmt.execute("insert into customer values ('" + id + "','" + name + "','" + address + "')");
    }
    stmt.close();
    return id;
  }

  public static void displayCustomer(Connection conn) throws SQLException {
    @SuppressWarnings("resource")
    Scanner myObj = new Scanner(System.in);
    System.out.print("Enter a customer name: ");
    String inputName = myObj.next();

    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM Customer");
    System.out.println("C#      Name             Address");
    System.out.println("----    -------------    -----------------");
    while (rs.next()) {
      try {
        if (rs.getString(2).equals(inputName)) {
          System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t\t" + rs.getString(3));
        }
      } catch (NumberFormatException e) {
      }
    }

    listCustomerOrders(conn, inputName);
  }

  public static void addProduct(Connection conn) throws SQLException {
    @SuppressWarnings("resource")
    Scanner myObj = new Scanner(System.in);
    int id = 0;
    String name;
    String price;
    boolean exists = false;

    System.out.print("Enter a name: ");
    name = myObj.nextLine();
    System.out.print("Enter a price: ");
    price = myObj.nextLine();

    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM Product");
    while (rs.next()) {
      try {
        id = Integer.valueOf(rs.getString(1));
        if (rs.getString(2).equals(name)) {
          exists = true;
        }
      } catch (NumberFormatException e) {
      }
    }
    id++;
    if (!exists) {
      stmt.execute("insert into product values (" + id + ",'" + name + "'," + Float.valueOf(price) + ")");
    }
    stmt.close();
  }

  public static void displayProduct(Connection conn) throws SQLException {
    @SuppressWarnings("resource")
    Scanner myObj = new Scanner(System.in);
    System.out.print("Enter a product name: ");
    String inputName = myObj.next();

    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM Product");
    System.out.println("P#      Name             Price");
    System.out.println("----    -------------    ------");
    while (rs.next()) {
      try {
        if (rs.getString(2).equals(inputName)) {
          System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t\t" + rs.getString(3));
        }
      } catch (NumberFormatException e) {
      }
    }
  }

  public static LineItem addLineItem(Connection conn, int providedOrderId) throws SQLException {
    @SuppressWarnings("resource")
    Scanner myObj = new Scanner(System.in);
    int id = 0;
    int orderId = 0;
    int productId = 0;
    int qty = 0;
    ArrayList<Double> productPrices = new ArrayList<Double>();
    double productPrice = 0;
    float currentTotalPrice = 0;

    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM LineItem");

    // get the biggest id of lineitems
    while (rs.next()) {
      try {
        id = Integer.valueOf(rs.getString(1));
      } catch (NumberFormatException e) {
      }
    }
    id++;

    int productIndex = -1;
    boolean moreProducts = true;
    while (moreProducts) {
      productIndex++;
      boolean found = false;
      while (!found) {
        System.out.print("Enter a valid product name: ");
        String productName = myObj.next();

        rs = stmt.executeQuery("SELECT * FROM Product");
        while (rs.next()) {
          try {
            if (rs.getString(2).equals(productName)) {
              productId = Integer.valueOf(rs.getString(1));
              productPrice = Double.valueOf(rs.getString(3));
              productPrices.add(productPrice);
              found = true;
            }
          } catch (NumberFormatException e) {
          }
        }
      }

      System.out.print("Enter a quantity for the product: ");
      qty = myObj.nextInt();

      productPrices.set(productIndex, productPrices.get(productIndex) * qty);

      boolean correctInput = false;

      while (!correctInput) {
        System.out.print("Do you want more products? yes or no: ");
        String decision = myObj.next();

        if (decision.equals("yes")) {
          correctInput = true;
        } else if (decision.equals("no")) {
          correctInput = true;
          moreProducts = false;
        } else {
          System.out.print("Invalid option, try again.");
        }
      }
    }

    int inputOrder = 0;

    if (providedOrderId > 0) {
      inputOrder = providedOrderId;
    }

    boolean validOrder = false;
    while (!validOrder) {
      if (!(providedOrderId > 0)) {
        System.out.print("Enter a valid order id: ");
        inputOrder = myObj.nextInt();
      }

      rs = stmt.executeQuery("SELECT * FROM OrderTable");
      while (rs.next()) {
        try {
          if (Integer.valueOf(rs.getString(1)) == inputOrder) {
            currentTotalPrice = rs.getFloat(4);
            for (double price : productPrices) {
              currentTotalPrice += (float) (price);
            }
            orderId = inputOrder;

            if (!(providedOrderId > 0)) {
              stmt.executeUpdate("UPDATE OrderTable SET TotalPrice = " + currentTotalPrice + " WHERE O# = " + orderId);
            }
            validOrder = true;
          }
        } catch (NumberFormatException e) {
        }
      }
      if (providedOrderId > 0) {
        validOrder = true;
      }
    }
    if (!(providedOrderId > 0)) {
      stmt.execute("insert into LineItem values ('" + id + "','" + orderId + "','" + productId + "','" + qty + "')");
    }

    for (double price : productPrices) {
      currentTotalPrice += (float) (price);
    }

    LineItem item = new LineItem();
    item.id = id;
    item.orderId = orderId;
    item.productId = productId;
    item.qty = qty;
    item.totalPrice = currentTotalPrice;

    return item;
  }

  public static Order addOrder(Connection conn, int inputCustomerId) throws SQLException {
    int orderId = 0;

    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM OrderTable");

    // get the biggest id of orders
    while (rs.next()) {
      try {
        orderId = Integer.valueOf(rs.getString(1));
      } catch (NumberFormatException e) {
      }
    }
    orderId++;
    LineItem item = addLineItem(conn, orderId);

    @SuppressWarnings("resource")
    Scanner myObj = new Scanner(System.in);
    System.out.print("Enter an order date: ");
    String orderDate = myObj.nextLine();
    int customerId = 0;

    boolean validInput = false;
    String customerInput = "";

    while (!validInput) {
      if (!(inputCustomerId > 0)) {
        System.out.print("New Customer or existing customer? new or existing: ");
        customerInput = myObj.next();

        if (customerInput.equals("new")) {
          validInput = true;
          customerId = addCustomer(conn);
        } else if (customerInput.equals("existing")) {
          validInput = true;

          stmt = conn.createStatement();
          rs = stmt.executeQuery("SELECT * FROM Customer");

          System.out.print("Enter a valid customer id: ");
          int customerIdInput = myObj.nextInt();

          boolean validCustomerId = false;
          while (!validCustomerId) {
            while (rs.next()) {
              try {
                if (customerIdInput == Integer.valueOf(rs.getString(1))) {
                  customerId = Integer.valueOf(rs.getString(1));
                  validCustomerId = true;
                }
              } catch (NumberFormatException e) {
              }
            }
          }
        } else {
          System.out.println("Invalid input.");
        }
      } else {
        validInput = true;
      }
    }
    if (!(inputCustomerId > 0)) {
      stmt.execute("insert into OrderTable values (" + orderId + "," + customerId + ",'" + orderDate + "',"
          + item.totalPrice + ")");
      stmt.execute("insert into LineItem values ('" + item.id + "','" + orderId + "','" + item.productId + "','"
          + item.qty + "')");
    }
    Order order = new Order();

    order.orderId = orderId;
    order.orderDate = orderDate;
    order.totalPrice = item.totalPrice;
    order.productId = item.productId;
    order.lineId = item.id;
    order.qty = item.qty;

    return order;
  }

  public static void addCustomerOrder(Connection conn) throws SQLException {
    int customerId = addCustomer(conn);

    Statement stmt = conn.createStatement();

    Order order = addOrder(conn, customerId);
    stmt.execute("insert into OrderTable values (" + order.orderId + "," + customerId + ",'" + order.orderDate + "',"
        + order.totalPrice + ")");
    stmt.execute("insert into LineItem values ('" + order.lineId + "','" + order.orderId + "','" + order.productId
        + "','" + order.qty + "')");
  }

  public static void dropTables(Connection conn) throws SQLException {
    Statement stmt = conn.createStatement();
    stmt.execute("DROP TABLE LineItem");
    stmt.execute("DROP TABLE OrderTable");
    stmt.execute("DROP TABLE Product");
    stmt.execute("DROP TABLE Customer");
    stmt.close();
  }

  public static void listCustomerOrders(Connection conn, String customerInputName) throws SQLException {
    int customerId = 0;
    @SuppressWarnings("resource")
    Scanner myObj = new Scanner(System.in);
    if (customerInputName == "") {
      System.out.print("Enter a valid customer name: ");
      String customerName = myObj.nextLine();

      Statement stmt = conn.createStatement();

      ResultSet rs = stmt.executeQuery("SELECT * FROM Customer");
      while (rs.next()) {
        try {
          if (rs.getString(2).equals(customerName)) {
            customerId = Integer.valueOf(rs.getString(1));
          }
        } catch (NumberFormatException e) {
        }
      }

      rs = stmt.executeQuery(
          "SELECT OrderTable.* FROM OrderTable JOIN Customer ON OrderTable.C# = Customer.C# WHERE Customer.C# = "
              + customerId);
      System.out.println("O#      C#      Date           Total Price");
      System.out.println("----    ----    -----------    -------------");
      while (rs.next()) {
        System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t" + rs.getString(4));
      }
      stmt.close();
    } else {
      Statement stmt = conn.createStatement();

      ResultSet rs = stmt.executeQuery("SELECT * FROM Customer");
      while (rs.next()) {
        try {
          if (rs.getString(2).equals(customerInputName)) {
            customerId = Integer.valueOf(rs.getString(1));
          }
        } catch (NumberFormatException e) {
        }
      }

      rs = stmt.executeQuery(
          "SELECT OrderTable.* FROM OrderTable JOIN Customer ON OrderTable.C# = Customer.C# WHERE Customer.C# = "
              + customerId);
      System.out.println();
      System.out.println("O#      C#      Date           Total Price");
      System.out.println("----    ----    -----------    -------------");
      while (rs.next()) {
        System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t" + rs.getString(4));
      }
      stmt.close();
    }
  }

  public static void listProductsOrderedByCustomer(Connection conn, String inputProductName) throws SQLException {
    int productId = 0;
    @SuppressWarnings("resource")
    Scanner myObj = new Scanner(System.in);
    if (inputProductName == "") {
      System.out.print("Enter a valid product name: ");
      String customerName = myObj.nextLine();

      Statement stmt = conn.createStatement();

      ResultSet rs = stmt.executeQuery("SELECT * FROM Product");
      while (rs.next()) {
        try {
          if (rs.getString(2).equals(customerName)) {
            productId = Integer.valueOf(rs.getString(1));
          }
        } catch (NumberFormatException e) {
        }
      }

      rs = stmt.executeQuery(
          "SELECT DISTINCT Customer.* FROM Customer JOIN OrderTable ON Customer.C# = OrderTable.C# JOIN LineItem ON OrderTable.O# = LineItem.O# WHERE LineItem.P# = "
              + productId);
      System.out.println("P#      Name             Price");
      System.out.println("----    -------------    ------");
      while (rs.next()) {
        System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3));
      }
      stmt.close();
    } else {
      Statement stmt = conn.createStatement();

      ResultSet rs = stmt.executeQuery("SELECT * FROM Product");
      while (rs.next()) {
        try {
          if (rs.getString(2).equals(inputProductName)) {
            productId = Integer.valueOf(rs.getString(1));
          }
        } catch (NumberFormatException e) {
        }
      }

      rs = stmt.executeQuery(
          "SELECT DISTINCT Customer.* FROM Customer JOIN OrderTable ON Customer.C# = OrderTable.C# JOIN LineItem ON OrderTable.O# = LineItem.O# WHERE LineItem.P# = "
              + productId);
      System.out.println();
      System.out.println("P#      Name             Price");
      System.out.println("----    -------------    ------");
      while (rs.next()) {
        System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3));
      }
      stmt.close();
    }
  }

  public static void createTables(Connection conn) throws SQLException {
    Statement stmt = conn.createStatement();

    stmt.execute("CREATE TABLE Customer (C# INT PRIMARY KEY, Name VARCHAR(10), Address VARCHAR(30))");
    stmt.execute("CREATE TABLE Product (P# INT PRIMARY KEY, Name VARCHAR(10), Price FLOAT)");
    stmt.execute(
        "CREATE TABLE OrderTable (O# INT PRIMARY KEY, C# INT, OrderDate VARCHAR(10), TotalPrice FLOAT, FOREIGN KEY (C#) REFERENCES Customer (C#))");
    stmt.execute(
        "CREATE TABLE LineItem (I# INT PRIMARY KEY, O# INT, P# INT, Qty INT, FOREIGN KEY (O#) REFERENCES OrderTable(O#), FOREIGN KEY(P#) REFERENCES Product (P#))");

    // insert into customer table
    stmt.execute("INSERT INTO Customer VALUES ('1', 'Smith', '1125 colonel by dr')");
    stmt.execute("INSERT INTO Customer VALUES ('2', 'Jones', '1125 colonel by dr')");
    stmt.execute("INSERT INTO Customer VALUES ('3', 'Blake', '1125 colonel by dr')");
    stmt.execute("INSERT INTO Customer VALUES ('4', 'Clark', '1125 colonel by dr')");
    stmt.execute("INSERT INTO Customer VALUES ('5', 'MacDiarmid', '1125 colonel by dr')");

    // insert into product table
    stmt.execute("INSERT INTO Product VALUES ('1', 'Apple', 1.25)");
    stmt.execute("INSERT INTO Product VALUES ('2', 'Banana', 0.79)");
    stmt.execute("INSERT INTO Product VALUES ('3', 'Orange', 1.75)");
    stmt.execute("INSERT INTO Product VALUES ('4', 'Peach', 2.75)");
    stmt.execute("INSERT INTO Product VALUES ('5', 'Watermelon', 4.99)");

    // insert into orders
    stmt.execute("INSERT INTO OrderTable VALUES ('1', '1', 'Mar 3 2024', 1.25)");
    stmt.execute("INSERT INTO OrderTable VALUES ('2', '2', 'Mar 3 2024', 2.04)");
    stmt.execute("INSERT INTO OrderTable VALUES ('3', '3', 'Mar 3 2024', 3.79)");
    stmt.execute("INSERT INTO OrderTable VALUES ('4', '4', 'Mar 3 2024', 6.54)");
    stmt.execute("INSERT INTO OrderTable VALUES ('5', '5', 'Mar 3 2024', 11.53)");

    // insert into line item
    stmt.execute("INSERT INTO LineItem VALUES ('1', '1', '1', '1')");
    stmt.execute("INSERT INTO LineItem VALUES ('2', '2', '1', '1')");
    stmt.execute("INSERT INTO LineItem VALUES ('3', '2', '2', '1')");
    stmt.execute("INSERT INTO LineItem VALUES ('4', '3', '1', '1')");
    stmt.execute("INSERT INTO LineItem VALUES ('5', '3', '2', '1')");
    stmt.execute("INSERT INTO LineItem VALUES ('6', '3', '3', '1')");
    stmt.execute("INSERT INTO LineItem VALUES ('7', '4', '1', '1')");
    stmt.execute("INSERT INTO LineItem VALUES ('8', '4', '2', '1')");
    stmt.execute("INSERT INTO LineItem VALUES ('9', '4', '3', '1')");
    stmt.execute("INSERT INTO LineItem VALUES ('10', '4', '4', '1')");
    stmt.execute("INSERT INTO LineItem VALUES ('11', '5', '1', '1')");
    stmt.execute("INSERT INTO LineItem VALUES ('12', '5', '2', '1')");
    stmt.execute("INSERT INTO LineItem VALUES ('13', '5', '3', '1')");
    stmt.execute("INSERT INTO LineItem VALUES ('14', '5', '4', '1')");
    stmt.execute("INSERT INTO LineItem VALUES ('15', '5', '5', '1')");
    stmt.close();
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
      System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t" + rs.getString(4));
    }
    stmt.close();
  }

  public static void selectAllLineItems(Connection conn) throws SQLException {
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM LineItem");
    System.out.println("I#      O#      P#      Qty");
    System.out.println("----    ----    ----    ----");
    while (rs.next()) {
      System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t" + rs.getString(4));
    }
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
}

class LineItem {
  int id = 0;
  int orderId = 0;
  int productId = 0;
  int qty = 0;
  float totalPrice = 0;
}

class Order {
  int orderId = 0;
  String orderDate = "";
  float totalPrice;
  int lineId = 0;
  int productId = 0;
  int qty = 0;
}
