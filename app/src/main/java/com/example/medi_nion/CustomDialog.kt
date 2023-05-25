package com.example.medi_nion

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.EditText
import kotlinx.android.synthetic.main.alert_dialog.*

class CustomDialog(context: Context)
{
    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener

    fun setOnClickListener(listener: OnDialogClickListener)
    {
        onClickListener = listener
    }

    fun showDialog()
    {
        dialog.setContentView(R.layout.alert_dialog)
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()


        dialog.noButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.yesButton.setOnClickListener {
            onClickListener.onClicked()
            dialog.dismiss()
        }

    }

    interface OnDialogClickListener
    {
        fun onClicked()
    }

}