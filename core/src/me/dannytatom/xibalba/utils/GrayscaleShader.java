package me.dannytatom.xibalba.utils;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

// https://stackoverflow.com/questions/28874621/libgdx-grayscale-shader-fade-effect
public class GrayscaleShader {
  private static final String vertexShader
      = "attribute vec4 a_position;\n"
      + "attribute vec4 a_color;\n"
      + "attribute vec2 a_texCoord0;\n"
      + "uniform mat4 u_projTrans;\n"
      + "varying vec4 v_color;\n"
      + "varying vec2 v_texCoords;\n"
      + "\n"
      + "void main() {\n"
      + "    v_color = a_color;\n"
      + "    v_texCoords = a_texCoord0;\n"
      + "    gl_Position = u_projTrans * a_position;\n"
      + "}";

  private static final String fragmentShader
      = "#ifdef GL_ES\n"
      + "    precision mediump float;\n"
      + "#endif\n"
      + "\n"
      + "varying vec4 v_color;\n"
      + "varying vec2 v_texCoords;\n"
      + "uniform sampler2D u_texture;\n"
      + "uniform float u_grayness;\n"
      + "\n"
      + "void main() {\n"
      + "  vec4 c = v_color * texture2D(u_texture, v_texCoords);\n"
      + "  float gray = dot(c.rgb, vec3(0.22, 0.707, 0.071));\n"
      + "  vec3 blendedColor = mix(c.rgb, vec3(gray), u_grayness);\n"
      + "  gl_FragColor = vec4(blendedColor.rgb, c.a);\n"
      + "}";

  public static final ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
}
