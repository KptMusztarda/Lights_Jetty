package me.kptmusztarda.webapp;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.exception.GpioPinExistsException;
import jdk.nashorn.internal.ir.debug.JSONWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/switch")
public class SwitchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GpioManager gpioManager = GpioManager.getInstance();
        PrintWriter out = response.getWriter();

        String strId = request.getParameter("id");
        String strState = request.getParameter("state");

        if(strId.equals("all")) {
            if(strState.equals("toggle")) gpioManager.toggleAll();
            else gpioManager.switchAll(Boolean.parseBoolean(strState));
        }
        else if (strId.equals("query")){
            gpioManager.addToQueue(strState);
        } else {
            gpioManager.switchOne(Integer.parseInt(strId), strState);
        }

        boolean state[] = gpioManager.getState();

//        for (int i=0; i<6; i++) {
//            out.write(i + " - " + (state[i] ? "ON" : "OFF") + "\n");
//        }

        for (int i=0; i<6; i++) {
            out.write(i + (state[i] ? "1" : "0"));
        }

    }
}

