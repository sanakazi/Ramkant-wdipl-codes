package com.debalink;

import com.debalink.utils.Contant;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ViewDocumentFragment extends Fragment {
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.view_document, null);
		WebView wv = (WebView) view.findViewById(R.id.webView1);

		wv.loadUrl(Contant.DOC_VIEWER_URL + Contant.DISCUSSION_PIC_URL+getArguments().getString("doc_name"));

		WebSettings webSettings = wv.getSettings();
		webSettings.setJavaScriptEnabled(true);

		wv.setWebViewClient(new WebViewClient());
		return view;
	}
}
