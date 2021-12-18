uniform sampler2D u_texture;

uniform float u_time;
uniform vec2 u_offset;

varying vec2 v_texCoords;

void main(){
	vec4 color = texture2D(u_texture, v_texCoords.xy);
	vec2 uv = gl_FragCoord.xy + u_offset;

	float alpha = color.a;
	if(alpha < 0.5) alpha = 0.0;
	else{
	    alpha -= 0.5;
	    alpha *= 2;
	}

    gl_FragColor = vec4(color.rbg, alpha);
}
