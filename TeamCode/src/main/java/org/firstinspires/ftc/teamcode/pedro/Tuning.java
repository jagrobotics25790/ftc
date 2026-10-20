package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.ForesightTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.MecanumTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.PinpointTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.Tests;

/**
 * AutoTune procedures. Every static @Tuner method here is picked up automatically and listed on
 * the robot-hosted AutoTune page. To use it, connect a laptop to the Robot Controller's Wi-Fi and
 * open http://192.168.43.1:10158
 *
 * The other localizer tuners (TwoWheelTuner, ThreeWheelTuner, OTOSTuner, ...) live in the
 * procedures package if the robot ever switches away from the Pinpoint.
 */
public class Tuning {
    @Tuner
    public static Procedure mecanumTuner() {
        return new MecanumTuner();
    }

    @Tuner
    public static Procedure pinpointTuner() {
        return new PinpointTuner();
    }

    @Tuner
    public static Procedure foresightTuner() {
        return new ForesightTuner(Constants::localizer, Constants::drivetrain);
    }

    // Driving, Localization, Odometry and Pose tests work right away. Hold, Line, Curve and
    // Interpolation tests need foresightConfig to be tuned first.
    @Tuner
    public static Procedure tests() {
        return new Tests(Constants::drivetrain, Constants::localizer, Constants::algorithm);
    }
}
