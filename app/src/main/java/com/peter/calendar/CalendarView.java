package com.peter.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.peter.calendardemo.R;

/**
 * @author Peter 自定义Calendar
 */
public class CalendarView extends View {

    private static final String TAG = "CalendarView";
    /**
     * 两种模式 （月份和星期）
     */
    public static final int MONTH_STYLE = 0;
    public static final int WEEK_STYLE = 1;

    private static final int TOTAL_COL = 7;
    private static final int TOTAL_ROW = 6;

    private Paint mCirclePaint;
    private Paint mTextPaint;
    private int mViewWidth;
    private int mViewHight;
    private int mCellSpace;
    private Row rows[] = new Row[TOTAL_ROW];

    /**
     * 自定义的日期  包括year month day
     */
    private static CustomDate mShowDate;
    public static int style = MONTH_STYLE;
    private static final int WEEK = 7;
    /**
     * 回调
     */
    private CallBack mCallBack;
    private int touchSlop;
    private boolean callBackCellSpace;
    private Context mContext;

    public interface CallBack {

        /**
         * 回调点击的日期
         *
         * @param date 日期
         */
        void clickDate(CustomDate date);


        /**
         * 回调cell的高度确定slidingDrawer高度
         *
         * @param cellSpace 高度
         */
        void onMeasureCellHeight(int cellSpace);

        /**
         * 回调滑动viewPager改变的日期
         *
         * @param date 日期
         */
        void changeDate(CustomDate date);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);

    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public CalendarView(Context context) {
        super(context);
        init(context);
    }

    public CalendarView(Context context, int style, CallBack mCallBack) {
        super(context);
        CalendarView.style = style;
        this.mCallBack = mCallBack;
        init(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < TOTAL_ROW; i++) {
            if (rows[i] != null) {
                rows[i].drawCells(canvas);
            }
        }
    }

    private void init(Context context) {
        mContext = context;
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        initDate();

    }

    private void initDate() {
        if (style == MONTH_STYLE) {
            mShowDate = new CustomDate();
        } else if (style == WEEK_STYLE) {
            mShowDate = DateUtil.getNextSunday();
        }
        fillDate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHight = h;
        mCellSpace = Math.min(mViewHight / TOTAL_ROW, mViewWidth / TOTAL_COL);
        if (!callBackCellSpace) {
            mCallBack.onMeasureCellHeight(mCellSpace);
            callBackCellSpace = true;
        }
        mTextPaint.setTextSize(mCellSpace / 3);
    }

    private Cell mClickCell;
    private float mDownX;
    private float mDownY;

    /**
     * 触摸事件为了确定点击的位置日期
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float disX = event.getX() - mDownX;
                float disY = event.getY() - mDownY;
                if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) {
                    int col = (int) (mDownX / mCellSpace);
                    int row = (int) (mDownY / mCellSpace);
                    measureClickCell(col, row);
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void measureClickCell(int col, int row) {
        if (col >= TOTAL_COL || row >= TOTAL_ROW) {
            return;
        }
        if (mClickCell != null) {
            rows[mClickCell.j].cells[mClickCell.i] = mClickCell;
        }
        if (rows[row] != null) {
            mClickCell = new Cell(rows[row].cells[col].date,
                    rows[row].cells[col].state, rows[row].cells[col].i,
                    rows[row].cells[col].j);
            rows[row].cells[col].state = State.CLICK_DAY;
            CustomDate date = rows[row].cells[col].date;
            date.week = col;
            mCallBack.clickDate(date);
            invalidate();
        }
    }

    /**
     * 组
     */
    class Row {
        int j;

        Row(int j) {
            this.j = j;
        }

        Cell[] cells = new Cell[TOTAL_COL];

        void drawCells(Canvas canvas) {
            for (Cell cell : cells) {
                if (cell != null) {
                    cell.drawSelf(canvas);
                }
            }

        }
    }

    /**
     * 单元格
     */
    class Cell {
        public CustomDate date;
        public State state;
        public int i;
        int j;

        Cell(CustomDate date, State state, int i, int j) {
            super();
            this.date = date;
            this.state = state;
            this.i = i;
            this.j = j;
        }


        /**
         * 绘制一个单元格 如果颜色需要自定义可以修改
         */
        void drawSelf(Canvas canvas) {
            switch (state) {
                case CURRENT_MONTH_DAY:
                    mTextPaint.setColor(ContextCompat.getColor(mContext, R.color.colorBlack));
                    break;
                case NEXT_MONTH_DAY:
                case PAST_MONTH_DAY:
                    mTextPaint.setColor(ContextCompat.getColor(mContext, R.color.colorDeepBlack));
                    break;
                case TODAY:
                    mTextPaint.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    break;
                case CLICK_DAY:
                    mTextPaint.setColor(ContextCompat.getColor(mContext, R.color.colorGray));
                    canvas.drawCircle((float) (mCellSpace * (i + 0.5)),
                            (float) ((j + 0.5) * mCellSpace), mCellSpace / 2,
                            mCirclePaint);
                    break;
                default:
                    break;
            }
            // 绘制文字
            String content = date.day + "";
            canvas.drawText(content,
                    (float) ((i + 0.5) * mCellSpace - mTextPaint.measureText(content) / 2),
                    (float) ((j + 0.7) * mCellSpace - mTextPaint.measureText(
                            content, 0, 1) / 2), mTextPaint);
        }
    }

    /**
     * cell的state
     */
    enum State {
        /**
         * 当前月日期，过去的月的日期，下个月的日期，今天，点击的日期
         */
        CURRENT_MONTH_DAY, PAST_MONTH_DAY, NEXT_MONTH_DAY, TODAY, CLICK_DAY
    }

    /**
     * 填充日期的数据
     */
    private void fillDate() {
        if (style == MONTH_STYLE) {
            fillMonthDate();
        } else if (style == WEEK_STYLE) {
            fillWeekDate();
        }
        mCallBack.changeDate(mShowDate);
    }

    /**
     * 填充星期模式下的数据 默认通过当前日期得到所在星期天的日期，然后依次填充日期
     */
    private void fillWeekDate() {
        int lastMonthDays = DateUtil.getMonthDays(mShowDate.year, mShowDate.month - 1);
        rows[0] = new Row(0);
        int day = mShowDate.day;
        for (int i = TOTAL_COL - 1; i >= 0; i--) {
            day -= 1;
            if (day < 1) {
                day = lastMonthDays;
            }
            CustomDate date = CustomDate.modifiDayForObject(mShowDate, day);
            if (DateUtil.isToday(date)) {
                mClickCell = new Cell(date, State.TODAY, i, 0);
                date.week = i;
                mCallBack.clickDate(date);
                rows[0].cells[i] = new Cell(date, State.CLICK_DAY, i, 0);
                continue;
            }
            rows[0].cells[i] = new Cell(date, State.CURRENT_MONTH_DAY, i, 0);
        }
    }

    /**
     * 填充月份模式下数据 通过getWeekDayFromDate得到一个月第一天是星期几就可以算出所有的日期的位置 然后依次填充
     * 这里最好重构一下
     */
    private void fillMonthDate() {
        int monthDay = DateUtil.getCurrentMonthDay();
        int lastMonthDays = DateUtil.getMonthDays(mShowDate.year, mShowDate.month - 1);
        int currentMonthDays = DateUtil.getMonthDays(mShowDate.year, mShowDate.month);
        int firstDayWeek = DateUtil.getWeekDayFromDate(mShowDate.year, mShowDate.month);
        boolean isCurrentMonth = false;
        if (DateUtil.isCurrentMonth(mShowDate)) {
            isCurrentMonth = true;
        }
        int day = 0;
        for (int j = 0; j < TOTAL_ROW; j++) {
            rows[j] = new Row(j);
            for (int i = 0; i < TOTAL_COL; i++) {
                int postion = i + j * TOTAL_COL;
                if (postion >= firstDayWeek
                        && postion < firstDayWeek + currentMonthDays) {
                    day++;
                    if (isCurrentMonth && day == monthDay) {
                        CustomDate date = CustomDate.modifiDayForObject(mShowDate, day);
                        mClickCell = new Cell(date, State.TODAY, i, j);
                        date.week = i;
                        mCallBack.clickDate(date);
                        rows[j].cells[i] = new Cell(date, State.CLICK_DAY, i, j);
                        continue;
                    }
                    rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(mShowDate, day),
                            State.CURRENT_MONTH_DAY, i, j);
                } else if (postion < firstDayWeek) {
                    rows[j].cells[i] = new Cell(new CustomDate(mShowDate.year, mShowDate.month - 1, lastMonthDays - (firstDayWeek - postion - 1)), State.PAST_MONTH_DAY, i, j);
                } else if (postion >= firstDayWeek + currentMonthDays) {
                    rows[j].cells[i] = new Cell((new CustomDate(mShowDate.year, mShowDate.month + 1, postion - firstDayWeek - currentMonthDays + 1)), State.NEXT_MONTH_DAY, i, j);
                }
            }
        }
    }

    public void update() {
        fillDate();
        invalidate();
    }

    public void backToday() {
        initDate();
        invalidate();
    }

    /**
     * 切换style
     */
    public void switchStyle(int style) {
        CalendarView.style = style;
        if (style == MONTH_STYLE) {
            update();
        } else if (style == WEEK_STYLE) {
            int firstDayWeek = DateUtil.getWeekDayFromDate(mShowDate.year,
                    mShowDate.month);
            mShowDate.day = 1 + WEEK - firstDayWeek;

            update();
        }

    }

    /**
     * 向右滑动
     */
    public void rightSlide() {
        if (style == MONTH_STYLE) {

            if (mShowDate.month == 12) {
                mShowDate.month = 1;
                mShowDate.year += 1;
            } else {
                mShowDate.month += 1;
            }

        } else if (style == WEEK_STYLE) {
            int currentMonthDays = DateUtil.getMonthDays(mShowDate.year, mShowDate.month);
            if (mShowDate.day + WEEK > currentMonthDays) {
                if (mShowDate.month == 12) {
                    mShowDate.month = 1;
                    mShowDate.year += 1;
                } else {
                    mShowDate.month += 1;
                }
                mShowDate.day = WEEK - currentMonthDays + mShowDate.day;
            } else {
                mShowDate.day += WEEK;

            }
        }
        update();
    }

    /**
     * 向左滑动
     */
    public void leftSlide() {

        if (style == MONTH_STYLE) {
            if (mShowDate.month == 1) {
                mShowDate.month = 12;
                mShowDate.year -= 1;
            } else {
                mShowDate.month -= 1;
            }

        } else if (style == WEEK_STYLE) {
            int lastMonthDays = DateUtil.getMonthDays(mShowDate.year, mShowDate.month);
            if (mShowDate.day - WEEK < 1) {
                if (mShowDate.month == 1) {
                    mShowDate.month = 12;
                    mShowDate.year -= 1;
                } else {
                    mShowDate.month -= 1;
                }
                mShowDate.day = lastMonthDays - WEEK + mShowDate.day;

            } else {
                mShowDate.day -= WEEK;
            }
            Log.i(TAG, "leftSlide" + mShowDate.toString());
        }
        update();
    }
}
