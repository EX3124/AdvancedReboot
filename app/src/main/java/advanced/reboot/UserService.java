package advanced.reboot;

import java.io.IOException;

public class UserService extends IUserService.Stub {

    @Override
    public void destroy() {
        System.exit(0);
    }

    @Override
    public void exit() {
        destroy();
    }

    @Override
    public void execLine(String command) {
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}