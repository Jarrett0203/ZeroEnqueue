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
import com.example.zeroenqueue.classes.Size
import com.example.zeroenqueue.common.Common

class SizeAdapter(
    var context: Context,
    var sizeList: ArrayList<Size>
) : RecyclerView.Adapter<SizeAdapter.SizeViewHolder>() {

    inner class SizeViewHolder(val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var sizeName: TextView = itemView.findViewById(R.id.sizeName)
        var sizePrice: TextView = itemView.findViewById(R.id.sizePrice)
        var editSize: ImageView = itemView.findViewById(R.id.editSize)
        var deleteSize: ImageView = itemView.findViewById(R.id.deleteSize)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SizeAdapter.SizeViewHolder {
        return SizeViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_size_item, parent, false)
        )
    }

    override fun getItemCount(): Int = sizeList.size

    override fun onBindViewHolder(
        holder: SizeAdapter.SizeViewHolder,
        position: Int
    ) {
        holder.sizeName.text = sizeList[position].name
        holder.sizePrice.text = StringBuilder("+ $").append(Common.formatPrice(sizeList[position].price))

        holder.editSize.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val itemView = LayoutInflater.from(context).inflate(R.layout.layout_add_size, null)
            val addSizeText = itemView.findViewById<TextView>(R.id.addSizeText)
            val sizeName = itemView.findViewById<EditText>(R.id.sizeName)
            val sizePrice = itemView.findViewById<EditText>(R.id.sizePrice)

            addSizeText.text = "Edit Size"
            sizeName.setText(holder.sizeName.text)
            sizePrice.setText(Common.formatPrice(sizeList[position].price))

            builder.setNegativeButton("CANCEL") { dialogInterface, _ -> dialogInterface.dismiss() }
            builder.setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                val newSize = Size()
                newSize.name = sizeName.text.toString()
                newSize.price = sizePrice.text.toString().toDouble()
                sizeList[position] = newSize
                notifyItemChanged(position)
            }

            builder.setView(itemView)
            val uploadDialog = builder.create()
            uploadDialog.show()
        }

        holder.deleteSize.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Are you sure you want to delete this size?")
            builder.setNegativeButton("CANCEL") { dialogInterface, _ -> dialogInterface.dismiss() }
            builder.setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                sizeList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount)
            }

            val uploadDialog = builder.create()
            uploadDialog.show()
        }
    }

    fun clearList() {
        sizeList = ArrayList()
    }
}