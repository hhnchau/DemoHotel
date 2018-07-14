package com.appromobile.hotel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.widgets.TextViewSFRegular;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by xuanquach on 8/5/15.
 */
public class CalendarCellAdapter extends BaseAdapter {
    private final Context _context;
    private final List<String> list;
    private static final int DAY_OFFSET = 1;
//    private String[] weekdays;// = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private int daysInMonth;
//    private int currentDayOfMonth;
//    private int currentWeekDay;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private final SimpleDateFormat dateCellFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
    private final SimpleDateFormat dayOfMonthFormatter = new SimpleDateFormat("dd", Locale.ENGLISH);
    Calendar calendar;
    Calendar selectedDate;
    Calendar minDate;
    private CellDayClickListener cellDayClickListener;

    public CalendarCellAdapter(Context context, int month, int year, Calendar selectedDate, Calendar minDate, CellDayClickListener cellDayClickListener) {
        super();
        this._context = context;
        this.selectedDate = selectedDate;
        this.minDate = minDate;

        this.minDate.set(Calendar.HOUR_OF_DAY, 23);
        this.minDate.set(Calendar.MINUTE, 59);
        this.minDate.set(Calendar.SECOND, 59);
//        weekdays = new String[]{context.getString(R.string.sun), context.getString(R.string.mon), context.getString(R.string.tue), context.getString(R.string.wed), context.getString(R.string.thu), context.getString(R.string.fri), context.getString(R.string.sat)};
        this.list = new ArrayList<>();
        calendar = Calendar.getInstance();
        this.cellDayClickListener = cellDayClickListener;

//        if(calendar.get(Calendar.MONTH) + 1 == month && calendar.get(Calendar.YEAR) == year)
//        {
//            setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
//        }
//        setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

        // Print Month
        printMonth(month, year);
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
    }

    private String getMonthAsString(int i) {
        return months[i];
    }

//    private String getWeekDayAsString(int i) {
//        return weekdays[i];
//    }

    private int getNumberOfDaysOfMonth(int i) {
        return daysOfMonth[i];
    }

    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    private void printMonth(int mm, int yy) {
        int trailingSpaces = 0;
        int daysInPrevMonth = 0;
        int prevMonth = 0;
        int prevYear = 0;
        int nextMonth = 0;
        int nextYear = 0;
        int currentMonth = mm - 1;

//        String currentMonthName = getMonthAsString(currentMonth);
        daysInMonth = getNumberOfDaysOfMonth(currentMonth);
        GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);

        if (currentMonth == 11) {
            prevMonth = currentMonth - 1;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = 0;
            prevYear = yy;
            nextYear = yy + 1;
        } else if (currentMonth == 0) {
            prevMonth = 11;
            prevYear = yy - 1;
            nextYear = yy;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = 1;
        } else {
            prevMonth = currentMonth - 1;
            nextMonth = currentMonth + 1;
            nextYear = yy;
            prevYear = yy;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
        }
        int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
        trailingSpaces = currentWeekDay;

        if (cal.isLeapYear(cal.get(Calendar.YEAR)))
            if (mm == 2) ++daysInMonth;
            else if (mm == 3)  ++daysInPrevMonth;

        // Trailing Month days
        for (int i = 0; i < trailingSpaces; i++) {
            list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i) + "-GREY" + "-" + getMonthAsString(prevMonth) + "-" + prevYear);
        }


        // Current Month Days
        for (int i = 1; i <= daysInMonth; i++) {
//            Log.d(currentMonthName, String.valueOf(i) + " " + getMonthAsString(currentMonth) + " " + yy);
//            if (i == getCurrentDayOfMonth()) {
//                list.add(String.valueOf(i) + "-BLUE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
//            } else {
                list.add(String.valueOf(i) + "-WHITE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
//            }
        }

        // Leading Month days
        for (int i = 0; i < list.size() % 7; i++) {
            list.add(String.valueOf(i + 1) + "-GREY" + "-" + getMonthAsString(nextMonth) + "-" + nextYear);
        }

    }

//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }

//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       final ViewHolder viewHolder;
        if(convertView==null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                convertView = inflater.inflate(R.layout.screen_gridcell, null);
                viewHolder.tvDay = convertView.findViewById(R.id.tvDay);
                viewHolder.imgBg = convertView.findViewById(R.id.imgBg);
                convertView.setTag(viewHolder);
            }
        }else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

         // Get a reference to the Day gridcell

//        viewHolder.tvDay.setOnClickListener(this);

        String[] day_color = list.get(position).split("-");
        System.out.println("day_color: ["+position+"] : "+list.get(position));
        String theday = day_color[0];
        String themonth = day_color[2];
        String theyear = day_color[3];
        Date parsedDate = Calendar.getInstance().getTime();
        Calendar currentCal = Calendar.getInstance();
        try {
            parsedDate = dateCellFormatter.parse(theday + "-" + themonth + "-" + theyear);
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentCal.setTimeInMillis(parsedDate.getTime());
        currentCal.set(Calendar.HOUR_OF_DAY, 0);
        currentCal.set(Calendar.MINUTE, 0);
        currentCal.set(Calendar.SECOND, 1);

        // Set the Day GridCell
        viewHolder.tvDay.setText(theday);
//        viewHolder.tvDay.setTag(theday + "-" + themonth + "-" + theyear);
        if (day_color[1].equals("GREY")) {
            viewHolder.tvDay.setTextColor(_context.getResources().getColor(R.color.lg));
        }
        if (day_color[1].equals("WHITE")) {
//            viewHolder.tvDay.setTextColor(_context.getResources().getColor(R.color.black));
            if(minDate.getTimeInMillis()<=parsedDate.getTime()){
                viewHolder.tvDay.setTextColor(_context.getResources().getColor(R.color.bk));
            }else{
                viewHolder.tvDay.setTextColor(_context.getResources().getColor(R.color.lg));
            }
            if(dateFormatter.format(currentCal.getTime()).equals(dateFormatter.format(minDate.getTime()))){
                viewHolder.tvDay.setTextColor(_context.getResources().getColor(R.color.bk));
            }
        }

        viewHolder.tvDay.setVisibility(View.INVISIBLE);
        if(!day_color[1].equals("GREY")) {
            viewHolder.tvDay.setVisibility(View.VISIBLE);
            viewHolder.imgBg.setVisibility(View.GONE);
            if(dateFormatter.format(currentCal.getTime()).equals(dateFormatter.format(selectedDate.getTime()))){
                viewHolder.tvDay.setTextColor(_context.getResources().getColor(R.color.wh));
                viewHolder.imgBg.setVisibility(View.VISIBLE);
            }
        }



        final Date finalParsedDate = parsedDate;
        viewHolder.tvDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cellDayClickListener!=null && (minDate.getTimeInMillis()<=finalParsedDate.getTime())){
                    SimpleDateFormat dateFormatView = new SimpleDateFormat(_context.getString(R.string.date_format_view), Locale.ENGLISH);
                    cellDayClickListener.onCellClick(dateFormatView.format(finalParsedDate));
                }else if(dateFormatter.format(finalParsedDate.getTime()).equals(dateFormatter.format(minDate.getTime()))){
                    SimpleDateFormat dateFormatView = new SimpleDateFormat(_context.getString(R.string.date_format_view), Locale.ENGLISH);
                    cellDayClickListener.onCellClick(dateFormatView.format(finalParsedDate));
                }
            }
        });

        return convertView;
    }
    private class ViewHolder{
        TextViewSFRegular tvDay;
        ImageView imgBg;
    }
}
