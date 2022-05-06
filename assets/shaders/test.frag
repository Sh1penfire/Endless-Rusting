#define HIGHP

uniform sampler2D u_texture;
uniform sampler2D u_screenspace;

varying vec2 v_texCoords;

void main(){
    vec4 color = texture(u_screenspace, v_texCoords.xy);

    gl_FragColor = color;
}