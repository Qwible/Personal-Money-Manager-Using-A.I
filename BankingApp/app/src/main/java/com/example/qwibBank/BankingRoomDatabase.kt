package com.example.qwibBank

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.qwibBank.Entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.util.*


// Annotates class to be a Room Database with a table (entity) of the Transaction class
@Database(entities = arrayOf(Account::class, Transaction::class, SavingGoal::class, Income::class, Bill::class, User::class, Category::class, AccessToken::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
public abstract class BankingRoomDatabase : RoomDatabase() {

    abstract fun bankingDao(): BankingDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: BankingRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): BankingRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            //Use SQL Cipher to encrypt database
            val passphrase = charArrayOf('A', 'B', 'C')
            val pass: ByteArray = SQLiteDatabase.getBytes(passphrase)
            val factory = SupportFactory(pass)
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BankingRoomDatabase::class.java,
                    "transaction_database"
                )
                    .addCallback(TransactionDatabaseCallback(scope))
                    .openHelperFactory(factory)
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
    private class TransactionDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.bankingDao())
                }
            }
        }

        suspend fun populateDatabase(bankingDao: BankingDao) {
            // Delete all content here.
            //bankingDao.deleteAllUsers()
            //bankingDao.deleteAllAccounts()
            //bankingDao.deleteAllSavings()
            //bankingDao.deleteAllBills()
            //bankingDao.deleteAllIncome()
            //bankingDao.deleteAllCategories()

            //Default entries
            var category = Category(1,"Other",0, R.drawable.ic_attach_money_black_24dp.toString())
            bankingDao.insertCategory(category)

            category = Category(2,"Uncategorised",0, R.drawable.ic_notifications_black_24dp.toString())
            bankingDao.insertCategory(category)

            category = Category(3,"Income",0, R.drawable.ic_add_black_24dp.toString())
            bankingDao.insertCategory(category)

            //Add sample user
            var user = User(1, 0, "Monthly")
            bankingDao.insertIncome(user)

            var current = Date().time
            // Add sample saving goals.
            var saving = SavingGoal(
                1,
                "Saved",
                200,
                Date(current)
            )
            bankingDao.insertSaving(saving)

            /*
            //Add sample accounts
            val logo = R.drawable.ic_home_black_24dp.toString()
            var account1 = Account(1, "cash", 200.0, logo)
            bankingDao.insertAccount(account1)
            var account2 = Account(null, "saver", 12.0, logo)
            bankingDao.insertAccount(account2)
            var account3 = Account(null, "student", -25.0, logo)
            bankingDao.insertAccount(account3)

            //sample categories
            category = Category(null,"Income", "user1", 0, R.drawable.ic_attach_money_black_24dp.toString())
            bankingDao.insertCategory(category)

            //sample categories
            category = Category(null, "Entertainment", "user1", 0, R.drawable.ic_attach_money_black_24dp.toString())
            bankingDao.insertCategory(category)

            //sample categories
            category = Category(null, "Groceries", "user1", 0, R.drawable.ic_add_black_24dp.toString())
            bankingDao.insertCategory(category)

            // Add sample transactions.
            var transaction =
                Transaction(null, "THE CLIMBING WORKS", -20.0, "cash",Date(current - TimeUnit.DAYS.toMillis(40)), "Entertainment")
            bankingDao.insertTransaction(transaction)
            bankingDao.updateBalance(transaction.amount, transaction.accountName)
            // Add sample transactions.
            transaction =
                Transaction(null, "THE FOUNDRY", -50.0, "cash", Date(current - TimeUnit.DAYS.toMillis(40)),"Entertainment")
            bankingDao.insertTransaction(transaction)
            bankingDao.updateBalance(transaction.amount, transaction.accountName)

            saving = SavingGoal(
                null,
                "Pembroke",
                200,
                Date(current + TimeUnit.DAYS.toMillis(40))
            )
            bankingDao.insertSaving(saving)

            // Add sample income.
            var income = Income(
                1, "Student Finance", 1800,
                arrayListOf(Date(current + TimeUnit.DAYS.toMillis(20)))
            )
            bankingDao.insertIncome(income)
            income = Income(
                null, "Student Finance", 1800,
                arrayListOf(Date(current + TimeUnit.DAYS.toMillis(20)))
            )
            bankingDao.insertIncome(income)
            income = Income(
                null, "Student Finance", 1800,
                arrayListOf(Date(current + TimeUnit.DAYS.toMillis(20)))
            )
            bankingDao.insertIncome(income)
            income = Income(
                null, "Student Finance", 1800,
                arrayListOf(Date(current + TimeUnit.DAYS.toMillis(20)))
            )
            bankingDao.insertIncome(income)
            income = Income(
                null, "Food Allowance", 400,
                arrayListOf(
                    Date(current + TimeUnit.DAYS.toMillis(10)),
                    Date(current + TimeUnit.DAYS.toMillis(40)),
                    Date(current + TimeUnit.DAYS.toMillis(70)),
                    Date(current + TimeUnit.DAYS.toMillis(100)),
                    Date(current + TimeUnit.DAYS.toMillis(130)),
                    Date(current + TimeUnit.DAYS.toMillis(160))
                )
            )
            bankingDao.insertIncome(income)


            // Add sample bills.
            var bill = Bill(
                1, "MBA Lettings", 320,
                arrayListOf(
                    Date(current + TimeUnit.DAYS.toMillis(10)),
                    Date(current + TimeUnit.DAYS.toMillis(40)),
                    Date(current + TimeUnit.DAYS.toMillis(70)),
                    Date(current + TimeUnit.DAYS.toMillis(100)),
                    Date(current + TimeUnit.DAYS.toMillis(130)),
                    Date(current + TimeUnit.DAYS.toMillis(160))
                )
            )
            bankingDao.insertBill(bill)

             */


        }
    }
}