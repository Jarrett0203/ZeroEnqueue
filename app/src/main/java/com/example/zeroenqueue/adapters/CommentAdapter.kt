package com.example.zeroenqueue.adapters

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.Comment

class CommentAdapter(
    var context: Context,
    private val commentList: List<Comment>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    inner class CommentViewHolder(val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var username: TextView = itemView.findViewById(R.id.username)
        var date: TextView = itemView.findViewById(R.id.date)
        var comment: TextView = itemView.findViewById(R.id.comment)
        var ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)

        override fun onClick(p0: View?) {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_comment_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val timeStamp = commentList[position].commentTimeStamp!!["timeStamp"]!!.toString().toLong()
        holder.date.text = DateUtils.getRelativeTimeSpanString(timeStamp)
        holder.username.text = commentList[position].name
        holder.comment.text = commentList[position].commentDetails
        holder.ratingBar.rating = commentList[position].ratingValue
    }

    override fun getItemCount(): Int {
        return commentList.size
    }
}