#version 120

uniform sampler2D tex;
uniform vec4 tint;
uniform float depth;
uniform float mode;
float ImgWidth = 1.0 / 2048.0;
float ImgHeight = 1.0 / 512.0;

float[] xC = float[4](-ImgWidth, 0, 0, ImgWidth);
float[] yC = float[4](0, -ImgHeight, ImgHeight, 0);

float IsOuterEdge(in vec2 coords){
	vec4 at = texture2D(tex, coords);
	float ret = 0.0;
	if(at.a > 0.68) {
		return 0.7;
	/*
		for(int i = 0; i < 4; i++) {
			if(texture2D(tex, coords + vec2(xC[i], yC[i])).a >= 0.75)
				ret = 1.0;				
			else if(texture2D(tex, coords + vec2(xC[i] / 2, yC[i])).a >= 0.75)
				ret += 0.5;			
			else if(texture2D(tex, coords + vec2(xC[i], yC[i] / 2)).a >= 0.75)
				ret += 0.5;				
			if(ret > 0.05)
				return ret;
		}
		*/
	}
	
	return ret;
}

void main() {
	float intensity = IsOuterEdge(gl_TexCoord[0].xy);
	gl_FragColor = vec4(tint.rgb, intensity);
	//gl_FragColor = gl_Color * texture2D(tex, gl_TexCoord[0].xy);
}