package com.example.bottombarfragmentreusedemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commitNow
import com.example.bottombarfragmentreusedemo.databinding.ActivityMainBinding
import com.example.bottombarfragmentreusedemo.ui.dashboard.DashboardFragment
import com.example.bottombarfragmentreusedemo.ui.home.HomeFragment
import com.example.bottombarfragmentreusedemo.ui.notifications.NotificationsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val fragmentsMap: MutableMap<Int, String> = mutableMapOf(
        R.id.navigation_home to HomeFragment::class.java.simpleName,
        R.id.navigation_dashboard to DashboardFragment::class.java.simpleName,
        R.id.navigation_notifications to NotificationsFragment::class.java.simpleName,
    )

    private fun getOrCreateFragment(id: Int): Fragment {
        supportFragmentManager.fragments
            .find { it.tag == fragmentsMap[id] }
            ?.let { return it }

        // If the fragment is not found, create a new instance.
        return when (id) {
            R.id.navigation_home -> HomeFragment()
            R.id.navigation_dashboard -> DashboardFragment()
            R.id.navigation_notifications -> NotificationsFragment()
            else -> throw IllegalArgumentException("Invalid id")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val fm = supportFragmentManager

        Log.d(TAG, "after onCreate fm.fragments = ${fm.fragments.strings()}")

        fm.commitNow(true) {
            val selectedId = savedInstanceState?.getInt("selectedFragmentId")
            val fragmentToShow = getOrCreateFragment(selectedId ?: R.id.navigation_home)

            addAndShow(fragmentToShow)
        }
        Log.d(TAG, "after adding home fm.fragments = ${fm.fragments.strings()}")

        navView.setOnItemSelectedListener { menuItem ->
            fm.commitNow(true) {
                val fragmentToShow = getOrCreateFragment(menuItem.itemId)
                addAndShow(fragmentToShow)

                // Hide the other fragments
                fm.fragments.forEach {
                    if (it != fragmentToShow) hide(it)
                }
            }
            return@setOnItemSelectedListener true
        }

        navView.setOnItemReselectedListener {
            // Do nothing, if not handled, it will create a new instance of the fragment by default.
        }
    }

    private fun FragmentTransaction.addAndShow(fragment: Fragment) {
        if (!fragment.isAdded) {
            add(R.id.fragmentContainer, fragment, fragment.javaClass.simpleName)
        }
        show(fragment)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val last = binding.navView.selectedItemId

        outState.putInt("selectedFragmentId", last)
    }

    private fun Collection<Fragment>.strings(): List<String> = map {
        it.javaClass.simpleName + "{${Integer.toHexString(System.identityHashCode(it))}}"
    }
}
