package com.mcdane.box

import android.content.res.Resources
import android.util.Log
import android.opengl.GLES30 as GL

class SimpleProgram(res: Resources):
    ShaderProgram(
        res,
        R.raw.simple_vertex_shader,
        R.raw.simple_frag_shader
    ) {

    val objRefLoc: Int = getUniformLocation("objRef")
    val viewportSizeLoc: Int = getUniformLocation("viewportSize")
    val viewportOriginLoc: Int = getUniformLocation("viewportOrigin")
    val positionLoc: Int = getAttributeLocation("position")
    val texPosLoc: Int = getAttributeLocation("texPos")
    val useColorLoc: Int = getUniformLocation("useColor")
    val useTexColorLoc: Int = getUniformLocation("useTexColor")
    val colorLoc: Int = getUniformLocation("color")
    val texColorLoc: Int = getUniformLocation("texColor")
    val texLoc: Int = getUniformLocation("tex")
    val alphaLoc: Int = getUniformLocation("alpha")
    val buffer = FloatArray(2)

    inline fun setObjRef(x: Float, y: Float) {
        buffer[0] = x
        buffer[1] = y
        GL.glUniform2fv(objRefLoc, 1, buffer, 0)
    }

    inline fun setViewportSize(width: Float, height: Float) {
        buffer[0] = width
        buffer[1] = height
        GL.glUniform2fv(viewportSizeLoc, 1, buffer, 0)
    }

    inline fun setViewportOrigin(x: Float, y: Float) {
        buffer[0] = x
        buffer[1] = y
        GL.glUniform2fv(viewportOriginLoc, 1, buffer, 0)
    }

    inline fun setColor(color: Color) =
        GL.glUniform4fv(colorLoc, 1, color.data, 0)

    inline fun setUseColor(use: Boolean) =
        GL.glUniform1i(useColorLoc, if (use) 1 else 0)

    inline fun setUseTexColor(use: Boolean) =
        GL.glUniform1i(useTexColorLoc, if (use) 1 else 0)

    inline fun setTexColor(texColor: Color) =
        GL.glUniform4fv(texColorLoc, 1, texColor.data, 0)

    inline fun setAlpha(alpha: Float) =
        GL.glUniform1f(alphaLoc, alpha)

    inline fun setPosition(pos: VertexArray) {
        GL.glEnableVertexAttribArray(positionLoc)
        GL.glVertexAttribPointer(
            positionLoc,
            FLOATS_PER_POS_2D,
            GL.GL_FLOAT,
            false,
            0,
            pos.data
        )
    }

    fun setTexPos(texPos: VertexArray) {
        GL.glEnableVertexAttribArray(texPosLoc)
        GL.glVertexAttribPointer(
            texPosLoc,
            FLOATS_PER_TEXPOS_2D,
            GL.GL_FLOAT,
            false,
            0,
            texPos.data
        )
    }

    fun setTexture(texId: Int) {
        GL.glUniform1i(texLoc, 0);
        GL.glActiveTexture(GL.GL_TEXTURE0);
        GL.glBindTexture(GL.GL_TEXTURE_2D, texId);
    }
}