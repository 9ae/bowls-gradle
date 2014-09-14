package me.valour.bowls;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.app.Fragment;


@SuppressLint("NewApi")
public class BowlsGroupMockery  extends FrameLayout {

    int mMeasuredWidth;
    int mMeasuredHeight;
    float centerX=0;
    float centerY=0;
    int tableRadius;
    int bowlRadius;
    boolean measuredScreen;
    boolean addRemovable = true;

    FrameLayout.LayoutParams defaultParams;
    BowlSelectListener selectListener;
    NewBowlListener newListener;
    DeleteDropListener deleteListener;
    BowlsGroupAgent agent;

    private LinkedList<BowlView> bowls;
    private BowlView newBowl;
    private FrameLayout trashBowl;
    private int bowlsIdCounter = 1;
    private LinkedList<Integer> disusedIds;
    int currentDisusedId = -1;

    public BowlsGroupMockery(Context context) {
        super(context);
        init();
    }

    public BowlsGroupMockery(Context context, AttributeSet ats, int ds) {
        super(context, ats, ds);
        init();
    }

    public BowlsGroupMockery(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom){
        measureView();

        double angleDelta = Math.PI*2.0/bowls.size();
        double topX = 0;
        double topY = -1.0*tableRadius;

        int i= 0;
        for(BowlView bowl: bowls){
            bowl.bringToFront();
            double angle = angleDelta*i;
            double px = Math.cos(angle)*topX - Math.sin(angle)*topY + centerX;
            double py = Math.sin(angle)*topX - Math.cos(angle)*topY + centerY;
            bowl.setAngle(angle);
            bowl.move((float)px, (float)py);
            i++;
        }

        super.onLayout(changed, left, top, right, bottom);
    }

    private void init() {
        selectListener = new BowlSelectListener();
        newListener = new NewBowlListener();
        deleteListener = new DeleteDropListener();
        measuredScreen = false;
        defaultParams = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        setClickable(true);
        setFocusable(true);

        bowls = new LinkedList<BowlView>();
        for (int i = 1; i <= Kitchen.minBowls; i++) {
            BowlView bowl = new BowlView(this.getContext());
            bowl.setColors(Kitchen.assignColor(bowlsIdCounter));
            bowls.add(bowl);
            bowlsIdCounter++;
            this.addView(bowl, defaultParams);
            bowl.setOnTouchListener(selectListener);
        }

        disusedIds = new LinkedList<Integer>();

        newBowl = new BowlView(this.getContext());
        newBowl.setColors(Kitchen.assignColor(bowlsIdCounter));
        this.addView(newBowl, defaultParams);
        newBowl.setOnTouchListener(newListener);
        newBowl.bringToFront();
        newBowl.setText("+");

        trashBowl = new FrameLayout(this.getContext());
        trashBowl.setBackgroundResource(R.drawable.ic_recycle);
        this.addView(trashBowl, defaultParams);
        trashBowl.setVisibility(View.GONE);
        trashBowl.setOnDragListener(deleteListener);
    }

    public void measureView(){
        if(measuredScreen){
            return;
        } else {
            mMeasuredWidth = getMeasuredWidth();
            mMeasuredHeight = getMeasuredHeight();
            int cx = mMeasuredWidth / 2;
            int cy = mMeasuredHeight / 2;
            tableRadius = Math.min(cx, cy);
            centerX = (float)cx;
            centerY = (float)cy;

            double q = ((double) tableRadius * 2.0 * Math.PI)
                    / (double) Kitchen.maxBowls;
            bowlRadius = (int) (q / 2.0);
            tableRadius -= bowlRadius;

            newBowl.setRadius(bowlRadius);
            newBowl.setTextSize(bowlRadius);
            newBowl.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
            newBowl.setPadding(0, -1*bowlRadius/2, 0, 0);
            for(BowlView bv: bowls){
                bv.setRadius(bowlRadius);
            }
            measuredScreen = true;

            newBowl.setX(centerX);
            newBowl.setY(centerY-(bowlRadius/2));

            float s = (float)(2*tableRadius-4*bowlRadius);
            if((s*0.9)>2*bowlRadius){
                s *= 0.9;
            }
            trashBowl.setMinimumHeight((int)s);
            trashBowl.setMinimumWidth((int)s);
            trashBowl.setX(centerX-s/2);
            trashBowl.setY(centerY-s/2);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);
        int d = Math.min(measuredWidth, measuredHeight);
        setMeasuredDimension(d, d);

    }

    private int measure(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.UNSPECIFIED) {
            result = 500;
        } else {
            result = specSize;
        }
        return result;
    }

    public void addRemoveIcons(boolean showAdd){
        if(showAdd){
            if(bowls.size()<Kitchen.maxBowls){
                newBowl.setVisibility(View.VISIBLE);
                trashBowl.setVisibility(View.GONE);
            }
        } else {
            newBowl.setVisibility(View.GONE);
            trashBowl.setVisibility(View.VISIBLE);
        }
    }

    public void clearCenter(){
        newBowl.setVisibility(View.GONE);
        trashBowl.setVisibility(View.GONE);
    }

    private BowlView getNewBowl(){
        BowlView bowl = new BowlView(this.getContext());
        bowl.setColors(newBowl.getColor());
        if(measuredScreen){
            bowl.setRadius(bowlRadius);
        }
        bowl.move(centerX, centerY-(bowlRadius/2));
        this.addView(bowl, defaultParams);
        bowl.setOnTouchListener(selectListener);
        bowl.bringToFront();
        return bowl;
    }

    public void addBowl(){
        BowlView bowl = getNewBowl();

        if(currentDisusedId==-1){
            bowlsIdCounter++;
        } else {
            bowl.setId(currentDisusedId);
        }

        bowls.add(bowl);
        agent.addBowl();
        bowl.formatText();

        if(disusedIds.isEmpty()){
            currentDisusedId = -1;
            newBowl.setColors(Kitchen.assignColor(bowlsIdCounter));
        } else {
            currentDisusedId = disusedIds.removeFirst();
            newBowl.setColors(Kitchen.assignColor(currentDisusedId));
        }
        newBowl.invalidate();

        if(bowls.size()>=Kitchen.maxBowls){
            newBowl.setVisibility(View.GONE);
            newBowl.setOnTouchListener(null);
        }
    }

    public void removeBowl(final BowlView bowl){
        disusedIds.add(bowl.getId());
        bowls.remove(bowl);
        refreshBowls();
        addRemoveIcons(true);

        if(currentDisusedId==-1 && !disusedIds.isEmpty()){
            currentDisusedId = disusedIds.removeFirst();
            newBowl.setColors(Kitchen.assignColor(currentDisusedId));
            newBowl.invalidate();
        }

        if((bowls.size()+1)==Kitchen.maxBowls){
            newBowl.setOnTouchListener(newListener);
        }
    }

    public void refreshBowls() {
        for(BowlView bv: bowls){
            bv.invalidate();
        }
    }

    public void attachBowlAgents(Fragment fragment){
        agent = (BowlsGroupAgent) fragment;
    }

    private class DeleteDropListener implements OnDragListener{

        public boolean deleteBowl(BowlView bowl){
            removeBowl(bowl);
            agent.removeBowl();
            return true;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //no action necessary
                    ((BowlView)event.getLocalState()).setVisibility(View.INVISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    if(v.getId()!=-1){
                        return(false);
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if(v.getId()==-1){
                        final BowlView view = ((BowlView)event.getLocalState());
                        if(event.getResult()){
                            view.post(new Runnable(){
                                @Override
                                public void run() {
                                    clearCenter();
                                    deleteBowl(view);
                                }});

                        } else {
                            view.post(new Runnable(){
                                @Override
                                public void run() {
                                    view.setVisibility(View.VISIBLE);
                                    addRemoveIcons(true);
                                }});
                        }
                    }
                    break;
                default:
                    break;
            }
            return true;
        }

    }

    private class BowlSelectListener implements OnTouchListener{

        public boolean moved = false;
        public float px=0;
        public float py=0;

        public BowlSelectListener(){

        }

        @Override
        public boolean onTouch(View v, MotionEvent move) {
            BowlView bv = (BowlView)v;
            int action = move.getAction();
            if (bowls.size()>Kitchen.minBowls && addRemovable) {
                switch(action){
                    case MotionEvent.ACTION_DOWN:
                        moved = false;
                        px = move.getX();
                        py = move.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        float movedX = Math.abs(move.getX() - px);
                        float movedY = Math.abs(move.getY() - py);

                        if(movedX>10 || movedY>10){
                            addRemoveIcons(false);
                            v.setOnDragListener(deleteListener);
                            ClipData data = ClipData.newPlainText("", "");
                            DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                            v.startDrag(data, shadowBuilder, v, 0);
                            moved = true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }
                return true;
            }
            else {
                return true;
            }
        }

    }

    public class NewBowlListener implements OnTouchListener{

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if(bowls.size()>=Kitchen.maxBowls){
                return false;
            }

            if(!addRemovable){
                return false;
            }

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                BowlView bv = (BowlView)view;
                if(bv==newBowl){
                    addBowl();
                }
                return true;
            }
            else {
                return false;
            }
        }
    }

    public interface BowlsGroupAgent{
        public void addBowl();
        public void removeBowl();
    }

}

