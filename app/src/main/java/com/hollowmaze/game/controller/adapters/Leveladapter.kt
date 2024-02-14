package com.hollowmaze.game.controller.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hollowmaze.game.controller.activities.LoadingscreenActivity
import com.hollowmaze.game.controller.helper.Level
import com.hollowmaze.game.controller.helper.LevelHelper
import com.hollowmaze.game.databinding.LevelselectItemBinding
import com.hollowmaze.game.databinding.LevelselectItemLockedBinding

const val LEVEL_UNLOCKED = 0
const val LEVEL_LOCKED = 1

class Leveladapter(private val mList: ArrayList<Level>, private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var mListener : onItemClickListener
    interface onItemClickListener{
        fun onItemClick(position : Int)
    }
    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    inner class ItemLockedViewHolder(
        private val binding: LevelselectItemLockedBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var dataItem: Level // Instanzvariable für dataItem

        fun bindLockedView(dataItem: Level) {
            this.dataItem = dataItem // Daten zuweisen

            binding.levelName.text = dataItem.name
            binding.levelImage.setImageResource(dataItem.image)
        }
    }

    inner class ItemUnlockedViewHolder(
        private val binding: LevelselectItemBinding,
    ) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        private lateinit var dataItem: Level // Instanzvariable für dataItem

        init {
            itemView.setOnClickListener(this)
        }
        @SuppressLint("SetTextI18n")
        fun bindUnlockedView(dataItem: Level) {
            this.dataItem = dataItem // Daten zuweisen
            binding.levelName.text = dataItem.name

            if (dataItem.completionTime == 0f) {
                binding.completionTime.text = "${binding.completionTime.text} No time available"
            } else {
                val completionTimeInSeconds = dataItem.completionTime / 1000

                val minutes = (completionTimeInSeconds / 60).toInt()
                val seconds = (completionTimeInSeconds % 60).toInt()

                val completionTimeString = if (minutes > 0) {
                    "$minutes:${String.format("%02d", seconds)} minutes"
                } else {
                    "$seconds seconds"
                }

                binding.completionTime.text = "${binding.completionTime.text} $completionTimeString"
            }

            binding.averageCommunityCompletionTime.text = "${binding.averageCommunityCompletionTime.text} ${dataItem.averageCommunityCompletionTime}"

            binding.levelImage.setImageResource(dataItem.image)
        }


        override fun onClick(v: View?) {
            LevelHelper.updateCurrentLevel(dataItem, context)

            // Hier startet eine neue Activity
            val context = binding.root.context
            val intent = Intent(context, LoadingscreenActivity::class.java)
            context.startActivity(intent)
            (context as Activity).finish()
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (mList[position].accessible == true) {
            return LEVEL_UNLOCKED
        } else {
            return LEVEL_LOCKED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == LEVEL_LOCKED) {
            val binding =
                LevelselectItemLockedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ItemLockedViewHolder(binding)
        } else {
            val binding =
                LevelselectItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ItemUnlockedViewHolder(binding)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == LEVEL_LOCKED) {
            (holder as ItemLockedViewHolder).bindLockedView(mList[position])
        } else {
            (holder as ItemUnlockedViewHolder).bindUnlockedView(mList[position])
        }
    }
}

