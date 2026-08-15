package com.fortytwo.hangouts

import android.content.Context
import android.icu.text.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.Date

class MessageAdapter(
    context: Context,
    private var messages: List<Message>
): BaseAdapter() {
    private val inflater = LayoutInflater.from(context)

    fun update(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    override fun getCount() = messages.size
    override fun getItem(position: Int) = messages[position]
    override fun getItemId(position: Int) = messages[position].id

    override fun getViewTypeCount() = 2
    override fun getItemViewType(position: Int) = if (messages[position].direction == Direction.SENT) 0 else 1

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val message = messages[position]
        val layoutRes = if (message.direction == Direction.SENT)
            R.layout.item_message_sent else R.layout.item_message_received

        val view = convertView ?: inflater.inflate(layoutRes, parent, false)
        view.findViewById<TextView>(R.id.textBody).text = message.body
        view.findViewById<TextView>(R.id.textTime).text =
            DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(message.timestamp))

        return view
    }
}