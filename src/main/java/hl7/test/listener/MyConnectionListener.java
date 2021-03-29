package hl7.test.listener;

import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.ConnectionListener;
import ca.uhn.hl7v2.app.Initiator;

import java.util.concurrent.TimeUnit;

public class MyConnectionListener implements ConnectionListener {
    @Override
    public void connectionReceived(Connection c) {
        System.out.println("新连接： "+c.getRemoteAddress().toString()+c.getRemotePort().toString());
    }

    @Override
    public void connectionDiscarded(Connection c) {
        System.out.println("断开连接"+c.getRemoteAddress().toString()+c.getRemotePort().toString());
    }
}
