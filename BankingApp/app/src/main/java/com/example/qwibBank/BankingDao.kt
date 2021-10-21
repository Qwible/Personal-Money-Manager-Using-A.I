package com.example.qwibBank

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.qwibBank.Entities.*
import com.example.qwibBank.Entities.Transaction

@Dao
interface BankingDao {

//Transaction queries
    @Query("SELECT * from transaction_table ORDER BY id ASC")
    fun orderedTransactions(): LiveData<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateTransaction(transaction: Transaction)


    @Query("SELECT * from transaction_table WHERE category = :category ORDER BY id ASC ")
    fun fetchCategoryTransactions(category: String): LiveData<List<Transaction>>

    @Query("SELECT * from transaction_table WHERE accountName = :account ORDER BY id ASC")
    fun fetchAccountTransactions(account: String): LiveData<List<Transaction>>

    @Query("DELETE FROM transaction_table")
    suspend fun deleteAllTransactions()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllTransactions(transactions: List<Transaction> )

    //Account queries
    @Query("SELECT * from account_table ORDER BY name ASC")
    fun orderedAccounts(): LiveData<List<Account>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAccount(account: Account)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateAccount(account: Account)

    @Query("UPDATE account_table SET balance = balance+:amount WHERE name = :name")
    suspend fun updateBalance(amount: Double, name: String)

    @Delete
    suspend fun deleteAccount(account: Account)

    @Query("DELETE FROM account_table")
    suspend fun deleteAllAccounts()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllAccounts(accounts: List<Account> )

    @Query("SELECT * from account_table WHERE id = :accountId")
    fun fetchAccount(accountId: Int): LiveData<Account>




//Savings queries
    @Query("SELECT * from saving_table ORDER BY name ASC")
    fun orderedSavings(): LiveData<List<SavingGoal>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSaving(savingGoal: SavingGoal)

    @Query("DELETE FROM saving_table")
    suspend fun deleteAllSavings()

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateSaving(savingGoal: SavingGoal)

    @Delete
    suspend fun deleteSaving(savingGoal: SavingGoal)


//Income queries
    @Query("SELECT * from income_table ORDER BY source ASC")
    fun orderedIncome(): LiveData<List<Income>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIncome(income: Income)

    @Query("DELETE FROM income_table")
    suspend fun deleteAllIncome()

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateIncome(income: Income)

    @Delete
    suspend fun deleteIncome(income: Income)




//bills queries
    @Query("SELECT * from bill_table ORDER BY recipient ASC")
    fun orderedBills(): LiveData<List<Bill>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBill(bill: Bill)

    @Query("DELETE FROM bill_table")
    suspend fun deleteAllBills()

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateBill(bill: Bill)

    @Delete
    suspend fun deleteBill(bill: Bill)


    //user queries
    @Query("SELECT * from user_table WHERE id = :id")
    fun getUser(id:Int): LiveData<User>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIncome(user: User)

    @Query("DELETE FROM user_table")
    suspend fun deleteAllUsers()

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateUser(user: User)



    //categories queries
    @Query("SELECT * from category_table")
    fun orderedCategories(): LiveData<List<Category>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: Category)

    @Query("DELETE FROM category_table")
    suspend fun deleteAllCategories()

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)


    //token queries
    @Query("SELECT * from token_table")
    fun allTokens(): LiveData<List<AccessToken>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToken(token: AccessToken)

    @Query("DELETE FROM token_table")
    suspend fun deleteAllTokens()

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateToken(token: AccessToken)

    @Delete
    suspend fun deleteToken(token: AccessToken)

}