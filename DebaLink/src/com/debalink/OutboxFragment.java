package com.debalink;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import com.debalink.adapters.OutboxAdapter;
import com.debalink.models.MessageModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Fonts;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;
import com.nhaarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.contextualundo.ContextualUndoAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.contextualundo.ContextualUndoAdapter.DeleteItemCallback;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class OutboxFragment extends Fragment implements OnClickListener, OnDismissCallback, DeleteItemCallback {
	private ListView lvMessage;
	private ArrayList<MessageModel> messageList, messageReadList, messageUnreadList;
	private OutboxAdapter adapter;
	private Button btnRead, btnUnread, btnAll, btnMark;
	private ImageButton btnDelete;
	private int listType = 0;
	private ContextualUndoAdapter contextualUndoAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.inbox_fragemnt, null);
		lvMessage = (ListView) view.findViewById(R.id.list);

		btnRead = (Button) view.findViewById(R.id.btnRead);
		btnUnread = (Button) view.findViewById(R.id.btnUnread);
		btnDelete = (ImageButton) view.findViewById(R.id.btnDelete);
		btnAll = (Button) view.findViewById(R.id.btnAll);
		btnMark = (Button) view.findViewById(R.id.btnMark);
		btnRead.setOnClickListener(this);
		btnUnread.setOnClickListener(this);
		// btnDelete.setOnClickListener(this);
		btnAll.setOnClickListener(this);
		btnRead.setTypeface(Fonts.getRobotoMedium(getActivity()));
		btnUnread.setTypeface(Fonts.getRobotoMedium(getActivity()));
		btnMark.setTypeface(Fonts.getRobotoMedium(getActivity()));
		btnAll.setTypeface(Fonts.getRobotoMedium(getActivity()));
		btnAll.performClick();
		lvMessage.bringToFront();
		btnDelete.setVisibility(View.INVISIBLE);

		

		btnDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<String> messageIds = new ArrayList<String>();
				ArrayList<MessageModel> currentList;
				if (listType == 1) {
					currentList = messageReadList;
				} else if (listType == 2) {
					currentList = messageUnreadList;
				} else {
					currentList = messageList;
				}

				for (MessageModel message : currentList) {

					if (message.isSelected()) {
						messageIds.add(message.getMessageId());

					}

				}
				new AsyncDeleteMessages(getActivity(), "Loading Messages", messageIds).execute();
			}
		});

		btnMark.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//btnDelete.setVisibility(View.VISIBLE);
				// btnMark.setSelected(true);
				ArrayList<MessageModel> currentList;
				
				if (listType == 1) {
					currentList = messageReadList;
				} else if (listType == 2) {
					currentList = messageUnreadList;
				} else {
					currentList = messageList;
				}

				for (MessageModel message : currentList) {

					message.setMarked(!message.isMarked());

				}
				

				if (btnMark.isSelected()) {
					btnMark.setText("  Mark");
					btnMark.setSelected(false);
					btnDelete.setVisibility(View.INVISIBLE);
					contextualUndoAdapter.notifyDataSetChanged();
					adapter.notifyDataSetChanged();

				} else {
					btnMark.setSelected(true);
					btnDelete.setVisibility(View.VISIBLE);

					//adapter = new OutboxAdapter(getActivity(), currentList);
					//ContextualUndoAdapter contextualUndoAdapter = new ContextualUndoAdapter(adapter, R.layout.undo_row,
							//R.id.undo_row_undobutton, 3000, OutboxFragment.this);
					//contextualUndoAdapter.setAbsListView(lvMessage);
					//lvMessage.setAdapter(contextualUndoAdapter);	
					
					contextualUndoAdapter.notifyDataSetChanged();
					adapter.notifyDataSetChanged();
					btnMark.setText("Unmark");
				}
			}
		});
		
		lvMessage.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				new AsyncMessageDetail(getActivity(), "Loading...", messageList.get(position).getMessageId()).execute();
			}
		});

		

		return view;
	}

	private class Async extends AsynDownloader {
		public Async(Context ctx, String message) {
			super(ctx, message);
		}

		private SoapObject res;
		private int errorFlag = 0;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("SenderId", UserPreferences.getUserId(getActivity()));

				res = hr.getSoapObjectFromServer(cv, "SelectUserOutbox");
				messageList = hr.parseMessageResponse(res);

				Log.i("SelectUserOutbox", res.toString());

			} catch (SocketTimeoutException e) {
				errorFlag = 1;
				e.printStackTrace();
			} catch (SocketException e) {
				errorFlag = 2;
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				errorFlag = 3;
				e.printStackTrace();
			} catch (IOException e) {
				errorFlag = 4;
				e.printStackTrace();
			} catch (Exception e) {
				errorFlag = 5;
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (errorFlag == 0) {
				adapter = new OutboxAdapter(getActivity(), messageList);

				contextualUndoAdapter = new ContextualUndoAdapter(adapter, R.layout.undo_row,
						R.id.undo_row_undobutton, 3000, OutboxFragment.this);

				contextualUndoAdapter.setAbsListView(lvMessage);
				lvMessage.setAdapter(contextualUndoAdapter);
			}
			if (messageList.size() > 0) {
				// lvMessage.addFooterView(footer);
			}
		}

	}

	private class AsyncReadMessages extends AsynDownloader {
		public AsyncReadMessages(Context ctx, String message) {
			super(ctx, message);
		}

		private SoapObject res;
		private int errorFlag = 0;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {
				messageReadList = new ArrayList<MessageModel>();
				messageUnreadList = new ArrayList<MessageModel>();
				ContentValues cv = new ContentValues();
				cv.put("SenderId", UserPreferences.getUserId(getActivity()));
				cv.put("State", "Outbox");

				res = hr.getSoapObjectFromServer(cv, "SelectReadMessage");
				messageReadList = hr.parseMessageResponse(res);

				Log.i("SelectReadMessage", res.toString());

			} catch (SocketTimeoutException e) {
				errorFlag = 1;
				e.printStackTrace();
			} catch (SocketException e) {
				errorFlag = 2;
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				errorFlag = 3;
				e.printStackTrace();
			} catch (IOException e) {
				errorFlag = 4;
				e.printStackTrace();
			} catch (Exception e) {
				errorFlag = 5;
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (errorFlag == 0) {
				adapter = new OutboxAdapter(getActivity(), messageReadList);
				contextualUndoAdapter = new ContextualUndoAdapter(adapter, R.layout.undo_row,
						R.id.undo_row_undobutton, 3000, OutboxFragment.this);

				contextualUndoAdapter.setAbsListView(lvMessage);
				lvMessage.setAdapter(contextualUndoAdapter);
			}

			if (messageList.size() > 0) {
				// lvMessage.addFooterView(footer);
			}

		}
	}

	private class AsyncUnreadMessages extends AsynDownloader {
		public AsyncUnreadMessages(Context ctx, String message) {
			super(ctx, message);
		}

		private SoapObject res;
		private int errorFlag = 0;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {
				messageReadList = new ArrayList<MessageModel>();
				messageUnreadList = new ArrayList<MessageModel>();
				ContentValues cv = new ContentValues();
				cv.put("SenderId", UserPreferences.getUserId(getActivity()));
				cv.put("State", "Outbox");

				res = hr.getSoapObjectFromServer(cv, "SelectUnReadMessage");
				messageUnreadList = hr.parseMessageResponse(res);

				Log.i("SelectUserInbox", res.toString());

			} catch (SocketTimeoutException e) {
				errorFlag = 1;
				e.printStackTrace();
			} catch (SocketException e) {
				errorFlag = 2;
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				errorFlag = 3;
				e.printStackTrace();
			} catch (IOException e) {
				errorFlag = 4;
				e.printStackTrace();
			} catch (Exception e) {
				errorFlag = 5;
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (errorFlag == 0) {
				adapter = new OutboxAdapter(getActivity(), messageUnreadList);
				contextualUndoAdapter = new ContextualUndoAdapter(adapter, R.layout.undo_row,
						R.id.undo_row_undobutton, 3000, OutboxFragment.this);

				contextualUndoAdapter.setAbsListView(lvMessage);
				lvMessage.setAdapter(contextualUndoAdapter);
			}

			if (messageList.size() > 0) {
				// lvMessage.addFooterView(footer);
			}

		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		btnUnread.setSelected(false);
		btnRead.setSelected(false);
		btnDelete.setSelected(false);
		btnAll.setSelected(false);

		btnMark.setText("Mark");
		if(btnMark.isSelected()){
			btnMark.setSelected(false);
			btnDelete.setVisibility(View.INVISIBLE);
		}
		
		
		switch (id) {
		case R.id.btnAll:
			listType = 0;

			btnAll.setSelected(true);
			new Async(getActivity(), "Loading Messages").execute();
			break;

		case R.id.btnRead:
			listType = 1;

			btnRead.setSelected(true);
			new AsyncReadMessages(getActivity(), "Loading Messages").execute();
			break;

		case R.id.btnUnread:
			listType = 2;
			btnUnread.setSelected(true);
			new AsyncUnreadMessages(getActivity(), "Loading Messages").execute();

			break;

		}
	}

	private class AsyncDeleteMessages extends AsynDownloader {
		public AsyncDeleteMessages(Context ctx, String message, ArrayList<String> messageIds) {
			super(ctx, message);
			this.messageIds = messageIds;
		}

		private String res;
		private int errorFlag = 0;
		private ArrayList<String> messageIds;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {
				for (int i = 0; i < messageIds.size(); i++) {
					ContentValues cv = new ContentValues();
					cv.put("SenderId", UserPreferences.getUserId(getActivity()));
					cv.put("MessageId", messageIds.get(i));
					res = hr.getDataFromServer(cv, "DeleteOutboxMessage");
				}
				Log.i("DeleteInboxMessage", res.toString());
			} catch (SocketTimeoutException e) {
				errorFlag = 1;
				e.printStackTrace();
			} catch (SocketException e) {
				errorFlag = 2;
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				errorFlag = 3;
				e.printStackTrace();
			} catch (IOException e) {
				errorFlag = 4;
				e.printStackTrace();
			} catch (Exception e) {
				errorFlag = 5;
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (errorFlag == 0) {
				if (listType == 1) {
					new AsyncReadMessages(getActivity(), "Loading Messages").execute();
				} else if (listType == 2) {
					new AsyncUnreadMessages(getActivity(), "Loading Messages").execute();
				} else {
					new Async(getActivity(), "Loading Messages").execute();
				}
			}

		}
	}

	@Override
	public void deleteItem(int position) {
		MessageModel mm=adapter.getList().get(position);
		
		adapter.notifyDataSetChanged();
		ArrayList<String> aa=new ArrayList<String>();
		aa.add(mm.getMessageId());
		adapter.remove(adapter.getList().get(position));
		new AsyncDeleteMessages(getActivity(), "Loading Messages", aa).execute();

	}

	@Override
	public void onDismiss(AbsListView listView, int[] reverseSortedPositions) {
		for (int position : reverseSortedPositions) {
			adapter.remove(adapter.getList().get(position));
		}
	}
	
	private class AsyncMessageDetail extends AsynDownloader {

		public AsyncMessageDetail(Context ctx, String message, String messageId) {
			super(ctx, message);
			this.messageId = messageId;

		}

		private String res;
		private int errorFlag = 0;
		private String messageId;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... param) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("MessageId", messageId);

				res = hr.getDataFromServer(cv, "SelectMeassage");
				// messageList = hr.parseMessageResponse(res);

				Log.i("SelectUserInbox", res.toString());

			} catch (SocketTimeoutException e) {
				errorFlag = 1;
				e.printStackTrace();
			} catch (SocketException e) {
				errorFlag = 2;
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				errorFlag = 3;
				e.printStackTrace();
			} catch (IOException e) {
				errorFlag = 4;
				e.printStackTrace();
			} catch (Exception e) {
				errorFlag = 5;
				e.printStackTrace();
			}
			return res;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (errorFlag == 0) {
				Fragment messageDetail = new MessageDetailFragment();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				Bundle bundle = new Bundle();
				bundle.putString("details", result);
				messageDetail.setArguments(bundle);
				ft.add(R.id.fragment, messageDetail, "messageDetail");
				ft.commit();
			}

		}

	}

}
