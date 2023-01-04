package com.example.zeroenqueue.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.Message
import com.example.zeroenqueue.common.Common
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatLogAdapter(
    var context: Context,
    val messageList: ArrayList<Message>
) : RecyclerView.Adapter<ChatLogAdapter.MessageViewHolder>() {
    inner class MessageViewHolder(val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var profileImage: ImageView = itemView.findViewById(R.id.profile_image)
        var message: TextView = itemView.findViewById(R.id.message)
        var timestamp: TextView = itemView.findViewById(R.id.timestamp)

        override fun onClick(p0: View?) {
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (Common.currentUser!!.userType == "Customer") {
            return if (Common.currentUser!!.uid!! == messageList[position].senderId) 0 else 1
        }
        return if (Common.foodStallSelected!!.id == messageList[position].senderId) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        var viewHolder = MessageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_to_message, parent, false)
        )
        when (viewType) {
            1 -> viewHolder = MessageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_from_message, parent, false)
            )
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        if (messageList[position].userImage!!.isNotEmpty())
            Glide.with(context).load(messageList[position].userImage).into(holder.profileImage)
        holder.message.text = messageList[position].message
        val formatter: DateFormat = SimpleDateFormat("HH:mm", Locale.US)
        formatter.timeZone = TimeZone.getTimeZone("GMT+8:00")
        holder.timestamp.text = formatter.format(Date(messageList[position].timestamp!!))
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}