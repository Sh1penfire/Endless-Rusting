#define HIGHP

uniform sampler2D u_texture;
uniform sampler2D u_screenspace;
uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;
uniform float u_alpha;

uniform vec4 u_startC;

varying vec2 v_texCoords;

void trns(out vec2 vo, float rotation){
    vec2 v = vec2(vo);
    rotation = radians(rotation);
    
    float sine = sin(rotation), cosin = cos(rotation);
    vo = vec2(
            v.x * cosin - v.y * sine,
            v.x * sine + v.y * cosin
            );
}

void main()
{
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = gl_FragCoord/u_resolution.xy;
    
    float scale = abs(sin(u_Time * 7.0) * sin(u_Time/1.5));
    
    vec2 scaling = vec2(0, scale);
    trns(scaling, u_Time * 90.0);
    if(scale < 0.96) scaling = vec2(0, 0);
    
    vec2 warpedUv = vec2(uv.x + sin(uv.y * 32.0)/5.0 * scaling.x, uv.y + cos(uv.y * 32.0)/5.0  * scaling.y);
    float cellSize = 5.0;
    
    vec2 pixelSize = vec2(0.05 * scale, 0.05 * scale);
    if(scale < 0.45){
        pixelSize.x = pixelSize.y = 0.01 * scale;
    }
    
    vec2 fixedUV = warpedUv+pixelSize/2.0;
    
    vec2 pxUV = floor(fixedUV/pixelSize)*pixelSize;
    
    vec4 tex = texture(iChannel0, vec2(fract(pxUV.x), fract(pxUV.y)));

    tex.rbg = tex.rbg;

    // Output to screen
    gl_fragColor = vec4(tex);
}