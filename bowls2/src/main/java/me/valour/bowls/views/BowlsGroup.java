package me.valour.bowls.views;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    boolean measuredScreen;
    boolean selectReady = false;
    boolean addRemovable = true;

    BowlSelectListener selectListener;
    DeleteDropListener deleteListener;

    LinkedList<BowlView> bowls;
    UserBowlAdapter usersAdapter;


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
        measureView();

        double angleDelta = Math.PI*2.0/usersAdapter.getCount();
        double topX = 0;
        double topY = -1.0*tableRadius;

        ViewGroup.LayoutParams defaultParams = new ViewGroup.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        if (getChildCount() == 0) {
            for(int j = 0; j<usersAdapter.getCount(); j++){
                BowlView bowl = (BowlView) usersAdapter.getView(j, null, this);
                addViewInLayout(bowl, j, defaultParams);
                bowl.bringToFront();
                double angle = angleDelta*j;
                double px = Math.cos(angle)*topX - Math.sin(angle)*topY + centerX;
                double py = Math.sin(angle)*topX - Math.cos(angle)*topY + centerY;
                bowl.setAngle(angle);
                bowl.move((float)px, (float)py);
            }
        }

            super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public View getSelectedView() {
        return null;
    }

    @Override
    public void setSelection(int position) {

    }

    private void init() {
        selectListener = new BowlSelectListener();
        deleteListener = new DeleteDropListener();
        measuredScreen = false;

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

            measuredScreen = true;

            float s = (float)(2*tableRadius-4*bowlRadius);
            if((s*0.9)>2*bowlRadius){
                s *= 0.9;
            }
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
       /*
        for(User u: users){
            u.view.setSelected(true);
            u.view.unfade();
        }
        */
    }

    public void enableActions(){
        addRemovable = true;
    }

    public void disableActions(){
        selectReady = false;
        addRemovable = false;
    }


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

}