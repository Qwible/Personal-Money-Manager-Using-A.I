package com.example.qwibBank.InputActivities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qwibBank.Entities.Category
import com.example.qwibBank.Misc.SVGUtils
import com.example.qwibBank.R
import com.example.qwibBank.Screens.TransactionFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton


class NewCategoryActivity : AppCompatActivity() {

    private lateinit var editNameView: EditText
    private lateinit var allocatedView: EditText
    private lateinit var editLogo: ImageView
    private var logo:String = R.drawable.ic_create_black_24dp.toString()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.getSupportActionBar()?.hide()

        var category: Category? = intent.getParcelableExtra("category")


        if (category != null) {
            setContentView(R.layout.activity_edit_category)
            editLogo = findViewById(R.id.logo)
            logo = category.logo
            SVGUtils()
                .fetchImage(this, category.logo, editLogo)
            allocatedView = findViewById(R.id.edit_allocated)
            allocatedView.setText(category.allocated.toString())
            editNameView = findViewById(R.id.edit_name)
            editNameView.setText(category.name)
            val transactions =  supportFragmentManager.findFragmentById(R.id.transactions_container) as TransactionFragment
            transactions.setCategory(category)

            val deleteButton = findViewById<FloatingActionButton>(R.id.button_delete)
            if (category.id != 1) {
                deleteButton.setOnClickListener {
                    val replyIntent = Intent()
                    replyIntent.putExtra("category", category)
                    replyIntent.putExtra("delete", true)
                    setResult(Activity.RESULT_OK, replyIntent)
                    finish()
                }
            } else {
                deleteButton.alpha = 0.25f
                deleteButton.setEnabled(false)
                deleteButton.setClickable(false)
                editNameView.setEnabled(false)
                editNameView.setClickable(false)
            }

        } else {
            setContentView(R.layout.activity_new_category)
            allocatedView = findViewById(R.id.edit_allocated)
            editNameView = findViewById(R.id.edit_name)
            editLogo = findViewById(R.id.logo)
            SVGUtils()
                .fetchImage(this, logo, editLogo)
        }

        editLogo.setOnClickListener {
            val intent = Intent(this, PickIcon::class.java)
            intent.putExtra("type", "category")
            startActivityForResult(intent, 1)
        }

        val fabBack = findViewById<FloatingActionButton>(R.id.back)
        fabBack.setOnClickListener {
            finish()
        }


        val button = findViewById<FloatingActionButton>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            val numeric = allocatedView.text.matches("-?\\d+(\\.\\d+)?".toRegex())
            if (editNameView.text.isEmpty()) {
                Toast.makeText(
                    this,
                    "Name cannot be empty",
                    Toast.LENGTH_LONG
                ).show()
            }else if (!numeric) {
                Toast.makeText(
                    this,
                    "Please insert allocated amount correctly",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                var newCategory:Category

                if (category != null){
                    newCategory = Category(category.id,
                        editNameView.text.toString(),
                        allocatedView.text.toString().toInt(),
                        logo
                    )
                    replyIntent.putExtra("edit", true)
                    replyIntent.putExtra("category", newCategory)
                } else {
                    newCategory = Category(null,
                        editNameView.text.toString(),
                        allocatedView.text.toString().toInt() ,
                        logo
                    )
                    replyIntent.putExtra("category", newCategory)
                }
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val logoUrl = data?.getStringExtra("icon")
        if (logoUrl != null) {
            logo = logoUrl
            val drawable = this.resources.getDrawable(logo.toInt())
            editLogo.setImageDrawable(drawable)
        }
    }
}

