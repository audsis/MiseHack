#version 330 core

in vec2 v_TexCoord;
in vec2 v_OneTexel;

uniform sampler2D u_Texture;
uniform int u_Width;
uniform float u_FillOpacity;
uniform int u_ShapeMode;
uniform float u_GlowMultiplier;

uniform bool u_DynamicRainbow;
uniform vec2 u_RainbowStrength;
uniform float u_RainbowSpeed;
uniform float u_Saturation;

out vec4 color;

vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    // color for the entity model, each vertex is assigned a color
    vec4 center = texture(u_Texture, v_TexCoord);

    if (center.a != 0.0) {
        if (u_ShapeMode == 0) discard;
        center = vec4(center.rgb, center.a * u_FillOpacity);
    }
    else {
        if (u_ShapeMode == 1) discard;

        // closest radius distance
        float dist = u_Width * 2.0F + 2.0F;

        // Radius determines the width of the shader
        for (int x = -u_Width; x <= u_Width; x++) {
            for (int y = -u_Width; y <= u_Width; y++) {

                // The current colour of the fragment based on the texture of the entity, we'll overwrite this colour
                vec4 currentColor = texture(u_Texture, v_TexCoord + vec2(v_OneTexel.x * x, v_OneTexel.y * y));

                // gl_FragColor is the current colour of the fragment, we'll update it according to our uniform (custom value)
                if (currentColor.a > 0) {
                    float currentDist = sqrt(x * x + y * y);

                    if (currentDist < dist) {
                        dist = currentDist;
                        center = currentColor;
                    }
                }
            }
        }

        center.a = max(0, (u_Width - (dist - 1)) / u_Width);

        if (u_DynamicRainbow) {
            if (u_RainbowSpeed > -1.0) {
                vec2 coords = vec2(gl_FragCoord.xy * u_RainbowStrength);
                vec3 rainbowColor = vec3(clamp ((abs(((fract((vec3((float(mod (((coords.x + coords.y) + u_RainbowSpeed), 1.0)))) + vec3(1.0, 0.6666667, 0.3333333))) * 6.0) - vec3(3.0, 3.0, 3.0))) - vec3(1.0, 1.0, 1.0)), 0.0, 1.0));
                vec3 hsv = vec3(rgb2hsv(rainbowColor).xyz);
                hsv.y = u_Saturation;
                vec3 finalColor = vec3(hsv2rgb(hsv).xyz);
                center.rgba = vec4(finalColor.x, finalColor.y, finalColor.z, max(0, (u_Width - (dist - 1)) / u_Width));
            }
//            center.rgb = mix(center.rgb, rainbowColor, center.a);
        }

        // Colour the area
//      center = vec4(center.x, center.y, center.z, max(0, (u_Width - (dist - 1)) / u_Width));
    }

    color = center;
}
