package com.example.zeroenqueue.ui.foodDetail

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.andremion.counterfab.CounterFab
import com.bumptech.glide.Glide
import com.example.zeroenqueue.R
import com.example.zeroenqueue.databinding.FragmentFoodDetailBinding
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.classes.Comment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import dmax.dialog.SpotsDialog

class foodDetailFragment : Fragment() {

    private var _binding: FragmentFoodDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var food_image: ImageView
    private lateinit var btnCart: CounterFab
    private lateinit var btnRating: FloatingActionButton
    private lateinit var food_name: TextView
    private lateinit var food_description: TextView
    private lateinit var food_price: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var btnShowComments: Button
    private lateinit var foodDetailViewModel: FoodDetailViewModel

    private lateinit var waitingDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        foodDetailViewModel =
            ViewModelProvider(this).get(FoodDetailViewModel::class.java)

        _binding = FragmentFoodDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        food_image = binding.foodImage
        btnCart = binding.btnCart
        btnRating = binding.btnRating
        food_name = binding.foodName
        food_description = binding.foodDescription
        food_price = binding.foodPrice
        ratingBar = binding.ratingBar
        btnShowComments = binding.btnShowComments

        initView()

        foodDetailViewModel.foodDetail.observe(viewLifecycleOwner) {
            displayInfo(it)
        }

        foodDetailViewModel.comment.observe(viewLifecycleOwner) {
            submitRatingtoFirebase(it)
        }
        return root
    }

    private fun submitRatingtoFirebase(comment: Comment?) {
        waitingDialog.show()

        FirebaseDatabase.getInstance()
            .getReference(Common.COMMENT_REF)
            .child(Common.foodSelected!!.id!!)
            .push()
            .setValue(comment)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    addRatingToFood(comment!!.ratingValue)
                }
                waitingDialog.dismiss()

            }
    }

    private fun addRatingToFood(ratingValue: Float) {
        FirebaseDatabase.getInstance()
            .getReference(Common.CATEGORY_REF)
            .child(Common.categorySelected!!.menu_id!!)
            .child("foods")
            .child(Common.foodSelected!!.key!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val food = snapshot.getValue(Food::class.java)
                        food!!.key = Common.foodSelected!!.key

                        val sumRating = food.ratingValue + ratingValue
                        val ratingCount = food.ratingCount + 1
                        val totalRating = sumRating/ratingCount

                        val updateData = HashMap<String, Any>()
                        updateData["ratingValue"] = totalRating
                        updateData["ratingCount"] = ratingCount

                        food.ratingCount = ratingCount
                        food.ratingValue = totalRating

                        snapshot.ref
                            .updateChildren(updateData)
                            .addOnCompleteListener{ task ->
                                waitingDialog.dismiss()
                                if (task.isSuccessful){
                                    Common.foodSelected = food
                                    foodDetailViewModel.setFood(food)
                                    Toast.makeText(context!!, "Thanks for reviewing", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                    else
                        waitingDialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                    waitingDialog.dismiss()
                    Toast.makeText(context!!, error.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun initView() {
        waitingDialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        btnRating.setOnClickListener {
            showDialogRating()
        }
    }

    private fun showDialogRating() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Rating Food")
        builder.setMessage("Please fill information")

        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_rating_comment, null)

        val ratingBar = itemView.findViewById<RatingBar>(R.id.ratingBar)
        val edit_comment = itemView.findViewById<EditText>(R.id.edit_comment)

        builder.setView(itemView)

        builder.setNegativeButton("CANCEL") { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        builder.setPositiveButton("OK") { dialogInterface, i ->
            val comment = Comment()
            comment.name = Common.currentUser!!.name
            comment.uid = Common.currentUser!!.uid
            comment.commentDetails = edit_comment.text.toString()
            comment.ratingValue = ratingBar.rating
            val serverTimeStamp = HashMap<String, Any>()
            serverTimeStamp["timeStamp"] = ServerValue.TIMESTAMP
            comment.commentTimeStamp = serverTimeStamp

            foodDetailViewModel.setComment(comment)
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun displayInfo(it: Food?) {
        context?.let { it1 -> Glide.with(it1).load(it!!.image).into(food_image) }
        food_name.text = StringBuilder(it!!.name!!)
        food_description.text = StringBuilder(it.description!!)
        food_price.text = StringBuilder(it.price.toString())
        ratingBar.rating = it.ratingValue
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}