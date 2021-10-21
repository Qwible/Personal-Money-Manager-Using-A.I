package com.example.qwibBank

import androidx.lifecycle.LiveData
import com.example.qwibBank.Entities.*
import java.util.*

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class BankingRepository(private val bankingDao: BankingDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allTransactions: LiveData<List<Transaction>> = bankingDao.orderedTransactions()
    val allAccounts: LiveData<List<Account>> = bankingDao.orderedAccounts()
    val allSavings: LiveData<List<SavingGoal>> = bankingDao.orderedSavings()
    val allIncome: LiveData<List<Income>> = bankingDao.orderedIncome()
    val allBills: LiveData<List<Bill>> = bankingDao.orderedBills()
    val allCategories: LiveData<List<Category>> = bankingDao.orderedCategories()
    val allTokens: LiveData<List<AccessToken>> = bankingDao.allTokens()

    suspend fun insertTransaction(transaction: Transaction) {
        bankingDao.insertTransaction(transaction)
    }
    suspend fun updateTransaction (transaction: Transaction) {
        bankingDao.updateTransaction(transaction)
    }
    suspend fun deleteTransaction(transaction: Transaction) {
        bankingDao.deleteTransaction(transaction)
    }


    suspend fun insertAccount(account: Account) {
        bankingDao.insertAccount(account)
    }
    suspend fun updateAccount(account: Account) {
        bankingDao.updateAccount(account)
    }

    suspend fun updateBalance(amount: Double, name: String) {
        bankingDao.updateBalance(amount, name)
    }

    suspend fun deleteAccount(account: Account) {
        bankingDao.deleteAccount(account)
    }

    suspend fun insertSaving(savingGoal: SavingGoal) {
        bankingDao.insertSaving(savingGoal)
    }
    suspend fun updateSaving(savingGoal: SavingGoal) {
        bankingDao.updateSaving(savingGoal)
    }
    suspend fun deleteSaving(savingGoal: SavingGoal) {
        bankingDao.deleteSaving(savingGoal)
    }

    suspend fun insertBill(bill: Bill) {
        bankingDao.insertBill(bill)
    }
    suspend fun updateBill(bill: Bill) {
        bankingDao.updateBill(bill)
    }
    suspend fun deleteBill(bill: Bill) {
        bankingDao.deleteBill(bill)
    }

    suspend fun insertUser(user: User) {
        bankingDao.insertIncome(user)
    }

    suspend fun updateUser(user: User) {
        bankingDao.updateUser(user)
    }


    fun getUser(id:Int) : LiveData<User> {
        return bankingDao.getUser(id)
    }


    suspend fun insertIncome(income: Income) {
        bankingDao.insertIncome(income)
    }
    suspend fun updateIncome(income: Income) {
        bankingDao.updateIncome(income)
    }
    suspend fun deleteIncome(income: Income) {
        bankingDao.deleteIncome(income)
    }

    suspend fun insertCategory(category: Category) {
        bankingDao.insertCategory(category)
    }
    suspend fun updateCategory(category: Category) {
        bankingDao.updateCategory(category)
    }
    suspend fun deleteCategory(category: Category) {
        bankingDao.deleteCategory(category)
    }

    suspend fun insertToken(token: AccessToken) {
        bankingDao.insertToken(token)
    }
    suspend fun updateToken(token: AccessToken)  {
        bankingDao.updateToken(token)
    }
    suspend fun deleteToken(token: AccessToken)  {
        bankingDao.deleteToken(token)
    }

    fun fetchCategoryTransactions(category: String) : LiveData<List<Transaction>> {
        return bankingDao.fetchCategoryTransactions(category)
    }
    fun fetchAccountTransactions(account: String) : LiveData<List<Transaction>> {
        return bankingDao.fetchAccountTransactions(account)
    }
    fun fetchAccount(accountId: Int) : LiveData<Account> {
        return bankingDao.fetchAccount(accountId)
    }



    suspend fun insertAllAccounts(accounts: List<Account>){
        bankingDao.insertAllAccounts(accounts)
    }
    suspend fun insertAllTransactions(transaction: List<Transaction>){
        bankingDao.insertAllTransactions(transaction)
    }

    //Get the current day. Called from the repository to allow easy testing of view model classes with respect to periods.
    fun getCurrentDay() : Calendar {
        return Calendar.getInstance()
    }

}