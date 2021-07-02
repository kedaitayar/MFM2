BEGIN TRANSACTION;
DROP TABLE IF EXISTS "android_metadata";
CREATE TABLE IF NOT EXISTS "android_metadata" (
	"locale"	TEXT
);
DROP TABLE IF EXISTS "Account";
CREATE TABLE IF NOT EXISTS "Account" (
	"accountId"	INTEGER NOT NULL,
	"accountName"	TEXT NOT NULL,
	PRIMARY KEY("accountId" AUTOINCREMENT)
);
DROP TABLE IF EXISTS "Transaction";
CREATE TABLE IF NOT EXISTS "Transaction" (
	"transactionId"	INTEGER NOT NULL,
	"transactionAmount"	REAL NOT NULL,
	"transactionTime"	TEXT,
	"transactionType"	INTEGER NOT NULL,
	"transactionAccountId"	INTEGER NOT NULL,
	"transactionBudgetId"	INTEGER,
	"transactionAccountTransferTo"	INTEGER,
	"transactionNote"	TEXT NOT NULL,
	FOREIGN KEY("transactionBudgetId") REFERENCES "Budget"("budgetId") ON UPDATE NO ACTION ON DELETE CASCADE,
	FOREIGN KEY("transactionAccountTransferTo") REFERENCES "Account"("accountId") ON UPDATE NO ACTION ON DELETE CASCADE,
	FOREIGN KEY("transactionAccountId") REFERENCES "Account"("accountId") ON UPDATE NO ACTION ON DELETE CASCADE,
	PRIMARY KEY("transactionId" AUTOINCREMENT)
);
DROP TABLE IF EXISTS "TransactionType";
CREATE TABLE IF NOT EXISTS "TransactionType" (
	"transactionTypeId"	INTEGER NOT NULL,
	"transactionTypeName"	TEXT NOT NULL,
	PRIMARY KEY("transactionTypeId" AUTOINCREMENT)
);
DROP TABLE IF EXISTS "Budget";
CREATE TABLE IF NOT EXISTS "Budget" (
	"budgetId"	INTEGER NOT NULL,
	"budgetGoal"	REAL NOT NULL,
	"budgetName"	TEXT NOT NULL,
	"budgetType"	INTEGER NOT NULL,
	PRIMARY KEY("budgetId" AUTOINCREMENT)
);
DROP TABLE IF EXISTS "BudgetTransaction";
CREATE TABLE IF NOT EXISTS "BudgetTransaction" (
	"budgetTransactionMonth"	INTEGER NOT NULL,
	"budgetTransactionYear"	INTEGER NOT NULL,
	"budgetTransactionAmount"	REAL NOT NULL,
	"budgetTransactionBudgetId"	INTEGER NOT NULL,
	PRIMARY KEY("budgetTransactionMonth","budgetTransactionYear","budgetTransactionBudgetId"),
	FOREIGN KEY("budgetTransactionBudgetId") REFERENCES "Budget"("budgetId") ON UPDATE NO ACTION ON DELETE CASCADE
);
DROP TABLE IF EXISTS "BudgetType";
CREATE TABLE IF NOT EXISTS "BudgetType" (
	"budgetTypeId"	INTEGER NOT NULL,
	"budgetTypeName"	TEXT NOT NULL,
	PRIMARY KEY("budgetTypeId" AUTOINCREMENT)
);
DROP TABLE IF EXISTS "BudgetDeadline";
CREATE TABLE IF NOT EXISTS "BudgetDeadline" (
	"budgetId"	INTEGER NOT NULL,
	"budgetDeadline"	TEXT,
	PRIMARY KEY("budgetId"),
	FOREIGN KEY("budgetId") REFERENCES "Budget"("budgetId") ON UPDATE NO ACTION ON DELETE CASCADE
);
DROP TABLE IF EXISTS "room_master_table";
CREATE TABLE IF NOT EXISTS "room_master_table" (
	"id"	INTEGER,
	"identity_hash"	TEXT,
	PRIMARY KEY("id")
);
INSERT INTO "android_metadata" ("locale") VALUES ('en_US');
INSERT INTO "Account" ("accountId","accountName") VALUES (1,'Cash');
INSERT INTO "TransactionType" ("transactionTypeId","transactionTypeName") VALUES (1,'Expense'),
 (2,'Income'),
 (3,'Transfer');
INSERT INTO "BudgetType" ("budgetTypeId","budgetTypeName") VALUES (1,'Monthly'),
 (2,'Yearly');
INSERT INTO "room_master_table" ("id","identity_hash") VALUES (42,'59dc03174ea70df454b5f1906ce69dcf');
DROP INDEX IF EXISTS "index_Account_accountName";
CREATE UNIQUE INDEX IF NOT EXISTS "index_Account_accountName" ON "Account" (
	"accountName"
);
DROP INDEX IF EXISTS "index_Transaction_transactionAccountId_transactionBudgetId_transactionAccountTransferTo";
CREATE INDEX IF NOT EXISTS "index_Transaction_transactionAccountId_transactionBudgetId_transactionAccountTransferTo" ON "Transaction" (
	"transactionAccountId",
	"transactionBudgetId",
	"transactionAccountTransferTo"
);
DROP INDEX IF EXISTS "index_Budget_budgetName";
CREATE UNIQUE INDEX IF NOT EXISTS "index_Budget_budgetName" ON "Budget" (
	"budgetName"
);
DROP INDEX IF EXISTS "index_BudgetType_budgetTypeName";
CREATE UNIQUE INDEX IF NOT EXISTS "index_BudgetType_budgetTypeName" ON "BudgetType" (
	"budgetTypeName"
);
COMMIT;
