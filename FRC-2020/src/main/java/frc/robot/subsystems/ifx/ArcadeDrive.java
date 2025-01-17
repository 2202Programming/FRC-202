/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems.ifx;

/**
 * Any arcade drive should support these methods.
 */
public interface ArcadeDrive extends Odometry {
    public void arcadeDrive(double xSpeed, double zRot);
    
}
