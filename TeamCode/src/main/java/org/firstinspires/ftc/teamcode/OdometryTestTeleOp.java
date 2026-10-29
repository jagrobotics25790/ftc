package org.firstinspires.ftc.teamcode;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.drivetrain.Drivetrain;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.localization.Localizer;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Velocity;
import com.pedropathing.utils.Angle;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.pedro.Constants;

/**
 * Drive the robot with a standard Logitech gamepad (F310 / F710, switch on the back set to "X")
 * while watching the odometry readout on the Driver Station.
 *
 * This only uses the drivetrain and localizer from {@link Constants}, so it works as soon as the
 * Mecanum and Pinpoint tuners have been run. Foresight does not need to be tuned.
 *
 * Pedro coordinates: +X is forward, +Y is left, heading is counterclockwise-positive.
 *
 * Controls (gamepad 1):
 *   Left stick       drive (forward / back / strafe)
 *   Right stick X    turn
 *   Right bumper     hold for slow mode
 *   Right trigger    spin the launcher: light press 3000 RPM, full press 6000 RPM, release off
 *   X / Square       toggle the intake on / off
 *   D-pad            creep straight forward / back / left / right at low power
 *   Y                toggle robot-centric / field-centric
 *   A                reset pose to (0, 0, 0) and set the mark there
 *   B                set the mark at the current pose
 *   Back / Share     reset heading to 0 (keeps X and Y)
 */
@TeleOp(name = "Odometry Test TeleOp", group = "Pedro")
public class OdometryTestTeleOp extends OpMode {
    private static final double SLOW_SCALE = 0.3;
    private static final double CREEP_POWER = 0.25;

    private Drivetrain drivetrain;
    private Localizer localizer;
    private Launcher launcher;
    private Intake intake;

    private final ElapsedTime loopTimer = new ElapsedTime();
    private boolean fieldCentric = false;

    // The mark is a reference pose; "since mark" readouts make it easy to check a measured push
    // or turn (e.g. drive exactly 48 in, or spin exactly 360 degrees, then compare).
    private Pose mark = Pose.zero();
    private double turnedSinceMark = 0;
    private double previousHeading = 0;

    @Override
    public void init() {
        drivetrain = Constants.drivetrain(hardwareMap);
        localizer = Constants.localizer(hardwareMap);
        localizer.setPose(Pose.zero());
        launcher = new Launcher(hardwareMap);
        intake = new Intake(hardwareMap);

        telemetry.addLine("Keep the robot still while the localizer initializes.");
        telemetry.update();
    }

    @Override
    public void init_loop() {
        // Updating during init lets you push the robot by hand and check the readout before
        // enabling the motors.
        localizer.update();
        telemetry.addLine("Ready. Pose resets to (0, 0, 0) on start.");
        addPoseTelemetry(localizer.pose());
        telemetry.update();
    }

    @Override
    public void start() {
        localizer.setPose(Pose.zero());
        setMark(Pose.zero());
        loopTimer.reset();
    }

    @Override
    public void loop() {
        localizer.update();
        Pose pose = localizer.pose();

        turnedSinceMark += Angle.normalizeSigned(pose.heading() - previousHeading);
        previousHeading = pose.heading();

        handleButtons(pose);
        drivetrain.drive(drivePowers(pose), true);
        launcher.setFromTrigger(gamepad1.right_trigger);

        telemetry.addData("Drive", "%s%s",
                fieldCentric ? "FIELD-centric" : "ROBOT-centric",
                gamepad1.right_bumper ? " (slow)" : "");
        addPoseTelemetry(localizer.pose());
        addMarkTelemetry(localizer.pose());

        Velocity velocity = localizer.velocity();
        telemetry.addData("Velocity", "vx %.1f  vy %.1f in/s  ω %.1f °/s",
                velocity.vx, velocity.vy, Math.toDegrees(velocity.omega));
        telemetry.addData("Launcher", "target %.0f  actual %.0f RPM",
                launcher.getTargetRpm(), launcher.getRpm());
        telemetry.addData("Intake", intake.isRunning() ? "ON" : "off");
        telemetry.addData("Loop", "%.0f Hz", 1.0 / Math.max(loopTimer.seconds(), 1e-6));
        loopTimer.reset();

        telemetry.addLine();
        telemetry.addLine("Y: field/robot centric | RB: slow | D-pad: creep");
        telemetry.addLine("RT: launcher | X/Square: intake");
        telemetry.addLine("A: reset pose | B: set mark | Back: reset heading");
        telemetry.update();
    }

    @Override
    public void stop() {
        if (drivetrain != null) drivetrain.stop();
        if (launcher != null) launcher.stop();
        if (intake != null) intake.stop();
    }

    private void handleButtons(Pose pose) {
        if (gamepad1.yWasPressed()) {
            fieldCentric = !fieldCentric;
        }
        if (gamepad1.aWasPressed()) {
            localizer.setPose(Pose.zero());
            setMark(Pose.zero());
        }
        if (gamepad1.bWasPressed()) {
            setMark(pose);
        }
        if (gamepad1.squareWasPressed()) {
            intake.toggle();
        }
        if (gamepad1.backWasPressed()) {
            localizer.setHeading(0);
            previousHeading = 0;
        }
    }

    private DrivePowers drivePowers(Pose pose) {
        // D-pad creep is always robot-centric so it can be used for straight-line checks.
        if (gamepad1.dpad_up || gamepad1.dpad_down || gamepad1.dpad_left || gamepad1.dpad_right) {
            double forward = (gamepad1.dpad_up ? CREEP_POWER : 0) - (gamepad1.dpad_down ? CREEP_POWER : 0);
            double strafe = (gamepad1.dpad_left ? CREEP_POWER : 0) - (gamepad1.dpad_right ? CREEP_POWER : 0);
            return new DrivePowers(forward, strafe, 0);
        }

        // Stick up is negative on the gamepad. Pedro's strafe and turn are positive to the left /
        // counterclockwise, so the X axes are negated as well.
        double scale = gamepad1.right_bumper ? SLOW_SCALE : 1.0;
        DrivePowers powers = new DrivePowers(
                -gamepad1.left_stick_y * scale,
                -gamepad1.left_stick_x * scale,
                -gamepad1.right_stick_x * scale);

        return fieldCentric ? ManualDrive.fieldCentric(powers, pose.heading()) : powers;
    }

    private void setMark(Pose pose) {
        mark = pose;
        turnedSinceMark = 0;
        previousHeading = localizer.pose().heading();
    }

    private void addPoseTelemetry(Pose pose) {
        telemetry.addData("X", "%.2f in", pose.x());
        telemetry.addData("Y", "%.2f in", pose.y());
        telemetry.addData("Heading", "%.1f°", Math.toDegrees(pose.heading()));
    }

    private void addMarkTelemetry(Pose pose) {
        double dx = pose.x() - mark.x();
        double dy = pose.y() - mark.y();
        telemetry.addData("Since mark", "dX %.2f  dY %.2f  dist %.2f in", dx, dy, Math.hypot(dx, dy));
        telemetry.addData("Turned since mark", "%.1f°", Math.toDegrees(turnedSinceMark));
    }
}
