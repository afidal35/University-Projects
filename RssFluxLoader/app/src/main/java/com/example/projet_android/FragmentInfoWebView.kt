package com.example.projet_android

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment

class FragmentInfoWebView : Fragment() {

    private val TAG = "keyFragChoix"

    private lateinit var webviewInfo: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_info_web_view, container, false)
        webviewInfo = view.findViewById(R.id.webviewInfo)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webviewInfo.webViewClient = WebViewClient()
        val url = arguments?.getString(FragmentInfoWebView.key_url) ?: "https://where-am-i.org"
        webviewInfo.loadUrl(url)
    }

    interface FragmentInteractionListener{
        fun launchFrag(url :String)
    }

    companion object {

        val key_url = "KEY_URL_FragmentInfoWebView"

        @JvmStatic
        fun newInstance(url : String) =
            FragmentInfoWebView().apply {
                Log.d(TAG, "Creation d'un frag FragmentInfoWebView")
                val bundle = Bundle()
                bundle.putString(key_url,url)
                arguments = bundle
            }
    }

}