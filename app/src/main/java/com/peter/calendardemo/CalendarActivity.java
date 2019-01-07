package com.peter.calendardemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.SlidingDrawer.OnDrawerScrollListener;
import android.widget.TextView;

import com.peter.calendar.AddPlanActivity;
import com.peter.calendar.CalendarView;
import com.peter.calendar.CalendarViewBuilder;
import com.peter.calendar.CalendarViewPagerLisenter;
import com.peter.calendar.CircleTextView;
import com.peter.calendar.CustomDate;
import com.peter.calendar.CustomViewPagerAdapter;
import com.peter.calendar.DateUtil;

import java.util.Locale;

/**
 * @author Peter 日历界面
 */
public class CalendarActivity extends Activity implements OnClickListener, CalendarView.CallBack {
    private ViewPager viewPager;
    private CalendarView[] views;
    private TextView showYearView;
    private TextView showMonthView;
    private TextView showWeekView;
    private TextView monthCalendarView;
    private TextView weekCalendarView;
    private CalendarViewBuilder builder = new CalendarViewBuilder();
    private SlidingDrawer mSlidingDrawer;
    private View mContentPager;
    private CustomDate mClickDate;
    public static final String MAIN_ACTIVITY_CLICK_DATE = "main_click_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        findViewById();
    }

    private void findViewById() {
        viewPager = this.findViewById(R.id.viewpager);
        showMonthView = this.findViewById(R.id.show_month_view);
        showYearView = this.findViewById(R.id.show_year_view);
        showWeekView = this.findViewById(R.id.show_week_view);
        views = builder.createMassCalendarViews(this, 5, this);
        monthCalendarView = this.findViewById(R.id.month_calendar_button);
        weekCalendarView = this.findViewById(R.id.week_calendar_button);
        mContentPager = this.findViewById(R.id.contentPager);
        mSlidingDrawer = this.findViewById(R.id.sliding_drawer);

        CircleTextView mNowCircleView = this.findViewById(R.id.now_circle_view);
        CircleTextView mAddCircleView = this.findViewById(R.id.add_circle_view);

        monthCalendarView.setOnClickListener(this);
        weekCalendarView.setOnClickListener(this);
        mNowCircleView.setOnClickListener(this);
        mAddCircleView.setOnClickListener(this);

        setViewPager();
        setOnDrawListener();
    }


    private void setViewPager() {
        CustomViewPagerAdapter<CalendarView> viewPagerAdapter = new CustomViewPagerAdapter<>(views);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(498);
        viewPager.setOnPageChangeListener(new CalendarViewPagerLisenter(viewPagerAdapter));
    }

    private void setOnDrawListener() {
        mSlidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

            @Override
            public void onDrawerOpened() {
                builder.swtichCalendarViewsStyle(CalendarView.WEEK_STYLE);
            }
        });
        mSlidingDrawer.setOnDrawerScrollListener(new OnDrawerScrollListener() {

            @Override
            public void onScrollStarted() {
                builder.swtichCalendarViewsStyle(CalendarView.MONTH_STYLE);
            }

            @Override
            public void onScrollEnded() {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setShowDateViewText(int year, int month) {
        showYearView.setText(String.format(Locale.getDefault(), "%d", year));
        showMonthView.setText(String.format(Locale.getDefault(), "%d月", month));
        showWeekView.setText(DateUtil.weekName[DateUtil.getWeekDay() - 1]);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.month_calendar_button:
                switchBackgroundForButton(true);
                builder.swtichCalendarViewsStyle(CalendarView.MONTH_STYLE);
                mSlidingDrawer.close();
                break;
            case R.id.week_calendar_button:
                switchBackgroundForButton(false);
                mSlidingDrawer.open();
                break;
            case R.id.now_circle_view:
                builder.backTodayCalendarViews();
                break;
            case R.id.add_circle_view:
                Intent i = new Intent(this, AddPlanActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(MAIN_ACTIVITY_CLICK_DATE, mClickDate);
                i.putExtras(mBundle);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            default:
                break;
        }
    }

    private void switchBackgroundForButton(boolean isMonth) {
        if (isMonth) {
            monthCalendarView.setBackgroundResource(R.drawable.press_left_text_bg);
            weekCalendarView.setBackgroundColor(Color.TRANSPARENT);
        } else {
            weekCalendarView.setBackgroundResource(R.drawable.press_right_text_bg);
            monthCalendarView.setBackgroundColor(Color.TRANSPARENT);
        }
    }


    @Override
    public void onMeasureCellHeight(int cellSpace) {
        mSlidingDrawer.getLayoutParams().height = mContentPager.getHeight() - cellSpace;
    }

    @Override
    public void clickDate(CustomDate date) {
        mClickDate = date;
        //Toast.makeText(this, date.year+"-"+date.month+"-"+date.day, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void changeDate(CustomDate date) {
        setShowDateViewText(date.year, date.month);
    }

}  



	


