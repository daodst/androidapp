

package common.app.model.net.okhttps;

import android.content.Context;

import java.util.ArrayList;



public class BaseModel implements BusinessResponse{
    protected ArrayList<BusinessResponse > businessResponseArrayList = new ArrayList<BusinessResponse>();

    protected Context mContext;



    public BaseModel(Context context)
    {
        mContext = context;
    }
    public void addResponseListener(BusinessResponse listener)
    {
        if (!businessResponseArrayList.contains(listener))
        {
            businessResponseArrayList.add(listener);
        }
    }

    public void removeResponseListener(BusinessResponse listener)
    {
        businessResponseArrayList.remove(listener);
    }


    @Override
    public void OnMessageResponse(int id, String jo) {
        for (BusinessResponse iterable_element : businessResponseArrayList)
        {
            iterable_element.OnMessageResponse(id,jo);
        }
    }
}
