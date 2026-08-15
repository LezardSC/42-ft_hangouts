package com.fortytwo.hangouts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class ContactAdapter(
    context: Context,
    contacts: List<Contact>
): ArrayAdapter<Contact>(context, 0, contacts.toMutableList()) {

    fun update(newContacts: List<Contact>) {
        clear()
        addAll(newContacts)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)

        val contact = getItem(position)!!
        view.findViewById<TextView>(R.id.textName).text = contact.displayName
        view.findViewById<TextView>(R.id.textNumber).text = contact.phoneNumber
        PhotoUtils.bindPhoto(view.findViewById<ImageView>(R.id.imagePhoto), contact.photoUri, THUMBNAIL_SIZE)

        return view
    }

    companion object {
        private const val THUMBNAIL_SIZE = 80
    }
}