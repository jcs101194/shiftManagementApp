package com.example.shiftmanagementapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

@SuppressLint({"ViewHolder", "SetTextI18n", "SimpleDateFormat"})
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    ArrayList<Shift>    mMasterShiftList;
    ArrayList<String>   mMasterNameList;
    ArrayList<String>   mMasterRoleList;
    String              mListOrder;
    ListView            mListView;
    CustomAdapter       mCustomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button addShiftButton = findViewById(R.id.addShiftButton);

        mListOrder = "none";

        addShiftButton.setOnClickListener(v -> openShiftAddFragment());

        try {
            JSONObject object = new JSONObject(getInfo());
            JSONArray desiredArray = object.getJSONArray("shifts");

            initializeSpinner();
            initializeMasterLists(desiredArray);
            initializeListView();

        } catch (Exception e) {
            Toast.makeText(this, "ERROR 0", Toast.LENGTH_SHORT).show();
        }
    }

    void openShiftAddFragment()
    {
        AddShiftFragment desiredFragment = new AddShiftFragment();

        desiredFragment.setMasterLists(mMasterNameList, mMasterRoleList);
        desiredFragment.setCallingActivity(this);
        desiredFragment.setFragmentManager(getSupportFragmentManager());

        desiredFragment.show(getSupportFragmentManager(), "addShiftFragment");
    }
    void openShiftEditFragment(Shift desiredShift, int objectIndex)
    {
        EditShiftFragment desiredFragment = new EditShiftFragment();

        desiredFragment.setMasterLists(mMasterNameList, mMasterRoleList);
        desiredFragment.setCallingActivity(this);
        desiredFragment.setShiftToEdit(desiredShift);
        desiredFragment.setObjectIndex(objectIndex);
        desiredFragment.setFragmentManager(getSupportFragmentManager());

        desiredFragment.show(getSupportFragmentManager(), "editShiftFragment");
    }
    boolean isNewName(String desiredName)
    {
        for(String currentName: mMasterNameList)
            if(currentName.equals(desiredName))
                return false;

        return true;
    }
    boolean isNewRole(String desiredRole)
    {
        for(String currentRole: mMasterRoleList)
            if(currentRole.equals(desiredRole))
                return false;

        return true;
    }
    void initializeMasterLists(JSONArray desiredArray)
    {
        mMasterShiftList = new ArrayList<>();
        mMasterNameList  = new ArrayList<>();
        mMasterRoleList  = new ArrayList<>();

        JSONObject currentObject;
        String role, name, startDate, endDate, color;

        try
        {
            for (int i = 0; i < desiredArray.length(); i++)
            {
                currentObject = desiredArray.getJSONObject(i);
                role = currentObject.getString("role");
                name = currentObject.getString("name");
                startDate = currentObject.getString("start_date");
                endDate = currentObject.getString("end_date");
                color = currentObject.getString("color");

                mMasterShiftList.add(new Shift(name, role, startDate, color, endDate));

                if(isNewName(name)) mMasterNameList.add(name);
                if(isNewRole(role)) mMasterRoleList.add(role);

            }
        } catch (Exception e) {
            Toast.makeText(this, "ERROR 1", Toast.LENGTH_SHORT).show();
        }

    }
    void initializeSpinner()
    {
        ArrayList<String> stringArray = new ArrayList<>();
        stringArray.add("");
        stringArray.add("Most Recent"); stringArray.add("Alphabetical");
        stringArray.add("By Color"); stringArray.add("By Role");
        Spinner spinner = findViewById(R.id.listOrderSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, stringArray);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    void initializeListView()
    {
        mListView = findViewById(R.id.listView);
        ArrayList<String> arr = new ArrayList<>(); for(int i = 0; i < mMasterShiftList.size(); i++) arr.add("-");
        mCustomAdapter = new CustomAdapter(this, mMasterShiftList, arr);

        mListView.setAdapter(mCustomAdapter);

        mListView.setOnClickListener(v ->
                Toast.makeText(this, "Item clicked", Toast.LENGTH_SHORT).show());
    }

    private String getInfo()
    {
        return "{\n" +
                "\"shifts\": [\n" +
                "{\n" +
                "\"role\": \"Waiter\",\n" +
                "\"name\": \"Anna\",\n" +
                "\"start_date\": \"2018-04-20 9:00:00 -08:00\",\n" +
                "\"end_date\": \"2018-4-20 12:00:00 -08:00\",\n" +
                "\"color\": \"red\"\n" +
                "},\n" +
                "{\n" +
                "\"role\": \"Prep\",\n" +
                "\"name\": \"Anton\",\n" +
                "\"start_date\": \"2018-04-20 9:00:00 -08:00\",\n" +
                "\"end_date\": \"2018-4-20 12:00:00 -08:00\",\n" +
                "\"color\": \"blue\"\n" +
                "},\n" +
                "{\n" +
                "\"role\": \"Front of House\",\n" +
                "\"name\": \"Eugene\",\n" +
                "\"start_date\": \"2018-04-20 12:00:00 -08:00\",\n" +
                "\"end_date\": \"2018-4-20 22:00:00 -08:00\",\n" +
                "\"color\": \"red\"\n" +
                "},\n" +
                "{\n" +
                "\"role\": \"Cook\",\n" +
                "\"name\": \"Keyvan\",\n" +
                "\"start_date\": \"2018-04-21 7:00:00 -08:00\",\n" +
                "\"end_date\": \"2018-4-21 12:00:00 -08:00\",\n" +
                "\"color\": \"green\"\n" +
                "},\n" +
                "{\n" +
                "\"role\": \"Waiter\",\n" +
                "\"name\": \"Anna\",\n" +
                "\"start_date\": \"2018-04-21 9:00:00 -08:00\",\n" +
                "\"end_date\": \"2018-4-21 14:00:00 -08:00\",\n" +
                "\"color\": \"red\"\n" +
                "},\n" +
                "{\n" +
                "\"role\": \"Prep\",\n" +
                "\"name\": \"Anton\",\n" +
                "\"start_date\": \"2018-04-21 7:00:00 -08:00\",\n" +
                "\"end_date\": \"2018-4-21 12:00:00 -08:00\",\n" +
                "\"color\": \"blue\"\n" +
                "},\n" +
                "{\n" +
                "\"role\":\"Front of House\",\n" +
                "\"name\": \"Eugene\",\n" +
                "\"start_date\": \"2018-04-21 12:00:00 -08:00\",\n" +
                "\"end_date\": \"2018-4-21 22:00:00 -08:00\",\n" +
                "\"color\": \"red\"\n" +
                "},\n" +
                "{\n" +
                "\"role\":\"Cook\",\n" +
                "\"name\": \"Keyvan\",\n" +
                "\"start_date\": \"2018-04-22 7:00:00 -08:00\",\n" +
                "\"end_date\": \"2018-4-22 12:00:00 -08:00\",\n" +
                "\"color\": \"green\"\n" +
                "},\n" +
                "{\n" +
                "\"role\": \"Waiter\",\n" +
                "\"name\": \"Anna\",\n" +
                "\"start_date\": \"2018-04-22 9:00:00 -08:00\",\n" +
                "\"end_date\": \"2018-4-22 14:00:00 -08:00\",\n" +
                "\"color\": \"red\"\n" +
                "},\n" +
                "{\n" +
                "\"role\":\"Prep\",\n" +
                "\"name\": \"Anton\",\n" +
                "\"start_date\": \"2018-04-22 7:00:00 -08:00\",\n" +
                "\"end_date\": \"2018-4-22 12:00:00 -08:00\",\n" +
                "\"color\": \"blue\"\n" +
                "},\n" +
                "{\n" +
                "\"role\": \"Front of House\",\n" +
                "\"name\": \"Eugene\",\n" +
                "\"start_date\": \"2018-04-22 12:00:00 -08:00\",\n" +
                "\"end_date\": \"2018-4-22 22:00:00 -08:00\",\n" +
                "\"color\": \"red\"\n" +
                "}\n" +
                "]\n" +
                "}";

    }

    String intToWeekday(int desiredInt)
    {
        switch (desiredInt) {
            case 1:
                return "Sun";
            case 2:
                return "Mon";
            case 3:
                return "Tues";
            case 4:
                return "Wed";
            case 5:
                return "Thur";
            case 6:
                return "Fri";
            case 7:
                return "Sat";
            default:
                return "";
        }

    }

    /**
     * @param desiredDate must be in format as found in JSON like so: 2018-04-20 9:00:00 -08:00
     * @return the format as M/YY
     */
    String returnMYYDate(String desiredDate)
    {
        String[] tokenArray = desiredDate.split(" ");
        String[] dateTokens = tokenArray[0].split("-");
        String month = dateTokens[1];
        String year = dateTokens[0];

        if (month.length() == 2)
            month = month.substring(1, 2);

        year = year.substring(2, 4);

        return month + "/" + year;
    }

    /**
     * @param startShift in the same format as 2018-04-20 9:00:00 -08:00
     * @param endShift   in the same format as 2018-04-20 9:00:00 -08:00
     * @return string in format of 9-12pm
     */
    String getShiftHours(String startShift, String endShift)
    {
        String startTime = startShift.split(" ")[1];
        String endTime = endShift.split(" ")[1];

        startTime = removeExtraneousDigitsInTime(startTime);
        endTime = removeExtraneousDigitsInTime(endTime);

        return startTime + "-" + endTime + " PM";
    }

    /**
     * @param desiredTime string in format of 9:00:00
     * @return return either 9 or 9:30 or 9:15, etc
     */
    String removeExtraneousDigitsInTime(String desiredTime)
    {
        String[] tokenArray = desiredTime.split(":");
        String hours = tokenArray[0];
        String minutes = tokenArray[1];
        int minutesInt = Integer.valueOf(minutes);

        if (minutes.equals("00")) {
            return hours;
        } else {
            //It's some weird case like 39 minutes or something
            //Note that seconds are ignored
            if (0 <= minutesInt && minutesInt <= 7) return hours;
            else if (8 <= minutesInt && minutesInt <= 22) return hours + ":15";
            else if (23 <= minutesInt && minutesInt <= 37) return hours + ":30";
            else if (38 <= minutesInt && minutesInt <= 52) return hours + ":45";
            else if (53 <= minutesInt && minutesInt <= 59) return hours;

        }

        return "";
    }

    /**
     * orders based on whatever mListOrder is
     *
     */
    void orderShiftsInListView()
    {
        ArrayList<Shift> adaptersShiftList = mCustomAdapter.getShiftList(); //For iteration

        if(mListOrder.equals("mostRecent"))
        {
            //By start date, end dates are irrelevant to humans
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            long earliestEpoch, currentEpoch;
            int indexOfEarliestEpoch;
            String currentStartDate, secondStartDate;

            try
            {
                for(int i = 0; i < adaptersShiftList.size(); i++)
                {
                    currentStartDate = adaptersShiftList.get(i).getStartDate();
                    calendar.setTime(format.parse(currentStartDate));
                    earliestEpoch = calendar.getTimeInMillis();
                    indexOfEarliestEpoch = i;
                    for(int j = i+1; j < adaptersShiftList.size(); j++)
                    {
                        secondStartDate = adaptersShiftList.get(j).getStartDate();
                        calendar.setTime(format.parse(secondStartDate));
                        currentEpoch = calendar.getTimeInMillis();

                        if (currentEpoch < earliestEpoch)
                        {
                            earliestEpoch = currentEpoch;
                            indexOfEarliestEpoch = j;
                        }
                    }

                    if(i != indexOfEarliestEpoch) adaptersShiftList = swapTwoElements(i, indexOfEarliestEpoch, adaptersShiftList);

                }
            } catch (Exception e){
                Toast.makeText(this, "ERROR 5", Toast.LENGTH_SHORT).show();
            }
        }
        if(mListOrder.equals("alphabetical"))
        {
            //By first name
            String currentFirstName, earliestAlphabeticalFirstName;
            int earliestAlphabeticalFirstNameIndex;
            for(int i = 0; i < adaptersShiftList.size(); i++)
            {
                earliestAlphabeticalFirstNameIndex = i;
                earliestAlphabeticalFirstName = adaptersShiftList.get(earliestAlphabeticalFirstNameIndex).getName();
                for(int j = i+1; j < adaptersShiftList.size(); j++)
                {
                    currentFirstName = adaptersShiftList.get(j).getName();
                    if(currentFirstName.compareTo(earliestAlphabeticalFirstName) <  0)
                    {
                        earliestAlphabeticalFirstNameIndex = j;
                        earliestAlphabeticalFirstName = adaptersShiftList.get(earliestAlphabeticalFirstNameIndex).getName();
                    }
                }

                if(i != earliestAlphabeticalFirstNameIndex)adaptersShiftList = swapTwoElements(i, earliestAlphabeticalFirstNameIndex, adaptersShiftList);
            }
        }
        if(mListOrder.equals("byColor"))
        {
            //Order by rgb, screw it.
            String searchingColor = "red", currentColor, secondColor;
            for(int i = 0; i < adaptersShiftList.size(); i++)
            {
                currentColor = adaptersShiftList.get(i).getColor();
                //No need to replace, current index is the correct color
                if(currentColor.equals(searchingColor)) continue;
                //Otherwise, switch with first correct color
                for(int j = i+1; j < adaptersShiftList.size(); j++)
                {
                    secondColor = adaptersShiftList.get(j).getColor();
                    if(secondColor.equals(searchingColor))
                    {
                        adaptersShiftList = swapTwoElements(i, j, adaptersShiftList);
                        break;
                    }

                    //If no instance of the searching color is found
                    if(j == adaptersShiftList.size()-1)
                    {
                        //then switch it
                        if(searchingColor.equals("red")) searchingColor = "green";
                        else if(searchingColor.equals("green")) searchingColor = "blue";

                        //Do the same iteration again with the correct searchingColor
                        i--;
                    }
                }
            }
        }
        if(mListOrder.equals("byRole"))
        {
            //By role alphabetically
            String currentRole, earliestAlphabeticalRole;
            int earliestAlphabeticalRoleIndex;
            for(int i = 0; i < adaptersShiftList.size(); i++)
            {
                earliestAlphabeticalRoleIndex = i;
                earliestAlphabeticalRole = adaptersShiftList.get(earliestAlphabeticalRoleIndex).getRole();
                for(int j = i+1; j < adaptersShiftList.size(); j++)
                {
                    currentRole = adaptersShiftList.get(j).getRole();
                    if(currentRole.compareTo(earliestAlphabeticalRole) <  0)
                    {
                        earliestAlphabeticalRoleIndex = j;
                        earliestAlphabeticalRole = adaptersShiftList.get(earliestAlphabeticalRoleIndex).getRole();
                    }
                }

                if(i != earliestAlphabeticalRoleIndex)adaptersShiftList = swapTwoElements(i, earliestAlphabeticalRoleIndex, adaptersShiftList);
            }
        }

        mCustomAdapter.setShiftList(adaptersShiftList);
        mCustomAdapter.notifyDataSetChanged();
    }

    ArrayList<Shift> swapTwoElements(int index1, int index2, ArrayList<Shift> desiredList)
    {
        ArrayList<Shift> answerList = new ArrayList<>();
        for(int i = 0; i < desiredList.size(); i++)
        {
            if(i ==  index1) answerList.add(i, desiredList.get(index2));
            else if(i == index2) answerList.add(i, desiredList.get(index1));
            else answerList.add(desiredList.get(i));

        }

        return answerList;
    }

    /**
     * This function will append the shift in the appropriate order, if specified
     * @param newShift the shift to append to master and listview
     */
    void appendNewShiftToLists(Shift newShift)
    {
        mCustomAdapter.addNewShift(newShift);
        orderShiftsInListView();    //The function will organize depending on the activity's selected mListOrder

    }
    void deleteShiftFromLists(Shift desiredShift)
    {
        mCustomAdapter.deleteShift(desiredShift);
        orderShiftsInListView();
    }
    void editShiftInLists(Shift desiredShift, int desiredIndex)
    {
        mCustomAdapter.editShift(desiredShift, desiredIndex);
        orderShiftsInListView();
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Implemented functions~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {

        switch(pos)
        {
            case 0: mListOrder = "none"; break;
            case 1: mListOrder = "mostRecent"; break;
            case 2: mListOrder = "alphabetical"; break;
            case 3: mListOrder = "byColor"; break;
            case 4: mListOrder = "byRole"; break;
        }

        orderShiftsInListView();
    }
    public void onNothingSelected(AdapterView<?> parent)
    {
        // Do nothing, then
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Inner classes~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    class CustomAdapter extends ArrayAdapter<String>
    {
        Context mContext;
        ArrayList<Shift> mShiftList;
        Calendar mCalendar;
        LayoutInflater mInflater;

        CustomAdapter(Context c, ArrayList<Shift> desiredShiftList, ArrayList<String> texts)
        {
            super(c, R.layout.list_view_item, R.id.employeeName, texts);

            mContext = c;
            mShiftList = desiredShiftList;
            mCalendar = Calendar.getInstance();
            mInflater = LayoutInflater.from(mContext);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent)
        {

            View listViewItem = mInflater.inflate(R.layout.list_view_item, parent, false);
            TextView name = listViewItem.findViewById(R.id.employeeName);
            TextView role = listViewItem.findViewById(R.id.role);
            TextView shiftDate = listViewItem.findViewById(R.id.date);
            TextView shiftHours = listViewItem.findViewById(R.id.hourRange);
            Shift currentShift = mShiftList.get(position);
            String startShift = currentShift.getStartDate();     //Format: yyyy-mm-dd hh:mm:ss -08:00
            String dayOfWeek = "", MYY = "", shiftHoursString = "";
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");

            try
            {
                //Set necessary variables first for formatting
                mCalendar.setTime(dateFormat.parse(startShift.split(" ")[0]));
                dayOfWeek = intToWeekday(mCalendar.get(Calendar.DAY_OF_WEEK));
                MYY = returnMYYDate(startShift);

                //Format shiftHours
                shiftHoursString = getShiftHours(startShift, currentShift.getEndDate());
            }
            catch(Exception e){ Toast.makeText(mContext, "ERROR 03", Toast.LENGTH_SHORT).show(); }

            //Item view format: Name (Role) <first_three_letters_of_weekday> <m/yy> [color drawable] 9-12pm
            name.setText(currentShift.getName());
            role.setText("("+currentShift.getRole()+")");
            shiftDate.setText(dayOfWeek + " " + MYY);
            shiftHours.setText(shiftHoursString);
            setColor(currentShift.getColor(), listViewItem);

            listViewItem.setOnClickListener(v -> openShiftEditFragment(currentShift, position));

            return listViewItem;
        }

        void setColor(String desiredColor, View desiredView)
        {
            View desiredCircle = desiredView.findViewById(R.id.colorCircle);
            switch (desiredColor)
            {
                case "red":     desiredCircle.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));  break;
                case "green":   desiredCircle.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_dark));break;
                case "blue":    desiredCircle.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark)); break;
            }
        }

        private void editShift(Shift desiredShift, int desiredIndex)
        {
            mShiftList.get(desiredIndex).setName(desiredShift.getName());
            mShiftList.get(desiredIndex).setRole(desiredShift.getRole());
            mShiftList.get(desiredIndex).setStartDate(desiredShift.getStartDate());
            mShiftList.get(desiredIndex).setEndDate(desiredShift.getEndDate());
            mShiftList.get(desiredIndex).setColor(desiredShift.getColor());
        }
        private void addNewShift(Shift newShift)
        {
            mShiftList.add(newShift);

            //To appease adapter, add new string to count
            add("-");
        }

        /**
         * Precondition desiredShift must exist in mShiftList of the adapter
         * @param desiredShift the shift to be deleted
         */
        private void deleteShift(Shift desiredShift)
        {
            Shift currentShift;
            for(int i = 0; i < mShiftList.size(); i++)
            {
                currentShift = mShiftList.get(i);
                if (currentShift.getName().equals(desiredShift.getName()) &&
                        currentShift.getStartDate().equals(desiredShift.getStartDate()) &&
                        currentShift.getEndDate().equals(desiredShift.getEndDate()) &&
                        currentShift.getColor().equals(desiredShift.getColor()) &&
                        currentShift.getRole().equals(desiredShift.getRole()))
                {
                    mShiftList.remove(currentShift);
                    //Again, remove from mList because that's all the adapter counts
                    remove("-");
                    return;
                }
            }

        }

        private void setShiftList(ArrayList<Shift> list){mShiftList = list;}
        private ArrayList<Shift> getShiftList(){return mShiftList;}

        @Override
        public void add(String object)
        {
            super.add(object);
        }

        @Override
        public void remove(String object)
        {
            super.remove(object);
        }
    }
    static class Shift
    {
        private String mName, mRole, mStartDate, mColor, mEndDate;

        /**
         *
         * @param desiredName employee's name
         * @param desiredRole employee's role
         * @param startDate start date in format "2018-04-21 7:00:00 -08:00"
         * @param desiredColor name of color in string
         * @param endDate end date in format "2018-04-21 7:00:00 -08:00"
         */
        Shift(String desiredName, String desiredRole, String startDate, String desiredColor, String endDate)
        {
            mName = desiredName;    mRole = desiredRole;    mStartDate = startDate;
            mColor = desiredColor;  mEndDate = endDate;
        }

        void setName(String s){mName = s;}
        void setRole(String s){mRole = s;}
        void setStartDate(String s){mStartDate = s;}
        void setEndDate(String s){mEndDate = s;}
        void setColor(String s){mColor = s;}

        String getName(){return mName;}
        String getRole(){return mRole;}
        String getStartDate(){return mStartDate;}
        String getColor(){return mColor;}
        String getEndDate(){return mEndDate;}
    }
}