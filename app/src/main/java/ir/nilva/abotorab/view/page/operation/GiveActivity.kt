package ir.nilva.abotorab.view.page.operation

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.google.android.material.tabs.TabLayout
import ir.nilva.abotorab.R
import ir.nilva.abotorab.db.AppDatabase
import ir.nilva.abotorab.db.model.DeliveryEntity
import ir.nilva.abotorab.db.model.OfflineDeliveryEntity
import ir.nilva.abotorab.helper.gotoBarcodePage
import ir.nilva.abotorab.helper.toastError
import ir.nilva.abotorab.helper.toastSuccess
import ir.nilva.abotorab.view.page.base.BaseActivity
import ir.nilva.abotorab.webservices.callWebserviceWithFailure
import ir.nilva.abotorab.webservices.getServices
import kotlinx.android.synthetic.main.activity_give.*
import kotlinx.android.synthetic.main.give_verification.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import permissions.dispatcher.*

@RuntimePermissions
class GiveActivity : BaseActivity() {

    @NeedsPermission(Manifest.permission.CAMERA)
    fun scanVisitCard() {
        gotoBarcodePage(true)
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    fun showDialogBeforeCameraPermission(request: PermissionRequest) {
        MaterialDialog(this).show {
            message(R.string.camera_permission_message)
            positiveButton(R.string.permisson_ok) { request.proceed() }
            negativeButton(R.string.permission_deny) { request.cancel() }
        }
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    fun onCameraDenied() {
        toast(getString(R.string.no_camera_permission))
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    fun onCameraNeverAskAgain() {
        toast(getString(R.string.no_camera_permission))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

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
            scanVisitCardWithPermissionCheck()
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
                try {
                    val text = String(
                        Base64.decode(
                            barcode.toString(),
                            Base64.DEFAULT
                        ),
                        Charsets.UTF_8
                    ).split("#")
                    val hashId = text[2]
                    val view = layoutInflater.inflate(R.layout.give_verification, null)
                    view.nickName.text = text[0]
                    view.county.text = "از کشور ${text[1]}"
                    view.phoneNumber.text = "شماره تلفن‌ : ${text[3]}" + "********"
                    view.cellCode.text = "شماره قفسه : ${text[4]}"
                    MaterialDialog(this).show {
                        customView(view = view)
                        title(text = "تایید")
                        positiveButton(text = "بله") { callGiveWS(hashId) }
                        negativeButton(text = "خیر")
                    }
                } catch (e: Exception) {
                    toastError("بارکد اسکن شده معتبر نمیباشد")
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
        callWebserviceWithFailure({ getServices().give(hashId) }) {
            toastSuccess("پس از برقراری ارتباط با سرور گزارش میشود")
            cacheHashId(hashId)
        }?.run {
            toastSuccess("محموله با موفقیت تحویل داده شد")
            AppDatabase.getInstance().deliveryDao().insertAndDeleteOther(
                DeliveryEntity(
                    nickname = pilgrim.name,
                    country = pilgrim.country,
                    phone = pilgrim.phone,
                    exitedAt = exitAt,
                    hashId = hashId
                )
            )
        }
    }

    private fun cacheHashId(hashId: String) = CoroutineScope(Dispatchers.IO).launch {
        AppDatabase.getInstance().offlineDeliveryDao().insert(OfflineDeliveryEntity(hashId))
    }

}
