package com.example.shiftmanagementapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

public class EditShiftFragment extends AppCompatDialogFragment
{
    private View mFragmentView;
    private ViewGroup mContainer;
    private FragmentManager mFragmentManager;
    private ArrayList<String> mMasterRoleList;
    private ArrayList<String> mMasterNameList;
    private EditText mStartDate;
    private EditText mEndDate;

    private String mDesiredStartDate;   //For on resume
    private String mDesiredEndDate;

    private int mObjectIndex;

    private Spinner mNameSpinner;
    private Spinner mRoleSpinner;
    private Spinner mColorSpinner;

    private MainActivity mCallingActivity;

    private MainActivity.Shift mShiftToEdit;
    private MainActivity.Shift mUneditedShift;  //In case user edits form, then changes their mind

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        assert getActivity() != null;
        LayoutInflater mInflater;
        mInflater = getActivity().getLayoutInflater();
        mFragmentView = mInflater.inflate(R.layout.fragment_add_shift, mContainer, false);

        mStartDate  = mFragmentView.findViewById(R.id.startDate);
        mEndDate    = mFragmentView.findViewById(R.id.endDate);

        mStartDate.setOnClickListener(v -> openCalendarFragment("startDate"));
        mEndDate.setOnClickListener(v -> openCalendarFragment("endDate"));

    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        mContainer = container;

        initializeOptionLists();
        initializeButtons();
        populateInformation();

        return mFragmentView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(mFragmentView).setTitle(Html.fromHtml("<font color='#480F7D'>Edit a Shift</font>")).setPositiveButton("Accept", (dialog, which) ->
                {
                    if(thereAreMissingValues())
                    {
                        Toast.makeText(mCallingActivity, "There are missing values. Form not submitted.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    MainActivity.Shift newShift = new MainActivity.Shift(mNameSpinner.getSelectedItem().toString(), mRoleSpinner.getSelectedItem().toString(),
                            mStartDate.getEditableText().toString(), mColorSpinner.getSelectedItem().toString().toLowerCase(), mEndDate.getEditableText().toString());

                    mCallingActivity.editShiftInLists(newShift, mObjectIndex);
                }
        ).setNeutralButton("Cancel", (dialog, which) ->
        {

        }).setNegativeButton("Delete", ((dialog, which) -> mCallingActivity.deleteShiftFromLists(mUneditedShift)));


        return builder.create();
    }

    private boolean thereAreMissingValues()
    {
        return (mStartDate.getText().toString().equals("") || mEndDate.getText().toString().equals(""));
    }

    /**
     * populate information into form to possibly edit
     */
    private void populateInformation()
    {
        mStartDate.setText(mShiftToEdit.getStartDate());
        mEndDate.setText(mShiftToEdit.getEndDate());
        mNameSpinner.setSelection(getSelectionNumber(mNameSpinner, mShiftToEdit.getName()));
        mRoleSpinner.setSelection(getSelectionNumber(mRoleSpinner, mShiftToEdit.getRole()));
        mColorSpinner.setSelection(getSelectionNumber(mColorSpinner, mShiftToEdit.getColor()));
    }

    private int getSelectionNumber(Spinner desiredSpinner, String desiredValue)
    {
       for(int i = 0; i < desiredSpinner.getAdapter().getCount(); i++)
           if(desiredSpinner.getItemAtPosition(i).toString().equalsIgnoreCase(desiredValue))
               return i;

       //The implication should be obvious, -1 must never return
       return -1;
    }

    void setObjectIndex(int i){mObjectIndex = i;}
    void setCallingActivity(MainActivity desiredActivity){mCallingActivity = desiredActivity;}
    void setShiftToEdit(MainActivity.Shift s){mShiftToEdit = s; mUneditedShift = s;}
    void setStartDate(String desiredDateTime)
    {
        mDesiredStartDate = desiredDateTime;
        assert getActivity() != null;
        getActivity().runOnUiThread(() -> mStartDate.setText(mDesiredStartDate));
    }
    void setEndDate(String desiredDateTime)
    {
        mDesiredEndDate = desiredDateTime;
        assert getActivity() != null;
        getActivity().runOnUiThread(() -> mEndDate.setText(mDesiredEndDate));
    }
    private String[] arrayListToArray(ArrayList<String> desiredList)
    {
        String[] arr = new String[desiredList.size()];

        for(int i = 0; i < desiredList.size(); i++) arr[i] = desiredList.get(i);

        return arr;
    }
    private void initializeOptionLists()
    {
        Context desiredContext = getContext();  assert desiredContext != null;
        String[] colorsArray = {"Red", "Green", "Blue"};
        ArrayAdapter adapterNameList = new ArrayAdapter<>(desiredContext, R.layout.support_simple_spinner_dropdown_item, arrayListToArray(mMasterNameList));
        ArrayAdapter adapterRoleList = new ArrayAdapter<>(desiredContext, R.layout.support_simple_spinner_dropdown_item, arrayListToArray(mMasterRoleList));
        ArrayAdapter adapterColorList = new ArrayAdapter<>(desiredContext, R.layout.support_simple_spinner_dropdown_item, colorsArray);

        mNameSpinner    = mFragmentView.findViewById(R.id.employeeNameList);
        mRoleSpinner    = mFragmentView.findViewById(R.id.roleList);
        mColorSpinner   = mFragmentView.findViewById(R.id.colorList);

        mNameSpinner.setAdapter(adapterNameList);
        mRoleSpinner.setAdapter(adapterRoleList);
        mColorSpinner.setAdapter(adapterColorList);

    }
    private void initializeButtons()
    {
        ImageView startDateCalendarButton   = mFragmentView.findViewById(R.id.startDateCalendarButton);
        ImageView endDateCalendarButton     = mFragmentView.findViewById(R.id.endDateCalendarButton);

        startDateCalendarButton.setOnClickListener(v -> openCalendarFragment("startDate"));
        endDateCalendarButton.setOnClickListener(v -> openCalendarFragment("endDate"));
    }
    void setMasterLists(ArrayList<String> desiredNameList, ArrayList<String> desiredRoleList)
    {
        mMasterNameList = desiredNameList;
        mMasterRoleList = desiredRoleList;
    }
    void setFragmentManager(FragmentManager fm){mFragmentManager = fm;}

    /**
     *
     * @param desiredQuery the type of input that you're looking for whether it's start or end date
     */
    private void openCalendarFragment(String desiredQuery)
    {
        CalendarWidget calendarWidget = new CalendarWidget();

        calendarWidget.setCallingFragmentForEdit(this);
        calendarWidget.setDesiredQuery(desiredQuery);
        calendarWidget.show(mFragmentManager, "calendarWidget");
    }
}
