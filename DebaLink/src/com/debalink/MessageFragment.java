package com.debalink;

import com.debalink.utils.Fonts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MessageFragment extends Fragment implements View.OnClickListener{
	private Button btnCompose,btnInbox,btnOutbox;
	private Fragment previousFragment;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.message,null);
		btnCompose=(Button)view.findViewById(R.id.btnCompose);
		btnInbox=(Button)view.findViewById(R.id.btnInbox);
		btnOutbox=(Button)view.findViewById(R.id.btnOutbox);
		
		btnCompose.setOnClickListener(this);
		btnInbox.setOnClickListener(this);
		btnOutbox.setOnClickListener(this);
		btnCompose.performClick();
		
		btnCompose.setTypeface(Fonts.getRobotoMedium(getActivity()));
		btnInbox.setTypeface(Fonts.getRobotoMedium(getActivity()));
		btnOutbox.setTypeface(Fonts.getRobotoMedium(getActivity()));
		
		return view;
	}
	
	@Override
	public void onClick(View v) {

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (previousFragment != null) {
			ft.hide(previousFragment);
		}

		int id = v.getId();
		
		btnCompose.setSelected(false);
		btnInbox.setSelected(false);
		btnOutbox.setSelected(false);

		switch (id) {
		case R.id.btnCompose:

			Fragment composeFragment = new ComposeFragment();
			ft.replace(R.id.fragment, composeFragment, "compose");
			// ft.show(discussionFragment);
			ft.commit();
			previousFragment = composeFragment;
			btnCompose.setSelected(true);
			
			
			break;

		case R.id.btnInbox:

			Fragment inboxFragment = new InboxFragment();
			ft.replace(R.id.fragment, inboxFragment, "inbox");
			// ft.replace(publicationsFragment);
			ft.commit();
			previousFragment = inboxFragment;
			btnInbox.setSelected(true);
			
			break;

		case R.id.btnOutbox:

			Fragment outboxFragment = new OutboxFragment();
			ft.replace(R.id.fragment, outboxFragment, "outbox");
			// ft.show(headlinesFragment);
			ft.commit();
			previousFragment = outboxFragment;
			btnOutbox.setSelected(true);
			break;

		}

	}

}
