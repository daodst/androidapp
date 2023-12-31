

package org.matrix.android.sdk.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.fail


fun assertListEquals(list1: List<Any>?, list2: List<Any>?) {
    if (list1 == null) {
        assertNull(list2)
    } else {
        assertNotNull(list2)

        assertEquals("List sizes must match", list1.size, list2!!.size)

        for (i in list1.indices) {
            assertEquals("Elements at index $i are not equal", list1[i], list2[i])
        }
    }
}


fun assertDictEquals(dict1: Map<String, Any>?, dict2: Map<String, Any>?) {
    if (dict1 == null) {
        assertNull(dict2)
    } else {
        assertNotNull(dict2)

        assertEquals("Map sizes must match", dict1.size, dict2!!.size)

        for (i in dict1.keys) {
            assertEquals("Values for key $i are not equal", dict1[i], dict2[i])
        }
    }
}


fun assertByteArrayNotEqual(a1: ByteArray, a2: ByteArray) {
    if (a1.size != a2.size) {
        fail("Arrays have not the same size.")
    }

    for (index in a1.indices) {
        if (a1[index] != a2[index]) {
            
            return
        }
    }

    fail("Arrays are equals.")
}
