package com.beome.adapter

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.beome.R
import com.beome.ui.home.following.FollowingPostFragment
import com.beome.ui.home.recent.RecentPostFragment

class SectionsPagerAdapterHome(private val context: Context, fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    @StringRes
    private val tabTitles = intArrayOf(R.string.recent, R.string.following)

    override fun getItem(position: Int): Fragment {
        var fragment : Fragment? = null
        when(position){
            0-> fragment =
                RecentPostFragment()
            1-> fragment =
                FollowingPostFragment()
        }
        return fragment as Fragment
    }

    override fun getCount(): Int {
        return tabTitles.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return context.resources.getString(tabTitles[position])
    }

}