package org.androidcru.crucentralcoast.presentation.views.forms;

import android.content.Context;
import android.support.v4.app.Fragment;

public abstract class FormContentFragment extends Fragment implements FormContent
{
    protected FormHolder formHolder;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        formHolder = (FormHolder) getActivity();
    }
}