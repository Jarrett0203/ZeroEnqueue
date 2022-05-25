package com.example.zeroenqueue.ui.foodDetail

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.andremion.counterfab.CounterFab
import com.bumptech.glide.Glide
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.Comment
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentFoodDetailBinding
import com.example.zeroenqueue.db.CartDataSource
import com.example.zeroenqueue.db.CartDatabase
import com.example.zeroenqueue.db.CartItem
import com.example.zeroenqueue.db.LocalCartDataSource
import com.example.zeroenqueue.eventBus.CountCartEvent
import com.example.zeroenqueue.ui.comment.CommentFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import dmax.dialog.SpotsDialog
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_food_detail.*
import org.greenrobot.eventbus.EventBus

class FoodDetailFragment : Fragment() {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var cartDataSource: CartDataSource

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
            ViewModelProvider(this)[FoodDetailViewModel::class.java]

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
            .getReference(Common.FOODLIST_REF)
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

        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(requireContext()).cartDAO())
        waitingDialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        btnRating.setOnClickListener {
            showDialogRating()
        }
        btnShowComments.setOnClickListener{
            val commentFragment = CommentFragment.getInstance()
            commentFragment.show(requireActivity().supportFragmentManager, "CommentFragment")
        }

        btnCart.setOnClickListener {
            val cartItem = CartItem()
            cartItem.uid = Common.currentUser!!.uid!!
            //cartItem.userPhone = Common.currentUser!!.phone!!

            cartItem.foodId = Common.foodSelected!!.id!!
            cartItem.foodName = Common.foodSelected!!.name!!
            cartItem.foodImage = Common.foodSelected!!.image!!
            cartItem.foodPrice = Common.foodSelected!!.price.toDouble()
            cartItem.foodQuantity = number_button!!.number.toInt()
            cartItem.foodAddon = "Default"
            cartItem.foodSize = "Default"

            cartDataSource.getItemWithAllOptionsInCart(Common.currentUser!!.uid!!,
                cartItem.foodId,
                cartItem.foodSize,
                cartItem.foodAddon
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: SingleObserver<CartItem> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(cartItem: CartItem) {
                        //if item is alr in db, update
                        if(cartItem.equals(cartItem)) {
                            cartItem.foodExtraPrice = cartItem.foodExtraPrice
                            cartItem.foodAddon = cartItem.foodAddon
                            cartItem.foodSize = cartItem.foodSize
                            cartItem.foodQuantity = cartItem.foodQuantity + cartItem.foodQuantity

                            cartDataSource.updateCart(cartItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object: SingleObserver<Int> {
                                    override fun onSuccess(t: Int) {
                                        Toast.makeText(context, "Update Cart Success", Toast.LENGTH_SHORT).show()
                                        EventBus.getDefault().postSticky(CountCartEvent(true))
                                    }

                                    override fun onSubscribe(d: Disposable) {

                                    }

                                    override fun onError(e: Throwable) {
                                        Toast.makeText(context, "{UPDATE CART}" + e.message, Toast.LENGTH_SHORT).show()
                                    }
                                })
                        } else {
                            //insert if it is not avail.
                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({}, {
                                        t: Throwable -> Toast.makeText(context, "{INSERT CART}" + t.message, Toast.LENGTH_SHORT).show()
                                }))
                        }
                    }

                    override fun onError(e: Throwable) {
                        if(e.message!!.contains("empty")) {
                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({}, {
                                        t: Throwable -> Toast.makeText(context, "{INSERT CART}" + t.message, Toast.LENGTH_SHORT).show()
                                }))
                        } else {
                            Toast.makeText(context, "[CART ERROR]" + e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
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

        builder.setNegativeButton("CANCEL") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        builder.setPositiveButton("OK") { _, _ ->
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