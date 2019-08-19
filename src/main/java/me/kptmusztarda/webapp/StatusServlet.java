package me.kptmusztarda.webapp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/status")
public class StatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();

        GpioManager gpioManager = GpioManager.getInstance();
        boolean state[] = gpioManager.getState();

        for (int i=0; i<6; i++) {
            out.write(i + (state[i] ? "1" : "0"));
        }
    }
}
