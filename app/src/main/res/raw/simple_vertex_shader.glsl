#version 300 es

uniform vec2 objRef;
uniform vec2 viewportSize;
uniform vec2 viewportOrigin;

in vec2 position;
in vec2 texPos;
out vec2 texCoord;

void main()
{
    vec2 pos = position + objRef;

    pos -= viewportOrigin;
    gl_Position = vec4(pos * 2.0 / viewportSize, 0.0, 1.0);

    texCoord = texPos;
}