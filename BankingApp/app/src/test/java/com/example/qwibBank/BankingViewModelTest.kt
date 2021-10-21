package com.example.qwibBank

import android.app.Application
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.qwibBank.Entities.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class BankingViewModelTest {


    @MockK
    private lateinit var repository: BankingRepository

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var bankingViewModel: BankingViewModel

    private val currentDate  = Calendar.getInstance()
    private val prevDate = Calendar.getInstance()
    private val lastDayofMonth = currentDate.getActualMaximum(Calendar.DATE)
    private val firstDayofMonth = currentDate.getActualMinimum(Calendar.DATE)



    private val accounts = MutableLiveData<List<Account>>()
    private val user = MutableLiveData(User(1, 200, "Weekly"))
    private val categories = MutableLiveData(listOf(Category(1, "category", 0, "logo")))
    private val transactions = MutableLiveData<List<Transaction>>()
    private val savings = MutableLiveData<List<SavingGoal>>()
    private val income = MutableLiveData<List<Income>>()
    private val bills = MutableLiveData<List<Bill>>()


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        val application = Mockito.mock(Application::class.java)

        currentDate.set(Calendar.DATE, lastDayofMonth)
        prevDate.set(Calendar.DATE, firstDayofMonth)

        every { repository.getCurrentDay() } returns currentDate
        every { repository.allAccounts } returns accounts
        every { repository.allTransactions } returns transactions
        every { repository.allCategories } returns categories
        every { repository.allSavings } returns savings
        every { repository.allIncome } returns income
        every { repository.allBills } returns bills
        every { repository.allTokens} returns MutableLiveData()
        every { repository.getUser(1)} returns user
        every { repository.fetchCategoryTransactions("Uncategorised")} returns MutableLiveData()

        bankingViewModel = BankingViewModel(application, repository)

    }

    @Test
    fun `updating repository updates LiveData attribute`() {

        assertEquals(bankingViewModel.allAccounts.waitForValue(), accounts.value)

        accounts.value = listOf(Account(1, "account1", 20.0, "logo", "obid"))

        assertEquals(bankingViewModel.allAccounts.waitForValue(), accounts.value)
    }

    @Test
    fun `updating user or category updates toAllocate amount correctly`() {

        assertEquals(bankingViewModel.toAllocate.waitForValue(), 200)

        user.value = User(1, 100, "Weekly")

        assertEquals(bankingViewModel.toAllocate.waitForValue(), 100)

        categories.value = listOf(Category(1, "category", 20, "logo"))

        assertEquals(bankingViewModel.toAllocate.waitForValue(), 80)
    }

    @Test
    fun `test that period transactions updates correctly`() {

        assertTrue(bankingViewModel.periodTransactions.waitForValue().second.isNullOrEmpty())
        assertEquals(bankingViewModel.periodTransactions.waitForValue().first, 0.0)

        //Transactions contains one item from the start of the month and one from the end. The current day is set as the last of the month - so 'Weekly' period should return 1 item, 'Monthly' should return 2.
        transactions.value = listOf(Transaction(1, "merchant", -10.0, "account", currentDate.time, "category"),
            Transaction(2, "merchant", -10.0, "account", prevDate.time, "category") )


        //Value has changed after transactions added, Period is weekly therefore size should be 1
        assertEquals(bankingViewModel.periodTransactions.waitForValue().second.size, 1)
        assertEquals(bankingViewModel.periodTransactions.waitForValue().first,10.0)

        user.value = User(1, 100, "Monthly")

        //Value has updated after period is changed, Period is now monthlyy therefore size should be 2
        assertEquals(bankingViewModel.periodTransactions.waitForValue().second.size, 2)
        assertEquals(bankingViewModel.periodTransactions.waitForValue().first, 20.0)
    }

    @Test
    fun `test that categoriesWithSpend updates correctly`() {

        assertEquals(bankingViewModel.categoriesWithSpend.waitForValue().size, 1)

        categories.value = listOf(Category(1, "category", 20, "logo"), Category(2, "category2", 20, "logo"))

        //Value updates if categories are added
        assertEquals(bankingViewModel.categoriesWithSpend.waitForValue().size, 2)

        //Transactions contains one item from the start of the month and one from the end. The current day is set as the last of the month - so 'Weekly' period should return 1 item, 'Monthly' should return 2.
        transactions.value = listOf(Transaction(1, "merchant", -10.0, "account", currentDate.time, "category"),
            Transaction(2, "merchant", -10.0, "account", prevDate.time, "category") )


        //Spend value has changed after transactions added, Period is weekly therefore amount should be 10
        assertEquals(bankingViewModel.categoriesWithSpend.waitForValue()[0].amount,10.0)

        user.value = User(1, 100, "Monthly")

        //Value has updated after period is changed, Period is now monthly therefore amount should be 20
        assertEquals(bankingViewModel.periodTransactions.waitForValue().first, 20.0)
    }


    @Test
    fun `test that overSpend updates correctly and returns correct values`() {
        user.value = User(1, 0, "Weekly")

        //0 allowance returns nobudget
        assertEquals(bankingViewModel.overBudget.waitForValue(), "nobudget")

        user.value = User(1, 20, "Weekly")

        //Value updates if user is changed
        assertEquals(bankingViewModel.overBudget.waitForValue(), "false")

        //A new transaction should mean that period spend > allowance
        transactions.value = listOf(Transaction(1, "merchant", -10.0, "account", currentDate.time, "category"),
            Transaction(2, "merchant", -20.0, "account", currentDate.time, "category"), Transaction(3, "merchant", -10.0, "account", prevDate.time, "category") )

        //Value updates if transactions are changed
        assertEquals(bankingViewModel.overBudget.waitForValue(), "true")

        //Allowance increased
        user.value = User(1, 31, "Weekly")

        //Value updates if user is changed
        assertEquals(bankingViewModel.overBudget.waitForValue(), "false")

        //Period is changed
        user.value = User(1, 31, "Monthly")

        //Value has updated after period is changed, Period is now monthly therefore spend should be over budget again
        assertEquals(bankingViewModel.periodTransactions.waitForValue().first, 40.0)
        assertEquals(bankingViewModel.user.waitForValue().allowance, 31)
        assertEquals(bankingViewModel.overBudget.waitForValue(), "true")
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