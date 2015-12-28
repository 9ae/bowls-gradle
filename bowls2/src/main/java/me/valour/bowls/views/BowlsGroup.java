package me.valour.bowls.views;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.ClipData;
import android.content.Context;

import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import me.valour.bowls.adapters.UserBowlAdapter;
import me.valour.bowls.services.Kitchen;
import me.valour.bowls.models.User;

public class BowlsGroup extends AdapterView<UserBowlAdapter> {

    int mMeasuredWidth;
    int mMeasuredHeight;
    float centerX=0;
    float centerY=0;
    int tableRadius;
    int bowlRadius;
    int bowlDiameter;
    boolean measuredScreen;
    boolean selectReady = false;
    boolean addRemovable = true;

    //BowlSelectListener selectListener;
    //DeleteDropListener deleteListener;

    UserBowlAdapter usersAdapter;

    ViewGroup.LayoutParams defaultParams;


    public BowlsGroup(Context context) {
        super(context);
        init();
    }

    public BowlsGroup(Context context, AttributeSet ats, int ds) {
        super(context, ats, ds);
        init();
    }

    public BowlsGroup(Context context, AttributeSet attr) {
        super(context, attr);

        init();
    }

    @Override
    public UserBowlAdapter getAdapter() {
        return usersAdapter;
    }

    @Override
    public void setAdapter(UserBowlAdapter adapter) {
        this.usersAdapter = adapter;
        removeAllViewsInLayout();
        requestLayout();
    }

    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom){
        super.onLayout(changed, left, top, right, bottom);
        measureView();

        int usersCount = usersAdapter.getCount();
        double angleDelta = Math.PI*2.0/(double)usersCount;

        if (getChildCount() != usersCount) {
            for(int j = 0; j<usersCount; j++){
                View convertView = this.getChildAt(j);
                BowlView bowl = (BowlView) usersAdapter.getView(j, convertView, this);
                if (convertView==null) {
                    addViewInLayout(bowl, j, defaultParams, true);
                    bowl.measure(bowlDiameter, bowlDiameter);
                    bowl.layout(0, 0, bowlDiameter, bowlDiameter);
                }

                double angle = angleDelta*j;
                double px = centerX + Math.sin(angle)*tableRadius;
                double py = centerY + Math.cos(angle)*tableRadius;

                Log.i("vars", "["+j+"] @ " +Math.toDegrees(angle) + " (" + (Math.sin(angle)>0 ?"+":"-") +
                        ","+(Math.cos(angle)>0 ?"+":"-")+")" );

                bowl.setAngle(angle);
                bowl.move((float) px, (float) py);
                bowl.bringToFront();
            }
        }
        Log.i("vars", "has "+getChildCount()+" child views");
    }

    @Override
    public View getSelectedView() {
        return null;
    }

    @Override
    public void setSelection(int position) {

    }

    private void init() {
//        selectListener = new BowlSelectListener();
//        deleteListener = new DeleteDropListener();
        measuredScreen = false;

        defaultParams = new ViewGroup.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

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

            Log.i("vars", centerX+","+centerY);

            double q = ((double) tableRadius * 2.0 * Math.PI)
                    / (double) Kitchen.maxBowls;
            bowlDiameter = (int) q;
            bowlRadius = (int) (q / 2.0);
            tableRadius -= bowlRadius;

            measuredScreen = true;

            if(usersAdapter!=null){
                usersAdapter.bowlRadius = bowlRadius;
            }
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
/*
    private BowlView getNewBowl(){
        BowlView bowl = new BowlView(this.getContext());

        if(measuredScreen){
            bowl.setRadius(bowlRadius);
        }
        bowl.move(centerX, centerY-(bowlRadius/2));

        bowl.setOnTouchListener(selectListener);
        bowl.bringToFront();
        return bowl;
    }

    public void addBowl(){
        BowlView bowl = getNewBowl();

        bowls.add(bowl);
    //        agent.addUser(bowl.user);
        bowl.formatText();


        if(bowls.size()>=Kitchen.maxBowls){
            //TODO: flag that no more new bowls can be added
        }
    }

    public void removeBowl(final BowlView bowl){

        bowls.remove(bowl);
        refreshBowls();

        if((bowls.size()+1)==Kitchen.maxBowls){
           //TODO: allow bowl to be added again
        }
    }

    public void refreshBowls() {
        for(BowlView bv: bowls){
            bv.formatText();
            bv.invalidate();
        }
    }

    public List<Integer> getBowlViewIds() {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for (BowlView bw : bowls) {
            ids.add(bw.getId());
        }
        return ids;
    }

    public List<User> getBowlUsers() {
        ArrayList<User> users = new ArrayList<User>();
        for (BowlView bw : bowls) {
            users.add(bw.user);
        }
        return users;
    }

    public void bowlsFocus(boolean unfade) {
        for (BowlView bv : bowls) {
            if (unfade) {
                bv.unfade();
            } else {
                bv.fade();
            }
        }
    }


    public void clearSelection(){
        selectListener.selected.clear();
        for(BowlView bv: bowls){
            bv.setSelected(false);
        }
    }

    public void readyBowlSelect(){
        selectReady = true;
        clearSelection();
        bowlsFocus(false);

    }

    public void stopBowlSelect(){
        selectReady = false;
        clearSelection();
        bowlsFocus(true);

    }

    public List<User> getSelectedUsers(){
        return selectListener.selected;
    }

    public void manualSelect(List<User> users){
        selectListener.selected.addAll(users);

        for(User u: users){
            u.view.setSelected(true);
            u.view.unfade();
        }

    }

    public void enableActions(){
        addRemovable = true;
    }

    public void disableActions(){
        selectReady = false;
        addRemovable = false;
    }
*/
/*
    private class DeleteDropListener implements OnDragListener{

        public boolean deleteBowl(BowlView bowl){

            removeBowl(bowl);

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

                                    deleteBowl(view);
                                }});

                        } else {
                            view.post(new Runnable(){
                                @Override
                                public void run() {
                                    view.setVisibility(View.VISIBLE);

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

        public List<User> selected;
        public boolean moved = false;
        public float px=0;
        public float py=0;

        public BowlSelectListener(){
            selected = new ArrayList<User>();
        }

        @Override
        public boolean onTouch(View v, MotionEvent move) {
            BowlView bv = (BowlView)v;
            int action = move.getAction();
            if(selectReady){
                if(action==MotionEvent.ACTION_DOWN){
                    if(bv.toggleSelected()){
                        selected.remove(bv.user);
                    } else {
                        selected.add(bv.user);
                    }
                } return false;
            } else if (bowls.size()>Kitchen.minBowls && addRemovable) {
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
                if(action==MotionEvent.ACTION_UP){

                }
                return true;
            }
        }

    }
*/
}