package advanced.reboot;

import android.content.pm.PackageManager;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import rikka.shizuku.Shizuku;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class MainActivity extends AppCompatActivity {
    Boolean ShizukuRunning = false;
    private IUserService iUserService;
    Process process;
    String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        EdgeToEdge.enable(this);

        Shizuku.addBinderReceivedListenerSticky(BINDER_RECEIVED_LISTENER);
        Shizuku.addBinderDeadListener(BINDER_DEAD_LISTENER);
        Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);

        if (ShizukuRunning && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            Shizuku.bindUserService(userServiceArgs, serviceConnection);
            chose();
        }
        if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            findViewById(R.id.bootloader).setVisibility(View.VISIBLE);
            findViewById(R.id.space1).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.shizuku).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShizukuRunning) {
                    if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                        Shizuku.bindUserService(userServiceArgs, serviceConnection);
                        chose();
                    } else
                        Shizuku.requestPermission(1);
                }
            }
        });

        findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    process = Runtime.getRuntime().exec("su");
                    DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
                    DataInputStream dataInputStream = new DataInputStream(process.getInputStream());
                    dataOutputStream.writeBytes("whoami\n");
                    dataOutputStream.flush();
                    result = dataInputStream.readLine();
                    if (result != null && result.equals("root"))
                        chose();
                } catch (Throwable ignored) {
                }
            }
        });

        findViewById(R.id.shutdown).setOnClickListener(run("svc power shutdown"));

        findViewById(R.id.reboot).setOnClickListener(run("svc power reboot"));

        findViewById(R.id.recovery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (result != null && result.equals("root")) {
                    DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
                    try {
                        dataOutputStream.writeBytes("input keyevent 26\n");
                        dataOutputStream.flush();
                        dataOutputStream.writeBytes("svc power reboot recovery\n");
                        dataOutputStream.flush();
                    } catch (Throwable ignored) {
                    }
                } else {
                    try {
                        iUserService.execLine("reboot recovery");
                    } catch (Throwable ignored) {
                    }
                }
            }
        });

        findViewById(R.id.bootloader).setOnClickListener(run("svc power reboot bootloader"));

        findViewById(R.id.download).setOnClickListener(run("svc power reboot download"));

        findViewById(R.id.edl).setOnClickListener(run("svc power reboot edl"));

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        Button back = findViewById(R.id.back);
        ((ViewGroup.MarginLayoutParams) back.getLayoutParams()).bottomMargin = size.y / 16;
        back.setLayoutParams(back.getLayoutParams());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.interface1).setVisibility(View.VISIBLE);
                findViewById(R.id.interface2).setVisibility(View.GONE);
                findViewById(R.id.back).setVisibility(View.GONE);
                TextView title = findViewById(R.id.title1);
                title.setText(R.string.title1);
            }
        });
    }

    private View.OnClickListener run(String command) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (result != null && result.equals("root")) {
                    DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
                    try {
                        dataOutputStream.writeBytes(command + "\n");
                        dataOutputStream.flush();
                    } catch (Throwable ignored) {
                    }
                } else {
                    try {
                        iUserService.execLine(command + "\n");
                    } catch (Throwable ignored) {
                    }
                }
            }
        };
    }

    private void chose() {
        findViewById(R.id.interface1).setVisibility(View.GONE);
        findViewById(R.id.interface2).setVisibility(View.VISIBLE);
        findViewById(R.id.back).setVisibility(View.VISIBLE);
        TextView title = findViewById(R.id.title1);
        title.setText(R.string.title2);
    }

    private final Shizuku.OnBinderReceivedListener BINDER_RECEIVED_LISTENER = () -> {
        if (!Shizuku.isPreV11())
            ShizukuRunning = true;
    };
    private final Shizuku.OnBinderDeadListener BINDER_DEAD_LISTENER = () -> ShizukuRunning = false;
    private final Shizuku.OnRequestPermissionResultListener REQUEST_PERMISSION_RESULT_LISTENER = this::onRequestPermissionsResult;
    private void onRequestPermissionsResult(int requestCode, int grantResult) {
        if (requestCode == 1 && grantResult == PackageManager.PERMISSION_GRANTED) {
            Shizuku.bindUserService(userServiceArgs, serviceConnection);
            chose();
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