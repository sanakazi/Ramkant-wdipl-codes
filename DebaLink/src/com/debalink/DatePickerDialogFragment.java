package com.debalink;

import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.DatePicker;

public class DatePickerDialogFragment extends DialogFragment {
	private Fragment mFragment;

	private static final int DATE_DIALOG_ID = 1;
	private int mYear, curYear;
	private int mMonth, curMonth;
	private int mDay, curDay;

	public DatePickerDialogFragment() {

	}

	public DatePickerDialogFragment(Fragment callback) {
		mFragment = callback;
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		
		mDay = c.get(Calendar.DAY_OF_MONTH);
	}

	public void setMinStartDate(int year, int month, int day) {
		mYear = year;
		mMonth = month;
		mDay = day;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		DatePickerDialog _date = new DatePickerDialog(getActivity(), (OnDateSetListener) mFragment, mYear, mMonth, mDay) {
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				if (year < curYear)
					view.updateDate(mYear, mMonth, mDay);

				if (monthOfYear < curMonth && year == curYear)
					view.updateDate(mYear, mMonth, mDay);

				if (dayOfMonth < (curDay) && year == curYear && monthOfYear == curMonth) {
					view.updateDate(mYear, mMonth, mDay);
					Log.i("DATE", dayOfMonth + "");
				}

			}
		};

		return _date;
	}

	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {

		case DATE_DIALOG_ID:
			DatePicker dp = new DatePicker(getActivity());
			dp.setMinDate(new Date().getTime() - 10000);
			((DatePickerDialog) dialog).onDateChanged(dp, mYear, mMonth, mDay);

			break;
		}
	}
}
