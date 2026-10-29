package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Intake driven by a single motor named "intake" in the robot configuration.
 *
 * The motor's RPM isn't known, so it runs at a fixed power ({@link #POWER}) rather than a target
 * speed. {@link #toggle()} switches it between running and stopped.
 */
public class Intake {
    public static final String MOTOR_NAME = "intake";

    // Power while running, -1 to 1. Make it negative if the intake spins the wrong way.
    public static final double POWER = 1.0;

    private final DcMotor motor;
    private boolean running = false;

    public Intake(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotor.class, MOTOR_NAME);
        // Open-loop power, so this works whether or not the motor's encoder is plugged in.
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    public void toggle() {
        if (running) {
            stop();
        } else {
            start();
        }
    }

    public void start() {
        running = true;
        motor.setPower(POWER);
    }

    public void stop() {
        running = false;
        motor.setPower(0);
    }

    public boolean isRunning() {
        return running;
    }
}
