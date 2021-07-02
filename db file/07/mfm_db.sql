BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "android_metadata" (
	"locale"	TEXT
);
CREATE TABLE IF NOT EXISTS "Account" (
	"accountId"	INTEGER NOT NULL,
	"accountName"	TEXT NOT NULL,
	PRIMARY KEY("accountId" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "Transaction" (
	"transactionId"	INTEGER NOT NULL,
	"transactionAmount"	REAL NOT NULL,
	"transactionTime"	TEXT,
	"transactionType"	INTEGER NOT NULL,
	"transactionAccountId"	INTEGER NOT NULL,
	"transactionBudgetId"	INTEGER,
	"transactionAccountTransferTo"	INTEGER,
	PRIMARY KEY("transactionId" AUTOINCREMENT),
	FOREIGN KEY("transactionAccountTransferTo") REFERENCES "Account"("accountId") ON UPDATE NO ACTION ON DELETE CASCADE,
	FOREIGN KEY("transactionAccountId") REFERENCES "Account"("accountId") ON UPDATE NO ACTION ON DELETE CASCADE,
	FOREIGN KEY("transactionBudgetId") REFERENCES "Budget"("budgetId") ON UPDATE NO ACTION ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS "TransactionType" (
	"transactionTypeId"	INTEGER NOT NULL,
	"transactionTypeName"	TEXT NOT NULL,
	PRIMARY KEY("transactionTypeId" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "Budget" (
	"budgetId"	INTEGER NOT NULL,
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
	"budgetTypeId"	INTEGER NOT NULL,
	"budgetTypeName"	TEXT NOT NULL,
	PRIMARY KEY("budgetTypeId" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "BudgetDeadline" (
	"budgetId"	INTEGER NOT NULL,
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
INSERT INTO "room_master_table" ("id","identity_hash") VALUES (42,'4568c62bea7d2ab18b659cf4fa6f604c');
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
