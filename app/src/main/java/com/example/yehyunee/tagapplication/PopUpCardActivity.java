package com.example.yehyunee.tagapplication;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.yehyunee.tagapplication.adapter.MainBackCommentAdapter;
import com.example.yehyunee.tagapplication.data.MainItem;

import java.util.ArrayList;

public class PopUpCardActivity extends Activity implements FragmentManager.OnBackStackChangedListener{

    private static boolean mShowingBack = false;

    private static int mFrontHeight = 0;
    private static int mFrontWidth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 타이틀바 제거


        setContentView(R.layout.activity_pop_up_card);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new CardFrontFragment())
                    .commit();
        } else {
            mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
        }
        getFragmentManager().addOnBackStackChangedListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.add(Menu.NONE, R.id.action_flip, Menu.NONE,
                mShowingBack ?
                        R.string.main_pop_up_card_front :
                        R.string.main_pop_up_card_back);
        item.setIcon(mShowingBack ?
                R.drawable.account :
                R.drawable.arrow);
        // 아무거나 집어넣었음.
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public void onBackStackChanged() {
        mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
        // When the back stack changes, invalidate the options menu (action bar).
        invalidateOptionsMenu();
    }

    public static class CardFrontFragment extends Fragment implements View.OnClickListener {
        public CardFrontFragment() {}

        private LinearLayout mFrontWholeLayout;

        private ImageView mCommentImg;
        private ImageView mMainImg;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_card_front, container, false);
            mFrontWholeLayout = (LinearLayout) view.findViewById(R.id.front_whole_layout);
            mFrontWholeLayout.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            //뷰의 생성된 후 크기와 위치 구하기
                            mFrontWidth = mFrontWholeLayout.getWidth();
                            mFrontHeight = mFrontWholeLayout.getHeight();
                        }
                    });

            mCommentImg = (ImageView) view.findViewById(R.id.main_comment_front);
            mMainImg = (ImageView) view.findViewById(R.id.tag_main_img);


            mCommentImg.setOnClickListener(this);

            return view;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.main_comment_front:
                    flipCard();
                    break;
            }
        }

        private void flipCard() {
            // Flip to the back.
            mShowingBack = true;
            getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.animator.flip_right_in, R.animator.flip_right_out,
                            R.animator.flip_left_in, R.animator.flip_left_out)
                    .replace(R.id.container, new CardBackFragment())
                    // Add this transaction to the back stack, allowing users to press Back
                    // to get to the front of the card.
                    .addToBackStack(null)
                    // Commit the transaction.
                    .commit();
        }
    }


    public static class CardBackFragment extends Fragment implements View.OnClickListener{

        private RelativeLayout mBackWholeLayout;

        private ImageView mBacktoFront;
        private RecyclerView mCommentMainRecyclerview;
        private ArrayList<MainItem> mItems;
        private MainBackCommentAdapter mCommentAdapter;
        public CardBackFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_card_back, container, false);

            mBacktoFront = (ImageView) view.findViewById(R.id.back_to_front_img);
            mBackWholeLayout = (RelativeLayout) view.findViewById(R.id.back_whole_layout);

            // Front화면과 동일하게 높이값 설정한다.
            mBackWholeLayout.setLayoutParams(new RelativeLayout.LayoutParams(mFrontWidth, mFrontHeight));

            mCommentMainRecyclerview = (RecyclerView) view.findViewById(R.id.main_comment_back_recyclerview);
            mCommentMainRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            mCommentAdapter = new MainBackCommentAdapter(mItems);
            mCommentMainRecyclerview.setAdapter(mCommentAdapter);

            mBacktoFront.setOnClickListener(this);

            return view;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.back_to_front_img:
                    getFragmentManager().popBackStack();
                    break;
            }
        }
    }
}
