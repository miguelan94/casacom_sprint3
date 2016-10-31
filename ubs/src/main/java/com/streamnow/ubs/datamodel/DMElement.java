package com.streamnow.ubs.datamodel;

import com.streamnow.ubs.interfaces.IMenuPrintable;

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
