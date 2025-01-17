/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Robot;
import frc.robot.subsystems.Lidar_Subsystem;
import frc.robot.subsystems.Limelight_Subsystem;
//import frc.robot.subsystems.VelocityDifferentialDrive_Subsystem;
//prefer interface over specific implementation
import frc.robot.subsystems.ifx.ArcadeDrive;

public class auto_drive_straight_until_limelight_cmd extends CommandBase {
  /**
   * Creates a new auto_drive_straight_until_lidar_cmd.
   */
  private final ArcadeDrive drive;
  @SuppressWarnings("unused")
  private final Lidar_Subsystem lidar;
  private final Limelight_Subsystem limelight;
  private final double speed;

  public auto_drive_straight_until_limelight_cmd(ArcadeDrive drive, Lidar_Subsystem lidar, Limelight_Subsystem limelight, double speed) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.lidar = lidar;
    this.drive = drive;
    this.speed = speed;
    this.limelight = limelight;

    addRequirements(drive);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    Robot.command = "Auto Drive Straight";
    limelight.enableLED();
    
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    drive.arcadeDrive(speed, 0);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    drive.arcadeDrive(0, 0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return limelight.valid();
  }
}
