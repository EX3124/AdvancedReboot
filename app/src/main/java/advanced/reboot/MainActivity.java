package advanced.reboot;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import rikka.shizuku.Shizuku;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class MainActivity extends AppCompatActivity {
    Boolean ShizukuRunning = false;
    private IUserService iUserService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        EdgeToEdge.enable(this);

        Shizuku.addBinderReceivedListenerSticky(BINDER_RECEIVED_LISTENER);
        Shizuku.addBinderDeadListener(BINDER_DEAD_LISTENER);
        Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);

        if (ShizukuRunning && Shizuku.checkSelfPermission() == PERMISSION_GRANTED) {
            Shizuku.bindUserService(userServiceArgs, serviceConnection);
            showtitle2();
        }
        if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            findViewById(R.id.bootloader).setVisibility(View.VISIBLE);
            findViewById(R.id.space1).setVisibility(View.VISIBLE);
        }
        interfacelocation();
        final Process[] process = {null};
        final String[] result = {null};

        Button shizuku = findViewById(R.id.shizuku);
        shizuku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShizukuRunning) {
                    if (Shizuku.checkSelfPermission() == PERMISSION_GRANTED) {
                        Shizuku.bindUserService(userServiceArgs, serviceConnection);
                        showtitle2();
                    } else
                        Shizuku.requestPermission(1);
                }
            }
        });

        Button root = findViewById(R.id.root);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    process[0] = Runtime.getRuntime().exec("su");
                    DataOutputStream dataOutputStream = new DataOutputStream(process[0].getOutputStream());
                    DataInputStream dataInputStream = new DataInputStream(process[0].getInputStream());
                    dataOutputStream.writeBytes("whoami\n");
                    dataOutputStream.flush();
                    result[0] = dataInputStream.readLine();
                    if (result[0] != null && result[0].equals("root"))
                        showtitle2();
                } catch (Exception ignored) {
                }
            }
        });

        Button shutdown = findViewById(R.id.shutdown);
        shutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (result[0] != null && result[0].equals("root")) {
                    DataOutputStream dataOutputStream = new DataOutputStream(process[0].getOutputStream());
                    try {
                        dataOutputStream.writeBytes("svc power shutdown\n");
                        dataOutputStream.flush();
                    } catch (Exception ignored) {
                    }
                } else {
                    try {
                        iUserService.execLine("svc power shutdown");
                    } catch (Exception ignored) {
                    }
                }
            }
        });

        Button reboot = findViewById(R.id.reboot);
        reboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (result[0] != null && result[0].equals("root")) {
                    DataOutputStream dataOutputStream = new DataOutputStream(process[0].getOutputStream());
                    try {
                        dataOutputStream.writeBytes("svc power reboot\n");
                        dataOutputStream.flush();
                    } catch (Exception ignored) {
                    }
                } else {
                    try {
                        iUserService.execLine("svc power reboot");
                    } catch (Exception ignored) {
                    }
                }
            }
        });

        Button recovery = findViewById(R.id.recovery);
        recovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (result[0] != null && result[0].equals("root")) {
                    DataOutputStream dataOutputStream = new DataOutputStream(process[0].getOutputStream());
                    try {
                        dataOutputStream.writeBytes("input keyevent 26\n");
                        dataOutputStream.flush();
                        dataOutputStream.writeBytes("svc power reboot recovery\n");
                        dataOutputStream.flush();
                    } catch (Exception ignored) {
                    }
                } else {
                    try {
                        iUserService.execLine("reboot recovery");
                    } catch (Exception ignored) {
                    }
                }
            }
        });

        Button bootloader = findViewById(R.id.bootloader);
        bootloader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (result[0] != null && result[0].equals("root")) {
                    DataOutputStream dataOutputStream = new DataOutputStream(process[0].getOutputStream());
                    try {
                        dataOutputStream.writeBytes("svc power reboot bootloader\n");
                        dataOutputStream.flush();
                    } catch (Exception ignored) {
                    }
                } else {
                    try {
                        iUserService.execLine("svc power reboot bootloader");
                    } catch (Exception ignored) {
                    }
                }
            }
        });

        Button download = findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (result[0] != null && result[0].equals("root")) {
                    DataOutputStream dataOutputStream = new DataOutputStream(process[0].getOutputStream());
                    try {
                        dataOutputStream.writeBytes("svc power reboot download\n");
                        dataOutputStream.flush();
                    } catch (Exception ignored) {
                    }
                } else {
                    try {
                        iUserService.execLine("svc power reboot download");
                    } catch (Exception ignored) {
                    }
                }
            }
        });

        Button edl = findViewById(R.id.edl);
        edl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (result[0] != null && result[0].equals("root")) {
                    DataOutputStream dataOutputStream = new DataOutputStream(process[0].getOutputStream());
                    try {
                        dataOutputStream.writeBytes("svc power reboot edl\n");
                        dataOutputStream.flush();
                    } catch (Exception ignored) {
                    }
                } else {
                    try {
                        iUserService.execLine("svc power reboot edl");
                    } catch (Exception ignored) {
                    }
                }
            }
        });

        Button back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showtitle1();
            }
        });
    }

    private void interfacelocation() {
        TextView title = findViewById(R.id.title1);
        if (title.getText().equals(getText(R.string.title1))) {
            LinearLayout interface1 = findViewById(R.id.interface1);
            interface1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int height = interface1.getTop();
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) title.getLayoutParams();
                    layoutParams.topMargin = height / 2;
                    title.setLayoutParams(layoutParams);
                }
            });
        }
        if (title.getText().equals(getText(R.string.title2))) {
            LinearLayout interface2 = findViewById(R.id.interface2);
            interface2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int height = interface2.getTop();
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) title.getLayoutParams();
                    layoutParams.topMargin = height / 2;
                    title.setLayoutParams(layoutParams);
                }
            });
        }
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int height = size.y;
        Button back = findViewById(R.id.back);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) back.getLayoutParams();
        layoutParams.bottomMargin = height / 16;
        back.setLayoutParams(layoutParams);
    }
    private void showtitle1() {
        findViewById(R.id.interface1).setVisibility(View.VISIBLE);
        findViewById(R.id.interface2).setVisibility(View.GONE);
        findViewById(R.id.back).setVisibility(View.GONE);
        TextView title = findViewById(R.id.title1);
        title.setText(R.string.title1);
        interfacelocation();
    }
    private void showtitle2() {
        findViewById(R.id.interface1).setVisibility(View.GONE);
        findViewById(R.id.interface2).setVisibility(View.VISIBLE);
        findViewById(R.id.back).setVisibility(View.VISIBLE);
        TextView title = findViewById(R.id.title1);
        title.setText(R.string.title2);
        interfacelocation();
    }

    private final Shizuku.OnBinderReceivedListener BINDER_RECEIVED_LISTENER = () -> {
        if (!Shizuku.isPreV11())
            ShizukuRunning = true;
    };
    private final Shizuku.OnBinderDeadListener BINDER_DEAD_LISTENER = () -> ShizukuRunning = false;
    private final Shizuku.OnRequestPermissionResultListener REQUEST_PERMISSION_RESULT_LISTENER = this::onRequestPermissionsResult;
    private void onRequestPermissionsResult(int requestCode, int grantResult) {
        if (requestCode == 1 && grantResult == PERMISSION_GRANTED) {
            Shizuku.bindUserService(userServiceArgs, serviceConnection);
            showtitle2();
        }
    }
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (iBinder != null && iBinder.pingBinder()) {
                iUserService = IUserService.Stub.asInterface(iBinder);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            iUserService = null;
        }
    };

    private final Shizuku.UserServiceArgs userServiceArgs =
            new Shizuku.UserServiceArgs(new ComponentName("advanced.reboot", UserService.class.getName()))
                    .daemon(false)
                    .processNameSuffix("adb_service")
                    .debuggable(false)
                    .version(1);

}