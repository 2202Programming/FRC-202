package frc.robot.commands;

import java.util.Set;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.Robot;
import frc.robot.util.ExpoShaper;

public class ArcadeDrive implements Command {
    private ExpoShaper speedShaper;
    private ExpoShaper rotationShaper;

    @Override
    public boolean isFinished() {
        return false;
    }

    public ArcadeDrive() {
        requires(Robot.driveTrain);
    }

    public void execute() {
        // Robot.driveTrain.ArcadeDrive(0.90, 0, true);
        double s = speedShaper.expo(Robot.oi.driver.getY(Hand.kLeft));
        // soften the input by limiting the max input
        double rot = rotationShaper.expo(0.8 * Robot.oi.driver.getX(Hand.kRight));
        Robot.driveTrain.arcadeDrive(s, rot);
    }

    @Override
    public Set<Subsystem> getRequirements() {
        // TODO Auto-generated method stub
        return null;
    }

}