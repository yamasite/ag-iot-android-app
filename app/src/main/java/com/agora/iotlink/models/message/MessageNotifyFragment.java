package com.agora.iotlink.models.message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agora.iotlink.base.BaseViewBindingFragment;
import com.agora.iotlink.common.Constant;
import com.agora.iotlink.databinding.FagmentMessageNotifyBinding;
import com.agora.iotlink.manager.PagePathConstant;
import com.agora.iotlink.manager.PagePilotManager;
import com.agora.iotlink.models.message.adapter.MessageNotifyAdapter;
import com.agora.iotsdk20.IotAlarm;
import com.agora.iotsdk20.IotDevMessage;
import com.agora.iotsdk20.IotDevMsgPage;
import com.alibaba.android.arouter.facade.annotation.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * 通知消息
 */
@Route(path = PagePathConstant.pageMessageNotify)
public class MessageNotifyFragment extends BaseViewBindingFragment<FagmentMessageNotifyBinding> {
    /**
     * 消息ViewModel
     */
    private MessageViewModel messageViewModel;


    private MessageNotifyAdapter messageNotifyAdapter;
    private ArrayList<IotDevMessage> mMessages = new ArrayList<>();

    @NonNull
    @Override
    protected FagmentMessageNotifyBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FagmentMessageNotifyBinding.inflate(inflater);
    }

    @Override
    public void initView() {
        messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        messageViewModel.setLifecycleOwner(this);
        messageNotifyAdapter = new MessageNotifyAdapter(mMessages);
        getBinding().rlMsgList.setAdapter(messageNotifyAdapter);
        getBinding().rlMsgList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void requestData() {
        messageViewModel.requestAllNotificationMgr();
    }

    @Override
    public void initListener() {
        messageViewModel.setISingleCallback((type, data) -> {
            if (type == Constant.CALLBACK_TYPE_MESSAGE_NOTIFY_QUERY_RESULT) {
                if (data instanceof IotDevMsgPage) {
                    mMessages.clear();
                    mMessages.addAll(((IotDevMsgPage) data).mDevMsgList);
                    List<Long> list = new ArrayList<>();
                    for (IotDevMessage iotDevMessage : mMessages) {
                        if (iotDevMessage.mStatus == 0) {
                            list.add(iotDevMessage.mMessageId);
                        }
                    }
                    if (!list.isEmpty()) {
                        messageViewModel.markNotifyMessage(list);
                    }
                    getBinding().rlMsgList.post(() -> {
                        messageNotifyAdapter.notifyDataSetChanged();
                        if (mMessages.isEmpty()) {
                            getBinding().rlMsgList.setVisibility(View.GONE);
                            getBinding().btnEdit.setVisibility(View.GONE);
                            getBinding().tvMessageNo.setVisibility(View.VISIBLE);
                        } else {
                            getBinding().rlMsgList.setVisibility(View.VISIBLE);
                            getBinding().btnEdit.setVisibility(View.GONE);
                            getBinding().tvMessageNo.setVisibility(View.GONE);
                        }
                    });
                }
            } else if (type == Constant.CALLBACK_TYPE_MESSAGE_ALARM_DETAIL_RESULT) {
                if (data instanceof IotAlarm) {
                    PagePilotManager.pagePlayMessage((IotAlarm) data);
                }
            }
        });
        getBinding().btnEdit.setOnClickListener(view -> {
            changeEditStatus(!messageNotifyAdapter.isEdit);
        });
//        getBinding().btnDoDelete.setOnClickListener(view -> {
//            List<String> deletes = new ArrayList<>();
//            for (IotDevMessage iotDevMessage : mMessages) {
//                if (iotNotification.mMarkFlag == 1) {
//                    deletes.add(iotNotification.mNotificationId);
//                }
//            }
//            messageViewModel.requestDeleteNotifyMgr(deletes);
//            messageNotifyAdapter.notifyDataSetChanged();
//            changeEditStatus(false);
//        });
//        getBinding().cbAllSelect.setOnCheckedChangeListener((compoundButton, b) -> {
//            for (IotNotification iotNotification : messageNotifyAdapter.getDatas()) {
//                if (b) {
//                    iotNotification.mMarkFlag = 1;
//                } else {
//                    iotNotification.mMarkFlag = 0;
//                }
//            }
//            messageNotifyAdapter.notifyDataSetChanged();
//        });
    }

    private void changeEditStatus(boolean toEdit) {
        if (!toEdit) {
            messageNotifyAdapter.isEdit = false;
            getBinding().bgBottomDel.setVisibility(View.GONE);
            getBinding().btnEdit.setText("编辑");
        } else {
            messageNotifyAdapter.isEdit = true;
            getBinding().bgBottomDel.setVisibility(View.VISIBLE);
            getBinding().btnEdit.setText("完成");
        }
        messageNotifyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        messageViewModel.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        messageViewModel.onStop();
    }
}
