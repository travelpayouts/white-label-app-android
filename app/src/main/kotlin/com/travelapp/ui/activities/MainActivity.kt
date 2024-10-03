package com.travelapp.ui.activities

import android.Manifest
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.squareup.seismic.ShakeDetector
import com.travelapp.BuildConfig
import com.travelapp.R
import com.travelapp.config.AppTabsList
import com.travelapp.databinding.ActivityMainBinding
import com.travelapp.debugmenu.DebugMenu
import com.travelapp.sdk.internal.core.config.providers.navigation.NavigationBarItem
import com.travelapp.sdk.internal.ui.base.BaseActivity
import com.travelapp.sdk.internal.ui.utils.BottomBarVisibilityHandler
import com.travelapp.sdk.internal.ui.utils.KeyboardVisibilityListener
import com.travelapp.sdk.internal.ui.utils.TabSelector
import com.travelapp.sdk.internal.ui.utils.doOnBackStackChanged
import com.travelapp.sdk.internal.ui.utils.dp
import com.travelapp.sdk.internal.ui.utils.setupWithNavController
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import timber.log.Timber

class MainActivity : BaseActivity(R.layout.activity_main), BottomBarVisibilityHandler, TabSelector {

    private val activeTabs by lazy {
        AppTabsList.get().map { tab ->
            NavigationBarItem(
                idRes = tab.idRes,
                destinationRes = tab.graphId,
                iconRes = tab.icon,
                titleRes = tab.title,
            )
        }
    }

    private val binding by viewBinding(ActivityMainBinding::bind, R.id.container)

    private var currentNavController: StateFlow<NavController?>? = null

    override val bottomNavViewHeight: Int
        get() = binding.bottomBar.height


    private var shakeDetector: ShakeDetector? = null

    private val permissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Timber.i("Permission granted = $isGranted")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initBottomNavigationBar()
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }

        initViews()
        initKeyboardVisibilityListener()
        initShakeDetector()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionRequestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onResume() {
        super.onResume()
        shakeDetector?.start(
            getSystemService(SENSOR_SERVICE) as SensorManager,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onPause() {
        super.onPause()
        shakeDetector?.stop()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    override fun getBottomBarHeight(): Int {
        return binding.bottomBar.height - binding.bottomBar.paddingBottom
    }

    override fun toggleBottomBar(visible: Boolean) = with(binding) {
        if (bottomBar.isVisible && visible) return
        if (!bottomBar.isVisible && !visible) return

        if (visible) {
            bottomBar.translationY = bottomBar.height.toFloat()
            bottomBar
                .animate()
                .translationYBy(-bottomBar.height.toFloat())
                .setDuration(150)
                .withStartAction {
                    bottomBar.isVisible = true
                    navHostContainer.setPadding(0, 0, 0, 80.dp.toInt())
                }
                .start()
        } else {
            bottomBar
                .animate()
                .translationY(bottomBar.height.toFloat())
                .setDuration(150)
                .withStartAction {
                    navHostContainer.setPadding(0, 0, 0, 0)
                }
                .withEndAction {
                    bottomBar.isVisible = false
                }
                .start()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    private fun initViews() = with(binding) {

    }

    private fun initShakeDetector() {
        if (BuildConfig.DEBUG) {
            shakeDetector = ShakeDetector {
                Timber.w("shake detected")

                openDebugFragment()
            }
        }
    }

    private fun initBottomNavigationBar() = with(binding) {
        activeTabs
            .forEachIndexed { index, item ->
                bottomBar.menu.add(
                    Menu.NONE,
                    item.idRes,
                    index,
                    item.titleRes
                ).apply {
                    this.setIcon(item.iconRes)
                }
            }
    }

    private fun setupBottomNavigationBar() = with(binding) {
        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomBar.setupWithNavController(
            tabs = activeTabs,
            fragmentManager = supportFragmentManager,
            containerId = R.id.navHostContainer,
            intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
        currentNavController = controller
    }

    private fun initKeyboardVisibilityListener() {
        KeyboardVisibilityListener(binding.root) { isOpen ->
            toggleBottomBar(!isOpen)
        }
    }

    /**
     * Select tab in bottom nav view and navigate to destination inside navController of selected tab
     */
    override fun selectTab(tab: Int, dest: Int, args: Bundle?) {
        val selectedNavController = currentNavController ?: return

        selectedNavController
            .drop(1)
            .filterNotNull()
            .take(1)
            .onEach {
                doOnBackStackChanged(supportFragmentManager) {
                    Timber.w("navController: ${it.findDestination(dest)}")
                    it.navigate(dest, args)
                }
            }
            .launchIn(lifecycleScope)

        binding.bottomBar.selectedItemId = tab
    }


    /**
     * Select first tab in bottom nav view
     */
    override fun selectFirstTab() {
        val firstBottomItemId = binding.bottomBar.menu.getItem(0).itemId
        binding.bottomBar.selectedItemId = firstBottomItemId
    }

    private fun openDebugFragment() {
        DebugMenu.open(supportFragmentManager, R.id.container)
    }

}