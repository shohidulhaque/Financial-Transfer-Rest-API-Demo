--This script is used for unit test cases, DO NOT CHANGE!

--SET REFERENTIAL_INTEGRITY FALSE;

DROP TABLE IF EXISTS AccountHolder;

CREATE TABLE AccountHolder (
  Id LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
  FirstName VARCHAR(30) NOT NULL,
  LastName VARCHAR(30) NOT NULL,
  AccountHolderId VARCHAR(30) NOT NULL
);

CREATE UNIQUE INDEX idx_ue on AccountHolder(AccountHolderId);

INSERT INTO AccountHolder (Id, AccountHolderId, FirstName, LastName) VALUES (1,'123yangluo', 'Shohidul1', 'Haque1');
INSERT INTO AccountHolder (Id, AccountHolderId, FirstName, LastName) VALUES (2,'123qinfran', 'Shohidul2', 'Haque2');
INSERT INTO AccountHolder (Id, AccountHolderId, FirstName, LastName) VALUES (3,'123liusisi', 'Shohidul3', 'Haque3');

DROP TABLE IF EXISTS Account;

CREATE TABLE Account (
  Id LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
  AccountNumber VARCHAR(30) NOT NULL,
  SortCode VARCHAR(8) NOT NULL,
  Balance DECIMAL(19,4) NOT NULL,
  AccountHolder LONG NOT NULL,
  FOREIGN KEY (AccountHolder) REFERENCES AccountHolder(Id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_acc on Account(AccountNumber);

INSERT INTO Account (Id, AccountNumber,SortCode,Balance, AccountHolder) VALUES (1,'112231237', '123456',100.0000,1);
INSERT INTO Account (AccountNumber,SortCode,Balance,AccountHolder) VALUES ('11223123', '123456',100.0000,1);
INSERT INTO Account (AccountNumber,SortCode,Balance,AccountHolder) VALUES ('21223123', '123456',200.0000,2);
INSERT INTO Account (AccountNumber,SortCode,Balance,AccountHolder) VALUES ('31223123', '123456',100.0000,3);
INSERT INTO Account (AccountNumber,SortCode,Balance,AccountHolder) VALUES ('87523123', '123456',500.0000,3);

DROP TABLE IF EXISTS AccountTransfer;
CREATE TABLE AccountTransfer(
  Id LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
  Amount DECIMAL(19,4) NOT NULL,
  FromAccountId LONG NOT NULL,
  ToAccountId LONG NOT NULL,
  TransactionTime TIMESTAMP NOT NULL,
  FOREIGN KEY (FromAccountId) REFERENCES Account(Id),
  FOREIGN KEY (ToAccountId) REFERENCES  Account(Id)
);

--SET REFERENTIAL_INTEGRITY TRUE;