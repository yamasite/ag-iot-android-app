package com.agora.iotlink.models.device.setting.mydevice;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.agora.baselibrary.base.BaseDialog;
import com.agora.baselibrary.listener.ISingleCallback;
import com.agora.baselibrary.utils.StringUtils;
import com.agora.baselibrary.utils.ToastUtils;
import com.agora.iotlink.R;
import com.agora.iotlink.api.bean.CountryBean;
import com.agora.iotlink.base.AgoraApplication;
import com.agora.iotlink.base.BaseViewBindingActivity;
import com.agora.iotlink.common.Constant;
import com.agora.iotlink.databinding.ActivityDeviceAddUserBinding;
import com.agora.iotlink.dialog.EditNameDialog;
import com.agora.iotlink.manager.PagePathConstant;
import com.agora.iotlink.manager.PagePilotManager;
import com.agora.iotlink.models.device.DeviceViewModel;
import com.alibaba.android.arouter.facade.annotation.Route;

/**
 * 添加共享给其他用户
 * <p>
 * 交付测试前sdk 未提供此功能
 */
@Route(path = PagePathConstant.pageDeviceShareToUserAdd)
public class DeviceShareAddUserActivity extends BaseViewBindingActivity<ActivityDeviceAddUserBinding> {
    /**
     * 设备模块统一ViewModel
     */
    private DeviceViewModel deviceViewModel;
    private EditNameDialog editNameDialog;
    /**
     * 当前选择的国家
     */
    private CountryBean countryBean;

    @Override
    protected ActivityDeviceAddUserBinding getViewBinding(@NonNull LayoutInflater inflater) {
        return ActivityDeviceAddUserBinding.inflate(inflater);
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        deviceViewModel.setLifecycleOwner(this);
        getBinding().tvDeviceName.setText(AgoraApplication.getInstance().getLivingDevice().mDeviceName);

    }

    @Override
    public void initListener() {
        getBinding().tvCountry.setOnClickListener(view -> {
            PagePilotManager.pageSelectCountry(this);
        });
        getBinding().tvAccount.setOnClickListener(view -> showEditNameDialog());
        getBinding().btnFinish.setOnClickListener(view -> deviceViewModel.shareDevice(AgoraApplication.getInstance().getLivingDevice(),
                getBinding().tvAccountValue.getText().toString(), 2, false));
        deviceViewModel.setISingleCallback((type, data) -> {
            if (type == Constant.CALLBACK_TYPE_DEVICE_SHARE_TO_SUCCESS) {
                mHealthActivityManager.popActivity();
            }
        });
    }

    public void showEditNameDialog() {
        if (editNameDialog == null) {
            editNameDialog = new EditNameDialog(this);
            editNameDialog.setDialogTitle(getString(R.string.account));
            editNameDialog.setDialogInputHint(getString(R.string.please_input_account));
            editNameDialog.setOnButtonClickListener(new BaseDialog.OnButtonClickListener() {
                @Override
                public void onLeftButtonClick() {

                }

                @Override
                public void onRightButtonClick() {
                }
            });
            editNameDialog.iSingleCallback = (integer, o) -> {
                if (integer == 0) {
                    if (o instanceof String) {
                        String account = (String) o;
                        if (countryBean == null || countryBean.countryId == 10) {
                            if (!StringUtils.INSTANCE.checkPhoneNum(account)) {
                                ToastUtils.INSTANCE.showToast("请输入正确手机号");
                            } else {
                                getBinding().tvAccountValue.setText(account);
                            }
                        } else {
                            if (!StringUtils.INSTANCE.checkEmailFormat(account)) {
                                ToastUtils.INSTANCE.showToast("请输入正确邮箱");
                            } else {
                                getBinding().tvAccountValue.setText(account);
                            }
                        }
                        if (!TextUtils.isEmpty(getBinding().tvAccountValue.getText().toString())) {
                            getBinding().btnFinish.setEnabled(true);
                        }
                    }
                }
            };
        }
        editNameDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                CountryBean countryBean = (CountryBean) data.getSerializableExtra(Constant.COUNTRY);
                if (countryBean != null) {
                    this.countryBean = countryBean;
                    getBinding().tvCountryValue.setText(countryBean.countryName);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        deviceViewModel.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        deviceViewModel.onStop();
    }
}
