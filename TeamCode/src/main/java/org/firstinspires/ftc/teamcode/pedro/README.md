# Pedro Pathing setup

Pedro Pathing 3 (`com.pedropathing:revhub:3.0.1`, `com.pedropathing:tuning:1.0.1`).
Docs: https://pedropathing.com/docs/pathing/tuning

- `Constants.java`: drivetrain, localizer and Foresight configs, plus `create()` for the Follower.
- `Tuning.java`: the AutoTune procedures shown on the tuning web page.
- `procedures/`: the procedure implementations, copied unchanged from the Pedro Quickstart.
- `../OdometryTestTeleOp.java`: drive with a Logitech gamepad while watching the odometry readout.

## Before tuning

1. In the Driver Station robot configuration, add the goBILDA Pinpoint as `pinpoint` on an I2C
   port **other than 0**. Mount it sticker side up, with the forward pod in X and the strafe pod in Y.
2. The drive motors must be named `front_left_drive`, `front_right_drive`, `back_left_drive`
   and `back_right_drive`.
3. Build and install the code on the robot.

## AutoTune web page

Connect a laptop to the Robot Controller's Wi-Fi and open **http://192.168.43.1:10158**.
Run the procedures in this order. After each one, open the **Java** tab and paste the generated
config over the matching one in `Constants.java`, then rebuild.

1. **Mecanum Tuner**: spins each wheel so you can confirm motor names and directions → `drivetrainConfig`
2. **Pinpoint Tuner**: push forward, push left, then spin 180° counterclockwise → `localizerConfig`
3. **Tests → Localization**: drive with gamepad 1. Forward must increase X and strafing left must increase Y.
4. **Foresight Tuner**: the robot drives itself, so give it plenty of room → `foresightConfig`
5. **Tests → Line / Curve / Hold**: checks path following

## Odometry Test TeleOp

Select **Odometry Test TeleOp** on the Driver Station. The Logitech gamepad's back switch must be on **X**.
It only needs steps 1–2 above. Foresight does not have to be tuned first.

| Control        | Action                                   |
|----------------|------------------------------------------|
| Left stick     | Drive / strafe                           |
| Right stick X  | Turn                                     |
| Right bumper   | Hold for slow mode                       |
| D-pad          | Creep forward / back / left / right      |
| Y              | Toggle robot-centric / field-centric     |
| A              | Reset pose to (0, 0, 0)                  |
| B              | Set the mark at the current pose         |
| X              | Reset heading to 0                       |

Telemetry shows X/Y (inches), heading, distance and degrees turned since the mark, velocity and
loop rate. To check accuracy, press B, drive exactly 48 in (or spin exactly 360°), and compare
the readout to the real distance.
