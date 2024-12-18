#version 330 core

in vec2 v_TexCoord;
in vec2 v_OneTexel;

uniform sampler2D u_Texture;
uniform int u_Width;
uniform float u_FillOpacity;
uniform int u_ShapeMode;
uniform float u_GlowMultiplier;
uniform float u_Time;

out vec4 color;

// Function to generate a rainbow color
vec3 rainbow(float t) {
    float r = sin(t * 2.0 * 3.14159 + 0.0) * 0.5 + 0.5;
    float g = sin(t * 2.0 * 3.14159 + 2.0 * 3.14159 / 3.0) * 0.5 + 0.5;
    float b = sin(t * 2.0 * 3.14159 + 4.0 * 3.14159 / 3.0) * 0.5 + 0.5;
    return vec3(r, g, b);
}

// Function to generate a swirl effect
vec3 swirlEffect(vec2 uv, float time) {
    uv -= 0.5;
    float angle = atan(uv.y, uv.x) + time * 0.1;
    float radius = length(uv);
    uv.x = cos(angle) * radius;
    uv.y = sin(angle) * radius;
    uv += 0.5;

    vec3 col = rainbow(uv.x + uv.y + time * 0.2);
    return col;
}

void main() {
    vec4 center = texture(u_Texture, v_TexCoord);

    if (center.a != 0.0) {
        if (u_ShapeMode == 0) discard;
        vec3 swirlColor = swirlEffect(v_TexCoord, u_Time);
        center = vec4(swirlColor, center.a * u_FillOpacity);
    }
    else {
        if (u_ShapeMode == 1) discard;

        float dist = u_Width * u_Width * 4.0;

        for (int x = -u_Width; x <= u_Width; x++) {
            for (int y = -u_Width; y <= u_Width; y++) {
                vec4 offset = texture(u_Texture, v_TexCoord + v_OneTexel * vec2(x, y));

                if (offset.a != 0) {
                    float ndist = x * x + y * y - 1.0;
                    dist = min(ndist, dist);
                    center = offset;
                }
            }
        }

        float minDist = u_Width * u_Width;

        if (dist > minDist) center.a = 0.0;
        else center.a = min((1.0 - (dist / minDist)) * u_GlowMultiplier, 1.0);
    }

    color = center;
}
