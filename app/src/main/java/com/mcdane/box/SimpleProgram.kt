package com.mcdane.box

import android.content.res.Resources
import android.util.Log
import android.opengl.GLES30 as GL

class SimpleProgram private constructor(): ShaderProgram() {
    private var useObjRefLoc: Int = -1
    private var objRefLoc: Int = -1
    private var viewportSizeLoc: Int = -1
    private var viewportOriginLoc: Int = -1
    private var directionLoc: Int = -1
    private var useDirectionLoc: Int = -1
    private var positionLoc: Int = -1
    private var texPosLoc: Int = -1
    private var useColorLoc: Int = -1
    private var useTexColorLoc: Int = -1
    private var colorLoc: Int = -1
    private var texColorLoc: Int = -1
    private var texLoc: Int = -1
    private var alphaLoc: Int = -1

    companion object {
        fun create(res: Resources): SimpleProgram? =
            SimpleProgram().takeIf{ it.init(res) }
    }

    fun setUseObj(enabled: Boolean) =
        GL.glUniform1i(useObjRefLoc, if (enabled) 1 else 0)

    fun setObjRef(objRef: FloatArray) =
        GL.glUniform2fv(objRefLoc, 1, objRef, 0)

    fun setViewportSize(viewportSize: FloatArray) =
        GL.glUniform2fv(viewportSizeLoc, 1, viewportSize, 0)

    fun setViewportOrigin(viewportOrigin: FloatArray) =
        GL.glUniform2fv(viewportOriginLoc, 1, viewportOrigin, 0)

    fun setColor(color: FloatArray) =
        GL.glUniform4fv(colorLoc, 1, color, 0)

    fun setUseColor(use: Boolean) =
        GL.glUniform1i(useColorLoc, if (use) 1 else 0)

    fun setUseDirection(use: Boolean) =
        GL.glUniform1i(useDirectionLoc, if (use) 1 else 0)

    fun setDirection(direction: FloatArray) =
        GL.glUniform2fv(directionLoc, 1, direction, 0)

    fun setUseTexColor(use: Boolean) =
        GL.glUniform1i(useTexColorLoc, if (use) 1 else 0)

    fun setTexColor(texColor: FloatArray) =
        GL.glUniform4fv(texColorLoc, 1, texColor, 0)

    fun setAlpha(alpha: Float) =
        GL.glUniform1f(alphaLoc, alpha)

    fun setPositionTexPos(va: VertexArray) {
        GL.glBindVertexArray(va.arrayObj)
        GL.glBindBuffer(GL.GL_ARRAY_BUFFER, va.bufferObj)
        GL.glVertexAttribPointer(positionLoc, FLOATS_PER_POS,  GL.GL_FLOAT, false,
                                 va.stride(0), va.offset(0))
        GL.glEnableVertexAttribArray(positionLoc)

        if (va.numBlocks > 1) {
            GL.glVertexAttribPointer(texPosLoc, FLOATS_PER_POS,  GL.GL_FLOAT, false,
                va.stride(1), va.offset(1))
            GL.glEnableVertexAttribArray(texPosLoc)
        }
    }

    fun setTexture(texId: Int) {
        GL.glUniform1i(texLoc, 0);
        GL.glActiveTexture(GL.GL_TEXTURE0);
        GL.glBindTexture(GL.GL_TEXTURE_2D, texId);
    }

    private fun init(res: Resources): Boolean =
        if (init(res, R.raw.simple_vertex_shader, R.raw.simple_frag_shader) && initVarLoc()) {
            Log.i(TAG, "SimpleProgram created successfully")
            true
        } else {
            Log.e(TAG, "Failed to create SimpleProgram")
            close()
            false
        }

    private fun initVarLoc(): Boolean {
        useObjRefLoc = getUniformLocation("useObjRef") ?: return false
        objRefLoc = getUniformLocation("objRef") ?: return false
        viewportSizeLoc = getUniformLocation("viewportSize") ?: return false
        viewportOriginLoc = getUniformLocation("viewportOrigin") ?: return false
        directionLoc = getUniformLocation("direction") ?: return false
        useDirectionLoc = getUniformLocation("useDirection") ?: return false
        positionLoc = getAttributeLocation("position") ?: return false
        texPosLoc = getAttributeLocation("texPos") ?: return false
        useColorLoc = getUniformLocation("useColor") ?: return false
        useTexColorLoc = getUniformLocation("useTexColor") ?: return false
        colorLoc = getUniformLocation("color") ?: return false
        texColorLoc = getUniformLocation("texColor") ?: return false
        texLoc = getUniformLocation("tex") ?: return false
        alphaLoc = getUniformLocation("alpha") ?: return false
        return true
    }
}