#version 120

// Made by skyrising

uniform float hue_value;

vec3 hue2rgb(float h)
{
    float r = abs(h * 6.0 - 3.0) - 1.0;
    float g = 2.0 - abs(h * 6.0 - 2.0);
    float b = 2.0 - abs(h * 6.0 - 4.0);
    return clamp(vec3(r, g, b), 0.0, 1.0);
}

vec3 hsv2rgb(vec3 hsv)
{
    vec3 rgb = hue2rgb(hsv.x);
    return ((rgb - 1.0) * hsv.y + 1.0) * hsv.z;
}

void main()
{
    gl_FragColor = vec4(hsv2rgb(vec3(mod(hue_value, 1.0), gl_TexCoord[0].xy)), 1.0);
}