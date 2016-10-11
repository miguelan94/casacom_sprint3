package com.streamnow.flow.datamodel;

import com.streamnow.flow.interfaces.IMenuPrintable;

/**
 * !
 * Created by Miguel Estévez on 12/2/16.
 */
public abstract class DMElement implements IMenuPrintable {
    public enum DMElementType {
        DMElementTypeCategory,
        DMElementTypeDocument
    }

    public DMElementType elementType;
}
