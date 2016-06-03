package com.debalink;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.debalink.interfaces.MenuCallback;
import com.debalink.services.CountService;
import com.debalink.utils.Fonts;

public class HomeActivity extends ActionBarActivity implements View.OnClickListener, MenuCallback {
	// private ActionBar actionBar;
	private Button btnDiscussion, btnPublication, btnHeadlines;
	private Fragment previousFragment;
	private ImageButton strip3;
	private int refreshSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		// intiHeader();

		new Header().showMenu(this, View.VISIBLE);

		btnDiscussion = (Button) findViewById(R.id.btnDescussion);
		btnHeadlines = (Button) findViewById(R.id.btnHeadlines);
		btnPublication = (Button) findViewById(R.id.btnPublications);
		strip3 = (ImageButton) findViewById(R.id.strip3);
		btnDiscussion.setOnClickListener(this);
		btnHeadlines.setOnClickListener(this);
		btnPublication.setOnClickListener(this);

		btnDiscussion.setTypeface(Fonts.getRobotoMedium(getBaseContext()));
		btnHeadlines.setTypeface(Fonts.getRobotoMedium(getBaseContext()));
		btnPublication.setTypeface(Fonts.getRobotoMedium(getBaseContext()));

		Intent serviceIntent = new Intent(this, CountService.class);
		startService(serviceIntent);

		switchScreens(getIntent());

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		switchScreens(intent);
	}

	@Override
	public void onClick(View v) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (previousFragment != null) {
			ft.hide(previousFragment);
		}

		int id = v.getId();

		refreshSelected = id;
		btnDiscussion.setSelected(false);
		btnHeadlines.setSelected(false);
		btnPublication.setSelected(false);
		v.setSelected(true);
		switch (id) {
		case R.id.btnDescussion:
			strip3.setImageResource(R.drawable.discussion_line);
			Fragment discussionFragment = new DiscussionFragment();
			ft.replace(R.id.relativeLayout, discussionFragment, "discussion");
			// ft.show(discussionFragment);
			ft.commit();
			previousFragment = discussionFragment;

			break;

		case R.id.btnPublications:
			strip3.setImageResource(R.drawable.pubheading);
			Fragment publicationsFragment = new PublicationsFragment();
			ft.replace(R.id.relativeLayout, publicationsFragment, "publications");
			// ft.replace(publicationsFragment);
			ft.commit();

			previousFragment = publicationsFragment;
			break;

		case R.id.btnHeadlines:
			strip3.setImageResource(R.drawable.headline_line);
			Fragment headlinesFragment = new HeadlinesFragment();
			ft.replace(R.id.relativeLayout, headlinesFragment, "headlines");
			// ft.show(headlinesFragment);
			ft.commit();

			previousFragment = headlinesFragment;
			break;
		}
	}

	@Override
	public void showPublicPublications() {

	}

	@Override
	public void showPublicDiscussions() {

	}

	private void switchScreens(Intent intent) {
		if (intent.hasExtra("discussion")) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			if (previousFragment != null) {
				ft.hide(previousFragment);
			}

			Fragment discussionFragment = new PublicDiscussionFragment();
			ft.replace(R.id.inner_content, discussionFragment, "public_discussion");

			strip3.setImageResource(R.drawable.discussion_line);
			ft.commit();
			previousFragment = discussionFragment;
			strip3.setImageResource(R.drawable.discussion_line);
		} else if (intent.hasExtra("publication")) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			if (previousFragment != null) {
				ft.hide(previousFragment);
			}

			Fragment publicationFragment = new PublicPublicationFragment();
			ft.replace(R.id.inner_content, publicationFragment, "public_publication");

			ft.commit();
			previousFragment = publicationFragment;
			strip3.setImageResource(R.drawable.pubheading);

		} else if (intent.hasExtra("feeds")) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			if (previousFragment != null) {
				ft.hide(previousFragment);
			}

			Fragment feedsFragment = new FeedsFragment();
			ft.replace(R.id.inner_content, feedsFragment, "feeds");

			ft.commit();
			previousFragment = feedsFragment;
			strip3.setImageResource(R.drawable.feed_line);
		} else if (intent.hasExtra("subscribers_list")) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			if (previousFragment != null) {
				ft.hide(previousFragment);
			}

			Fragment subscribersList = new SubscribersListFragment();
			ft.replace(R.id.inner_content, subscribersList, "subscribersList");

			ft.commit();
			previousFragment = subscribersList;
			strip3.setImageResource(R.drawable.subsribe_line);
		} else if (intent.hasExtra("subscription_list")) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			if (previousFragment != null) {
				ft.hide(previousFragment);
			}

			Fragment subscriptionList = new SubscriptionListFragment();
			ft.replace(R.id.inner_content, subscriptionList, "subscriptionList");

			ft.commit();
			previousFragment = subscriptionList;
			strip3.setImageResource(R.drawable.yousubscriptions_line);
		} else if (intent.hasExtra("dashboard")) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			if (previousFragment != null) {
				ft.hide(previousFragment);
			}
			Fragment headlinesFragment = new HeadlinesFragment();

			ft.add(R.id.relativeLayout, headlinesFragment, "headlines");
			ft.commit();

			previousFragment = headlinesFragment;
			btnHeadlines.performClick();

			strip3.setImageResource(R.drawable.headline_line);
		} else if (intent.hasExtra("notifications")) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			if (previousFragment != null) {
				ft.hide(previousFragment);
			}
			Fragment notificationsFragment = new NotificationFragment();
			ft.add(R.id.inner_content, notificationsFragment, "notifications");
			ft.commit();
			previousFragment = notificationsFragment;
			strip3.setImageResource(R.drawable.notifications_line);
		} else if (intent.hasExtra("message")) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			if (previousFragment != null) {
				ft.hide(previousFragment);
			}
			Fragment messageFragment = new MessageFragment();
			ft.replace(R.id.inner_content, messageFragment, "message");
			ft.commit();
			previousFragment = messageFragment;

			strip3.setImageResource(R.drawable.messegs_line);
		} else if (intent.hasExtra("logout")) {
			Intent intent1 = new Intent(this, SplashActivity.class);
			startActivity(intent1);
			finish();
		} else if (intent.hasExtra("add_publication")) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			if (previousFragment != null) {
				ft.hide(previousFragment);
			}

			Fragment addPublicationFragment = new AddPublicationFragment();
			ft.replace(R.id.inner_content, addPublicationFragment, "add_public_publication");
			ft.addToBackStack(null);
			ft.commit();
			// previousFragment = addPublicationFragment;
			strip3.setImageResource(R.drawable.pubheading);
		}

		else if (intent.hasExtra("add_discussion")) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			if (previousFragment != null) {
				ft.hide(previousFragment);
			}

			Fragment addDiscussionFragment = new AddDiscussionFragment();
			ft.replace(R.id.inner_content, addDiscussionFragment, "add_public_discussion");
			ft.addToBackStack(null);
			ft.commit();
			// previousFragment = addPublicationFragment;
			strip3.setImageResource(R.drawable.discussion_line);
		} else if (intent.hasExtra("profile")) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			if (previousFragment != null) {
				ft.hide(previousFragment);
			}

			Fragment profile = new ManageProfileFragment();
			ft.replace(R.id.inner_content, profile, "profile");
			ft.addToBackStack(null);
			ft.commit();
			previousFragment = profile;
			strip3.setImageResource(R.drawable.discussion_line);
		} else if (intent.hasExtra("setting")) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			if (previousFragment != null) {
				ft.hide(previousFragment);
			}

			Fragment privacy = new EditPrivacySettingsFragment();
			ft.replace(R.id.inner_content, privacy, "profile");
			ft.addToBackStack(null);
			ft.commit();
			previousFragment = privacy;
			strip3.setImageResource(R.drawable.discussion_line);
		}


		else {
			btnHeadlines.performClick();
		}
	}

	public void refreshHeadlines() {
		if (refreshSelected == R.id.btnHeadlines) {
			showOwnHeadlineFragment(View.VISIBLE);
		}
		// btnHeadlines.performClick();
	}

	public void showOwnHeadlineFragment(int visibility) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (visibility == View.VISIBLE) {

			if (previousFragment != null) {
				ft.hide(previousFragment);
			}

			strip3.setImageResource(R.drawable.headline_line);
			Fragment headlinesFragment = new OwnHeadlineFragment();
			ft.replace(R.id.inner_content, headlinesFragment, "OwnHeadlineFragment");

			ft.commit();

			previousFragment = headlinesFragment;
		} else {
			if (previousFragment != null) {
				ft.hide(previousFragment);
			}
			btnHeadlines.performClick();
		}
	}

	public void showLastFramgent() {
		btnHeadlines.performClick();
	}
}
