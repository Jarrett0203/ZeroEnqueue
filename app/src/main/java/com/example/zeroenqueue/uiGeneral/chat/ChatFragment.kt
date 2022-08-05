package com.example.zeroenqueue.uiGeneral.chat

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.adapters.ChatLogAdapter
import com.example.zeroenqueue.classes.FoodStall
import com.example.zeroenqueue.classes.Message
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentChatBinding
import com.example.zeroenqueue.eventBus.HideFABCart
import com.example.zeroenqueue.interfaces.IMessageCallback
import com.google.firebase.database.*
import dmax.dialog.SpotsDialog
import org.greenrobot.eventbus.EventBus

class ChatFragment : Fragment(), IMessageCallback {

    private var _binding: FragmentChatBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var listener: IMessageCallback = this
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var dialog: AlertDialog
    private lateinit var adapter: ChatLogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val chatLogId : String
        if (Common.currentUser!!.userType == "Customer") {
            chatLogId = Common.orderSelected!!.foodStallId + Common.currentUser!!.uid!!
            FirebaseDatabase.getInstance().getReference(Common.FOODSTALL_REF)
                .child(Common.orderSelected!!.foodStallId!!)
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            val foodStall = snapshot.getValue(FoodStall::class.java)
                            (activity as AppCompatActivity).supportActionBar?.title = foodStall!!.name
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

        }
        else {
            chatLogId = Common.foodStallSelected!!.id + Common.orderSelected!!.userId
            (activity as AppCompatActivity).supportActionBar?.title = Common.orderSelected!!.userName
        }
        fun loadMessageList() {
            val messages = ArrayList<Message>()
            FirebaseDatabase.getInstance().getReference(Common.MESSAGES_REF)
                .child(chatLogId)
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(messageSnapshot in snapshot.children) {
                            val message = messageSnapshot.getValue(Message::class.java)
                            messages.add(message!!)
                        }
                        listener.onMessageLoadSuccess(messages)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onMessageLoadFailed(error.message)
                    }

                })
        }

        EventBus.getDefault().postSticky(HideFABCart(true))
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val swipeRefresh = binding.swipeRefresh
        val recyclerMessages = binding.recyclerMessages
        recyclerMessages.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        val editMessage = binding.editMessage
        val btnSend = binding.btnSend

        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()

        swipeRefresh.setOnRefreshListener {
            loadMessageList()
            swipeRefresh.isRefreshing = false
        }

        recyclerMessages.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                recyclerMessages.postDelayed({
                    recyclerMessages.smoothScrollToPosition(
                        adapter.messageList.size - 1
                    )
                }, 100)
            }
        }

        btnSend.setOnClickListener {
            if (editMessage.text.toString().isNotEmpty()) {
                val senderId: String
                val receiverId: String
                val userImage: String
                if (Common.currentUser!!.userType == "Customer") {
                    senderId = Common.currentUser!!.uid!!
                    receiverId = Common.orderSelected!!.foodStallId!!
                    userImage = if (Common.currentUser!!.image != null)
                        Common.currentUser!!.image!!
                    else
                        ""
                }
                else {
                    senderId = Common.foodStallSelected!!.id!!
                    receiverId = Common.orderSelected!!.userId!!
                    userImage = Common.foodStallSelected!!.image!!
                }

                val id = FirebaseDatabase.getInstance().getReference(Common.MESSAGES_REF).push().key
                val message = editMessage.text.toString()
                val timestamp = System.currentTimeMillis() / 1000
                val newMessage = Message(id, senderId, receiverId, userImage, message, timestamp)

                adapter.messageList.add(adapter.messageList.size, newMessage)
                adapter.notifyItemInserted(adapter.messageList.size)
                recyclerMessages.adapter = adapter

                FirebaseDatabase.getInstance()
                    .getReference(Common.MESSAGES_REF)
                    .child(chatLogId)
                    .setValue(adapter.messageList)
                    .addOnFailureListener { e -> Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()}

                editMessage.text.clear()
                recyclerMessages.scrollToPosition(adapter.messageList.size - 1)
            }
        }

        loadMessageList()
        chatViewModel.messageList.observe(viewLifecycleOwner) {
            adapter = ChatLogAdapter(requireContext(), it)
            recyclerMessages.adapter = adapter
            FirebaseDatabase.getInstance().getReference(Common.MESSAGES_REF).child(chatLogId).addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.exists()) {
                        val newMessage = snapshot.getValue(Message::class.java)
                        adapter.messageList.add(adapter.messageList.size, newMessage!!)
                        adapter.notifyItemInserted(adapter.messageList.size)
                        recyclerMessages.adapter = adapter
                        recyclerMessages.scrollToPosition(adapter.messageList.size - 1)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }

            })
        }

        return root
    }

    override fun onMessageLoadSuccess(messageList: ArrayList<Message>) {
        dialog.dismiss()
        chatViewModel.setMessageList(messageList)
    }

    override fun onMessageLoadFailed(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        dialog.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().postSticky(HideFABCart(false))
    }
}