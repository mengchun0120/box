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

    private var useObjRefLoc: Int = getUniformLocation("useObjRef")
    private var objRefLoc: Int = getUniformLocation("objRef")
    private var viewportSizeLoc: Int = getUniformLocation("viewportSize")
    private var viewportOriginLoc: Int = getUniformLocation("viewportOrigin")
    private var directionLoc: Int = getUniformLocation("direction")
    private var useDirectionLoc: Int = getUniformLocation("useDirection")
    private var positionLoc: Int = getAttributeLocation("position")
    private var texPosLoc: Int = getAttributeLocation("texPos")
    private var useColorLoc: Int = getUniformLocation("useColor")
    private var useTexColorLoc: Int = getUniformLocation("useTexColor")
    private var colorLoc: Int = getUniformLocation("color")
    private var texColorLoc: Int = getUniformLocation("texColor")
    private var texLoc: Int = getUniformLocation("tex")
    private var alphaLoc: Int = getUniformLocation("alpha")

    fun setUseObjRef(enabled: Boolean) =
        GL.glUniform1i(useObjRefLoc, if (enabled) 1 else 0)

    fun setObjRef(objRef: FloatArray) =
        GL.glUniform2fv(objRefLoc, 1, objRef, 0)

    fun setViewportSize(viewportSize: FloatArray) =
        GL.glUniform2fv(viewportSizeLoc, 1, viewportSize, 0)

    fun setViewportOrigin(viewportOrigin: FloatArray) =
        GL.glUniform2fv(viewportOriginLoc, 1, viewportOrigin, 0)

    fun setColor(color: Color) =
        GL.glUniform4fv(colorLoc, 1, color.data, 0)

    fun setUseColor(use: Boolean) =
        GL.glUniform1i(useColorLoc, if (use) 1 else 0)

    fun setUseDirection(use: Boolean) =
        GL.glUniform1i(useDirectionLoc, if (use) 1 else 0)

    fun setDirection(direction: FloatArray) =
        GL.glUniform2fv(directionLoc, 1, direction, 0)

    fun setUseTexColor(use: Boolean) =
        GL.glUniform1i(useTexColorLoc, if (use) 1 else 0)

    fun setTexColor(texColor: Color) =
        GL.glUniform4fv(texColorLoc, 1, texColor.data, 0)

    fun setAlpha(alpha: Float) =
        GL.glUniform1f(alphaLoc, alpha)

    fun setPosition(pos: VertexArray) {
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