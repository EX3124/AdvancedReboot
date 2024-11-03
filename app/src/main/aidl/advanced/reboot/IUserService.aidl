package advanced.reboot;

interface IUserService {

    void destroy() = 16777114;

    void exit() = 1;

    void execLine(String command) = 2;
}


