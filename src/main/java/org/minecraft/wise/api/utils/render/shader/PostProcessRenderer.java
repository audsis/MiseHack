package org.minecraft.wise.api.utils.render.shader;

import net.minecraft.client.util.math.MatrixStack;

public class PostProcessRenderer {
    private static Mesh mesh;
    private static final MatrixStack matrices = new MatrixStack();

    private PostProcessRenderer() {
    }

    public static void init() {
        mesh = new Mesh(DrawMode.Triangles, Mesh.Attrib.Vec2);
        mesh.begin();

        mesh.quad(
                mesh.vec2(-1, -1).next(),
                mesh.vec2(-1, 1).next(),
                mesh.vec2(1, 1).next(),
                mesh.vec2(1, -1).next()
        );

        mesh.end();
    }

    public static void beginRender() {
        mesh.beginRender(matrices);
    }

    public static void render() {
        mesh.render(matrices);
    }

    public static void endRender() {
        mesh.endRender();
    }
}