import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.qwibBank.BankingDao
import com.example.qwibBank.BankingRoomDatabase
import com.example.qwibBank.Entities.*
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


//Testing the doa and database behaviour

@RunWith(AndroidJUnit4::class)
class BankingDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var bankingDao: BankingDao
    private lateinit var db: BankingRoomDatabase
    private lateinit var currentTime:Date

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, BankingRoomDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        bankingDao = db.bankingDao()
    }

    @Before
    fun initDate() {
        currentTime = Calendar.getInstance().time
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetTransaction() = runBlocking {

        val mockAccount = Account(1, "account", 20.0, "logo")
        bankingDao.insertAccount(mockAccount)

        val mockCategory = Category(1, "category", 0, "logo")
        bankingDao.insertCategory(mockCategory)

        val transaction = Transaction(1, "merchant",20.0, "account", currentTime, "category")
        bankingDao.insertTransaction(transaction)

        val allTransactions = bankingDao.orderedTransactions().waitForValue()
        assertEquals(allTransactions[0].id, transaction.id)
        assertEquals(allTransactions[0].merchant, transaction.merchant)
        assertEquals(allTransactions[0].amount, transaction.amount)
        assertEquals(allTransactions[0].accountName, transaction.accountName)
        assertEquals(allTransactions[0].timestamp, transaction.timestamp)
        assertEquals(allTransactions[0].category, transaction.category)
    }


    @Test
    @Throws(Exception::class)
    fun insertAndGetAccount() = runBlocking {
        val mockAccount = Account(1, "cash", 20.0, "logo")
        bankingDao.insertAccount(mockAccount)

        val allAccounts = bankingDao.orderedAccounts().waitForValue()
        assertEquals(allAccounts[0].id, mockAccount.id)
        assertEquals(allAccounts[0].name, mockAccount.name)
        assertEquals(allAccounts[0].balance, mockAccount.balance)
        assertEquals(allAccounts[0].logo, mockAccount.logo)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetCategory() = runBlocking {
        val mockCategory = Category(1, "category", 0, "logo")
        bankingDao.insertCategory(mockCategory)

        val allCategories = bankingDao.orderedCategories().waitForValue()
        assertEquals(allCategories[0].id, mockCategory.id)
        assertEquals(allCategories[0].name, mockCategory.name)
        assertEquals(allCategories[0].allocated, mockCategory.allocated)
        assertEquals(allCategories[0].logo, mockCategory.logo)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetUser() = runBlocking {
        val mockUser = User(1, 20, "Weekly")
        bankingDao.insertIncome(mockUser)

        val user = bankingDao.getUser(1).waitForValue()
        assertEquals(user.id, mockUser.id)
        assertEquals(user.allowance, mockUser.allowance)
        assertEquals(user.period, mockUser.period)
    }

    @Throws(Exception::class)
    fun insertAndGetIncome() = runBlocking {
        val timeList = arrayListOf<Date>(currentTime, currentTime)
        val mockIncome = Income(1, "Source", 20, timeList)
        bankingDao.insertIncome(mockIncome)

        val allIncome = bankingDao.orderedIncome().waitForValue()
        assertEquals(allIncome[0].id, mockIncome.id)
        assertEquals(allIncome[0].amount, mockIncome.amount)
        assertEquals(allIncome[0].repeat, mockIncome.repeat)
        assertEquals(allIncome[0].source, mockIncome.source)
    }

    @Throws(Exception::class)
    fun insertAndGetSavingGoal() = runBlocking {
        val mockSaving = SavingGoal(1, "savingGoal", 20, currentTime)
        bankingDao.insertSaving(mockSaving)

        val allSaving = bankingDao.orderedSavings().waitForValue()
        assertEquals(allSaving[0].id, mockSaving.id)
        assertEquals(allSaving[0].amount, mockSaving.amount)
        assertEquals(allSaving[0].date, mockSaving.date)
        assertEquals(allSaving[0].name, mockSaving.name)
    }


    @Throws(Exception::class)
    fun insertAndGetBill() = runBlocking {
        val timeList = arrayListOf<Date>(currentTime, currentTime)
        val mockBill = Bill(1, "Bill", 20, timeList)
        bankingDao.insertBill(mockBill)

        val allBills = bankingDao.orderedBills().waitForValue()
        assertEquals(allBills[0].id, mockBill.id)
        assertEquals(allBills[0].amount, mockBill.amount)
        assertEquals(allBills[0].repeat, mockBill.repeat)
        assertEquals(allBills[0].recipient, mockBill.recipient)
    }

    @Throws(Exception::class)
    fun insertAndGetAccessToken() = runBlocking {
        val mockToken = AccessToken("token1", "token2", "type", 2)
        bankingDao.insertToken(mockToken)

        val allTokens = bankingDao.allTokens().waitForValue()
        assertEquals(allTokens[0].token_type, mockToken.token_type)
        assertEquals(allTokens[0].access_token, mockToken.access_token)
        assertEquals(allTokens[0].expires_in, mockToken.expires_in)
        assertEquals(allTokens[0].refresh_token, mockToken.refresh_token)
    }



    @Test
    @Throws(Exception::class)
    fun getAll() = runBlocking {
        val mockCategory1 = Category(1, "category",0, "logo")
        bankingDao.insertCategory(mockCategory1)

        val mockCategory2 = Category(2, "other", 0, "logo")
        bankingDao.insertCategory(mockCategory2)

        val allCategories = bankingDao.orderedCategories().waitForValue()
        assertEquals(allCategories[0].id, mockCategory1.id)
        assertEquals(allCategories[1].id, mockCategory2.id)
    }

    @Test
    @Throws(Exception::class)
    fun updateOne() = runBlocking {
        val mockCategory1 = Category(1, "category", 0, "logo")
        bankingDao.insertCategory(mockCategory1)

        val mockCategoryUpdate = Category(1, "other", 0, "logo")
        bankingDao.updateCategory(mockCategoryUpdate)

        val allCategories = bankingDao.orderedCategories().waitForValue()
        assertTrue(allCategories.size == 1)
        assertTrue(allCategories[0].name == "other")
    }

    @Test
    @Throws(Exception::class)
    fun deleteOne() = runBlocking {
        val mockCategory1 = Category(1, "category", 0, "logo")
        bankingDao.insertCategory(mockCategory1)

        val mockCategory2 = Category(2, "other", 0, "logo")
        bankingDao.insertCategory(mockCategory2)

        bankingDao.deleteCategory(mockCategory1)

        val allCategories = bankingDao.orderedCategories().waitForValue()
        assertTrue(allCategories.size == 1)
        assertTrue(allCategories[0].id == mockCategory2.id)
    }

    @Test
    @Throws(Exception::class)
    fun deleteAll() = runBlocking {
        val mockCategory1 = Category(1, "category", 0, "logo")
        bankingDao.insertCategory(mockCategory1)

        val mockCategory2 = Category(2, "category", 0, "logo")
        bankingDao.insertCategory(mockCategory2)

        bankingDao.deleteAllCategories()

        val allCategories = bankingDao.orderedCategories().waitForValue()
        assertTrue(allCategories.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun insertAllAccounts() = runBlocking {
        val mockAccount1 = Account(1, "cash", 20.0, "logo")
        val mockAccount2 = Account(2, "saver", 20.0, "logo")
        val accountsList = arrayListOf<Account>(mockAccount1, mockAccount2)

        bankingDao.insertAllAccounts(accountsList)

        val allAccounts = bankingDao.orderedAccounts().waitForValue()
        assertTrue(allAccounts.size == 2)
        assertEquals(allAccounts[0].id, mockAccount1.id)
        assertEquals(allAccounts[1].id, mockAccount2.id)
    }


    @Test
    @Throws(Exception::class)
    fun fetchAccountTransactions() = runBlocking {
        val mockAccount1 = Account(1, "account", 20.0, "logo")
        bankingDao.insertAccount(mockAccount1)

        val mockCategory = Category(1, "category", 0, "logo")
        bankingDao.insertCategory(mockCategory)

        val transaction1 = Transaction(1, "merchant",20.0, "account", currentTime, "category")
        bankingDao.insertTransaction(transaction1)
        val transaction2 = Transaction(2, "merchant",20.0, "account", currentTime, "category")
        bankingDao.insertTransaction(transaction2)

        val allTransactions = bankingDao.fetchAccountTransactions("account").waitForValue()

        assertTrue(allTransactions.size == 2)
        assertEquals(allTransactions[0].id, transaction1.id)
        assertEquals(allTransactions[1].id, transaction2.id)
    }


    @Test
    @Throws(Exception::class)
    fun fetchCategoryTransactions() = runBlocking {
        val mockAccount1 = Account(1, "account", 20.0, "logo")
        bankingDao.insertAccount(mockAccount1)

        val mockCategory = Category(1, "category", 0, "logo")
        bankingDao.insertCategory(mockCategory)

        val transaction1 = Transaction(1, "merchant",20.0, "account", currentTime, "category")
        bankingDao.insertTransaction(transaction1)
        val transaction2 = Transaction(2, "merchant",20.0, "account", currentTime, "category")
        bankingDao.insertTransaction(transaction2)

        val allTransactions = bankingDao.fetchCategoryTransactions(("category")).waitForValue()

        assertTrue(allTransactions.size == 2)
        assertEquals(allTransactions[0].id, transaction1.id)
        assertEquals(allTransactions[1].id, transaction2.id)
    }

    @Test
    @Throws(Exception::class)
    fun updateBalance() = runBlocking {
        val mockAccount1 = Account(1, "account", 20.0, "logo")
        bankingDao.insertAccount(mockAccount1)

        bankingDao.updateBalance(2.0, "account")

        var allAccounts = bankingDao.orderedAccounts().waitForValue()
        assertEquals(allAccounts[0].balance , 22.0)

        bankingDao.updateBalance(-4.0, "account")

        allAccounts = bankingDao.orderedAccounts().waitForValue()
        assertEquals(allAccounts[0].balance , 18.0)

    }


    @Test
    @Throws(Exception::class)
    fun testIgnore() = runBlocking {
        val mockAccount1 = Account(1, "account", 20.0, "logo")
        bankingDao.insertAccount(mockAccount1)

        val mockAccount2 = Account(1, "cash", 20.0, "logo")
        bankingDao.insertAccount(mockAccount2)

        val allAccounts = bankingDao.orderedAccounts().waitForValue()
        assertTrue(allAccounts.size == 1)

    }

    @Test
    @Throws(Exception::class)
    fun testUnique() = runBlocking {
        val mockAccount1 = Account(1, "account", 20.0, "logo")
        bankingDao.insertAccount(mockAccount1)

        val mockAccount2 = Account(2, "account", 20.0, "logo")
        bankingDao.insertAccount(mockAccount2)

        val allAccounts = bankingDao.orderedAccounts().waitForValue()
        assertTrue(allAccounts.size == 1)
    }


    @Test
    @Throws(Exception::class)
    fun testCascadeOnDelete() = runBlocking {
            val mockAccount1 = Account(1, "account", 20.0, "logo")
            bankingDao.insertAccount(mockAccount1)

            val mockCategory = Category(1, "category", 0, "logo")
            bankingDao.insertCategory(mockCategory)

            val transaction1 = Transaction(1, "merchant",20.0, "account", currentTime, "category")
            bankingDao.insertTransaction(transaction1)
            val transaction2 = Transaction(2, "merchant",20.0, "account", currentTime, "category")
            bankingDao.insertTransaction(transaction2)

            bankingDao.deleteAccount(mockAccount1)
            val allTransactions = bankingDao.orderedTransactions().waitForValue()

            assertTrue(allTransactions.size == 0)
        }

    @Test
    @Throws(Exception::class)
    fun testCascadeOnUpdate() = runBlocking {
        val mockAccount1 = Account(1, "account", 20.0, "logo")
        bankingDao.insertAccount(mockAccount1)

        val mockCategory = Category(1, "category", 0, "logo")
        bankingDao.insertCategory(mockCategory)

        val transaction1 = Transaction(1, "merchant",20.0, "account", currentTime, "category")
        bankingDao.insertTransaction(transaction1)
        val transaction2 = Transaction(2, "merchant",20.0, "account", currentTime, "category")
        bankingDao.insertTransaction(transaction2)

        bankingDao.updateCategory(Category(1, "new", 0, "logo"))
        val allTransactions = bankingDao.orderedTransactions().waitForValue()

        assertEquals(allTransactions[0].category, "new")
        assertEquals(allTransactions[1].category, "new")
    }
}

//Util for testing LiveData objects
@Throws(InterruptedException::class)
fun <T> LiveData<T>.waitForValue(): T {
    val data = arrayOfNulls<Any>(1)
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data[0] = o
            latch.countDown()
            this@waitForValue.removeObserver(this)
        }
    }
    this.observeForever(observer)
    latch.await(2, TimeUnit.SECONDS)

    return data[0] as T
}
