package com.remote2call.server;

import com.remote2call.server.starter.AbstractLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutDownHook implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(ShutDownHook.class);

    private AbstractLauncher launcher;

    public ShutDownHook(AbstractLauncher launcher) {
        this.launcher = launcher;
    }

    public void addHook() {
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        Runtime.getRuntime().addShutdownHook(thread);
        logger.info("Add ShutDownHook Success");
    }

    public void run() {
        this.launcher.close();
    }
}
