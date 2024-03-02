-- You need to create all four tables exactly the same as below 
-- in order to create the package successfully. In order to use 
-- it for your own programs, you should modify the table schemas. 

CREATE OR REPLACE PACKAGE CPO AS 
    PROCEDURE createTables;
    PROCEDURE dropTables;
    PROCEDURE addCustomer(name IN VARCHAR2, address IN VARCHAR2);
    PROCEDURE addProduct(name IN VARCHAR2, Price IN FLOAT);
    PROCEDURE printCustomer(name IN VARCHAR2);
    PROCEDURE printProduct(name IN VARCHAR2);
    PROCEDURE addLineitem(name IN VARCHAR2, quantity in INT);
    PROCEDURE addOrderLineitems;
    PROCEDURE addCustomerOrder(name IN VARCHAR2);
    PROCEDURE listCustomerOrder(name IN VARCHAR2);
    PROCEDURE listCustomerByProduct(name IN VARCHAR2);
END CPO;
/
CREATE OR REPLACE PACKAGE BODY CPO AS

    -- Implementation of procedures and functions
    PROCEDURE createTables AS
    BEGIN
        -- Create Customer table
        EXECUTE IMMEDIATE 'CREATE TABLE Customer (C# INT PRIMARY KEY, Name VARCHAR(10), Address VARCHAR(30))';
        
        -- Create Product table
        EXECUTE IMMEDIATE 'CREATE TABLE Product (P# INT PRIMARY KEY, Name VARCHAR(10), Price FLOAT)';
        
        -- Create OrderTable table
        EXECUTE IMMEDIATE 'CREATE TABLE OrderTable (O# INT PRIMARY KEY, C# INT, OrderDate VARCHAR(10), TotalPrice FLOAT, FOREIGN KEY (C#) REFERENCES Customer (C#))';
        
        -- Create LineItem table
        EXECUTE IMMEDIATE 'CREATE TABLE LineItem (I# INT PRIMARY KEY, O# INT, P# INT, Qty INT, FOREIGN KEY (O#) REFERENCES OrderTable(O#), FOREIGN KEY(P#) REFERENCES Product (P#))';
    END createTables;

    PROCEDURE dropTables AS
    BEGIN
        -- Create Customer table
        EXECUTE IMMEDIATE 'DROP TABLE LINEITEM';
        EXECUTE IMMEDIATE 'DROP TABLE ORDERTABLE';
        EXECUTE IMMEDIATE 'DROP TABLE PRODUCT';
        EXECUTE IMMEDIATE 'DROP TABLE CUSTOMER';
    END dropTables;

PROCEDURE addCustomer(name IN VARCHAR2, address IN VARCHAR2) AS
    maxid INT;
BEGIN
    -- Find the maximum C# in the CUSTOMER table
    SELECT NVL(MAX("C#"), 0) INTO maxid FROM CUSTOMER;
    
    -- Increment the ID by 1 for the new customer
    maxid := maxid + 1;
    
    -- Insert the new customer
    EXECUTE IMMEDIATE 'INSERT INTO CUSTOMER ("C#", Name, Address) VALUES (:1, :2, :3)'
        USING maxid, name, address;
END addCustomer;

PROCEDURE addProduct(name IN VARCHAR2, price IN FLOAT) AS
    maxid INT;
BEGIN
    -- Find the maximum C# in the CUSTOMER table
    SELECT NVL(MAX("P#"), 0) INTO maxid FROM PRODUCT;
    
    -- Increment the ID by 1 for the new customer
    maxid := maxid + 1;
    
    -- Insert the new customer
    EXECUTE IMMEDIATE 'INSERT INTO PRODUCT ("P#", Name, Price) VALUES (:1, :2, :3)'
        USING maxid, name, price;
END addProduct;

PROCEDURE printCustomer(name IN VARCHAR2) AS
        c_num INT;
        c_name VARCHAR2(10);
        c_address VARCHAR2(30);
    BEGIN
        -- Retrieve customer information based on the provided name
        SELECT "C#", Name, Address INTO c_num, c_name, c_address
        FROM Customer
        WHERE Name = name;

        -- Print customer information
        DBMS_OUTPUT.PUT_LINE('Customer ID: ' || c_num);
        DBMS_OUTPUT.PUT_LINE('Name: ' || c_name);
        DBMS_OUTPUT.PUT_LINE('Address: ' || c_address);
     COMMIT;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            DBMS_OUTPUT.PUT_LINE('Customer with name ' || name || ' not found.');
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
    END printCustomer;

    PROCEDURE printProduct(name IN VARCHAR2) AS
        p_num INT;
        p_name VARCHAR2(10);
        p_price FLOAT;
    BEGIN
        -- Retrieve product information based on the provided name
        SELECT "P#", Name, Price INTO p_num, p_name, p_price
        FROM Product
        WHERE Name = name;

        -- Print product information
        DBMS_OUTPUT.PUT_LINE('Product ID: ' || p_num);
        DBMS_OUTPUT.PUT_LINE('Name: ' || p_name);
        DBMS_OUTPUT.PUT_LINE('Price: $' || p_price);
     COMMIT;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            DBMS_OUTPUT.PUT_LINE('Product with name ' || name || ' not found.');
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
    END printProduct;

    PROCEDURE addLineitem(name IN VARCHAR2, quantity IN INT) AS
        product_id INT;
    BEGIN
        -- Find the product ID based on the provided name
        SELECT "P#" INTO product_id
        FROM Product
        WHERE Name = name;

        -- Insert the line item with the obtained product ID and provided quantity
        EXECUTE IMMEDIATE 'INSERT INTO LineItem (I#, O#, P#, Qty) VALUES ((SELECT NVL(MAX("I#"), 0) + 1 FROM LineItem), NULL, :1, :2)'
            USING product_id, quantity;
    COMMIT;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            DBMS_OUTPUT.PUT_LINE('Product with name ' || name || ' not found.');
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
    END addLineitem;

    PROCEDURE addOrderLineitems AS
    order_id INT;
    total_price FLOAT;
BEGIN
    -- Create a new order with null customer ID and a generic date
    EXECUTE IMMEDIATE 'INSERT INTO OrderTable (O#, C#, OrderDate) VALUES ((SELECT NVL(MAX("O#"), 0) + 1 FROM OrderTable), NULL, ''2024-01-02'')';

    -- Retrieve the newly created order ID
    SELECT MAX("O#") INTO order_id FROM OrderTable;

    -- Calculate the total price of line items with O# as null
    SELECT SUM(Qty * Price) INTO total_price
    FROM LineItem
    JOIN Product ON LineItem.P# = Product.P#
    WHERE O# IS NULL;

    -- Update the total price of the newly created order
    EXECUTE IMMEDIATE 'UPDATE OrderTable SET TotalPrice = :1 WHERE "O#" = :2'
        USING total_price, order_id;

    -- Update the O# of line items to the newly created order ID
    EXECUTE IMMEDIATE 'UPDATE LineItem SET O# = :1 WHERE O# IS NULL'
        USING order_id;

    DBMS_OUTPUT.PUT_LINE('Order created with ID: ' || order_id);
    DBMS_OUTPUT.PUT_LINE('Total Price: $' || total_price);
     COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END addOrderLineitems;

    PROCEDURE addCustomerOrder(name IN VARCHAR2) AS
    customer_id INT;
BEGIN
    -- Find the customer ID based on the provided name
    SELECT "C#" INTO customer_id
    FROM Customer
    WHERE Name = name;

    -- Update all OrderTable rows with null C# to the obtained customer ID
    EXECUTE IMMEDIATE 'UPDATE OrderTable SET C# = :1 WHERE C# IS NULL'
        USING customer_id;

    DBMS_OUTPUT.PUT_LINE('Customer order assigned to customer: ' || name);
     COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Customer with name ' || name || ' not found.');
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END addCustomerOrder;

    PROCEDURE listCustomerOrder(name IN VARCHAR2) AS
    customer_id INT;
BEGIN
    -- Find the customer ID based on the provided name
    SELECT "C#" INTO customer_id
    FROM Customer
    WHERE Name = name;

    -- Display all orders for the customer
    FOR order_rec IN (SELECT * FROM OrderTable WHERE "C#" = customer_id) LOOP
        DBMS_OUTPUT.PUT_LINE('Order ID: ' || order_rec."O#");
        DBMS_OUTPUT.PUT_LINE('Order Date: ' || order_rec.OrderDate);
        DBMS_OUTPUT.PUT_LINE('Total Price: $' || order_rec.TotalPrice);
        DBMS_OUTPUT.PUT_LINE('--------------------------');
    END LOOP;

    IF SQL%NOTFOUND THEN
        DBMS_OUTPUT.PUT_LINE('No orders found for customer: ' || name);
    END IF;
     COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Customer with name ' || name || ' not found.');
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END listCustomerOrder;

PROCEDURE listCustomerByProduct(name IN VARCHAR2) AS
    product_id INT;
BEGIN
    -- Find the product ID based on the provided name
    SELECT "P#" INTO product_id
    FROM Product
    WHERE Name = name;

    -- Display all customers who ordered the product
    FOR customer_rec IN (
        SELECT DISTINCT Customer.Name
        FROM Customer
        JOIN OrderTable ON Customer."C#" = OrderTable."C#"
        JOIN LineItem ON OrderTable."O#" = LineItem."O#"
        WHERE LineItem."P#" = product_id
    ) LOOP
        DBMS_OUTPUT.PUT_LINE('Customer Name: ' || customer_rec.Name);
    END LOOP;

    IF SQL%NOTFOUND THEN
        DBMS_OUTPUT.PUT_LINE('No customers found who ordered product: ' || name);
    END IF;
    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Product with name ' || name || ' not found.');
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END listCustomerByProduct;
END CPO;
/
