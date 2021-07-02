BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "BudgetDeadline" (
	"budgetId"	INTEGER,
	"budgetDeadline"	TEXT,
	PRIMARY KEY("budgetId")
);
CREATE TABLE IF NOT EXISTS "BudgetType" (
	"budgetTypeId"	INTEGER PRIMARY KEY AUTOINCREMENT,
	"budgetTypeName"	TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS "BudgetTransaction" (
	"budgetTransactionMonth"	INTEGER NOT NULL,
	"budgetTransactionYear"	INTEGER NOT NULL,
	"budgetTransactionAmount"	REAL NOT NULL,
	"budgetTransactionBudgetId"	INTEGER NOT NULL,
	PRIMARY KEY("budgetTransactionMonth","budgetTransactionYear","budgetTransactionBudgetId"),
	FOREIGN KEY("budgetTransactionBudgetId") REFERENCES "Budget"("budgetId") ON UPDATE NO ACTION ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS "Budget" (
	"budgetId"	INTEGER PRIMARY KEY AUTOINCREMENT,
	"budgetGoal"	REAL NOT NULL,
	"budgetName"	TEXT NOT NULL,
	"budgetType"	INTEGER NOT NULL
);
CREATE TABLE IF NOT EXISTS "TransactionType" (
	"transactionTypeId"	INTEGER PRIMARY KEY AUTOINCREMENT,
	"transactionTypeName"	TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS "Transaction" (
	"transactionId"	INTEGER PRIMARY KEY AUTOINCREMENT,
	"transactionAmount"	REAL NOT NULL,
	"transactionTime"	TEXT,
	"transactionType"	INTEGER NOT NULL,
	"transactionAccountId"	INTEGER NOT NULL,
	"transactionBudgetId"	INTEGER,
	"transactionAccountTransferTo"	INTEGER,
	FOREIGN KEY("transactionAccountId") REFERENCES "Account"("accountId") ON UPDATE NO ACTION ON DELETE CASCADE,
	FOREIGN KEY("transactionBudgetId") REFERENCES "Budget"("budgetId") ON UPDATE NO ACTION ON DELETE CASCADE,
	FOREIGN KEY("transactionAccountTransferTo") REFERENCES "Account"("accountId") ON UPDATE NO ACTION ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS "Account" (
	"accountId"	INTEGER PRIMARY KEY AUTOINCREMENT,
	"accountName"	TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS "android_metadata" (
	"locale"	TEXT
);
CREATE TABLE IF NOT EXISTS "room_master_table" (
	"id"	INTEGER,
	"identity_hash"	TEXT,
	PRIMARY KEY("id")
);
INSERT INTO "BudgetType" ("budgetTypeId","budgetTypeName") VALUES (1,'Monthly');
INSERT INTO "BudgetType" ("budgetTypeId","budgetTypeName") VALUES (2,'Yearly');
INSERT INTO "BudgetType" ("budgetTypeId","budgetTypeName") VALUES (3,'Goal/Debt');
INSERT INTO "TransactionType" ("transactionTypeId","transactionTypeName") VALUES (1,'Expense');
INSERT INTO "TransactionType" ("transactionTypeId","transactionTypeName") VALUES (2,'Income');
INSERT INTO "TransactionType" ("transactionTypeId","transactionTypeName") VALUES (3,'Transfer');
INSERT INTO "android_metadata" ("locale") VALUES ('en_US');
INSERT INTO "room_master_table" ("id","identity_hash") VALUES (42,'c247629707e0f5e796c00fe667a00509');
CREATE UNIQUE INDEX IF NOT EXISTS "index_BudgetType_budgetTypeName" ON "BudgetType" (
	"budgetTypeName"
);
CREATE UNIQUE INDEX IF NOT EXISTS "index_Budget_budgetName" ON "Budget" (
	"budgetName"
);
CREATE INDEX IF NOT EXISTS "index_Transaction_transactionAccountId_transactionBudgetId_transactionAccountTransferTo" ON "Transaction" (
	"transactionAccountId",
	"transactionBudgetId",
	"transactionAccountTransferTo"
);
CREATE UNIQUE INDEX IF NOT EXISTS "index_Account_accountName" ON "Account" (
	"accountName"
);
COMMIT;
