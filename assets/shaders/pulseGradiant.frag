#define HIGHP

uniform sampler2D u_texture;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;
uniform float u_alpha;

uniform vec4 u_startC;

varying vec2 v_texCoords;

float lerp(float a, float b, float amount){
	return b * amount + a * (1.0 - amount);
}

void main(){

	vec4 color = texture2D(u_texture, v_texCoords.xy);

	vec2 c = v_texCoords.xy;

	vec2 coords = vec2(c.x * u_resolution.x + u_campos.x, c.y * u_resolution.y + u_campos.y);

	vec4 lp = vec4(color.x * u_startC.x, color.y * u_startC.y, color.z * u_startC.z, color.a * u_alpha * u_startC.a);

	lp.x *= abs(sin(gl_FragCoord.x + u_time/3));
	lp.y *= abs(sin(gl_FragCoord.y + u_time/3));

	// Output to screen
	gl_FragColor = lp;
}