package com.example.shiftmanagementapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class CalendarWidget extends AppCompatDialogFragment implements CalendarView.OnDateChangeListener
{
    private View mFragmentView;
    private ViewGroup mContainer;
    private AddShiftFragment mCallingFragment;
    private EditShiftFragment mEditShiftFragment;
    private String mAddOrEdit;
    private CalendarView mCalendarView;
    private TimePicker mTimePicker;
    private String mDesiredQuery;
    private String mSelectedDate;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        LayoutInflater mInflater;
        assert getActivity() != null;
        mInflater = getActivity().getLayoutInflater();
        mFragmentView = mInflater.inflate(R.layout.fragment_calendar_widget, mContainer, false);

        mCalendarView   = mFragmentView.findViewById(R.id.datePicker);
        mTimePicker     = mFragmentView.findViewById(R.id.timePicker);

        mCalendarView.setOnDateChangeListener(this);
        mSelectedDate = "";

    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        mContainer = container;

        return mFragmentView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(mFragmentView).setTitle(Html.fromHtml("<font color='#480F7D'>Date Picker</font>")).setPositiveButton("Accept", (dialog, which) ->
        {
            //possible date time format: "2018-04-21 7:00:00 -08:00"
            String dateTime = getStringDateFromCalendarView()+ " " + mTimePicker.getHour() + ":" + mTimePicker.getMinute();

            if(mAddOrEdit.equals("add"))
            {
                switch (mDesiredQuery)
                {
                    case "startDate":
                        mCallingFragment.setStartDate(dateTime);
                        break;
                    case "endDate":
                        mCallingFragment.setEndDate(dateTime);
                        break;
                }
            }
            if(mAddOrEdit.equals("edit"))
            {
                switch(mDesiredQuery)
                {
                    case "startDate":
                        mEditShiftFragment.setStartDate(dateTime);
                        break;
                    case "endDate":
                        mEditShiftFragment.setEndDate(dateTime);
                        break;
                }
            }
        }
        ).setNegativeButton("Cancel", (dialog, which) -> { });

        return builder.create();
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
    {
        //Note: month is indexed at 0. The rest the numerical values represent exactly what they mean.
        Log.e("Hello", "year: "+year+" month:"+month+" day: "+dayOfMonth);

        mSelectedDate = year + "-" + month+1 + "-" + dayOfMonth;
    }

    private String getStringDateFromCalendarView()
    {
        //Note that mSelectedDate is empty if the widget is not changed.
        if(!mSelectedDate.isEmpty())
            return mSelectedDate;

        long desiredEpoch = mCalendarView.getDate();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");

        return simpleDate.format(new Date(desiredEpoch));
    }

    void setCallingFragment(AddShiftFragment desiredFragment){mCallingFragment = desiredFragment; mAddOrEdit = "add";}
    void setCallingFragmentForEdit(EditShiftFragment desiredFragment){mEditShiftFragment = desiredFragment; mAddOrEdit = "edit";}
    void setDesiredQuery(String desiredQuery){mDesiredQuery = desiredQuery;}
}
