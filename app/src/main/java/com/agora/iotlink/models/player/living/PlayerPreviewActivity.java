package com.agora.iotlink.models.player.living;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.agora.baselibrary.utils.StringUtils;
import com.agora.iotlink.R;
import com.agora.iotlink.base.AgoraApplication;
import com.agora.iotlink.base.BaseViewBindingActivity;
import com.agora.iotlink.databinding.ActivityPreviewPlayBinding;
import com.agora.iotlink.manager.PagePathConstant;
import com.agora.iotlink.manager.PagePilotManager;
import com.agora.iotlink.models.player.adapter.ViewPagerAdapter;
import com.agora.iotlink.utils.AnimUtils;
import com.agora.iotsdk20.IotDevice;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

/**
 * 实时预览 播放页
 */
@Route(path = PagePathConstant.pagePreviewPlay)
public class PlayerPreviewActivity extends BaseViewBindingActivity<ActivityPreviewPlayBinding> {

    @Override
    protected ActivityPreviewPlayBinding getViewBinding(@NonNull LayoutInflater inflater) {
        return ActivityPreviewPlayBinding.inflate(inflater);
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        getWindow().setBackgroundDrawableResource(R.color.black);
        getBinding().viewPager.setAdapter(new ViewPagerAdapter(this));
        AnimUtils.radioGroupLineAnim(getBinding().rBtnFunction, getBinding().lineCheck);
    }

    @Override
    public void initListener() {
        getBinding().titleView.setRightIconClick(view -> {
            if ("0".equals(AgoraApplication.getInstance().getLivingDevice().mSharer)) {
                PagePilotManager.pageDeviceSetting();
            } else {
                //分享的设备走这里
                PagePilotManager.pageShareDeviceSetting();
            }
        });
        getBinding().radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {

        });
        getBinding().rBtnFunction.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                getBinding().viewPager.setCurrentItem(0);
            }
        });
        getBinding().rBtnMessage.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                getBinding().viewPager.setCurrentItem(1);
            }
        });
        getBinding().viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    getBinding().rBtnFunction.setChecked(true);
                    AnimUtils.radioGroupLineAnim(getBinding().rBtnFunction, getBinding().lineCheck);
                } else {
                    getBinding().rBtnMessage.setChecked(true);
                    AnimUtils.radioGroupLineAnim(getBinding().rBtnMessage, getBinding().lineCheck);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getBinding().titleView.getVisibility() == View.GONE) {
                if (getBinding().viewPager.getCurrentItem() == 0) {
                    ((PlayerFunctionListFragment) ((ViewPagerAdapter) getBinding().viewPager.getAdapter())
                            .registeredFragments.get(0)).onBtnBack();
                } else {
                    ((PlayerMessageListFragment) ((ViewPagerAdapter) getBinding().viewPager.getAdapter())
                            .registeredFragments.get(1)).onBtnBack();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showTitle() {
        getBinding().titleView.setVisibility(View.VISIBLE);
        getBinding().viewPager.setUserInputEnabled(true);
        getBinding().radioGroup.setVisibility(View.VISIBLE);
        getBinding().lineCheck.setVisibility(View.VISIBLE);
    }

    public void hideTitle() {
        getBinding().titleView.setVisibility(View.GONE);
        getBinding().viewPager.setUserInputEnabled(false);
        getBinding().radioGroup.setVisibility(View.GONE);
        getBinding().lineCheck.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IotDevice livingDevice = AgoraApplication.getInstance().getLivingDevice();
        if (!TextUtils.isEmpty(StringUtils.INSTANCE.getBase64String(livingDevice.mDeviceName))) {
            getBinding().titleView.setTitle(StringUtils.INSTANCE.getBase64String(livingDevice.mDeviceName));
        } else {
            getBinding().titleView.setTitle("");
        }
    }

    @Override
    public boolean isBlackDarkStatus() {
        return false;
    }
}
