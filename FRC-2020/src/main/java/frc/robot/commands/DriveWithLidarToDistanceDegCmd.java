/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpiutil.math.MathUtil;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.Lidar_Subsystem;
import frc.robot.subsystems.ifx.ArcadeDrive;

public class DriveWithLidarToDistanceDegCmd extends CommandBase {
  final double mm2in = 1.0 / 25.4;

  private final ArcadeDrive drive;
  private final Lidar_Subsystem lidar;

  private final double stopDist; // inches
  private double tolerancePct = .05;
  private double angleToleranceDeg = 3;
  private double kInchesToPerPower = -0.8;
  private double kDegreesToPerPower = -1;
  private double maxSpeed;
  private double angleTarget;
  private final double Kp = 0.2, Ki = 0.04, Kd = 0.25;
  private final double Kap = 0.05, Kai = 0.001, Kad = 0.0;
  private final PIDController distancePIDController;
  private final PIDController anglePIDController;

  /**
   * Creates a new DriveWithLidarToDistanceCmd.
   * 
   * stopDistance = inches to stop from the wall maxSpeed = percent max speed (+-
   * 1.0 max) angleTarget = degrees from front of robot to target Recommend using
   * this command with withTimeout()
   * 
   * D Laufenberg
   * 
   */
  public DriveWithLidarToDistanceDegCmd(final ArcadeDrive drive, final Lidar_Subsystem lidar, final double stopDist,
      final double angleTarget, final double maxSpeed) {
    this.drive = drive;
    this.lidar = lidar;
    this.stopDist = stopDist; // inches
    this.maxSpeed = maxSpeed;
    this.angleTarget = angleTarget;

    // create the PID with vel and accl limits
    distancePIDController = new PIDController(Kp, Ki, Kd);
    anglePIDController = new PIDController(Kap, Kai, Kad);

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(this.lidar);
    addRequirements(this.drive);
  }

  public DriveWithLidarToDistanceDegCmd(ArcadeDrive drive, Lidar_Subsystem lidar, double stopDist, double maxSpeed,
      double angleTarget, double tolerancePct) {
    this(drive, lidar, stopDist, maxSpeed, angleTarget);
    this.tolerancePct = tolerancePct;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    distancePIDController.reset();
    distancePIDController.setSetpoint(stopDist);
    distancePIDController.setTolerance(stopDist * tolerancePct, 0.5);
    distancePIDController.setIntegratorRange(0, 3);

    anglePIDController.reset();
    anglePIDController.setSetpoint(angleTarget);
    anglePIDController.setTolerance(angleToleranceDeg, 0.5);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // read Lidar range
    double range = lidar.getAverageRange() * mm2in;
    double speedCmd = distancePIDController.calculate(range) * kInchesToPerPower;
    double angleCmd = kDegreesToPerPower * anglePIDController.calculate(lidar.findAngle());
    speedCmd = MathUtil.clamp(speedCmd, -maxSpeed, maxSpeed);
    angleCmd = MathUtil.clamp(angleCmd, -maxSpeed, maxSpeed);

    SmartDashboard.putNumber("PID error (inches)", distancePIDController.getPositionError());
    SmartDashboard.putNumber("PID Verr", distancePIDController.getVelocityError());
    SmartDashboard.putNumber("Range (inches)", range);
    SmartDashboard.putNumber("PID Output (%)", speedCmd);

    SmartDashboard.putNumber("PID error (degrees)", anglePIDController.getPositionError());
    SmartDashboard.putNumber("Angle", lidar.findAngle());
    SmartDashboard.putNumber("PID Output (%) (Angle)", angleCmd);
    // move forward, with rotation
    // Derek says "Use the velArcadeDrive() speed/angle control"
    //drive.velocityArcadeDrive(feetPerSecond, degreePerSecond);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(final boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    // return distancePIDController.atSetpoint();
    return false;
  }
}