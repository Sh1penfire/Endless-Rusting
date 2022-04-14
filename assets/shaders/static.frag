float maxStrength = 0.5;
float minStrength = 0.125;

float speed = 1.00;
float offsetSpeed = 6.00;

float flashSpeed = 2.0;

float scanlineAlpha = 0.85;

vec4 staticCol = vec4(0.8, 2, 1, scanlineAlpha);

uniform float u_time;
uniform vec2 u_offset;
uniform sampler2D u_texture;
uniform vec2 u_resolution;
uniform vec2 u_campos;

uniform vec4 u_drawCol;

varying vec2 v_texCoords;

float scanLines (vec2 noise)
{
    return (fract(sin(dot(noise.xy*1000.1, vec2(10, 10.0 + sin(u_time) * 35.0)) + u_time) - u_time) +
    fract(sin(dot(noise.xy,vec2(0.0001,98.233)))*873103.285) -
    fract(abs(cos(dot(noise.xy,vec2(3.5,98.233)))*6.285)) +
    fract(sin(dot(noise.xy/300.0, vec2(0, 10)) + sin(u_time * flashSpeed) * flashSpeed * 4.0)))/4.0;
}

void main()
{
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = gl_FragCoord.xy/u_resolution.xy;


    vec2 c = v_texCoords;
    vec2 v = vec2(1.0/u_resolution.x, 1.0/u_resolution.y);

    //coords of the region
    vec2 coords = vec2(c.x / v.x + u_campos.x, c.y / v.y + u_campos.y);

    vec2 uv2 = fract(gl_FragCoord.xy/u_resolution.xy*fract(abs(sin(u_time*speed) - sin(u_time*speed * offsetSpeed))));

    // Time varying pixel color
    //vec3 colour = vec3((random(uv2.xy) + scanLines(uv2.xy)))*maxStrength * 0.5;
    vec4 colour = vec4(scanLines(coords.xy))*maxStrength * 0.5;

    colour.a *= scanlineAlpha;

    // Output to screen
    gl_FragColor  = vec4(u_drawCol - colour);

}