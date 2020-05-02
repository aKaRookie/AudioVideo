#version 120
//定义一个attribute aPosition ，类型为vec4。4个方向的向量
attribute vec4 vPosition;
void main() {
    /*dasdadas
    ssss*/
    //这里的gl_Position是OpenGL内置的变量。
    gl_Position = vPosition;
}
