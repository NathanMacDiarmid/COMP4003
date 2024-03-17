-- Part 1.1 --

create type item_v as object (i# varchar2(5), o# varchar2(5), p# varchar2(5), qty varchar2(5));
/

create type itemtable as table of item_v;
/

create type order_v as object (o# varchar2(5), c# varchar2(5), odate varchar2(20), price varchar2(10), items itemtable);
/

create type ordertable as table of order_v;
/

create type item_id as varray(10) of varchar2(10);
/

create table Customer (c# varchar2(5), name varchar2(15), address varchar2(20), orders ordertable) nested table orders store as order_t(nested table items store as item_t);
create table Product(p# varchar2(5), name varchar2(10), price varchar2(10), i# item_id);

-- Part 1.2 --

insert into Customer values ('1', 'Smith', '1125 Colonel By Dr', ordertable(order_v('1', '1', 'Jan 1, 2024', '1.99', itemtable(item_v('1', '1', '1', '1'))), order_v('10', '1', 'Feb 5, 2024', '13.81', itemtable(item_v('26', '10', '1', '1'), item_v('27', '10', '2', '1'), item_v('28', '10', '3', '1'), item_v('29', '10', '4', '1'), item_v('30', '10', '5', '1')))));
insert into Customer values ('2', 'Jones', '1125 Colonel By Dr', ordertable(order_v('2', '2', 'Jan 2, 2024', '2.78', itemtable(item_v('2', '2', '1', '1'), item_v('3', '2', '2', '1'))), order_v('9', '2', 'Feb 4, 2024', '11.82', itemtable(item_v('22', '9', '2', '1'), item_v('23', '9', '3', '1'), item_v('24', '9', '4', '1'), item_v('25', '9', '5', '1')))));
insert into Customer values ('3', 'Blake', '1125 Colonel By Dr', ordertable(order_v('3', '3', 'Jan 3, 2024', '5.57', itemtable(item_v('4', '3', '1', '1'), item_v('5', '3', '2', '1'), item_v('6', '3', '3', '1'))), order_v('8', '3', 'Feb 3, 2024', '11.03', itemtable(item_v('19', '8', '3', '1'), item_v('20', '8', '4', '1'), item_v('21', '8', '5', '1')))));
insert into Customer values ('4', 'Clark', '1125 Colonel By Dr', ordertable(order_v('4', '4', 'Jan 4, 2024', '8.82', itemtable(item_v('7', '4', '1', '1'), item_v('8', '4', '2', '1'), item_v('9', '4', '3', '1'), item_v('10', '4', '4', '1'))), order_v('7', '4', 'Feb 2, 2024', '8.24', itemtable(item_v('17', '7', '4', '1'), item_v('18', '7', '5', '1')))));
insert into Customer values ('5', 'MacDiarmid', '1125 Colonel By Dr', ordertable(order_v('5', '5', 'Jan 5, 2024', '13.81', itemtable(item_v('11', '5', '1', '1'), item_v('12', '5', '2', '1'), item_v('13', '5', '3', '1'), item_v('14', '5', '4', '1'), item_v('15', '5', '5', '1'))), order_v('6', '5', 'Feb 1, 2024', '4.99', itemtable(item_v('16', '6', '5', '1')))));

insert into Product values ('1', 'apple', '1.99', item_id('1', '2', '4', '7', '11', '26'));
insert into Product values ('2', 'banana', '0.79', item_id('3', '5', '8', '12', '27', '22'));
insert into Product values ('3', 'orange', '2.79', item_id('6', '9', '13', '28', '23', '19'));
insert into Product values ('4', 'peach', '3.25', item_id('10', '14', '29', '24', '20', '17'));
insert into Product values ('5', 'watermelon', '4.99', item_id('15', '30', '25', '21', '18', '16'));

-- Part 1.3.1 --

SELECT c.c#, c.name, c.address FROM Customer c;

-- Part 1.3.2 --

SELECT p.p#, p.name, p.price FROM Product p;

-- Part 1.3.3 --

SELECT DISTINCT c.name FROM Customer c JOIN TABLE(c.orders) o ON 1 = 1 JOIN TABLE(o.items) i ON i.o# = o.o# JOIN Product p ON p.p# = i.p# WHERE p.name = 'banana';

-- Part 1.3.4 --

SELECT c.c#, c.name, c.address, CAST(MULTISET(SELECT o.* FROM TABLE(c.orders) o WHERE o.c# = c.c#) AS ordertable) AS orders FROM Customer c WHERE c.name = 'MacDiarmid';

-- Part 1.3.5 --

SELECT c.name FROM Customer c WHERE (SELECT COUNT(DISTINCT p.p#) FROM Product p) = (SELECT COUNT(DISTINCT i.p#) FROM TABLE(c.orders) o JOIN TABLE(o.items) i ON i.o# = o.o#);

drop table Product;
drop table Customer;
drop type ordertable;
drop type order_v;
drop type itemtable;
drop type item_v;
drop type item_id;

