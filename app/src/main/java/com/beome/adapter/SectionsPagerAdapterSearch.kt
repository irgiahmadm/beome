package com.beome.adapter

import android.content.Context
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.beome.R
import com.beome.ui.home.following.FollowingPostFragment
import com.beome.ui.home.recent.RecentPostFragment
import com.beome.ui.search.SearchPeopleFragment
import com.beome.ui.search.SearchPostFragment

class SectionsPagerAdapterSearch(private val context: Context, fragmentManager: FragmentManager):
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private lateinit var arguments : Bundle
    @StringRes
    private val tabTitles = intArrayOf(R.string.post, R.string.people)

    override fun getCount(): Int {
        return tabTitles.size
    }

    override fun getItem(position: Int): Fragment {
        var fragment : Fragment? = null
        when(position){
            0-> fragment =
                SearchPostFragment()
            1-> fragment =
                SearchPeopleFragment()
        }
        return fragment as Fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(tabTitles[position])
    }
}