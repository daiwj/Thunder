package com.example.thunder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.thunder.download.UrlInfo;
import com.yuntianhe.thunder.Thunder;
import com.yuntianhe.thunder.download.DownloadInfo;
import com.yuntianhe.thunder.listener.TaskStateListener;
import com.yuntianhe.thunder.listener.TaskStateMissed;
import com.yuntianhe.thunder.task.TaskInfo;
import com.yuntianhe.thunder.task.TaskState;
import com.yuntianhe.thunder.util.ThunderFileUtil;
import com.yuntianhe.thunder.util.ThunderLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    RecyclerView mRecyclerView;
    Button btnStartAll;
    Button btnPauseAll;
    Button btnCancelAll;

    Thunder mThunder = new Thunder();

    private UrlInfo[] urls = new UrlInfo[]{
//            new UrlInfo("图片1", "http://up.enterdesk.com/edpic_source/41/c7/a8/41c7a87fc4b03e6d15be6db8b15980a1.jpg"),
//            new UrlInfo("图片2", "http://pic1.win4000.com/wallpaper/6/59bcc088a7c7e.jpg"),
//            new UrlInfo("图片3", "http://attach.bbs.miui.com/forum/201504/05/094406wb74eokgpu7fuxnz.jpg"),
//            new UrlInfo("图片4", "http://pic.3h3.com/up/2014-6/20146614141119032572.jpg"),
//            new UrlInfo("图片5", "http://p0.qhimgs4.com/t010faaec8ae2adde1d.jpg"),
//            new UrlInfo("图片6", "http://img.mp.sohu.com/upload/20170603/e68b7ae76640429da2d74744dd0b4c42_th.png"),
//            new UrlInfo("图片7", "https://img.3dmgame.com/uploads/images2/news/20190925/1569385959_397635.jpg"),
//            new UrlInfo("图片8", "http://img.3dmgame.com/uploads/images2/news/20191016/1571200728_394787.jpg"),
            new UrlInfo("知乎", "http://gdown.baidu.com/data/wisegame/4e4d364abdfa3016/zhihu_1706.apk"),
            new UrlInfo("QQ音乐", "http://gdown.baidu.com/data/wisegame/320542b459bb1caa/QQyinle_1148.apk"),
            new UrlInfo("腾讯视频", "http://gdown.baidu.com/data/wisegame/ee9a757a506866d8/tengxunshipin_20239.apk"),
            new UrlInfo("高德地图", "http://gdown.baidu.com/data/wisegame/feee45dd5e3b15cb/gaodedituinteldingzhiban_521.apk"),
            new UrlInfo("掌上百度", "http://gdown.baidu.com/data/wisegame/7fe67cfb52012e6c/baidu_96228608.apk"),
            new UrlInfo("微信", "http://gdown.baidu.com/data/wisegame/86474a7bc4a51adc/weixin_1540.apk"),
            new UrlInfo("XMind-For-Mac", "http://dl2.xmind.cn/XMind-ZEN-Update-2019-for-macOS-9.1.3.201812042238.dmg"),
            new UrlInfo("优酷视频", "http://gdown.baidu.com/data/wisegame/c961a2ba2e09f72b/youkushipin_208.apk"),
            new UrlInfo("支付宝", "http://gdown.baidu.com/data/wisegame/97fe39d58260e1c8/zhifubao_180.apk"),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycler_view);
        btnStartAll = findViewById(R.id.btn_start_all);
        btnPauseAll = findViewById(R.id.btn_pause_all);
        btnCancelAll = findViewById(R.id.btn_cancel_all);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 10000);
            }
        }

        Thunder.setLogEnable(true);

        final List<TaskInfo> infoList = new ArrayList<>();

        for (int i = 0, size = urls.length; i < size; i++) {
            UrlInfo urlInfo = urls[i];
            final DownloadInfo info = new DownloadInfo();
            info.setUrl(urlInfo.getUrl());
            info.setFilePath(ThunderFileUtil.makeFilePath(this, ThunderFileUtil.getFileNameWithSuffix(urlInfo.getUrl())));
            info.setEnableRange(true);
            info.setEnableRename(false);
            info.setTaskName(urlInfo.getName());
            info.setTaskStateMissed(new TaskStateMissed() {
                @Override
                public void onComplete(String filePath) {
                    MyAdapter adapter = (MyAdapter) mRecyclerView.getAdapter();
                    adapter.remove(info);
                    adapter.notifyItemRemoved(infoList.indexOf(info));
                }
            });
            infoList.add(info);
        }

        mRecyclerView.setItemViewCacheSize(0);
        mRecyclerView.setAdapter(new MyAdapter(infoList));

        btnStartAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThunder.startAll(infoList);
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
        btnPauseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThunder.pauseAll(infoList);
            }
        });
        btnCancelAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThunder.cancel(infoList);
            }
        });

    }

    class MyAdapter extends RecyclerView.Adapter<VH> {

        final String fileUrl = "http://img0.pconline.com.cn/pconline/1410/17/5585300_04.jpg";

        private List<TaskInfo> mData;

        public MyAdapter(List<TaskInfo> data) {
            mData = data;
        }

        public void remove(TaskInfo info) {
            mData.remove(info);
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download_view, parent, false);
            return new VH(itemView, this);
        }

        @Override
        public void onBindViewHolder(@NonNull final VH holder, final int position) {
            final DownloadInfo info = (DownloadInfo) mData.get(position);
            final int state = info.getTaskState();

            holder.bind(info, position);

            mThunder.setTaskStateListener(info.getTaskId(), holder.listener);

            holder.btnState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (info.isStart()) {
                        mThunder.pause(info);
                    } else {
                        mThunder.start(info, holder.listener);
                    }
                }
            });

            switch (state) {
                case TaskState.STATE_START:
                case TaskState.STATE_PROGRESS:
                case TaskState.STATE_RESUME:
                    holder.btnState.setText("暂停");
                    holder.btnState.setEnabled(true);
                    break;
                case TaskState.STATE_PAUSE:
                    holder.btnState.setText("继续");
                    holder.btnState.setEnabled(true);
                    break;
                case TaskState.STATE_WAITING:
                    holder.btnState.setText("等待下载");
                    holder.btnState.setEnabled(false);
                    break;
                case TaskState.STATE_NONE:
                case TaskState.STATE_CANCEL:
                case TaskState.STATE_ERROR:
                    holder.btnState.setText("开始下载");
                    holder.btnState.setEnabled(true);
                    break;
            }

            holder.tvTaskName.setText(info.getTaskName());
            holder.showProgress("onScroll", info.getTotal(), info.getCurrent());
            holder.showSpeed("onScroll", info.getSpeed());
            holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mThunder.cancel(info);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    class VH extends RecyclerView.ViewHolder {

        MyAdapter mAdapter;
        DownloadInfo mInfo;
        int mPosition;

        TextView tvTaskName;
        TextView tvSpeed;
        ProgressBar mProgressBar;
        Button btnState;
        Button btnCancel;

        final TaskStateListener listener = new TaskStateListener() {

            @Override
            public void onWaiting() {
                super.onWaiting();
                tvSpeed.setText("0B/s");
                btnState.setText("等待下载");
                btnState.setEnabled(false);
            }

            @Override
            public void onStart() {
                super.onStart();
                tvSpeed.setText("0B/s");
                btnState.setText("暂停");
                btnState.setEnabled(true);
            }

            @Override
            public void onPause() {
                super.onPause();
                tvSpeed.setText("0B/s");
                btnState.setText("继续");
                btnState.setEnabled(true);
            }

            @Override
            public void onProgress(long total, long current, long speed) {
                super.onProgress(total, current, speed);
                showProgress("onProgress", total, current);
            }

            @Override
            public void onSpeed(long speed) {
                super.onSpeed(speed);
                showSpeed("onSpeed", speed);
            }

            @Override
            public void onComplete(String filePath) {
                super.onComplete(filePath);
                mAdapter.remove(mInfo);
                mAdapter.notifyItemRemoved(getAdapterPosition());
            }

            @Override
            public void onCancel() {
                super.onCancel();
                long current = mInfo.getCurrent();
                long total = mInfo.getTotal();
                long speed = mInfo.getSpeed();
                showProgress("onCancel", total, current);
                showSpeed("onCancel", speed);
                btnState.setText("开始下载");
                btnState.setEnabled(true);
            }

            @Override
            public void onError(String error) {
                super.onError(error);
                long current = mInfo.getCurrent();
                long total = mInfo.getTotal();
                long speed = mInfo.getSpeed();
                showProgress("onError", total, current);
                showSpeed("onError", speed);
                btnState.setText("点击重试");
                btnState.setEnabled(true);
            }
        };

        public VH(View itemView, MyAdapter adapter) {
            super(itemView);
            mAdapter = adapter;

            tvTaskName = itemView.findViewById(R.id.tv_task_name);
            tvSpeed = itemView.findViewById(R.id.tv_speed);
            mProgressBar = itemView.findViewById(R.id.progressbar);
            btnState = itemView.findViewById(R.id.btn_state);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
        }

        public void bind(DownloadInfo info, int position) {
            if (mInfo != null) {
                listener.setTaskId(mInfo.getTaskId());
                listener.setTaskName(mInfo.getTaskName());
            } else {
                listener.setTaskId(info.getTaskId());
                listener.setTaskName(info.getTaskName());
            }
            mInfo = info;
            mPosition = position;
        }

        public void showProgress(String from, long total, long current) {
            int progress = (int) (current * 1.0f / total * 100);
            mProgressBar.setProgress(progress);
        }

        public void showSpeed(String from, long speed) {
            String speedStr;
            if (speed < 1000) { // B
                speedStr = speed + "B/s";
            } else if (speed < 1000 * 1000) { // kb
                long KB = speed / 1000;
                speedStr = KB + "K/s";
            } else {
                float MB = speed * 1.0f / 1000 / 1000;
                speedStr = String.format("%.2fM/s", MB);
            }
            tvSpeed.setText(speedStr);
            ThunderLog.d(TAG, mInfo.getTaskName() + "_" + from + " speed: " + speedStr);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10000) {
            String rootPath = Environment.getExternalStorageDirectory().getPath() + "/Thunder";
            File dir = new File(rootPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }
}
