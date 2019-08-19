package me.kptmusztarda.webapp;

import com.pi4j.io.gpio.*;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GpioManager {

    private static final int[] pins = {7, 0, 2, 3, 4, 5};
    private static final String ACTION_SEPARATOR = ";";
    private static final String ACTION_SWITCH = "S";
    private static final String ACTION_WAIT = "W";
    private static final String PARAMETER_SEPARATOR = ",";

    private static final GpioManager instance = new GpioManager();
    static GpioManager getInstance() {
        return instance;
    }

    private GpioController gpio;
    private GpioPinDigitalOutput[] bulbs;
    private final List<String> queue;
    private boolean isRunning = false;
//    private PrintWriter out;

    private GpioManager() {
        gpio = GpioFactory.getInstance();
        bulbs = new GpioPinDigitalOutput[pins.length];
        for(int i=0; i<bulbs.length; i++) {
            bulbs[i] = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(pins[i]));
//            bulbs[i] = gpio.provisionDigitalOutputPin(RaspiPin.allPins()[pins[i]], "Bulb " + (i+1), null);
        }
        queue = new ArrayList<>(1);
    }

//    void setOutput(PrintWriter out) {
//        this.out = out;
//    }

    void addToQueue(String query) {
        this.queue.add(query);
//        processQuery(query);
        if(!isRunning) processQueue();
    }

    void processQueue() {
        if(queue.size() > 0) {
            Thread thread = new Thread(() -> {
                isRunning = true;
                while (queue.size() > 0) {
                    processQuery(queue.get(0));
                    queue.remove(0);
                }
                isRunning = false;
            });
            thread.start();
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    synchronized void processQuery(String query) {
        String str[] = query.split(ACTION_SEPARATOR);
        for(String s : str) {
            String param[] = s.split(PARAMETER_SEPARATOR);
            switch (param[0]) {
                case ACTION_SWITCH: switchOne(Integer.parseInt(param[1]), param[2]);
                    break;
                case ACTION_WAIT:
                    try {
                        wait(Long.parseLong(param[1]));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    boolean[] getState() {
        boolean[] state = new boolean[bulbs.length];
        for(int i=0; i<bulbs.length; i++)
            state[i] = bulbs[i].getState().isHigh();
        return state;
    }

    void switchAll(boolean b) {
        for(GpioPinDigitalOutput bulb : bulbs)
            bulb.setState(b);
    }

    void switchOne(int id, String state) {
        if(!state.equals("toggle"))
            bulbs[id].setState(Boolean.parseBoolean(state));
        else
            bulbs[id].setState(!bulbs[id].getState().isHigh());
    }

    void toggleAll() {
        boolean b = !atLeastOneOn();
        for(GpioPinDigitalOutput bulb : bulbs)
            bulb.setState(b);
    }

    boolean atLeastOneOn() {
        boolean b = false;
        for(GpioPinDigitalOutput bulb : bulbs)
            if(bulb.getState().isHigh()) {
                b = true;
                break;
            }
        return b;
    }

}
