package com.agora.iotlink.models.player.called;

import android.view.SurfaceView;

import com.agora.baselibrary.base.BaseViewModel;
import com.agora.baselibrary.utils.ToastUtils;
import com.agora.iotlink.common.Constant;
import com.agora.iotsdk20.AIotAppSdkFactory;
import com.agora.iotsdk20.ErrCode;
import com.agora.iotsdk20.ICallkitMgr;
import com.agora.iotsdk20.IotDevice;

/**
 * 主页接收消息 viewModel
 */
public class CalledInComingViewModel extends BaseViewModel implements ICallkitMgr.ICallback {

    public void onStart() {
        AIotAppSdkFactory.getInstance().getCallkitMgr().registerListener(this);
    }

    public void onStop() {
        AIotAppSdkFactory.getInstance().getCallkitMgr().unregisterListener(this);
    }

    /**
     * 设置视频显示View控件
     */
    public void setPeerVideoView(SurfaceView peerView) {
        AIotAppSdkFactory.getInstance().getCallkitMgr().setPeerVideoView(peerView);
    }

    /**
     * 挂断当前通话
     */
    public void callHangup() {
        int ret = AIotAppSdkFactory.getInstance().getCallkitMgr().callHangup();
        if (ret != ErrCode.XOK && ret != -10004) {
            ToastUtils.INSTANCE.showToast("不能挂断当前通话, 错误码: " + ret);
        }
    }

    /**
     * 接通通话
     */
    public void callAnswer() {
        int ret = AIotAppSdkFactory.getInstance().getCallkitMgr().callAnswer();
        if (ret != ErrCode.XOK) {
            ToastUtils.INSTANCE.showToast("不能接通通话, 错误码: " + ret);
        }
    }

    /**
     * 变声
     *
     * @param effectId ICallkitMgr.AudioEffectId.NORMAL  正常
     *                 ICallkitMgr.AudioEffectId.OLDMAN 老人
     *                 ICallkitMgr.AudioEffectId.BABYBOY 小孩
     *                 ICallkitMgr.AudioEffectId.BABYGIRL 萝莉
     *                 ICallkitMgr.AudioEffectId.ZHUBAJIE 猪八戒
     *                 ICallkitMgr.AudioEffectId.ETHEREAL
     *                 ICallkitMgr.AudioEffectId.HULK
     */
    public void setAudioEffect(ICallkitMgr.AudioEffectId effectId) {
        AIotAppSdkFactory.getInstance().getCallkitMgr().setAudioEffect(effectId);
    }

    /**
     * 对端设备挂断呼叫或通话
     *
     * @param iotDevice : 对端设备
     */
    @Override
    public void onPeerHangup(IotDevice iotDevice) {
        getISingleCallback().onSingleCallback(Constant.CALLBACK_TYPE_PLAYER_CALL_HANG_UP, iotDevice);
    }
}
