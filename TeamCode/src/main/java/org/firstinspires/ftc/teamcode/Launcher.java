package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Flywheel launcher driven by a single 6000 RPM motor named "launcher_motor" in the robot
 * configuration.
 *
 * The motor runs under the hub's built-in velocity control, so it holds its speed as balls are
 * fed through. The speed is set from a 0-1 trigger value: released is off, a light press spins at
 * {@link #MIN_RPM}, and a full press spins at {@link #MAX_RPM}, scaling linearly in between.
 */
public class Launcher {
    public static final String MOTOR_NAME = "launcher_motor";

    public static final double MIN_RPM = 3000;
    public static final double MAX_RPM = 6000;

    // Encoder counts per revolution of the output shaft. 28 is correct for a 1:1 (6000 RPM)
    // goBILDA Yellow Jacket / REV HD Hex motor; change it if the motor has a gearbox.
    public static final double TICKS_PER_REV = 28;

    // Trigger values below this count as released, so a resting finger doesn't spin the flywheel.
    private static final double TRIGGER_DEADZONE = 0.05;

    private final DcMotorEx motor;
    private double targetRpm = 0;

    public Launcher(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotorEx.class, MOTOR_NAME);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // Let the flywheel coast down instead of braking hard when it is turned off.
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    /** Sets the speed from a trigger value: off when released, MIN_RPM to MAX_RPM when pressed. */
    public void setFromTrigger(double trigger) {
        if (trigger < TRIGGER_DEADZONE) {
            setRpm(0);
            return;
        }
        double t = (Math.min(trigger, 1.0) - TRIGGER_DEADZONE) / (1.0 - TRIGGER_DEADZONE);
        setRpm(MIN_RPM + (MAX_RPM - MIN_RPM) * t);
    }

    /** Spins at the given RPM, clamped to MAX_RPM. Zero (or less) turns the launcher off. */
    public void setRpm(double rpm) {
        if (rpm <= 0) {
            stop();
            return;
        }
        targetRpm = Math.min(rpm, MAX_RPM);
        motor.setVelocity(targetRpm / 60.0 * TICKS_PER_REV);
    }

    public void stop() {
        targetRpm = 0;
        // setPower(0) rather than setVelocity(0) so the flywheel coasts instead of being actively
        // driven to a stop.
        motor.setPower(0);
    }

    public double getTargetRpm() {
        return targetRpm;
    }

    public double getRpm() {
        return motor.getVelocity() / TICKS_PER_REV * 60.0;
    }
}
