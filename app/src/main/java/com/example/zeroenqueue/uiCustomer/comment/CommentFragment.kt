package com.example.zeroenqueue.uiCustomer.comment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.adapters.CommentAdapter
import com.example.zeroenqueue.classes.Comment
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentCommentBinding
import com.example.zeroenqueue.interfaces.ICommentCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog

class CommentFragment : BottomSheetDialogFragment(), ICommentCallback {

    private var _binding: FragmentCommentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dialog: AlertDialog
    private var listener: ICommentCallback = this
    private lateinit var commentViewModel: CommentViewModel
    private lateinit var recyclerComment: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        commentViewModel =
            ViewModelProvider(this)[CommentViewModel::class.java]

        _binding = FragmentCommentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerComment = binding.recyclerComment
        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()

        recyclerComment.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        recyclerComment.layoutManager = layoutManager
        recyclerComment.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))

        loadCommentFromFirebase()

        commentViewModel.commentList.observe(viewLifecycleOwner) {
            recyclerComment.adapter = CommentAdapter(requireContext(), it)
        }
        return root
    }

    private fun loadCommentFromFirebase() {
        dialog.show()
        val comments = ArrayList<Comment>()
        FirebaseDatabase.getInstance().getReference(Common.COMMENT_REF)
            .child(Common.foodSelected!!.id!!)
            .orderByChild("commentTimeStamp")
            .limitToLast(100)
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(commentSnapShot in snapshot.children){
                        val comment = commentSnapShot.getValue(Comment::class.java)
                        comments.add(comment!!)
                    }
                    listener.onCommentLoadSuccess(comments)
                }

                override fun onCancelled(error: DatabaseError) {
                    listener.onCommentLoadFailed(error.message)
                }

            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCommentLoadSuccess(commentList: List<Comment>) {
        dialog.dismiss()
        commentViewModel.setCommentList(commentList)
    }

    override fun onCommentLoadFailed(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        dialog.dismiss()
    }

    companion object{
        private var instance: CommentFragment? = null
        fun getInstance(): CommentFragment{
            if (instance == null){
                instance = CommentFragment()
            }
            return instance!!
        }
    }


}