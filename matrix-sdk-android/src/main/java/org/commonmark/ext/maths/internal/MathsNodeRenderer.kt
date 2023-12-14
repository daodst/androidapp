
package org.commonmark.ext.maths.internal

import org.commonmark.ext.maths.DisplayMaths
import org.commonmark.ext.maths.InlineMaths
import org.commonmark.node.Node
import org.commonmark.renderer.NodeRenderer

internal abstract class MathsNodeRenderer : NodeRenderer {
    override fun getNodeTypes(): Set<Class<out Node>> {
        val types: MutableSet<Class<out Node>> = HashSet()
        types.add(InlineMaths::class.java)
        types.add(DisplayMaths::class.java)
        return types
    }
}
