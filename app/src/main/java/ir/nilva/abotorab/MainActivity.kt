package ir.nilva.abotorab

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import com.ramotion.circlemenu.CircleMenuView
import ir.nilva.abotorab.helper.gotoCabinetListPage
import ir.nilva.abotorab.helper.gotoGivePage
import ir.nilva.abotorab.helper.gotoReportPage
import ir.nilva.abotorab.helper.gotoTakePage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initCircularMenu()
    }

    private fun initCircularMenu() {
        circularLayout.eventListener = object : CircleMenuView.EventListener() {
            override fun onButtonClickAnimationEnd(view: CircleMenuView, buttonIndex: Int) {
                super.onButtonClickAnimationEnd(view, buttonIndex)
                getMenuItem(buttonIndex).action(this@MainActivity)
            }
        }

        Handler().postDelayed({
            circularLayout.open(true)
        }, 500)
    }

}

fun MenuItem?.action(activity: Activity) {
    when (this) {
        MenuItem.CABINET_GIVE -> activity.gotoGivePage()
        MenuItem.CABINET_TAKE -> activity.gotoTakePage()
        MenuItem.CABINET_INIT -> activity.gotoCabinetListPage()
        MenuItem.CABINET_REPORT -> activity.gotoReportPage()
    }
}