package frc.robot;
import edu.wpi.first.wpilibj.Joystick;

public class Axis {
    private int controller_id;
    private int axis_id;
    private Joystick axisController;

    //Axis IDs
    public static final int LEFT_STICK_X = 0;
    public static final int LEFT_STICK_Y = 1;
    public static final int LEFT_TRIGGER = 2;
    public static final int RIGHT_TRIGGER = 3;
    public static final int RIGHT_STICK_X = 4;
    public static final int RIGHT_STICK_Y = 5;

    public Axis(int controller_id, int axis_id) {
        this.controller_id = controller_id;
        this.axis_id = axis_id;
        this.axisController = new Joystick(this.controller_id);
    }

   public double readAxis() {
    return axisController.getRawAxis(axis_id);
   }
}