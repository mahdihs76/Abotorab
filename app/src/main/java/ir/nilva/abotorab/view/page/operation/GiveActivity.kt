package ir.nilva.abotorab.view.page.operation

import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.google.android.material.tabs.TabLayout
import ir.nilva.abotorab.ApplicationContext
import ir.nilva.abotorab.R
import ir.nilva.abotorab.db.AppDatabase
import ir.nilva.abotorab.db.model.DeliveryEntity
import ir.nilva.abotorab.helper.gotoBarcodePage
import ir.nilva.abotorab.helper.toastError
import ir.nilva.abotorab.helper.toastSuccess
import ir.nilva.abotorab.view.page.base.BaseActivity
import ir.nilva.abotorab.webservices.MyRetrofit
import kotlinx.android.synthetic.main.activity_give.*
import kotlinx.android.synthetic.main.activity_take.bottom_navigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GiveActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_give)

        bottom_navigation.addItem(
            AHBottomNavigationItem(
                "دوربین",
                android.R.drawable.ic_menu_camera
            )
        )
        bottom_navigation.defaultBackgroundColor = Color.parseColor("#0E4C59")
        bottom_navigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
        bottom_navigation.accentColor = Color.parseColor("#00E990")
        bottom_navigation.inactiveColor = Color.parseColor("#00E990")

        bottom_navigation.setOnTabSelectedListener { _, _ ->
            gotoBarcodePage(true)
            true
        }

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager.currentItem = tab.position
                val fm = supportFragmentManager
                val ft = fm.beginTransaction()
                val count = fm.backStackEntryCount
                if (count >= 1) {
                    supportFragmentManager.popBackStack()
                }
                ft.commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // setAdapter();
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                //   viewPager.notifyAll();
            }
        })

        setStatePageAdapter()

        val extras = intent.extras
        if (extras != null && extras.containsKey("barcode")) {
            val barcode = extras.getString("barcode")
            if (!barcode.isNullOrEmpty()) {
                val data = Base64.decode(barcode.toString(), Base64.DEFAULT)
                val text = String(data, Charsets.UTF_8)
                val splitted = text.split("#")
                val firstName = splitted[0]
                val lastName = splitted[1]
                val lastPhoneDigits = splitted[2]
                val hashId = splitted[3]
                MaterialDialog(this).show {
                    title(text = "تایید")
                    message(text = "محموله به $firstName $lastName تحویل داده شود؟ ")
                    positiveButton(text = "بله") { callGiveWS(hashId) }
                    negativeButton(text = "خیر")
                }
            }
        }
    }

    private fun setStatePageAdapter() {
        val myViewPageStateAdapter = MyViewPageStateAdapter(supportFragmentManager)
        myViewPageStateAdapter.addFragment(GiveFragment(), "جست و جو")
        myViewPageStateAdapter.addFragment(RecentGivesFragment(), "تحویل های اخیر")
        pager.adapter = myViewPageStateAdapter
        tabs.setupWithViewPager(pager, true)

    }

    class MyViewPageStateAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        private val fragmentList: MutableList<Fragment> = ArrayList()
        private val fragmentTitleList: MutableList<String> = ArrayList()

        override fun getItem(position: Int): Fragment {
            return fragmentList.get(position)
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitleList.get(position)
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            fragmentTitleList.add(title)

        }
    }

    fun callGiveWS(hashId: String) = CoroutineScope(Dispatchers.Main).launch {
        try {
            val response = MyRetrofit.getService().give(hashId)
            if (response.isSuccessful) {
                toastSuccess("محموله با موفقیت تحویل داده شد")
                val delivery = response.body() ?: return@launch
                AppDatabase.getInstance().deliveryDao().insert(
                    listOf(
                    DeliveryEntity(
                        nickname = delivery.pilgrim,
                        exitedAt = delivery.exitAt,
                        hashId = delivery.hashId)
                )
                )
            } else toastError(response.toString())
        } catch (e: Exception) {
            toastError(e.message.toString())
        }
    }

}
