package com.example.zeroenqueue.uiCustomer.foodDetail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.contains
import androidx.core.view.iterator
import androidx.core.view.size
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
import com.example.zeroenqueue.uiCustomer.comment.CommentFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.gson.Gson
import dmax.dialog.SpotsDialog
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_food_detail.*
import org.greenrobot.eventbus.EventBus
import kotlin.math.roundToInt

class FoodDetailFragment : Fragment(), TextWatcher {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var cartDataSource: CartDataSource

    private var _binding: FragmentFoodDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var addOnBottomSheetDialog: BottomSheetDialog

    private lateinit var food_image: ImageView
    private lateinit var btnCart: CounterFab
    private lateinit var btnRating: FloatingActionButton
    private lateinit var food_name: TextView
    private lateinit var food_description: TextView
    private lateinit var food_price: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var btnShowComments: Button
    private lateinit var rdi_group_size: RadioGroup
    private lateinit var addon_image: ImageView
    private lateinit var chipGroupAddonSelected: ChipGroup
    private lateinit var chipGroupAddon: ChipGroup
    private lateinit var searchAddon: EditText
    private lateinit var foodDetailViewModel: FoodDetailViewModel

    private lateinit var dialog: AlertDialog

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
        chipGroupAddonSelected = binding.chipGroupAddonSelected
        rdi_group_size = binding.rdiGroupSize
        addon_image = binding.addAddonImage

        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(requireContext()).cartDAO())
        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()

        addOnBottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
        val layout_selected_addon = layoutInflater.inflate(R.layout.layout_addon_display, null)
        chipGroupAddon = layout_selected_addon.findViewById(R.id.layout_addon_chipGroup)
        searchAddon = layout_selected_addon.findViewById(R.id.search_addon)
        addOnBottomSheetDialog.setContentView(layout_selected_addon)

        addOnBottomSheetDialog.setOnDismissListener {
            displayUserSelectedAddon()
            calculateTotalPrice()
        }

        addon_image.setOnClickListener {
            addOnBottomSheetDialog.show()
        }

        btnRating.setOnClickListener {
            showDialogRating()
        }

        btnShowComments.setOnClickListener {
            val commentFragment = CommentFragment.getInstance()
            commentFragment.show(requireActivity().supportFragmentManager, "CommentFragment")
        }

        btnCart.setOnClickListener {
            val cartItem = CartItem()
            cartItem.uid = Common.currentUser!!.uid!!
            cartItem.userPhone = Common.currentUser!!.phone!!
            cartItem.foodId = Common.foodSelected!!.id!!
            cartItem.foodName = Common.foodSelected!!.name!!
            cartItem.foodImage = Common.foodSelected!!.image!!
            cartItem.foodPrice = Common.foodSelected!!.price
            cartItem.foodQuantity = number_button!!.number.toInt()
            cartItem.foodExtraPrice = Common.calculateExtraPrice(Common.foodSelected!!.sizeSelected, Common.foodSelected!!.addOnSelected)

            if (Common.foodSelected!!.addOnSelected != null)
                cartItem.foodAddon = Gson().toJson(Common.foodSelected!!.addOnSelected)
            else
                cartItem.foodAddon = "Default"

            if (Common.foodSelected!!.sizeSelected != null)
                cartItem.foodSize = Gson().toJson(Common.foodSelected!!.sizeSelected)
            else
                cartItem.foodSize = "Default"

            cartDataSource.getItemWithAllOptionsInCart(
                Common.currentUser!!.uid!!,
                cartItem.foodId,
                cartItem.foodSize,
                cartItem.foodAddon
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<CartItem> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(cartItemFromDB: CartItem) {
                        //if item is alr in db, update
                        if (cartItemFromDB == cartItem) {
                            cartItemFromDB.foodExtraPrice = cartItem.foodExtraPrice
                            cartItemFromDB.foodAddon = cartItem.foodAddon
                            cartItemFromDB.foodSize = cartItem.foodSize
                            cartItemFromDB.foodQuantity =
                                cartItemFromDB.foodQuantity + cartItem.foodQuantity

                            cartDataSource.updateCart(cartItemFromDB)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : SingleObserver<Int> {
                                    override fun onSuccess(t: Int) {
                                        Toast.makeText(
                                            context,
                                            "Update Cart Success",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        EventBus.getDefault().postSticky(CountCartEvent(true))
                                    }

                                    override fun onSubscribe(d: Disposable) {

                                    }

                                    override fun onError(e: Throwable) {
                                        Toast.makeText(
                                            context,
                                            "[UPDATE CART]" + e.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                        } else {
                            //insert if it is not avail.
                            compositeDisposable.add(
                                cartDataSource.insertOrReplaceAll(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT)
                                            .show()
                                        EventBus.getDefault().postSticky(CountCartEvent(true))
                                    }, { t: Throwable ->
                                        Toast.makeText(
                                            context,
                                            "{INSERT CART}" + t.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    })
                            )
                        }
                    }

                    override fun onError(e: Throwable) {
                        if (e.message!!.contains("empty")) {
                            compositeDisposable.add(
                                cartDataSource.insertOrReplaceAll(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT)
                                            .show()
                                        EventBus.getDefault().postSticky(CountCartEvent(true))
                                    }, { t: Throwable ->
                                        Toast.makeText(
                                            context,
                                            "{INSERT CART}" + t.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    })
                            )
                        } else {
                            Toast.makeText(context, "[CART ERROR]" + e.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                })
        }

        foodDetailViewModel.foodDetail.observe(viewLifecycleOwner) {
            displayInfo(it)
            displayAllAddons()
            displayUserSelectedAddon()
        }

        foodDetailViewModel.comment.observe(viewLifecycleOwner) {
            submitRatingToFirebase(it)
        }
        return root
    }

    @SuppressLint("InflateParams")
    private fun displayAllAddons() {
        if (Common.foodSelected!!.addon.isNotEmpty()) {
            chipGroupAddon.clearCheck()
            chipGroupAddon.removeAllViews()
            searchAddon.addTextChangedListener(this)
            for (addOn in Common.foodSelected!!.addon) {
                val chip = layoutInflater.inflate(R.layout.layout_chip, null, false) as Chip
                chip.text =
                    StringBuilder(addOn.name!!).append("(+$")
                        .append(Common.formatPrice(addOn.price)).append(")").toString()
                chip.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        if (Common.foodSelected!!.addOnSelected == null)
                            Common.foodSelected!!.addOnSelected = ArrayList()
                        if (!Common.foodSelected!!.addOnSelected!!.contains(addOn))
                            Common.foodSelected!!.addOnSelected!!.add(addOn)
                    }
                    else {
                        Common.foodSelected!!.addOnSelected!!.remove(addOn)
                    }
                }
                chipGroupAddon.addView(chip)
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun displayUserSelectedAddon() {
        if (Common.foodSelected!!.addOnSelected != null && Common.foodSelected!!.addOnSelected!!.size > 0) {
            chipGroupAddonSelected.removeAllViews()
            for (addOn in Common.foodSelected!!.addOnSelected!!) {
                val chip =
                    layoutInflater.inflate(R.layout.layout_chip_with_delete, null, false) as Chip
                chip.text =
                    StringBuilder(addOn.name!!).append("(+$")
                        .append(Common.formatPrice(addOn.price)).append(")").toString()
                chip.isClickable = false

                chip.setOnCloseIconClickListener {
                    chipGroupAddonSelected.removeView(it)
                    Common.foodSelected!!.addOnSelected!!.remove(addOn)
                    calculateTotalPrice()
                }

                chipGroupAddonSelected.addView(chip)
            }
        } else
            chipGroupAddonSelected.removeAllViews()
    }

    private fun submitRatingToFirebase(comment: Comment?) {
        dialog.show()

        FirebaseDatabase.getInstance()
            .getReference(Common.COMMENT_REF)
            .child(Common.foodSelected!!.id!!)
            .push()
            .setValue(comment)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    addRatingToFood(comment!!.ratingValue)
                }
                dialog.dismiss()

            }
    }

    private fun addRatingToFood(ratingValue: Float) {
        FirebaseDatabase.getInstance()
            .getReference(Common.FOODLIST_REF)
            .child(Common.foodSelected!!.id!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val food = snapshot.getValue(Food::class.java)
                        food!!.id = Common.foodSelected!!.id

                        val sumRating = food.ratingValue + ratingValue
                        val ratingCount = food.ratingCount + 1

                        val updateData = HashMap<String, Any>()
                        updateData["ratingValue"] = sumRating
                        updateData["ratingCount"] = ratingCount

                        food.ratingCount = ratingCount
                        food.ratingValue = sumRating

                        snapshot.ref
                            .updateChildren(updateData)
                            .addOnCompleteListener { task ->
                                dialog.dismiss()
                                if (task.isSuccessful) {
                                    foodDetailViewModel.setFood(food)
                                    calculateTotalPrice()
                                    Toast.makeText(
                                        requireContext(),
                                        "Thanks for reviewing",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else
                        dialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                    dialog.dismiss()
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }

            })
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
        context?.let { context -> Glide.with(context).load(it!!.image).into(food_image) }
        food_name.text = it!!.name!!
        food_description.text = it.description!!
        food_price.text = Common.formatPrice(it.price)
        ratingBar.rating = it.ratingValue / it.ratingCount
        for (size in it.size) {
            var duplicateButton = false
            val radioButton = RadioButton(context)
            radioButton.setOnCheckedChangeListener { _, b ->
                if (b)
                    Common.foodSelected!!.sizeSelected = size
                calculateTotalPrice()
            }
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f)
            radioButton.layoutParams = params
            radioButton.text = size.name
            radioButton.tag = size.price
            for (i in 0 until rdi_group_size.childCount) {
                val radio = rdi_group_size.getChildAt(i) as RadioButton
                if (radio.text == radioButton.text && radio.tag == radioButton.tag) {
                    duplicateButton = true
                    break
                }
            }
            if (!duplicateButton)
                rdi_group_size.addView(radioButton)
        }

        if (rdi_group_size.childCount > 0) {
            val radioButton = rdi_group_size.getChildAt(0) as RadioButton
            radioButton.isChecked = true
        }
    }

    private fun calculateTotalPrice() {
        var totalPrice = Common.foodSelected!!.price

        if (Common.foodSelected!!.addOnSelected != null && Common.foodSelected!!.addOnSelected!!.size > 0) {
            for (addOn in Common.foodSelected!!.addOnSelected!!)
                totalPrice += addOn.price
        }

        totalPrice += Common.foodSelected!!.sizeSelected!!.price
        val displayPrice: Double =
            ((totalPrice * number_button.number.toInt()) * 100).roundToInt() / 100.0

        food_price.text = Common.formatPrice(displayPrice)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    @SuppressLint("InflateParams")
    override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
        chipGroupAddon.clearCheck()
        chipGroupAddon.removeAllViews()
        for (addOn in Common.foodSelected!!.addon) {
            if (addOn.name!!.lowercase().contains(charSequence.toString().lowercase())) {
                val chip = layoutInflater.inflate(R.layout.layout_chip, null, false) as Chip
                chip.text =
                    StringBuilder(addOn.name!!).append("(+$")
                        .append(Common.formatPrice(addOn.price)).append(")").toString()
                chip.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        if (Common.foodSelected!!.addOnSelected == null)
                            Common.foodSelected!!.addOnSelected = ArrayList()
                        Common.foodSelected!!.addOnSelected!!.add(addOn)
                    }
                }
                chipGroupAddon.addView(chip)
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {
    }

}