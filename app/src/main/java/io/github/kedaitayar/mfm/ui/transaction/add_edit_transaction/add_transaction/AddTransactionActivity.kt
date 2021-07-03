package io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.add_transaction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.fragment.app.commit
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.ActivityAddTransactionBinding

private const val TAG = "AddTransactionActivity"

@AndroidEntryPoint
class AddTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)

        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(binding.addTransactionRoot)
            setAllContainerColors(MaterialColors.getColor(binding.root, R.attr.colorSurface))
            pathMotion = MaterialArcMotion()
            duration = 300
            interpolator = FastOutSlowInInterpolator()
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
        }

        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(binding.addTransactionRoot)
            setAllContainerColors(MaterialColors.getColor(binding.root, R.attr.colorSurface))
            pathMotion = MaterialArcMotion()
            duration = 250
            interpolator = FastOutSlowInInterpolator()
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
        }

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.add_transaction_container_view, AddTransactionFragment())
            }
        }
    }
}