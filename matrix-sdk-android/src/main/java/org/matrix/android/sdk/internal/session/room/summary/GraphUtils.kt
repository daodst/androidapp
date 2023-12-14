

package org.matrix.android.sdk.internal.session.room.summary

import java.util.LinkedList

internal data class GraphNode(
        val name: String
)

internal data class GraphEdge(
        val source: GraphNode,
        val destination: GraphNode
)

internal class Graph {

    private val adjacencyList: HashMap<GraphNode, ArrayList<GraphEdge>> = HashMap()

    fun getOrCreateNode(name: String): GraphNode {
        return adjacencyList.entries.firstOrNull { it.key.name == name }?.key
                ?: GraphNode(name).also {
                    adjacencyList[it] = ArrayList()
                }
    }

    fun addEdge(sourceName: String, destinationName: String) {
        val source = getOrCreateNode(sourceName)
        val destination = getOrCreateNode(destinationName)
        adjacencyList.getOrPut(source) { ArrayList() }.add(
                GraphEdge(source, destination)
        )
    }

    fun addEdge(source: GraphNode, destination: GraphNode) {
        adjacencyList.getOrPut(source) { ArrayList() }.add(
                GraphEdge(source, destination)
        )
    }

    fun edgesOf(node: GraphNode): List<GraphEdge> {
        return adjacencyList[node]?.toList() ?: emptyList()
    }

    fun withoutEdges(edgesToPrune: List<GraphEdge>): Graph {
        val output = Graph()
        this.adjacencyList.forEach { (vertex, edges) ->
            output.getOrCreateNode(vertex.name)
            edges.forEach {
                if (!edgesToPrune.contains(it)) {
                    
                    output.addEdge(it.source, it.destination)
                }
            }
        }
        return output
    }

    
    fun findBackwardEdges(startFrom: GraphNode? = null): List<GraphEdge> {
        val backwardEdges = mutableSetOf<GraphEdge>()
        val visited = mutableMapOf<GraphNode, Int>()
        val notVisited = -1
        val inPath = 0
        val completed = 1
        adjacencyList.keys.forEach {
            visited[it] = notVisited
        }
        val stack = LinkedList<GraphNode>()

        (startFrom ?: adjacencyList.entries.firstOrNull { visited[it.key] == notVisited }?.key)
                ?.let {
                    stack.push(it)
                    visited[it] = inPath
                }

        while (stack.isNotEmpty()) {
            val vertex = stack.peek() ?: break
            
            var destination: GraphNode? = null
            edgesOf(vertex).forEach {
                when (visited[it.destination]) {
                    notVisited -> {
                        
                        destination = it.destination
                    }
                    inPath     -> {
                        
                        backwardEdges.add(it)
                    }
                    completed  -> {
                        
                    }
                }
            }
            if (destination == null) {
                
                stack.pop().let {
                    visited[it] = completed
                }
            } else {
                
                stack.push(destination)
                visited[destination!!] = inPath
            }

            if (stack.isEmpty()) {
                
                adjacencyList.entries.firstOrNull { visited[it.key] == notVisited }?.key?.let {
                    stack.push(it)
                    visited[it] = inPath
                }
            }
        }

        return backwardEdges.toList()
    }

    
    fun flattenDestination(): Map<GraphNode, Set<GraphNode>> {
        val result = HashMap<GraphNode, Set<GraphNode>>()
        adjacencyList.keys.forEach { vertex ->
            result[vertex] = flattenOf(vertex)
        }
        return result
    }

    private fun flattenOf(node: GraphNode): Set<GraphNode> {
        val result = mutableSetOf<GraphNode>()
        val edgesOf = edgesOf(node)
        result.addAll(edgesOf.map { it.destination })
        edgesOf.forEach {
            result.addAll(flattenOf(it.destination))
        }
        return result
    }

    override fun toString(): String {
        return buildString {
            adjacencyList.forEach { (node, edges) ->
                append("${node.name} : [")
                append(edges.joinToString(" ") { it.destination.name })
                append("]\n")
            }
        }
    }
}
