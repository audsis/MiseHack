package org.minecraft.wise.api.utils.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.minecraft.wise.api.wrapper.IMinecraft;
import org.minecraft.wise.impl.WiseMod;
import org.apache.commons.io.IOUtils;
import org.joml.Matrix4f;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL32C.*;

public class Shader implements IMinecraft {
    public static Shader BOUND;

    private final int id;
    private final Object2IntMap<String> uniformLocations = new Object2IntOpenHashMap<>();

    public Shader(String vertPath, String fragPath) {
        int vert = GL.createShader(GL_VERTEX_SHADER);
        GL.shaderSource(vert, read(vertPath));

        String vertError = GL.compileShader(vert);
        if (vertError != null) {
            throw new RuntimeException("Failed to compile vertex shader (" + vertPath + "): " + vertError);
        }

        int frag = GL.createShader(GL_FRAGMENT_SHADER);
        GL.shaderSource(frag, read(fragPath));

        String fragError = GL.compileShader(frag);
        if (fragError != null) {
            throw new RuntimeException("Failed to compile fragment shader (" + fragPath + "): " + fragError);
        }

        id = GL.createProgram();

        String programError = GL.linkProgram(id, vert, frag);
        if (programError != null) {
            throw new RuntimeException("Failed to link program: " + programError);
        }

        GL.deleteShader(vert);
        GL.deleteShader(frag);
    }

    private String read(String path) {
        try {
            return IOUtils.toString(mc.getResourceManager().getResource(WiseMod.identifier("shaders/" + path)).get().getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read shader '" + path + "'", e);
        }
    }

    public void bind() {
        GL.useProgram(id);
        BOUND = this;
    }

    private int getLocation(String name) {
        if (uniformLocations.containsKey(name)) return uniformLocations.getInt(name);

        int location = GL.getUniformLocation(id, name);
        uniformLocations.put(name, location);
        return location;
    }

    public void set(String name, boolean v) {
        GL.uniformInt(getLocation(name), v ? GL_TRUE : GL_FALSE);
    }

    public void set(String name, int v) {
        GL.uniformInt(getLocation(name), v);
    }

    public void set(String name, double v) {
        GL.uniformFloat(getLocation(name), (float) v);
    }

    public void set(String name, double v1, double v2) {
        GL.uniformFloat2(getLocation(name), (float) v1, (float) v2);
    }

    public void set(String name, Color color) {
        GL.uniformFloat4(getLocation(name), color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }

    public void set(String name, Matrix4f mat) {
        GL.uniformMatrix(getLocation(name), mat);
    }

    public void setDefaults() {
        set("u_Proj", RenderSystem.getProjectionMatrix());
        set("u_ModelView", RenderSystem.getModelViewStack());
    }
}