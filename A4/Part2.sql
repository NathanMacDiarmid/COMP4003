-- create type test_t as object (name varchar2(10));
-- /

-- create type item as varray(10) of ref test_t;
-- /

-- create table test of test_t;
-- alter table test add spouse item;
-- insert into test values ('Anna', null);
-- insert into test select 'Cook', ref(t) from test t where name = 'Anna';
-- update test set spouse = (select ref(t) from test t where t.name = 'Cook') where name = 'Anna';

create type item_t as object(i_id int, o_id int, p_id int, qty int);
/

create type item_v as varray(10) of ref item_t;
/

create type order_t as object(o_id int, c_id int, odate varchar2(20), price varchar2(20), items item_v);
/

create type order_v as varray(10) of ref order_t;
/

create type customer_t as object(c_id int, name varchar2(10), address varchar2(20), orders order_v);
/

create type product_t as object(p_id int, name varchar2(10), price varchar2(10), items item_v);
/

create table Customer of customer_t;
create table Orders of order_t;
create table LineItems of item_t;
create table Product of product_t;

desc Customer;
desc Orders;
desc LineItems;
desc Product;

insert into LineItems values (1, 1, 1, 1);
insert into LineItems values (2, 2, 1, 1);
insert into LineItems values (3, 2, 2, 1);
insert into LineItems values (4, 3, 1, 1);
insert into LineItems values (5, 3, 2, 1);
insert into LineItems values (6, 3, 3, 1);
insert into LineItems values (7, 4, 1, 1);
insert into LineItems values (8, 4, 2, 1);
insert into LineItems values (9, 4, 3, 1);
insert into LineItems values (10, 4, 4, 1);
insert into LineItems values (11, 5, 1, 1);
insert into LineItems values (12, 5, 2, 1);
insert into LineItems values (13, 5, 3, 1);
insert into LineItems values (14, 5, 4, 1);
insert into LineItems values (15, 5, 5, 1);

insert into LineItems values (16, 6, 5, 1);
insert into LineItems values (17, 7, 5, 1);
insert into LineItems values (18, 7, 4, 1);
insert into LineItems values (19, 8, 5, 1);
insert into LineItems values (20, 8, 4, 1);
insert into LineItems values (21, 8, 3, 1);
insert into LineItems values (22, 9, 5, 1);
insert into LineItems values (23, 9, 4, 1);
insert into LineItems values (24, 9, 3, 1);
insert into LineItems values (25, 9, 2, 1);
insert into LineItems values (26, 10, 5, 1);
insert into LineItems values (27, 10, 4, 1);
insert into LineItems values (28, 10, 3, 1);
insert into LineItems values (29, 10, 2, 1);
insert into LineItems values (30, 10, 1, 1);

insert into Product values (1, 'apple', '1.25', item_v((select ref(i) from LineItems i where i_id = 1), (select ref(i) from LineItems i where i_id = 2), (select ref(i) from LineItems i where i_id = 4), (select ref(i) from LineItems i where i_id = 7), (select ref(i) from LineItems i where i_id = 11), (select ref(i) from LineItems i where i_id = 30)));
insert into Product values (2, 'banana', '0.79', item_v((select ref(i) from LineItems i where i_id = 3), (select ref(i) from LineItems i where i_id = 5), (select ref(i) from LineItems i where i_id = 8), (select ref(i) from LineItems i where i_id = 12), (select ref(i) from LineItems i where i_id = 25), (select ref(i) from LineItems i where i_id = 29)));
insert into Product values (3, 'orange', '1.75', item_v((select ref(i) from LineItems i where i_id = 6), (select ref(i) from LineItems i where i_id = 9), (select ref(i) from LineItems i where i_id = 13), (select ref(i) from LineItems i where i_id = 21), (select ref(i) from LineItems i where i_id = 24), (select ref(i) from LineItems i where i_id = 28)));
insert into Product values (4, 'peach', '3.25', item_v((select ref(i) from LineItems i where i_id = 10), (select ref(i) from LineItems i where i_id = 14), (select ref(i) from LineItems i where i_id = 18), (select ref(i) from LineItems i where i_id = 20), (select ref(i) from LineItems i where i_id = 23), (select ref(i) from LineItems i where i_id = 27)));
insert into Product values (5, 'watermelon', '4.99', item_v((select ref(i) from LineItems i where i_id = 15), (select ref(i) from LineItems i where i_id = 16), (select ref(i) from LineItems i where i_id = 17), (select ref(i) from LineItems i where i_id = 19), (select ref(i) from LineItems i where i_id = 22), (select ref(i) from LineItems i where i_id = 26)));

insert into Orders values (1, 1, 'Jan 1, 2024', '1.25', item_v((select ref(i) from LineItems i where i_id = 1)));
insert into Orders values (2, 2, 'Jan 2, 2024', '2.04', item_v((select ref(i) from LineItems i where i_id = 2), (select ref(i) from LineItems i where i_id = 3)));
insert into Orders values (3, 3, 'Jan 3, 2024', '3.79', item_v((select ref(i) from LineItems i where i_id = 4), (select ref(i) from LineItems i where i_id = 5), (select ref(i) from LineItems i where i_id = 6)));
insert into Orders values (4, 4, 'Jan 4, 2024', '7.04', item_v((select ref(i) from LineItems i where i_id = 7), (select ref(i) from LineItems i where i_id = 8), (select ref(i) from LineItems i where i_id = 9), (select ref(i) from LineItems i where i_id = 10)));
insert into Orders values (5, 5, 'Jan 5, 2024', '12.03', item_v((select ref(i) from LineItems i where i_id = 11), (select ref(i) from LineItems i where i_id = 12), (select ref(i) from LineItems i where i_id = 13), (select ref(i) from LineItems i where i_id = 14), (select ref(i) from LineItems i where i_id = 15)));

insert into Orders values (6, 5, 'Feb 1, 2024', '12.03', item_v((select ref(i) from LineItems i where i_id = 16)));
insert into Orders values (7, 4, 'Feb 2, 2024', '10.78', item_v((select ref(i) from LineItems i where i_id = 17), (select ref(i) from LineItems i where i_id = 18)));
insert into Orders values (8, 3, 'Feb 3, 2024', '9.99', item_v((select ref(i) from LineItems i where i_id = 19), (select ref(i) from LineItems i where i_id = 20), (select ref(i) from LineItems i where i_id = 21)));
insert into Orders values (9, 2, 'Feb 4, 2024', '8.24', item_v((select ref(i) from LineItems i where i_id = 22), (select ref(i) from LineItems i where i_id = 23), (select ref(i) from LineItems i where i_id = 24), (select ref(i) from LineItems i where i_id = 25)));
insert into Orders values (10, 1, 'Feb 5, 2024', '4.99', item_v((select ref(i) from LineItems i where i_id = 26), (select ref(i) from LineItems i where i_id = 27), (select ref(i) from LineItems i where i_id = 28), (select ref(i) from LineItems i where i_id = 29), (select ref(i) from LineItems i where i_id = 30)));

insert into Customer values (1, 'Smith', '1125 Colonel By Dr', order_v((select ref(o) from Orders o where o_id = 1), (select ref(o) from Orders o where o_id = 10)));
insert into Customer values (2, 'Jones', '1125 Colonel By Dr', order_v((select ref(o) from Orders o where o_id = 2), (select ref(o) from Orders o where o_id = 9)));
insert into Customer values (3, 'Blake', '1125 Colonel By Dr', order_v((select ref(o) from Orders o where o_id = 3), (select ref(o) from Orders o where o_id = 8)));
insert into Customer values (4, 'Clark', '1125 Colonel By Dr', order_v((select ref(o) from Orders o where o_id = 4), (select ref(o) from Orders o where o_id = 7)));
insert into Customer values (5, 'MacDiarmid', '1125 Colonel By Dr', order_v((select ref(o) from Orders o where o_id = 5), (select ref(o) from Orders o where o_id = 6)));

select * from Customer;
select * from Orders;
select * from LineItems;
select * from Product;

-- SELECT ROWID, o.*
-- FROM Orders o
-- WHERE odate = 'Feb 1, 2024';

-- -- Get the ID of the inserted customer in the Customer table
-- SELECT ROWID, c.*
-- FROM Customer c
-- WHERE name = 'Smith';

drop table Customer;
drop table Orders;
drop table LineItems;
drop table Product;

drop type customer_t;
drop type product_t;
drop type order_v;
drop type order_t;
drop type item_v;
drop type item_t;

-- create type item as object (qty varchar2(5)) not final;
-- /
-- create type ord as object (odate varchar2(20), price varchar2(10) items ref item) not final;
-- /
-- create type cus as object (name varchar2(15), address varchar2(20), orders ref ord) not final;
-- /
-- create type prod as object (name varchar2(10), price varchar2(10), items item) not final;
-- /

-- create type LineItems as table of item;
-- create type Orders as table of ord;
-- create type Customer as table of cus;
-- create type Product as table of prod;

-- select * from Customer;
-- select * from Product;
-- select * from Orders;
-- select * from LineItems;

-- drop type Customer;
-- drop type Product;
-- drop type Orders;
-- drop type LineItems;

-- drop type cus;
-- drop type prod;
-- drop type ord;
-- drop type item;