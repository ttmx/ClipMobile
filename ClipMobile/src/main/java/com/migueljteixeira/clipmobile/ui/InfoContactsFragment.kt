package com.migueljteixeira.clipmobile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.migueljteixeira.clipmobile.R
import com.migueljteixeira.clipmobile.adapters.InfoContactsListViewAdapter
import com.migueljteixeira.clipmobile.databinding.ListViewBinding

class InfoContactsFragment : BaseFragment() {
    private var mListView: ListView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ListViewBinding.inflate(inflater)
        val view: View = binding.root
        //        ButterKnife.bind(this, view);
        mListView = binding.listView
        super.bindHelperViews(view)
        val adapter = InfoContactsListViewAdapter(requireActivity())
        val resources = resources

        // Set 'internal contacts' title
        adapter.add(ContactTitle(resources.getString(R.string.info_contacts_internal_title)))

        // Add 'internal contacts'
        var contacts = resources.getStringArray(R.array.info_contacts_internal)
        run {
            var i = 0
            while (i < contacts.size) {
                adapter.add(ContactInternal(contacts[i], contacts[i + 1], contacts[i + 2]))
                i += 3
            }
        }

        // Set 'external contacts' title
        adapter.add(ContactTitle(resources.getString(R.string.info_contacts_external_title)))

        // Add 'external contacts'
        contacts = resources.getStringArray(R.array.info_contacts_external)
        var i = 0
        while (i < contacts.size) {
            adapter.add(ContactExternal(contacts[i], contacts[i + 1]))
            i += 2
        }
        mListView!!.adapter = adapter
        return view
    }

    open class ContactTitle(var name: String)
    open class ContactExternal(name: String, var phone: String?) : ContactTitle(name)
    class ContactInternal(name: String, phone: String?, var schedule: String?) :
        ContactExternal(name, phone)
}