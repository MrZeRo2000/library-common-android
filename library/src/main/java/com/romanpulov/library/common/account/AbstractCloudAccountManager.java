package com.romanpulov.library.common.account;

import android.app.Activity;
import android.content.Context;

public abstract class AbstractCloudAccountManager<T>  {
    protected T mAccountHolder;

    protected final Activity mActivity;
    protected final Context mContext;

    protected T getAccountHolder() {
        return mAccountHolder;
    }

    protected void setAccountHolder(T value) {
        mAccountHolder = value;
    }

    protected abstract T createAccountHolder();

    public interface OnAccountSetupListener {
        void onAccountSetupSuccess();
        void onAccountSetupFailure(String errorText);
    }

    protected OnAccountSetupListener mAccountSetupListener;

    public interface OnAccountSetupItemListener {
        void onSetupItemSuccess(String itemId);
        void onSetupItemFailure(String errorText);
    }

    protected OnAccountSetupItemListener mAccountSetupItemListener;

    public void setOnAccountSetupItemListener(OnAccountSetupItemListener listener) {
        this.mAccountSetupItemListener = listener;
    }

    public void setOnAccountSetupListener(OnAccountSetupListener value) {
        this.mAccountSetupListener = value;
    }

    protected abstract void internalSetupAccount();

    public void setupItemId(String path) {
        if (mAccountSetupItemListener != null) {
            mAccountSetupItemListener.onSetupItemSuccess(path);
        }
    }

    public void setupAccount() {
        if (mAccountSetupListener != null) {
            internalSetupAccount();
        }
    };

    public AbstractCloudAccountManager(Activity activity) {
        mActivity = activity;
        mContext = activity.getApplicationContext();
        mAccountHolder = createAccountHolder();
    }
}
