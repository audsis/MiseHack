package org.minecraft.wise.impl.features.modules.render;

import org.minecraft.wise.api.feature.module.Module;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;

public class ViewmodelChanger extends Module {

    public static ViewmodelChanger INSTANCE;
    private final Value<Number> leftX = new ValueBuilder<Number>().withDescriptor("Left X").withValue(0).withRange(-2, 2).register(this);
    private final Value<Number> leftY = new ValueBuilder<Number>().withDescriptor("Left Y").withValue(0).withRange(-2, 2).register(this);
    private final Value<Number> leftZ = new ValueBuilder<Number>().withDescriptor("Left Z").withValue(0).withRange(-2, 2).register(this);
    private final Value<Number> rightX = new ValueBuilder<Number>().withDescriptor("Right X").withValue(0).withRange(-2, 2).register(this);
    private final Value<Number> rightY = new ValueBuilder<Number>().withDescriptor("Right Y").withValue(0).withRange(-2, 2).register(this);
    private final Value<Number> rightZ = new ValueBuilder<Number>().withDescriptor("Right Z").withValue(0).withRange(-2, 2).register(this);

    private final Value<Number> leftRotateX = new ValueBuilder<Number>().withDescriptor("Left Rotate X").withValue(0).withRange(0, 360).register(this);
    private final Value<Number> leftRotateY = new ValueBuilder<Number>().withDescriptor("Left Rotate Y").withValue(0).withRange(0, 360).register(this);
    private final Value<Number> leftRotateZ = new ValueBuilder<Number>().withDescriptor("Left Rotate Z").withValue(0).withRange(0, 360).register(this);
    private final Value<Number> rightRotateX = new ValueBuilder<Number>().withDescriptor("Right Rotate X").withValue(0).withRange(0, 360).register(this);
    private final Value<Number> rightRotateY = new ValueBuilder<Number>().withDescriptor("Right Rotate Y").withValue(0).withRange(0, 360).register(this);
    private final Value<Number> rightRotateZ = new ValueBuilder<Number>().withDescriptor("Right Rotate Z").withValue(0).withRange(0, 360).register(this);

    private final Value<Number> leftScaleX = new ValueBuilder<Number>().withDescriptor("Left Scale X").withValue(1).withRange(-3, 3).register(this);
    private final Value<Number> leftScaleY = new ValueBuilder<Number>().withDescriptor("Left Scale Y").withValue(1).withRange(-3, 3).register(this);
    private final Value<Number> leftScaleZ = new ValueBuilder<Number>().withDescriptor("Left Scale Z").withValue(1).withRange(-3, 3).register(this);
    private final Value<Number> rightScaleX = new ValueBuilder<Number>().withDescriptor("Right Scale X").withValue(1).withRange(-3, 3).register(this);
    private final Value<Number> rightScaleY = new ValueBuilder<Number>().withDescriptor("Right Scale Y").withValue(1).withRange(-3, 3).register(this);
    private final Value<Number> rightScaleZ = new ValueBuilder<Number>().withDescriptor("Right Scale Z").withValue(1).withRange(-3, 3).register(this);

    public ViewmodelChanger() {
        super("ViewmodelChanger", Category.Render);
        INSTANCE = this;
    }

    public void doModel(MatrixStack matrix, Hand hand) {
        if (hand == Hand.OFF_HAND) {
            matrix.scale(leftScaleX.getValue().floatValue(), leftScaleY.getValue().floatValue(), leftScaleZ.getValue().floatValue());

            matrix.multiply(RotationAxis.POSITIVE_X.rotationDegrees(leftRotateX.getValue().floatValue()));
            matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(leftRotateY.getValue().floatValue()));
            matrix.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(leftRotateZ.getValue().floatValue()));

            matrix.translate(leftX.getValue().floatValue(), leftY.getValue().floatValue(), leftZ.getValue().floatValue());
        } else {
            matrix.scale(rightScaleX.getValue().floatValue(), rightScaleY.getValue().floatValue(), rightScaleZ.getValue().floatValue());

            matrix.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rightRotateX.getValue().floatValue()));
            matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rightRotateY.getValue().floatValue()));
            matrix.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rightRotateZ.getValue().floatValue()));

            matrix.translate(rightX.getValue().floatValue(), rightY.getValue().floatValue(), rightZ.getValue().floatValue());
        }
    }
}
