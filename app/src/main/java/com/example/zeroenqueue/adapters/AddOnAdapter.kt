package com.example.zeroenqueue.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.AddOn
import com.example.zeroenqueue.classes.Size
import com.example.zeroenqueue.common.Common

class AddOnAdapter(
    var context: Context,
    var addOnList: ArrayList<AddOn>
) : RecyclerView.Adapter<AddOnAdapter.AddOnViewHolder>() {

    inner class AddOnViewHolder(val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var addOnName: TextView = itemView.findViewById(R.id.addOnName)
        var addOnPrice: TextView = itemView.findViewById(R.id.addOnPrice)
        var editAddOn: ImageView = itemView.findViewById(R.id.editAddOn)
        var deleteAddOn: ImageView = itemView.findViewById(R.id.deleteAddOn)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddOnAdapter.AddOnViewHolder {
        return AddOnViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_addon_item, parent, false)
        )
    }

    override fun getItemCount(): Int = addOnList.size

    override fun onBindViewHolder(
        holder: AddOnAdapter.AddOnViewHolder,
        position: Int
    ) {
        holder.addOnName.text = addOnList[position].name
        holder.addOnPrice.text = StringBuilder("+ $").append(Common.formatPrice(addOnList[position].price))

        holder.editAddOn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val itemView = LayoutInflater.from(context).inflate(R.layout.layout_add_addon, null)
            val addAddOnText = itemView.findViewById<TextView>(R.id.addAddOnText)
            val addOnName = itemView.findViewById<EditText>(R.id.addOnName)
            val addOnPrice = itemView.findViewById<EditText>(R.id.addOnPrice)

            addAddOnText.text = "Edit Add-On"
            addOnName.setText(holder.addOnName.text)
            addOnPrice.setText(Common.formatPrice(addOnList[position].price))

            builder.setNegativeButton("CANCEL") { dialogInterface, _ -> dialogInterface.dismiss() }
            builder.setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                val newAddOn = AddOn()
                newAddOn.name = addOnName.text.toString()
                newAddOn.price = addOnPrice.text.toString().toDouble()
                addOnList[position] = newAddOn
                notifyItemChanged(position)
            }

            builder.setView(itemView)
            val uploadDialog = builder.create()
            uploadDialog.show()
        }

        holder.deleteAddOn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Are you sure you want to delete this add-on?")
            builder.setNegativeButton("CANCEL") { dialogInterface, _ -> dialogInterface.dismiss() }
            builder.setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                addOnList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount)
            }

            val uploadDialog = builder.create()
            uploadDialog.show()
        }
    }

    fun clearList() {
        addOnList = ArrayList()
    }
}