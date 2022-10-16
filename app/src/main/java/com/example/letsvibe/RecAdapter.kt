package com.example.letsvibe

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecAdapter(private var songList : ArrayList<Songs>,
                 private val listener: OnItemClickListener
) : RecyclerView.Adapter<RecAdapter.myViewHolder>() {

    inner class myViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView),
    OnClickListener{
        val songName : TextView = itemView.findViewById(R.id.custom_songName)
        val imageRes : ImageView = itemView.findViewById(R.id.custom_image)
        val singer : TextView = itemView.findViewById(R.id.custom_singerName)
        val viewer : LinearLayout = itemView.findViewById(R.id.recID)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position : Int = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    public fun setFilteredList(arrayLi : ArrayList<Songs>){
        this.songList = arrayLi
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cutome_list_view,parent,false)
        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentItem = songList[position]
        holder.songName.text = currentItem.Name
        holder.singer.text = currentItem.singerName
        Glide.with(holder.imageRes.context).load(songList[position].imageURL).into(holder.imageRes)

        holder.viewer.startAnimation(AnimationUtils.loadAnimation(holder.imageRes.context,R.anim.adapter_loader))

    }

    override fun getItemCount(): Int {
        return songList.size
    }
}