package com.example.zeroenqueue.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.asksira.loopingviewpager.LoopingPagerAdapter
import com.bumptech.glide.Glide
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.Recommended
import com.example.zeroenqueue.eventBus.RecommendedClick
import org.greenrobot.eventbus.EventBus

class RecommendedAdapter(itemList:List<Recommended>, isInfinite:Boolean)
    :LoopingPagerAdapter<Recommended>(itemList, isInfinite) {
    override fun bindView(convertView: View, listPosition: Int, viewType: Int) {
        val imageView = convertView.findViewById<ImageView>(R.id.img_recommend)
        val textView = convertView.findViewById<TextView>(R.id.txt_recommend)

        Glide.with(convertView.context).load(itemList?.get(listPosition)?.image).into(imageView)
        textView.text = itemList?.get(listPosition)?.name

        convertView.setOnClickListener{
            EventBus.getDefault().postSticky(RecommendedClick(true, itemList!![listPosition]))
        }
    }

    override fun inflateView(viewType: Int, container: ViewGroup, listPosition: Int): View {
        return LayoutInflater.from(container.context).inflate(R.layout.layout_recommended_food_items, container, false)
    }


}