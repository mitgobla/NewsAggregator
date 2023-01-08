package com.mitgobla.newsaggregator.frontpage

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.mitgobla.newsaggregator.R

class CommentDialogFragment : DialogFragment() {
    internal lateinit var listener: CommentDialogListener

    interface CommentDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as CommentDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement NoticeDialogListener"))
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.addComment)
                .setPositiveButton(R.string.post, DialogInterface.OnClickListener { dialog, id ->
                    listener.onDialogPositiveClick(this)
                })
                .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialog, id ->
                    listener.onDialogNegativeClick(this)
                })
            builder.setView(requireActivity().layoutInflater.inflate(R.layout.comment_dialog_layout, null))
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}