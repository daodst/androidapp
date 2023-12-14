

package common.app.base.base;

import java.util.List;


public interface PermissionListener {

    
    void onGranted();

    
    void onDenied(List<String> deniedList);

}
