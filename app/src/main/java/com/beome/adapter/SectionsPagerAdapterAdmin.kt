package com.beome.adapter

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.beome.R
import com.beome.ui.admin.ReportedAccountFragment
import com.beome.ui.admin.ReportedFeedbackFragment
import com.beome.ui.admin.ReportedPostFragment

class SectionsPagerAdapterAdmin(private val context: Context, fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    @StringRes
    private val tabTitles = intArrayOf(R.string.post, R.string.account, R.string.feedback)

    override fun getItem(position: Int): Fragment {
        var fragment : Fragment? = null
        when(position){
            0-> fragment =
                ReportedPostFragment()
            1-> fragment =
                ReportedAccountFragment()
            2 ->fragment =
                ReportedFeedbackFragment()
        }
        return fragment as Fragment
    }

    override fun getCount(): Int {
        return tabTitles.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(tabTitles[position])
    }

}