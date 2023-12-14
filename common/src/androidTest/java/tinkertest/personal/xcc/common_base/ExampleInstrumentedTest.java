package tinkertest.personal.xcc.common_base;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("tinkertest.personal.xcc.common_base.test", appContext.getPackageName());
    }
}
