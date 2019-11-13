package com.example.scangun

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_po_row.view.*
import org.jetbrains.anko.async

class RecyclerAdapter(private val pos : ArrayList<PO>) : RecyclerView.Adapter<RecyclerAdapter.PoHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.PoHolder {
        val inflatedView = parent.inflate(R.layout.recyclerview_po_row, false)

        return PoHolder(inflatedView)
    }

    override fun getItemCount() = pos.size


    override fun onBindViewHolder(holder: RecyclerAdapter.PoHolder, position: Int) {
        val itemPo = pos[position]
        if (position % 2 == 1){
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"))
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#add8e6"))
        }
        holder.bindPo(itemPo)
        var sMsg = "CD#: " + itemPo.cdnum.toString() +"\n" +
                "Name: " + itemPo.name.toString() + "\n" +
                "Order #: " + itemPo.orid.toString() + "\n" +
                "PO #: " + itemPo.ponum.toString()
        var sndAct :ScanActivity = ScanActivity()
        //holder.itemView.setOnClickListener { async{sndAct.sendMessage(sMsg)}}// printer on/off


    }

    fun clearView(){
      val size = pos.size
      pos.clear()
      notifyItemRangeRemoved(0,size)

    }


    class PoHolder(v: View) : RecyclerView.ViewHolder(v),View.OnClickListener {
        //2
        private var view: View=v
        private var po: PO? = null

        //3
        init{v.setOnClickListener(this)}

        //4
        override fun onClick(v: View){
            Log.d("RecyclerView", "CLICK!")
        }

        fun bindPo(po:PO){
            this.po = po
            view.trckNum.text = po.trknum
            view.cdNum.text = po.cdnum
            view.orid.text = po.orid
            view.poNum.text = po.ponum
            view.quant.text = po.quant
            view.name.text = po.name


        }

        companion object{
            //5
            private val PO_KEY = "PO"
        }

    }
}
//1
