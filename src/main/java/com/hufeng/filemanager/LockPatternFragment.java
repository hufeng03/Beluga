package com.hufeng.filemanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hufeng.filemanager.lock.LockPatternUtils;
import com.hufeng.filemanager.lock.LockPatternView;

import java.util.List;

/**
 * Created by feng on 13-10-3.
 */
public class LockPatternFragment extends BaseFragment implements LockPatternView.OnPatternListener {

    private LockPatternView mLockPatternView;
    private LockPatternUtils mLockPatternUtils;
    private TextView mLockPatternTip;
    private boolean mVerify = true;
    private boolean mSetSecond = false;
    private String mFirstPatternString = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lock_pattern_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLockPatternView = (LockPatternView) view.findViewById(R.id.lock_pattern_view);
        mLockPatternTip = (TextView)view.findViewById(R.id.lock_pattern_tip);
        initLockView();
    }

    private void initLockView(){

        mLockPatternUtils = new LockPatternUtils(getActivity());

        mVerify = !TextUtils.isEmpty(mLockPatternUtils.getLockPaternString());

        if(mVerify){
            mLockPatternTip.setText(R.string.lock_verify_password);
            mLockPatternTip.setTextColor(getResources().getColor(R.color.gray));
        }else{
            mLockPatternTip.setText(R.string.lock_set_password);
            mLockPatternTip.setTextColor(getResources().getColor(R.color.gray));
        }

        mLockPatternView.setOnPatternListener(this);
    }

    public void onPatternStart() {

    }

    public void onPatternDetected(List<LockPatternView.Cell> pattern) {
        if(mVerify){
            int result = mLockPatternUtils.checkPattern(pattern);
            if (result!= 1) {
                if(result==0){
                    mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
//							Toast.makeText(SafeLockActivity.this, "密码错误", Toast.LENGTH_SHORT)
//							.show();
                    mLockPatternTip.setText(R.string.lock_pattern_tip_input_error);
                    mLockPatternTip.setTextColor(getResources().getColor(R.color.red));
                }else{
                    mLockPatternView.clearPattern();
//							Toast.makeText(SafeLockActivity.this, "请设置密码", Toast.LENGTH_SHORT)
//							.show();
                    mLockPatternTip.setText(R.string.lock_pattern_tip_reset);
                    mLockPatternTip.setTextColor(getResources().getColor(R.color.gray));
                    mVerify = false;
//                            getSupportActionBar().setTitle(R.string.lock_set_password);
                }

            } else {
//						Toast.makeText(SafeLockActivity.this, "密码正确", Toast.LENGTH_SHORT)
//								.show();
                mLockPatternTip.setText(R.string.lock_pattern_tip_input_match);
                mLockPatternTip.setTextColor(getResources().getColor(R.color.gray));
                if (mLockPatternListener != null) {
                    mLockPatternListener.onPatternCheckSuccess();
                }
            }
        }else{
            if(mSetSecond){
                String secondPatternString = mLockPatternUtils.patternToString(pattern);
                if(secondPatternString.equals(mFirstPatternString)){
                    mLockPatternUtils.saveLockPattern(pattern);
//							Toast.makeText(SafeLockActivity.this, "密码设置成功", Toast.LENGTH_SHORT)
//							.show();
                    mLockPatternTip.setText(R.string.lock_pattern_tip_set_ok);
                    mLockPatternTip.setTextColor(getResources().getColor(R.color.gray));
//							mLockPatternView.clearPattern();
//							mVerify = true;
//							getSupportActionBar().setTitle(R.string.lock_verify_password);
                    if (mLockPatternListener != null) {
                        mLockPatternListener.onPatternCheckSuccess();
                    }
                }else{
                    mSetSecond = false;
                    mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
//							Toast.makeText(SafeLockActivity.this, "两次设置不一致，请重新设置", Toast.LENGTH_SHORT)
//							.show();
                    mLockPatternTip.setText(R.string.lock_pattern_tip_set_not_match);
                    mLockPatternTip.setTextColor(getResources().getColor(R.color.gray));
                    //	mLockPatternView.clearPattern();
                }
            }else{
                mFirstPatternString = mLockPatternUtils.patternToString(pattern);
//						Toast.makeText(SafeLockActivity.this, "请再次输入要设置的密码", Toast.LENGTH_SHORT)
//						.show();
                mLockPatternTip.setText(R.string.lock_pattern_tip_set_again);
                mLockPatternTip.setTextColor(getResources().getColor(R.color.gray));
                mLockPatternView.clearPattern();
                mSetSecond = true;
            }
        }

    }

    public void onPatternCleared() {

    }

    public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

    }



    public interface LockPatternListener{
        public void onPatternSetSuccess();
        public void onPatternCheckSuccess();
    }

    private LockPatternListener mLockPatternListener = null;
    public void setLockPatternListener(LockPatternListener listener) {
        mLockPatternListener = listener;
    }



}
