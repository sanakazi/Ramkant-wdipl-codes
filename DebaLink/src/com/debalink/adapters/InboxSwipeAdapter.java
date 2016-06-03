package com.debalink.adapters;

import java.util.ArrayList;

import android.widget.BaseAdapter;

import com.debalink.models.MessageModel;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.contextualundo.ContextualUndoAdapter;

public class InboxSwipeAdapter extends ContextualUndoAdapter {

	public InboxSwipeAdapter(BaseAdapter baseAdapter, int undoLayoutResId, int undoActionResId, int autoDeleteTimeMillis,
			DeleteItemCallback deleteItemCallback, ArrayList<MessageModel> messageList) {
		super(baseAdapter, undoLayoutResId, undoActionResId, autoDeleteTimeMillis, deleteItemCallback);

	}

}
