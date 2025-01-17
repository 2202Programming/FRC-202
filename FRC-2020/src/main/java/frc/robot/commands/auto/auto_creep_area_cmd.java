/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpiutil.math.MathUtil;
import frc.robot.Robot;
import frc.robot.subsystems.Lidar_Subsystem;
import frc.robot.subsystems.Limelight_Subsystem;
// use the interface over a specific type for drive
import frc.robot.subsystems.ifx.VelocityDrive;    
import frc.robot.subsystems.ifx.Shifter;

public class auto_creep_area_cmd extends CommandBase {
  /**
   * Creates a new auto_creep_cmd.
   */

  private final VelocityDrive drive;
  private final Shifter shifter;
  private final Limelight_Subsystem limelight;
  @SuppressWarnings("unused")
  private final Lidar_Subsystem lidar;
  private double angleTarget;
  private double targetArea;
  private double Kap = 2, Kai = 0.00, Kad = 0.02; // angle drive PIDs
  private double Kp = 2, Ki = 0.1, Kd = 0.02; // distance drive PIDs
  private final PIDController anglePIDController;
  private final PIDController distancePIDController;
  private double tolerancePct = .05;
  private double angleToleranceDeg = 3;
  private double maxAngleRate;
  private double maxSpeed;
  private double kDegreesToDPS = 1; // convert PID rotation output to degrees per second for
                                    // VelocityDifferentalDrive
  private double kAreaToPid = 2; //
  private double current_position;
  private boolean forward;

  public auto_creep_area_cmd(final VelocityDrive drive, final Limelight_Subsystem limelight,
      final Lidar_Subsystem lidar, final double angleTarget, final double maxSpeed, final double maxAngleRate,
      final double targetArea, final boolean forward) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.drive = drive;
    this.shifter = drive.getShifter();    //could be null, so check
    this.limelight = limelight;
    this.targetArea = targetArea; // feet
    this.maxSpeed = maxSpeed;
    this.angleTarget = angleTarget;
    this.maxAngleRate = maxAngleRate;
    this.lidar = lidar;
    this.forward = forward;

    anglePIDController = new PIDController(Kap, Kai, Kad);
    distancePIDController = new PIDController(Kp, Ki, Kd);

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(limelight);
    addRequirements(drive);
    
    limelight.enableLED();

    }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    
    // put the drive system into low gear if we have it.
    if (shifter != null) {
      shifter.shiftDown(); 
    }
    limelight.enableLED();
    Robot.command = "Auto Limelight Move";

 //   if (forward)
 //     kAreaToPid = 2;
 //  else
  //    kAreaToPid = -2;

    drive.resetPosition();

    distancePIDController.reset();
    distancePIDController.setSetpoint(targetArea);
    distancePIDController.setTolerance((targetArea) * tolerancePct, 0.5);
    // distancePIDController.setIntegratorRange(0, 3);

    anglePIDController.reset();
    anglePIDController.setTolerance(angleToleranceDeg, 0.5);
    anglePIDController.setSetpoint(angleTarget);


  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {


    // angle pid to limelight angle
    double current_angle = limelight.getFilteredX();
    double angleCmd = kDegreesToDPS * anglePIDController.calculate(current_angle);
    angleCmd = MathUtil.clamp(angleCmd, -maxAngleRate, maxAngleRate);

    // distanace pid
    current_position = limelight.getFilteredArea();
    double speedCmd = kAreaToPid * distancePIDController.calculate(current_position);
    speedCmd = MathUtil.clamp(speedCmd, -maxSpeed, maxSpeed);

    // SmartDashboard.putNumber("PID error (degrees)",
    // anglePIDController.getPositionError());
    SmartDashboard.putNumber("Max Angle Rate", maxAngleRate);
    SmartDashboard.putNumber("Filtered Angle", current_angle);
    SmartDashboard.putNumber("PID Output DPS", angleCmd);
    // SmartDashboard.putData(anglePIDController);

    SmartDashboard.putNumber("Max Speed", maxSpeed);
    SmartDashboard.putNumber("Target Area", targetArea);
    SmartDashboard.putNumber("Current Area", current_position);
    SmartDashboard.putNumber("PID Output Distance", speedCmd);
    // SmartDashboard.putData(distancePIDController);

    // move rotation only
    drive.velocityArcadeDrive(speedCmd, angleCmd);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    drive.velocityArcadeDrive(0, 0); 
    Robot.command = "None";
    drive.resetPosition();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if (forward) 
      return (current_position > (targetArea - targetArea*tolerancePct));
    else return (current_position < targetArea); 
  }
}
