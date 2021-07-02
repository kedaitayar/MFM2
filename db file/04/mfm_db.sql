BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "android_metadata" (
	"locale"	TEXT
);
CREATE TABLE IF NOT EXISTS "Account" (
	"accountId"	INTEGER,
	"accountName"	TEXT NOT NULL,
	PRIMARY KEY("accountId" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "Transaction" (
	"transactionId"	INTEGER,
	"transactionAmount"	REAL NOT NULL,
	"transactionTime"	TEXT,
	"transactionType"	INTEGER NOT NULL,
	"transactionAccountId"	INTEGER NOT NULL,
	"transactionBudgetId"	INTEGER,
	"transactionAccountTransferTo"	INTEGER,
	PRIMARY KEY("transactionId" AUTOINCREMENT),
	FOREIGN KEY("transactionAccountTransferTo") REFERENCES "Account"("accountId") ON UPDATE NO ACTION ON DELETE CASCADE,
	FOREIGN KEY("transactionBudgetId") REFERENCES "Budget"("budgetId") ON UPDATE NO ACTION ON DELETE CASCADE,
	FOREIGN KEY("transactionAccountId") REFERENCES "Account"("accountId") ON UPDATE NO ACTION ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS "TransactionType" (
	"transactionTypeId"	INTEGER,
	"transactionTypeName"	TEXT NOT NULL,
	PRIMARY KEY("transactionTypeId" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "Budget" (
	"budgetId"	INTEGER,
	"budgetGoal"	REAL NOT NULL,
	"budgetName"	TEXT NOT NULL,
	"budgetType"	INTEGER NOT NULL,
	PRIMARY KEY("budgetId" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "BudgetTransaction" (
	"budgetTransactionMonth"	INTEGER NOT NULL,
	"budgetTransactionYear"	INTEGER NOT NULL,
	"budgetTransactionAmount"	REAL NOT NULL,
	"budgetTransactionBudgetId"	INTEGER NOT NULL,
	PRIMARY KEY("budgetTransactionMonth","budgetTransactionYear","budgetTransactionBudgetId"),
	FOREIGN KEY("budgetTransactionBudgetId") REFERENCES "Budget"("budgetId") ON UPDATE NO ACTION ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS "BudgetType" (
	"budgetTypeId"	INTEGER,
	"budgetTypeName"	TEXT NOT NULL,
	PRIMARY KEY("budgetTypeId" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "BudgetDeadline" (
	"budgetId"	INTEGER,
	"budgetDeadline"	TEXT,
	PRIMARY KEY("budgetId"),
	FOREIGN KEY("budgetId") REFERENCES "Budget"("budgetId") ON UPDATE NO ACTION ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS "room_master_table" (
	"id"	INTEGER,
	"identity_hash"	TEXT,
	PRIMARY KEY("id")
);
INSERT INTO "android_metadata" ("locale") VALUES ('en_US');
INSERT INTO "Account" ("accountId","accountName") VALUES (1,'Cash');
INSERT INTO "TransactionType" ("transactionTypeId","transactionTypeName") VALUES (1,'Expense');
INSERT INTO "TransactionType" ("transactionTypeId","transactionTypeName") VALUES (2,'Income');
INSERT INTO "TransactionType" ("transactionTypeId","transactionTypeName") VALUES (3,'Transfer');
INSERT INTO "Budget" ("budgetId","budgetGoal","budgetName","budgetType") VALUES (1,0.0,'Food',1);
INSERT INTO "Budget" ("budgetId","budgetGoal","budgetName","budgetType") VALUES (2,0.0,'Groceries',1);
INSERT INTO "Budget" ("budgetId","budgetGoal","budgetName","budgetType") VALUES (3,0.0,'Rent',1);
INSERT INTO "Budget" ("budgetId","budgetGoal","budgetName","budgetType") VALUES (4,0.0,'Electric',1);
INSERT INTO "Budget" ("budgetId","budgetGoal","budgetName","budgetType") VALUES (5,0.0,'Water',1);
INSERT INTO "Budget" ("budgetId","budgetGoal","budgetName","budgetType") VALUES (6,0.0,'Transportation',1);
INSERT INTO "Budget" ("budgetId","budgetGoal","budgetName","budgetType") VALUES (7,0.0,'Bills',1);
INSERT INTO "Budget" ("budgetId","budgetGoal","budgetName","budgetType") VALUES (8,0.0,'Car Maintenance',2);
INSERT INTO "Budget" ("budgetId","budgetGoal","budgetName","budgetType") VALUES (9,0.0,'Home Maintenance',2);
INSERT INTO "Budget" ("budgetId","budgetGoal","budgetName","budgetType") VALUES (10,0.0,'Medical',2);
INSERT INTO "Budget" ("budgetId","budgetGoal","budgetName","budgetType") VALUES (11,0.0,'Clothing',2);
INSERT INTO "Budget" ("budgetId","budgetGoal","budgetName","budgetType") VALUES (12,0.0,'Fun Fund',1);
INSERT INTO "Budget" ("budgetId","budgetGoal","budgetName","budgetType") VALUES (13,0.0,'Vacation',2);
INSERT INTO "Budget" ("budgetId","budgetGoal","budgetName","budgetType") VALUES (14,0.0,'Stuff I Forgot to Budget for',2);
INSERT INTO "Budget" ("budgetId","budgetGoal","budgetName","budgetType") VALUES (15,0.0,'Emergency Funds',2);
INSERT INTO "BudgetType" ("budgetTypeId","budgetTypeName") VALUES (1,'Monthly');
INSERT INTO "BudgetType" ("budgetTypeId","budgetTypeName") VALUES (2,'Yearly');
INSERT INTO "room_master_table" ("id","identity_hash") VALUES (42,'4d4072a23799216c0675a71384991ec5');
CREATE UNIQUE INDEX IF NOT EXISTS "index_Account_accountName" ON "Account" (
	"accountName"
);
CREATE INDEX IF NOT EXISTS "index_Transaction_transactionAccountId_transactionBudgetId_transactionAccountTransferTo" ON "Transaction" (
	"transactionAccountId",
	"transactionBudgetId",
	"transactionAccountTransferTo"
);
CREATE UNIQUE INDEX IF NOT EXISTS "index_Budget_budgetName" ON "Budget" (
	"budgetName"
);
CREATE UNIQUE INDEX IF NOT EXISTS "index_BudgetType_budgetTypeName" ON "BudgetType" (
	"budgetTypeName"
);
COMMIT;
