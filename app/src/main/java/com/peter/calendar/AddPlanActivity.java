package com.peter.calendar;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.peter.calendardemo.CalendarActivity;
import com.peter.calendardemo.R;

/**
 * @author Peter 添加日历计划事件
 */
public class AddPlanActivity extends Activity implements OnClickListener {
    private TextView mCancelTv;
    private TextView mConfirmTv;
    private CustomDate mCustomDate;
    private TextView mPlanContentTv;
    private TextView mStartPlanTimeTv;
    private TextView mEndPlanTimeTv;
    private TextView mNoCancelPlanTv;
    private TextView mConfirmCancelPlanTv;
    private View mShowDialogLayout;
    private View mCancelDialogLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);
        getIntentData();
        findById();
        setTextData();
    }

    private void findById() {
        mCancelTv = this.findViewById(R.id.cancel_tv);
        mConfirmTv = this.findViewById(R.id.confirm_tv);
        mPlanContentTv = this.findViewById(R.id.plan_content_tv);
        mStartPlanTimeTv = this.findViewById(R.id.start_plan_time_tv);
        mEndPlanTimeTv = this.findViewById(R.id.end_plan_time_tv);
        mNoCancelPlanTv = this.findViewById(R.id.no_cancel_plan_tv);
        mConfirmCancelPlanTv = this.findViewById(R.id.confirm_cancel_plan_tv);
        mShowDialogLayout = this.findViewById(R.id.dialog_show_layout);
        mCancelDialogLayout = this.findViewById(R.id.cancel_dialog_layout);
        setOnClickListener();

    }

    private void setTextData() {
        StringBuffer str = new StringBuffer();
        str.append(mCustomDate.toString());
        Log.i("huang", mCustomDate.toString());
        str.append(DateUtil.weekName[mCustomDate.week]);
        str.append(DateUtil.getHour());
        str.append(":");
        int minute1 = DateUtil.getMinute();
        str.append(minute1 < 10 ? "0" + minute1 : minute1);
        mStartPlanTimeTv.setText(str.toString());
        str = new StringBuffer();
        str.append(mCustomDate.toString());
        str.append(DateUtil.weekName[mCustomDate.week]);
        str.append(DateUtil.getHour() + 1);
        str.append(":");
        int minute2 = DateUtil.getMinute();
        str.append(minute2 < 10 ? "0" + minute2 : minute2);
        mEndPlanTimeTv.setText(str.toString());

    }

    private void setOnClickListener() {
        mCancelTv.setOnClickListener(this);
        mConfirmTv.setOnClickListener(this);
        mPlanContentTv.setOnClickListener(this);
        mStartPlanTimeTv.setOnClickListener(this);
        mEndPlanTimeTv.setOnClickListener(this);
        mNoCancelPlanTv.setOnClickListener(this);
        mConfirmCancelPlanTv.setOnClickListener(this);

    }


    private void getIntentData() {
        mCustomDate = (CustomDate) getIntent()
                .getSerializableExtra(CalendarActivity.MAIN_ACTIVITY_CLICK_DATE);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_tv:
                showCancelDialogState(true);
                break;
            case R.id.no_cancel_plan_tv:
                showCancelDialogState(false);
                break;
            case R.id.confirm_cancel_plan_tv:
                finish();
                break;
            case R.id.confirm_tv:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showCancelDialogState(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showCancelDialogState(boolean isVisible) {
        Animation anim;
        if (isVisible) {
            anim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_to_up);
            mCancelDialogLayout.setAnimation(anim);
            mShowDialogLayout.setVisibility(View.VISIBLE);


        } else {
            anim = AnimationUtils.loadAnimation(this, R.anim.slide_up_to_bottom);
            mCancelDialogLayout.setAnimation(anim);
            mShowDialogLayout.setVisibility(View.GONE);
        }
    }
}
