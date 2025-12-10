package com.vsk.dr

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vsk.dr.tech.Fun
import com.vsk.dr.ui.theme.Dr___Theme
import timber.log.Timber


class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel
    private var counter: Int = 0
    private val ctx = this

    private var masterItemId: Int = 0

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val appsInfo = viewModel.getInstalledApps().value
        if (appsInfo != null) {
            Timber.d("installed applications size: ${appsInfo.size}")
        }
        enableEdgeToEdge()

        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                Dr___Theme {
                    val navController = rememberNavController()
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(text = getString(R.string.app_name))
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        if (counter > 0) {
                                            counter--
                                            navController.popBackStack()
                                        } else {
                                            finishAndRemoveTask()
                                        }
                                    }) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            ""
                                        )
                                    }
                                }
                            )
                        }, modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .padding(innerPadding),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = "ShowApplications"
                            ) {
                                composable("ShowApplications") {
                                    if (appsInfo != null) {
                                        ShowApplications(
                                            ctx,
                                            navController = navController,
                                            viewModel = viewModel,
                                            appInfoList = appsInfo
                                        )
                                    }
                                }
                                composable(
                                    "ItemComposable",
                                ) {
                                    if (appsInfo != null) {
                                        ItemComposable(
                                            navController = navController,
                                            viewModel = viewModel,
                                            appInfoList = appsInfo
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    private fun ItemComposable(
        navController: NavHostController,
        viewModel: MainViewModel,
        appInfoList: List<PackageInfo>
    ) {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        val longVersionCode = PackageInfoCompat.getLongVersionCode(pInfo)
        val versionCode = longVersionCode.toInt() // avoid huge version numbers and you will be ok
        counter++
        Scaffold(modifier = Modifier.padding(16.dp), content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val appInfo: ApplicationInfo? = appInfoList[masterItemId].applicationInfo
//                val drawable : Drawable = packageManager.getApplicationIcon(
//                    appInfoList[masterItemId].packageName
//                )
//                        as BitmapDrawable).bitmap
                Text(fontWeight = FontWeight.Bold, text = getString(R.string.title) + ":")
                appInfo?.let {
                    Text(appInfo.sourceDir)
                    Text(packageManager.getApplicationLabel(it).toString())
                    if (it.className != null) {
                        val icon: ImageBitmap? = Fun.getAppIcon(
                            packageManager,
                            it.className
                        ) as ImageBitmap?
                       icon?.let {image ->
                           Image(
                               bitmap = image,
                               contentDescription = "application logo"
                           )
                       }
                    }
                    Text(packageManager.getApplicationLabel(it).toString())
                }

                Text(
                    fontWeight = FontWeight.Bold, text = getString(
                        R.string.version_code
                    ) + ":"
                )

                Text(versionCode.toString())
                Text(fontWeight = FontWeight.Bold, text = getString(R.string.version_name) + ":")
                Text(viewModel.getVersionName(appInfoList[masterItemId].packageName)!!)
                Text(fontWeight = FontWeight.Bold, text = getString(R.string.package_name) + ":")
                Text(appInfoList[masterItemId].packageName)
                Button(onClick = { navController.popBackStack() }) {
                    Text("<--", fontSize = 25.sp)
                }
                Button(onClick = {
                    appInfoList[masterItemId].packageName.let {
                        viewModel.openApp(this@MainActivity, it)
                    }
                }) {
                    Text("open", fontSize = 25.sp)
                }

                Fun.getIconFromPackageName(appInfoList[masterItemId].packageName, ctx)
            }
        })
    }

    @Composable
    fun ShowApplications(
        ctx: Context,
        navController: NavHostController,
        viewModel: MainViewModel,
        appInfoList: List<PackageInfo>
    ) {
        LazyColumn {
            items(appInfoList.size) { item ->
                masterItemId = item
                val interactionSource = remember { MutableInteractionSource() }
                viewModel.getApplicationName(appInfoList[item].applicationInfo)?.let {
                    val appInfo: ApplicationInfo? = appInfoList[masterItemId].applicationInfo
                    Text(
                        text = packageManager.getApplicationLabel(appInfo!!).toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .padding(16.dp)
                            .clickable(
                                onClick = {
                                    masterItemId = item
                                    Toast.makeText(
                                        ctx,
                                        it,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("ItemComposable")
                                },
                                interactionSource = interactionSource,
                                indication = ripple()
                            ),
                        fontSize = 16.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = appInfo.dataDir,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .padding(16.dp)
                            .clickable(
                                onClick = {
                                    masterItemId = item
                                    Toast.makeText(
                                        ctx,
                                        it,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("ItemComposable")
                                },
                                interactionSource = interactionSource,
                                indication = ripple()
                            ),
                        fontSize = 16.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}