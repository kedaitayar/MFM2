package io.github.kedaitayar.mfm.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4_Test = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE Account_backup (`accountId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `accountName` TEXT NOT NULL)")
        database.execSQL("INSERT INTO Account_backup (`accountId`, `accountName`) SELECT `accountId`, `accountName` FROM Account")
        database.execSQL("DROP TABLE Account")
        database.execSQL("ALTER TABLE Account_backup RENAME TO Account")

        database.execSQL("CREATE TABLE `Transaction_backup` (`transactionId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `transactionAmount` REAL NOT NULL, `transactionTime` TEXT, `transactionType` INTEGER NOT NULL, `transactionAccountId` INTEGER NOT NULL, `transactionBudgetId` INTEGER, `transactionAccountTransferTo` INTEGER, FOREIGN KEY(`transactionAccountId`) REFERENCES `Account`(`accountId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`transactionBudgetId`) REFERENCES `Budget`(`budgetId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`transactionAccountTransferTo`) REFERENCES `Account`(`accountId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        database.execSQL("INSERT INTO `Transaction_backup` (`transactionId`, `transactionAmount`, `transactionTime`, `transactionType`, `transactionAccountId`, `transactionBudgetId`, `transactionAccountTransferTo`) SELECT `transactionId`, `transactionAmount`, `transactionTime`, `transactionType`, `transactionAccountId`, `transactionBudgetId`, `transactionAccountTransferTo` FROM `Transaction`")
        database.execSQL("DROP TABLE `Transaction`")
        database.execSQL("ALTER TABLE `Transaction_backup` RENAME TO `Transaction`")

        database.execSQL("CREATE TABLE `TransactionType_backup` (`transactionTypeId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `transactionTypeName` TEXT NOT NULL)")
        database.execSQL("INSERT INTO `Transaction_backup` (`transactionTypeId`, `transactionTypeName`) SELECT `transactionTypeId`, `transactionTypeName` FROM `TransactionType`")
        database.execSQL("DROP TABLE `TransactionType`")
        database.execSQL("ALTER TABLE `Transaction_backup` RENAME TO `TransactionType`")

        database.execSQL("CREATE TABLE `Budget_backup` (`budgetId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `budgetGoal` REAL NOT NULL, `budgetName` TEXT NOT NULL, `budgetType` INTEGER NOT NULL)")
        database.execSQL("INSERT INTO `Budget_backup` (`budgetId`, `budgetGoal`, `budgetName`, `budgetType`) SELECT `budgetId`, `budgetGoal`, `budgetName`, `budgetType` FROM `Budget`")
        database.execSQL("DROP TABLE `Budget`")
        database.execSQL("ALTER TABLE `Budget_backup` RENAME TO `Budget`")

        database.execSQL("CREATE TABLE `BudgetTransaction_backup` (`budgetTransactionMonth` INTEGER NOT NULL, `budgetTransactionYear` INTEGER NOT NULL, `budgetTransactionAmount` REAL NOT NULL, `budgetTransactionBudgetId` INTEGER NOT NULL, PRIMARY KEY(`budgetTransactionMonth`, `budgetTransactionYear`, `budgetTransactionBudgetId`), FOREIGN KEY(`budgetTransactionBudgetId`) REFERENCES `Budget`(`budgetId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        database.execSQL("INSERT INTO `BudgetTransaction_backup` (`budgetTransactionMonth`, `budgetTransactionYear`, `budgetTransactionAmount`, `budgetTransactionBudgetId`) SELECT `budgetTransactionMonth`, `budgetTransactionYear`, `budgetTransactionAmount`, `budgetTransactionBudgetId` FROM `BudgetTransaction`")
        database.execSQL("DROP TABLE `BudgetTransaction`")
        database.execSQL("ALTER TABLE `BudgetTransaction_backup` RENAME TO `BudgetTransaction`")

        database.execSQL("CREATE TABLE `BudgetType_backup` (`budgetTypeId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `budgetTypeName` TEXT NOT NULL)")
        database.execSQL("INSERT INTO `BudgetType_backup` (`budgetTypeId`, `budgetTypeName`) SELECT `budgetTypeId`, `budgetTypeName` FROM `BudgetType`")
        database.execSQL("DROP TABLE `BudgetType`")
        database.execSQL("ALTER TABLE `BudgetType_backup` RENAME TO `BudgetType`")

        database.execSQL("CREATE TABLE `BudgetDeadline_backup` (`budgetId` INTEGER NOT NULL, `budgetDeadline` TEXT, PRIMARY KEY(`budgetId`), FOREIGN KEY(`budgetId`) REFERENCES `Budget`(`budgetId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        database.execSQL("INSERT INTO `BudgetDeadline_backup` (`budgetId`, `budgetDeadline`) SELECT `budgetId`, `budgetDeadline` FROM `BudgetDeadline`")
        database.execSQL("DROP TABLE `BudgetDeadline`")
        database.execSQL("ALTER TABLE `BudgetDeadline_backup` RENAME TO `BudgetDeadline`")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE Account_backup (`accountId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `accountName` TEXT NOT NULL)")
        database.execSQL("INSERT INTO Account_backup (`accountId`, `accountName`) SELECT `accountId`, `accountName` FROM Account")
        database.execSQL("DROP TABLE Account")
        database.execSQL("ALTER TABLE Account_backup RENAME TO Account")
        database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Account_accountName` ON `Account` (`accountName`)")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE `Transaction_backup` (`transactionId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `transactionAmount` REAL NOT NULL, `transactionTime` TEXT, `transactionType` INTEGER NOT NULL, `transactionAccountId` INTEGER NOT NULL, `transactionBudgetId` INTEGER, `transactionAccountTransferTo` INTEGER, FOREIGN KEY(`transactionAccountId`) REFERENCES `Account`(`accountId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`transactionBudgetId`) REFERENCES `Budget`(`budgetId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`transactionAccountTransferTo`) REFERENCES `Account`(`accountId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        database.execSQL("INSERT INTO `Transaction_backup` (`transactionId`, `transactionAmount`, `transactionTime`, `transactionType`, `transactionAccountId`, `transactionBudgetId`, `transactionAccountTransferTo`) SELECT `transactionId`, `transactionAmount`, `transactionTime`, `transactionType`, `transactionAccountId`, `transactionBudgetId`, `transactionAccountTransferTo` FROM `Transaction`")
        database.execSQL("DROP TABLE `Transaction`")
        database.execSQL("ALTER TABLE `Transaction_backup` RENAME TO `Transaction`")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_Transaction_transactionAccountId_transactionBudgetId_transactionAccountTransferTo` ON `Transaction` (`transactionAccountId`,`transactionBudgetId`,`transactionAccountTransferTo`)")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE `TransactionType_backup` (`transactionTypeId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `transactionTypeName` TEXT NOT NULL)")
        database.execSQL("INSERT INTO `TransactionType_backup` (`transactionTypeId`, `transactionTypeName`) SELECT `transactionTypeId`, `transactionTypeName` FROM `TransactionType`")
        database.execSQL("DROP TABLE `TransactionType`")
        database.execSQL("ALTER TABLE `TransactionType_backup` RENAME TO `TransactionType`")
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE `Budget_backup` (`budgetId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `budgetGoal` REAL NOT NULL, `budgetName` TEXT NOT NULL, `budgetType` INTEGER NOT NULL)")
        database.execSQL("INSERT INTO `Budget_backup` (`budgetId`, `budgetGoal`, `budgetName`, `budgetType`) SELECT `budgetId`, `budgetGoal`, `budgetName`, `budgetType` FROM `Budget`")
        database.execSQL("DROP TABLE `Budget`")
        database.execSQL("ALTER TABLE `Budget_backup` RENAME TO `Budget`")
        database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Budget_budgetName` ON `Budget` (`budgetName`)")

        database.execSQL("CREATE TABLE `BudgetTransaction_backup` (`budgetTransactionMonth` INTEGER NOT NULL, `budgetTransactionYear` INTEGER NOT NULL, `budgetTransactionAmount` REAL NOT NULL, `budgetTransactionBudgetId` INTEGER NOT NULL, PRIMARY KEY(`budgetTransactionMonth`, `budgetTransactionYear`, `budgetTransactionBudgetId`), FOREIGN KEY(`budgetTransactionBudgetId`) REFERENCES `Budget`(`budgetId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        database.execSQL("INSERT INTO `BudgetTransaction_backup` (`budgetTransactionMonth`, `budgetTransactionYear`, `budgetTransactionAmount`, `budgetTransactionBudgetId`) SELECT `budgetTransactionMonth`, `budgetTransactionYear`, `budgetTransactionAmount`, `budgetTransactionBudgetId` FROM `BudgetTransaction`")
        database.execSQL("DROP TABLE `BudgetTransaction`")
        database.execSQL("ALTER TABLE `BudgetTransaction_backup` RENAME TO `BudgetTransaction`")

        database.execSQL("CREATE TABLE `BudgetType_backup` (`budgetTypeId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `budgetTypeName` TEXT NOT NULL)")
        database.execSQL("INSERT INTO `BudgetType_backup` (`budgetTypeId`, `budgetTypeName`) SELECT `budgetTypeId`, `budgetTypeName` FROM `BudgetType`")
        database.execSQL("DROP TABLE `BudgetType`")
        database.execSQL("ALTER TABLE `BudgetType_backup` RENAME TO `BudgetType`")
        database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_BudgetType_budgetTypeName` ON `BudgetType` (`budgetTypeName`)")

        database.execSQL("CREATE TABLE `BudgetDeadline_backup` (`budgetId` INTEGER NOT NULL, `budgetDeadline` TEXT, PRIMARY KEY(`budgetId`), FOREIGN KEY(`budgetId`) REFERENCES `Budget`(`budgetId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        database.execSQL("INSERT INTO `BudgetDeadline_backup` (`budgetId`, `budgetDeadline`) SELECT `budgetId`, `budgetDeadline` FROM `BudgetDeadline`")
        database.execSQL("DROP TABLE `BudgetDeadline`")
        database.execSQL("ALTER TABLE `BudgetDeadline_backup` RENAME TO `BudgetDeadline`")
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `Transaction` ADD COLUMN `transactionNote` TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE `Budget_backup` (`budgetId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `budgetGoal` REAL NOT NULL, `budgetName` TEXT NOT NULL, `budgetType` INTEGER NOT NULL, `budgetPosition` INTEGER NOT NULL)")
        database.execSQL("INSERT INTO `Budget_backup` (`budgetId`, `budgetGoal`, `budgetName`, `budgetType`, `budgetPosition`) SELECT `budgetId`, `budgetGoal`, `budgetName`, `budgetType`, `budgetId` as `budgetPosition` FROM `Budget`")
        database.execSQL("DROP TABLE `Budget`")
        database.execSQL("ALTER TABLE `Budget_backup` RENAME TO `Budget`")
        database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Budget_budgetName` ON `Budget` (`budgetName`)")
    }
}

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `QuickTransaction` (`quickTransactionId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `quickTransactionName` TEXT NOT NULL, `transactionAmount` REAL NOT NULL, `transactionType` INTEGER NOT NULL, `transactionAccountId` INTEGER NOT NULL, `transactionBudgetId` INTEGER, `transactionAccountTransferTo` INTEGER, FOREIGN KEY(`transactionAccountId`) REFERENCES `Account`(`accountId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`transactionBudgetId`) REFERENCES `Budget`(`budgetId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`transactionAccountTransferTo`) REFERENCES `Account`(`accountId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
    }
}

val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `QuickTransaction` ADD COLUMN `transactionNote` TEXT NOT NULL DEFAULT ''")
    }

}