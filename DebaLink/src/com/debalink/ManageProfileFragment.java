package com.debalink;

import com.debalink.utils.Fonts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class ManageProfileFragment extends Fragment  implements View.OnClickListener{
	
	private Button btnManageDiscussions, btnManagePublications, btnEditProfile;
	private Fragment previousFragment;
	private ImageButton strip3;
	private int refreshSelected;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.manage, null);
		
		btnManageDiscussions = (Button)view. findViewById(R.id.btnManageDiscussions);
		btnManagePublications = (Button) view.findViewById(R.id.btnManagePublications);
		btnEditProfile = (Button) view.findViewById(R.id.btnEditProfile);
		strip3 = (ImageButton) view.findViewById(R.id.strip3);
		btnManageDiscussions.setOnClickListener(this);
		btnEditProfile.setOnClickListener(this);
		btnManagePublications.setOnClickListener(this);

		btnEditProfile.setTypeface(Fonts.getRobotoMedium(getActivity()));
		btnManageDiscussions.setTypeface(Fonts.getRobotoMedium(getActivity()));
		btnManagePublications.setTypeface(Fonts.getRobotoMedium(getActivity()));
		btnEditProfile.performClick();
		return view;
	}
	
	@Override
	public void onClick(View v) {

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (previousFragment != null) {
			ft.hide(previousFragment);
		}

		int id = v.getId();

		refreshSelected = id;
		btnManageDiscussions.setSelected(false);
		btnEditProfile.setSelected(false);
		btnManagePublications.setSelected(false);
		v.setSelected(true);
		switch (id) {
		case R.id.btnManageDiscussions:
			strip3.setImageResource(R.drawable.discussion_line);
			Fragment discussionFragment = new ManageDiscussionsFragment();
			ft.replace(R.id.relativeLayoutFragment, discussionFragment, "discussion");
			// ft.show(discussionFragment);
			ft.commit();
			previousFragment = discussionFragment;

			break;

		case R.id.btnManagePublications:
			strip3.setImageResource(R.drawable.pubheading);
			Fragment publicationsFragment = new ManagePublicationsFragment();
			ft.replace(R.id.relativeLayoutFragment, publicationsFragment, "publications");
		
			ft.commit();

			previousFragment = publicationsFragment;
			break;

		case R.id.btnEditProfile:
			strip3.setImageResource(R.drawable.headline_line);
			Fragment headlinesFragment = new EditProfileFragment();
			ft.replace(R.id.relativeLayoutFragment, headlinesFragment, "headlines");
			// ft.show(headlinesFragment);
			ft.commit();

			previousFragment = headlinesFragment;
			break;
		}
	}

}
