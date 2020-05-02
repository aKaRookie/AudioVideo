package com.fhl.test.fragment

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log

import com.fhl.test.R
import com.fhl.test.base.BaseFragment
import com.fhl.test.open.GLRender
import com.fhl.test.open.isSupportEs2
import kotlinx.android.synthetic.main.fragment_a.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
val TAG1: String? = AFragment::class.java.simpleName

class AFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    companion object {

        val TAG: String? = AFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(param1: String? = null, param2: String? = null) =
            AFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    var isRenderSet = true
    override fun getResLayoutId(): Int = R.layout.fragment_a
    override fun init() {
        if (isSupportEs2(context!!).also { Log.d(TAG, "是否支持ES 2.0: $it") }) {
            glSurface.setEGLContextClientVersion(2)
        }
        glSurface.setRenderer(GLRender(context!!))
        glSurface.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        isRenderSet = true

    }

    override fun onPause() {
        super.onPause()
        if (isRenderSet) {
            glSurface.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isRenderSet) {
            glSurface.onResume()
        }
    }
}
