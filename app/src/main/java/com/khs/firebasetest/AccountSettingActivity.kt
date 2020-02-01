package com.khs.firebasetest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_account_setting.*

class AccountSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)
        setUpListener()
    }

    private fun setUpListener() {
        account_setting_back.setOnClickListener     { onBackPressed() }
        account_setting_logout.setOnClickListener   { signOutAccount() }
        account_setting_delete.setOnClickListener   { showDeleteDialog() }
    }

    private fun deleteAccount() {
       AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
                MoveToMainActivity()
                Toast.makeText(this,"계정을 삭제했습니다..",Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteDialog(){
        AccountDeleteDialog().apply {
            addAccountDeleteDialogInterface(object : AccountDeleteDialog.AccountDeleteDialogInterface {
                override fun delete() {
                    deleteAccount()
                }
                override fun cancelDelete() {
                }
            })
        }.show(supportFragmentManager,"")
    }

    private fun signOutAccount() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                MoveToMainActivity()
                Toast.makeText(this,"로그아웃 했습니다.",Toast.LENGTH_SHORT).show()
            }
    }

    private fun MoveToMainActivity() {
        startActivity(Intent(this,MainActivity::class.java))
    }
}
