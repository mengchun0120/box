package com.mcdane.box

import android.content.Context
import android.content.res.Resources
import android.util.Log

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
        fun create(res: Resources): SimpleProgram? {
            val program = SimpleProgram()
            return if (program.init(res)) {
                Log.i(TAG, "SimpleProgram created successfully")
                program
            } else {
                Log.i(TAG, "Failed to create SimpleProgram")
                program.close()
                null
            }
        }
    }

    private fun init(res: Resources): Boolean =
        if (init(res, R.raw.simple_vertex_shader, R.raw.simple_frag_shader)) {
            initVarLoc()
        } else {
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