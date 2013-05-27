[VERTEX SHADER]
#version 110

const float tileWidth = 64.0;
const float tileHeight = tileWidth / 2.0;
const float tileHalfWidth = tileWidth / 2.0;
const float tileHalfHeight = tileHeight / 2.0;
const float tileStepX = 0.125;
uniform float mapWidthInTiles;

void main(void) {
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_Vertex.x, gl_Vertex.y, 0.0f, 1.0);
//	gl_TexCoord[0].y = ((gl_Vertex.x / tileWidth - (gl_Vertex.y - tileHalfHeight) / tileHeight)) * tileStepX;
//	gl_TexCoord[0].x = ((mapWidthInTiles - gl_TexCoord[0].y + gl_Vertex.x / tileHalfWidth)) * tileStepX;

	gl_TexCoord[0].y = (gl_Vertex.x - gl_Vertex.y * 2.0 - 1.0) / tileWidth * tileStepX;
	gl_TexCoord[0].x = (mapWidthInTiles - gl_TexCoord[0].y + gl_Vertex.x / tileHalfWidth) * tileStepX;
	
	int index = int(gl_Vertex.z);
	if(index == 0 || index == 1)	gl_TexCoord[1].x = 0.0;
	else							gl_TexCoord[1].x = 1.0;
	
	if(index == 0 || index == 3)	gl_TexCoord[1].y = 0.0;
	else							gl_TexCoord[1].y = 1.0;
	gl_FrontColor = gl_Color;
}

[FRAGMENT SHADER]
#version 120

uniform sampler2D img;
uniform sampler2D mask;

void main (void) {
	vec4 texval1 = texture2D(img, vec2(gl_TexCoord[0]));
	vec4 texval2 = texture2D(mask, vec2(gl_TexCoord[1]));
	gl_FragColor = vec4(texval1.rgb, texval1.a * texval2.a) * gl_Color;
}