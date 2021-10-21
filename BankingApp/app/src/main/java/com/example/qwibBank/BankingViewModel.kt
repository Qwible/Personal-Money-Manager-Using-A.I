package com.example.qwibBank

import android.app.Application
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.qwibBank.Entities.*
import kotlinx.android.synthetic.main.fragment_allocate.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.math.sign


// Class extends AndroidViewModel and requires application as a parameter.
class BankingViewModel : AndroidViewModel {

    // The ViewModel maintains a reference to the repository to get data.
    private var repository: BankingRepository
    private var app: Application

    // LiveData gives us updated transactions when they change.
    val allTransactions: LiveData<List<Transaction>>
    val allAccounts: LiveData<List<Account>>
    val allSavings: LiveData<List<SavingGoal>>
    val allIncome: LiveData<List<Income>>
    val allBills: LiveData<List<Bill>>
    val allCategories: LiveData<List<Category>>
    val allTokens: LiveData<List<AccessToken>>
    val periodTransactions: LiveData<Pair<Double, List<Transaction>>>
    val user: LiveData<User>
    val overBudget : LiveData<String>
    val categoriesWithSpend : LiveData<List<Category>>
    val toAllocate : LiveData<Int>
    val uncategorisedTransactions : LiveData<List<Transaction>>
    val prediction : LiveData<Boolean>
    


    constructor(application: Application) : super(application) {
        // Gets reference to BankingDao from BankingRoomDatabase to construct
        // the correct BankingRepository.
        this.app = application


        val bankingDao = BankingRoomDatabase.getDatabase(
            application,
            GlobalScope
        ).bankingDao()
        repository = BankingRepository(bankingDao)



        //get variables from repositiory
        allTransactions = repository.allTransactions
        allAccounts = repository.allAccounts
        allSavings = repository.allSavings
        allIncome = repository.allIncome
        allBills = repository.allBills
        allCategories = repository.allCategories
        allTokens = repository.allTokens
        user = fetchUser(1)


        //init mediator live data objects
        periodTransactions = fetchPeriodTransactions()
        overBudget = fetchOverBudget()
        categoriesWithSpend = fetchCategoriesWithSpend()
        toAllocate = fetchToAllocate()
        uncategorisedTransactions = fetchCategoryTransactions("Uncategorised")
        prediction = fetchPrediction()
    }



    //methods for combining live data objects and calculating other values

    //Methods for prediction
    //check if the user is predicted to go over budget
    private fun fetchPrediction() : LiveData<Boolean> {
        //get all outgoings for this period
        val prediction = MediatorLiveData<Boolean>()

        prediction.addSource(periodTransactions) {
            if (user.value?.period == "Weekly") {
                prediction.value = getPrediction() > user.value?.allowance!!
            } else if (user.value?.period == "Monthly") {
                prediction.value = getPrediction()*4 > user.value?.allowance!!
            }

        }
        prediction.addSource(user) {
            if (user.value?.period == "Weekly") {
                prediction.value = getPrediction() > user.value?.allowance!!
            } else if (user.value?.period == "Monthly") {
                prediction.value = getPrediction()*4 > user.value?.allowance!!
            }
        }
        return prediction
    }


    //Load the .tflite file and make a prediction with the model
    private fun getPrediction() : Double{
        val dayNumber = repository.getCurrentDay().get(Calendar.DAY_OF_WEEK)
        val total = if (user.value?.period == "Weekly") {
            periodTransactions.value?.first?.toFloat()
        } else {
            periodTransactions.value?.first?.toFloat()?.div(4)
        }

        var tflite: Interpreter
        val input = total?.let { floatArrayOf(dayNumber.toFloat(), it) }
        val output = Array(1) { FloatArray(1)}

        tflite = Interpreter(getModelFromFile())
        tflite.run(input, output)

        return output[0][0].toDouble()
    }

    //Load the .tflite model as a MappedByteBuffer
    @Throws(IOException::class)
    private fun getModelFromFile(): MappedByteBuffer {
        val descriptor : AssetFileDescriptor = app.assets.openFd("converted_model.tflite")
        val stream = FileInputStream(descriptor.fileDescriptor)
        val channel : FileChannel = stream.channel
        val startOffset: Long = descriptor.startOffset
        val declaredLength: Long = descriptor.declaredLength;
        return channel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }


    //fetch the amount left to allocate
    private fun fetchToAllocate() : LiveData<Int> {
        //get all outgoings for this period
        val toAllocate = MediatorLiveData<Int>()

        toAllocate.addSource(user) {
            var allocate = it.allowance
            allCategories.value?.forEach {
                allocate -= it.allocated
            }
            toAllocate.value = allocate
        }

        toAllocate.addSource(allCategories) {
            var allocate = user.value?.allowance!!
            it.forEach {
                allocate -= it.allocated
            }
            toAllocate.value = allocate
        }

        return toAllocate
    }


    //check if the user is over budget
    private fun fetchOverBudget() : LiveData<String> {
        //get all outgoings for this period
        val spend = MediatorLiveData<String>()

        spend.addSource(user) {
            val transactions = periodTransactions.value?.second
            val total = periodTransactions.value?.first

            if (it.allowance == 0 || it == null) {
                spend.value = "nobudget"
            } else if (transactions.isNullOrEmpty()) {
                spend.value = "false"
            } else {
                if (total != null) {
                    spend.value = (total > it.allowance).toString()
                }
            }
        }

        spend.addSource(periodTransactions) {
            if (user.value?.allowance == 0 || user.value == null) {
                spend.value = "nobudget"
            } else if (it.second.isNullOrEmpty()) {
                spend.value = "false"
            } else {
                spend.value = (it.first > user.value?.allowance!!).toString()
            }
        }
        return spend
    }

    //fetch all the transactions for the current period
    private fun fetchPeriodTransactions() : LiveData<Pair<Double, List<Transaction>>> {
        //get all outgoings for this period
        val periodTransactions = MediatorLiveData<Pair<Double, List<Transaction>>>()

        periodTransactions.addSource(user) { value ->
            periodTransactions.value = getPeriodTransactions(allTransactions, user)
        }
        periodTransactions.addSource(allTransactions) { value ->
            periodTransactions.value = getPeriodTransactions(allTransactions, user)
        }
        return periodTransactions
    }


    //returns a list of all spending categories with spend amount added.
    private fun fetchCategoriesWithSpend() : LiveData<List<Category>> {
        //get all outgoings for this period
        val categoriesWithSpend = MediatorLiveData<List<Category>>()

        categoriesWithSpend.addSource(user) { value ->
            val periodTransactions = periodTransactions.value?.second
            val categoriesList = ArrayList<Category>()
            allCategories.value?.forEach { category ->
                if (category.name != "Income") {
                    var amount = 0.0
                    periodTransactions?.forEach {
                        if (it.category == category.name) {
                            amount += -it.amount
                        }
                    }
                    category.amount = amount
                    categoriesList.add(category)
                }
            }
            categoriesWithSpend.value = categoriesList
        }

        categoriesWithSpend.addSource(periodTransactions) { value ->
            val periodTransactions = periodTransactions.value?.second
            val categoriesList = ArrayList<Category>()
            allCategories.value?.forEach { category ->
                if (category.name != "Income") {
                    var amount = 0.0
                    periodTransactions?.forEach {
                        if (it.category == category.name) {
                            amount += -it.amount
                        }
                    }
                    category.amount = amount
                    categoriesList.add(category)
                }
            }
            categoriesWithSpend.value = categoriesList
        }

        categoriesWithSpend.addSource(allCategories) { value ->
            val periodTransactions = periodTransactions.value?.second
            val categoriesList = ArrayList<Category>()
            allCategories.value?.forEach { category ->
                if (category.name != "Income") {
                    var amount = 0.0
                    periodTransactions?.forEach {
                        if (it.category == category.name) {
                            amount += -it.amount
                        }
                    }
                    category.amount = amount
                    categoriesList.add(category)
                }
            }
            categoriesWithSpend.value = categoriesList
        }

        return categoriesWithSpend
    }


    //Get the transactions for the current period
    private fun getPeriodTransactions(
        transactions: LiveData<List<Transaction>>,
        user: LiveData<User>
    ): Pair<Double, List<Transaction>>{
        val period = user.value?.period
        val transactions = transactions.value
        var total = 0.0
        val periodTransactions: MutableList<Transaction> = ArrayList()
        if (!transactions.isNullOrEmpty()) {
            //get all outgoings for this period
            val c = repository.getCurrentDay()
            val currentYear = c.get(Calendar.YEAR)
            val currentWeek = c.get(Calendar.WEEK_OF_YEAR)
            val currentMonth = c.get(Calendar.MONTH)

            if (period == "Weekly") {
                transactions.forEach {
                    c.setTime(it.timestamp)
                    val year = c.get(Calendar.YEAR)
                    val week = c.get(Calendar.WEEK_OF_YEAR)
                    if(currentYear==year && currentWeek==week && it.amount.sign == -1.0) {
                        total -= it.amount
                        periodTransactions.add(it)
                    }
                    total = Math.round(total * 100.0) / 100.0
                }
            } else {
                transactions.forEach {
                    c.setTime(it.timestamp)
                    val year = c.get(Calendar.YEAR)
                    val month = c.get(Calendar.MONTH)
                    if(currentYear==year && currentMonth==month && it.amount.sign == -1.0) {
                        total -= it.amount
                        periodTransactions.add(it)
                    }
                    total = Math.round(total * 100.0) / 100.0
                }
            }
        }
        return Pair(total, periodTransactions)
    }


    //Methods for retrieving or inserting data from the repositiory

    fun insertTransaction(transaction: Transaction) = GlobalScope.launch {
        repository.updateBalance(transaction.amount, transaction.accountName)
        repository.insertTransaction(transaction)
    }
    fun updateTransaction(transaction: Transaction, oldAmount: Double) = GlobalScope.launch {
        repository.updateBalance(transaction.amount-oldAmount, transaction.accountName)
        repository.updateTransaction(transaction)
    }
    fun updateTransaction2(transaction: Transaction) = GlobalScope.launch {
        repository.updateTransaction(transaction)
    }
    fun deleteTransaction(transaction: Transaction) = GlobalScope.launch {
        repository.updateBalance(-transaction.amount, transaction.accountName)
        repository.deleteTransaction(transaction)
    }
    fun insertAccount(account: Account) = GlobalScope.launch {
        repository.insertAccount(account)
    }
    fun updateAccount(account: Account) = GlobalScope.launch {
        repository.updateAccount(account)
    }
    fun deleteAccount(account: Account) = GlobalScope.launch {
        repository.deleteAccount(account)
    }
    fun fetchAccount(accountId: Int) : LiveData<Account> {
        return repository.fetchAccount(accountId)
    }
    fun insertSaving(savingGoal: SavingGoal) = GlobalScope.launch {
        repository.insertSaving(savingGoal)
    }
    fun updateSaving(savingGoal: SavingGoal) = GlobalScope.launch {
        repository.updateSaving(savingGoal)
    }
    fun deleteSaving(savingGoal: SavingGoal)= GlobalScope.launch {
        repository.deleteSaving(savingGoal)
    }

    fun insertIncome(income: Income) = GlobalScope.launch {
        repository.insertIncome(income)
    }
    fun updateIncome(income: Income) = GlobalScope.launch {
        repository.updateIncome(income)
    }
    fun deleteIncome(income: Income) = GlobalScope.launch {
        repository.deleteIncome(income)
    }
    fun insertBill(bill: Bill) = GlobalScope.launch {
        repository.insertBill(bill)
    }
    fun updateBill(bill: Bill) = GlobalScope.launch {
        repository.updateBill(bill)
    }
    fun deleteBill(bill: Bill) = GlobalScope.launch {
        repository.deleteBill(bill)
    }
    fun updateUser(user: User) = GlobalScope.launch {
        repository.updateUser(user)
    }
    fun fetchUser(id: Int): LiveData<User> {
        return repository.getUser(id)
    }
    fun insertCategory(category: Category) = GlobalScope.launch {
        repository.insertCategory(category)
    }
    fun updateCategory(category: Category) = GlobalScope.launch {
        repository.updateCategory(category)
    }
    fun deleteCategory(category: Category) = GlobalScope.launch {
        repository.deleteCategory(category)
    }

    fun insertToken(accessToken: AccessToken) = GlobalScope.launch {
        repository.insertToken(accessToken)
    }
    fun fetchCategoryTransactions(category:String): LiveData<List<Transaction>> {
        return repository.fetchCategoryTransactions(category)
    }
    fun fetchAccountTransactions(account: String): LiveData<List<Transaction>> {
        return repository.fetchAccountTransactions(account)
    }
    suspend fun insertAccountAndTransactions(account: Account, transactions: List<Transaction>) = GlobalScope.launch {
        repository.insertAccount(account)
        repository.insertAllTransactions(transactions)
    }


    ///TEST CONSTRUCTOR - allows repository to be mocked --
    constructor(application: Application, repo: BankingRepository) : super(application) {
        // Gets reference to BankingDao from BankingRoomDatabase to construct
        // the correct BankingRepository.
        this.app = application
        this.repository = repo

        //get variables from repositiory
        allTransactions = repository.allTransactions
        allAccounts = repository.allAccounts
        allSavings = repository.allSavings
        allIncome = repository.allIncome
        allBills = repository.allBills
        allCategories = repository.allCategories
        allTokens = repository.allTokens
        user = fetchUser(1)


        //init mediator live data objects
        periodTransactions = fetchPeriodTransactions()
        overBudget = fetchOverBudget()
        categoriesWithSpend = fetchCategoriesWithSpend()
        toAllocate = fetchToAllocate()
        uncategorisedTransactions = fetchCategoryTransactions("Uncategorised")
        prediction = fetchPrediction()

    }

}



