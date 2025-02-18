package com.agora.iotlink.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//专门用于维护activity生命周期
public class ActivityLifecycleCallback implements Application.ActivityLifecycleCallbacks, ActivityState {
    private static final String TAG = ActivityLifecycleCallback.class.getSimpleName();
    private List<Activity> activityList = new ArrayList<>();
    private List<Activity> resumeActivity = new ArrayList<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        Log.d(TAG, "onActivityCreated "+activity.getLocalClassName());
        activityList.add(0, activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d(TAG, "onActivityStarted "+activity.getLocalClassName());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(TAG, "onActivityResumed "+activity.getLocalClassName());
        if (resumeActivity.isEmpty()) {
            //APP首次或者从后台启动
            Log.d(TAG, "APP启动到前台了");
        }
        if (!resumeActivity.contains(activity)) {
            resumeActivity.add(activity);
            restartSingleInstanceActivity(activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(TAG, "onActivityPaused "+activity.getLocalClassName());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(TAG, "onActivityStopped "+activity.getLocalClassName());
        resumeActivity.remove(activity);
        if(resumeActivity.isEmpty()) {
            Log.d(TAG, "在后台了");
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        Log.d(TAG, "onActivitySaveInstanceState "+activity.getLocalClassName());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG, "onActivityDestroyed "+activity.getLocalClassName());
        activityList.remove(activity);
    }

    @Override
    public Activity current() {
        return activityList.size()>0 ? activityList.get(0):null;
    }

    @Override
    public List<Activity> getActivityList() {
        return activityList;
    }

    @Override
    public int count() {
        return activityList.size();
    }

    @Override
    public boolean isFront() {
        return resumeActivity.size() > 0;
    }

    /**
     * 跳转到目标activity
     * @param cls
     */
    public void skipToTarget(Class<?> cls, Map<String, String> intent_args) {
        if(activityList != null && activityList.size() > 0) {
            Intent intent = new Intent(current(), cls);
            if ((intent_args != null) && !intent_args.isEmpty()) {
                Iterator<Map.Entry<String, String>> it = intent_args.entrySet().iterator();
                Map.Entry<String, String> entry;
                while (it.hasNext()) {
                    entry = it.next();
                    intent.putExtra(entry.getKey(), entry.getValue());
                }
            }
            current().startActivity(intent);
        }
    }

    /**
     * 关闭目标activity
     * @param cls
     */
    public void finishTarget(Class<?> cls) {
        if(activityList != null && !activityList.isEmpty()) {
            for (Activity activity : activityList) {
                if(activity.getClass() == cls) {
                    activity.finish();
                }
            }
        }
    }

    /**
     * 判断app是否在前台
     * @return
     */
    public boolean isOnForeground() {
        return resumeActivity != null && !resumeActivity.isEmpty();
    }


    /**
     * 用于按下home键，点击图标，检查启动模式是singleInstance，且在activity列表中首位的Activity
     * 下面的方法，专用于解决启动模式是singleInstance, 为开启悬浮框的情况
     * @param activity
     */
    private void restartSingleInstanceActivity(Activity activity) {
        boolean isClickByFloat = activity.getIntent().getBooleanExtra("isClickByFloat", false);
        if(isClickByFloat) {
            return;
        }
        //刚启动，或者从桌面返回app
        //至少需要activityList中有两个activity
        if(resumeActivity.size() == 1 && activityList.size() > 1) {
            Activity topActivity = activityList.get(0);
            if(!topActivity.isFinishing()
                    && topActivity != activity
                    && topActivity.getTaskId() != activity.getTaskId()
            ){
                Log.d(TAG, "启动了activity = " + topActivity.getClass().getName());
                activity.startActivity(new Intent(activity, topActivity.getClass()));
            }
        }
    }

    /**
     * 此方法用于设置启动模式为singleInstance的activity调用
     * 用于解决点击悬浮框后，然后finish当前的activity，app回到桌面的问题
     * 需要如下两个权限：
     *     <uses-permission android:name="android.permission.GET_TASKS" />
     *     <uses-permission android:name="android.permission.REORDER_TASKS"/>
     * @param activity
     */
    public void makeMainTaskToFront(Activity activity) {
        //当前activity正在finish，且可见的activity列表中只有这个正在finish的activity,且没有销毁的activity个数大于等于2
        if(activity.isFinishing() && resumeActivity.size() == 1 && resumeActivity.get(0) == activity && activityList.size() > 1) {
            ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(20);
            for(int i = 0; i < runningTasks.size(); i++) {
                ActivityManager.RunningTaskInfo taskInfo = runningTasks.get(i);
                ComponentName topActivity = taskInfo.topActivity;
                //判断是否是相同的包名
                if(topActivity != null && topActivity.getPackageName().equals(activity.getPackageName())) {
                    int taskId;
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        taskId = taskInfo.taskId;
                    }else {
                        taskId = taskInfo.id;
                    }
                    //将任务栈置于前台
                    Log.e(TAG, "执行moveTaskToFront，current activity:" + activity.getClass().getName());
                    manager.moveTaskToFront(taskId, ActivityManager.MOVE_TASK_WITH_HOME);
                }
            }
        }
    }
}

