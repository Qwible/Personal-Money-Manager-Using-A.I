package com.example.qwibBank.TrueLayer

import android.content.Context
import android.util.Log
import com.example.qwibBank.BankingViewModel
import com.example.qwibBank.Entities.AccessToken
import com.example.qwibBank.Entities.Account
import com.example.qwibBank.Entities.Transaction
import com.example.qwibBank.Misc.SVGUtils
import com.example.qwibBank.R
import com.example.qwibBank.exportData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sign

interface DataApi {
    @GET("/data/v1/accounts")
    suspend fun getAccounts(
        @Header("Authorization" ) accessToken: String) : Response<AccountResults>

    @GET("/data/v1/accounts/{account_id}/transactions")
    suspend fun getTransactions(
        @Header("Authorization" ) accessToken: String,
        @Path("account_id" ) accountId: String) : Response<TransactionResults>

    @GET("/data/v1/accounts/{account_id}/transactions")
    suspend fun getTransactionDates(
        @Header("Authorization" ) accessToken: String,
        @Path("account_id" ) accountId: String,
        @Query("from" ) startDate: String,
        @Query("to" ) endDate: String) : Response<TransactionResults>

   /* @GET("/data/v1/accounts/{account_id}/balance")
    suspend fun getAccountBalance(
        @Header("Authorization" ) accessToken: String,
        @Path("account_id" ) accountId: String) : Response<BalanceResults> */
}


class TransactionResults (var results: List<TransactionData>) {
    override fun toString(): String {
        var transactionString = ""
        results.forEach{
            transactionString += " "+it.toString()
        }
        return transactionString
    }
}

class TransactionData (
    var transaction_id: String,
    var timestamp: String,
    var description: String,
    var amount: String,
    var currenty: String,
    var transaction_type: String,
    var transaction_category: String,
    var transaction_classification: Array<String>,
    var merchant_name: String,
    var running_balance: RunningBalance,
    var meta: Meta
) {
    override fun toString(): String {
        return (transaction_id+" "+description)
    }
}

class RunningBalance(
    var amount: String,
    var currency: String
)

class Meta(
    var bank_transaction_id: String,
    var provider_transaction_category:String
)

class AccountResults (var results: List<AccountData>) {
    override fun toString(): String {
        var accountString = ""
        results.forEach{
            accountString += " "+it.toString()
        }
        return accountString
    }
}

class AccountData (
    var update_timestamp: String,
    var account_id: String,
    var account_type: String,
    var display_name: String,
    var currency: String,
    var account_number: AccountNumber,
    var provider: Provider
) {
    override fun toString(): String {
        return account_id.toString()
    }
}

class AccountNumber (
    var iban: String,
    var number: String,
    var sort_code: String,
    var swift_bic: String
)

class Provider (
    var display_name: String,
    var logo_uri: String,
    var provider_id: String
)


fun truelayerLink(code: String, bankingViewModel: BankingViewModel, token: AccessToken?, context: Context) {
    val authService =
        RetrofitFactory.makeAuthService()
    var accessToken: AccessToken?
    CoroutineScope(Dispatchers.IO).launch {
            val response1:Response<AccessToken>
            if (token != null) {
                response1 = authService.refreshAccessToken(
                    "refresh_token",
                    "sandbox-qwible-feab6b",
                    "c069ae8c-f0fa-43b6-96ef-3ab846a930c8",
                    token.refresh_token
                )
            } else {
                response1 = authService.getAccessToken(
                    "authorization_code",
                    "sandbox-qwible-feab6b",
                    "c069ae8c-f0fa-43b6-96ef-3ab846a930c8",
                    "http://qwibbank.example.com/auth",
                    code)
            }
            withContext(Dispatchers.Main) {
            try {
                if (response1.isSuccessful) {
                    accessToken = response1.body()
                    accessToken?.let {
                        bankingViewModel.insertToken(it)
                        getAccountsWithTransactions(
                            it.access_token,
                            bankingViewModel,
                            context
                        )
                    }
                } else {
                    Log.d("URLSTUFF:ELSE1", response1.toString())
                }
            } catch (e: HttpException) {
                Log.d("URLSTUFF:HTTP1", e.toString())
            } catch (e: Throwable) {
                Log.d("URLSTUFF:THROW1", e.toString())
            }
        }
    }
}


suspend fun getAccountsWithTransactions(accessToken: String, bankingViewModel: BankingViewModel, context: Context) {
    val token = "Bearer "+accessToken.toString()
    val dataService =
        RetrofitFactory.makeDataService()
    val response2 = dataService.getAccounts(token)
    var accounts: List<AccountData>? = null

    withContext(Dispatchers.Main) {
        try {
            if (response2.isSuccessful) {
                accounts = response2.body()?.results
            } else {
                Log.d("URLSTUFF:ELSE2", response2.toString())
            }
        } catch (e: HttpException) {
            Log.d("URLSTUFF:HTTP2", response2.toString())
        } catch (e: Throwable) {
            Log.d("URLSTUFF:THROW2", response2.toString())
        }
    }

    accounts?.forEach {
        val accountName = it.display_name+" "+it.account_number.number
        val accountId = it.account_id
        val response3 = dataService.getTransactions(token ,it.account_id)

        //retrieve SVG from uri and saving in internal storage
        var accountLogo = SVGUtils().saveSVG(it.provider.logo_uri, context, accountName)

        //if no logo path is returned then set to default image
        if (accountLogo.isNullOrBlank()) {
            accountLogo = R.drawable.ic_create_black_24dp.toString()
        }

        withContext(Dispatchers.Main) {
            try {
                if (response3.isSuccessful) {
                    val response = response3.body()?.results
                    val transactionList: ArrayList<Transaction> = ArrayList()
                    response?.forEach {
                        var merchant = it.description
                        if (merchant == "null") {
                            merchant = "Unknown"
                        }
                        val date = SimpleDateFormat("yyyy-MM-dd").parse(it.timestamp.take(10))
                        var category = "Uncategorised"
                        var amount = it.amount.toDouble()
                        if (amount.sign == 1.0) {
                            category = "Income"
                        }
                        val transaction = Transaction(null, merchant,
                            amount, accountName, date, category)
                        transactionList.add(transaction)
                    }
                    var balance = 0.0
                    response?.let {
                        balance = response[0].running_balance.amount.toDouble()
                    }
                    val account = Account(null,
                        accountName, balance, accountLogo, accountId)
                    bankingViewModel.insertAccountAndTransactions(account, transactionList)

                } else {
                    Log.d("URLSTUFF:ELSE3", response3.toString())
                }
            } catch (e: HttpException) {
                Log.d("URLSTUFF:HTTP3", response3.toString())
            } catch (e: Throwable) {
                Log.d("URLSTUFF:THROW3", response3.toString())
                Log.d("URLSTUFF:THROW3", e.toString())
            }
        }
    }
}

fun toSimpleString(date: Date) : String {
    val format = SimpleDateFormat("dd/MM/yyy")
    return format.format(date)
}

fun toSimpleString(dates: ArrayList<Date>) : String {
    var datesString = ""
    dates.forEach {
        datesString = datesString + toSimpleString(
            it
        ) +","
    }
    datesString = datesString.substring(0, datesString.length - 1)
    return datesString
}


object RetrofitFactory {

    fun makeAuthService(): AuthApi {
        return Retrofit.Builder()
            .baseUrl("https://auth.truelayer-sandbox.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(AuthApi::class.java)
    }

    fun makeDataService(): DataApi {
        return Retrofit.Builder()
            .baseUrl("https://api.truelayer-sandbox.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(DataApi::class.java)
    }
}





