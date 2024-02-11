CREATE OR REPLACE PACKAGE BODY CPO AS

PROCEDURE AddCustomer(p_Name in VARCHAR2, p_Address in VARCHAR2)
IS
    MaxCid INT;
BEGIN
    SELECT MAX(Cid) INTO MaxCid FROM Customer;

    IF MaxCid IS NOT NULL THEN
        MaxCid := MaxCid + 1;
    ELSE
        MaxCid := 1;
    END IF

    INSERT INTO Customer VALUES (MaxCid, p_Name, p_Address);
COMMIT;

DBMS_OUTPUT.PUT_LINE('Customer added successfully');

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error adding customer: ' || SQLERRM);
        ROLLBACK;
END AddCustomer;