package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

public class BodyPartsComponent implements Component {
  public final int head;
  public final int body;
  public final int rightArm;
  public final int leftArm;
  public final int rightLeg;
  public final int leftLeg;

  /**
   * Body parts, ya dingus. Each part is measured as a die type.
   *
   * @param head     The head
   * @param body     and body
   * @param rightArm and right arm
   * @param leftArm  and left arm
   * @param rightLeg and right leg
   * @param leftLeg  and then the left leg
   */
  public BodyPartsComponent(int head, int body, int rightArm, int leftArm,
                            int rightLeg, int leftLeg) {
    this.head = head;
    this.body = body;
    this.rightArm = rightArm;
    this.leftArm = leftArm;
    this.rightLeg = rightLeg;
    this.leftLeg = leftLeg;
  }
}
