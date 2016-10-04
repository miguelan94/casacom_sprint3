package com.streamnow.lsmobile.datamodel;
import com.streamnow.lsmobile.utils.Lindau;
import org.json.JSONObject;

/** !
 * Created by Miguel Estévez on 2/2/16.
 */
public class LDPartner
{
    public int fontColorTop;
    public int fontColorService;
    public int fontColorBottom;
    public String phone;
    public boolean supportButton;
    public String footerContent;
    public int colorService;
    public String url;
    public String company;
    public int fontColorSmartphone;
    public String logo;
    public int colorTop;
    public boolean footerActive;
    public boolean paidServices;
    public String email;
    public String name;
    public String backgroundImage;
    public String backgroundSmartphoneImage;
    public int backgroundColorSmartphone;
    public int backgroundColorIconSmartphone;
    public int lineColorSmartphone;
    public String smartphoneAppName;

    public LDPartner(JSONObject o)
    {
        Lindau ld = Lindau.getInstance();
        try
        {
            if(!o.isNull("font_color_top") && !o.getString("font_color_top").equals("")) this.fontColorTop = ld.colorFromRGBAString(o.getString("font_color_top"));
            if(!o.isNull("font_color_service") && !o.getString("font_color_service").equals("") ) this.fontColorService = ld.colorFromRGBAString(o.getString("font_color_service"));
            if(!o.isNull("font_color_bottom") && !o.getString("font_color_bottom").equals("")) this.fontColorBottom = ld.colorFromRGBAString(o.getString("font_color_bottom"));
            if(!o.isNull("phone")) this.phone = o.getString("phone");
            if(!o.isNull("support_button")) this.supportButton = o.getBoolean("support_button");
            if(!o.isNull("footer_content")) this.footerContent = o.getString("footer_content");
            if(!o.isNull("color_service") && !o.getString("color_service").equals("")) this.colorService = ld.colorFromRGBAString(o.getString("color_service"));
            if(!o.isNull("url")) this.url = o.getString("url");
            if(!o.isNull("company")) this.company = o.getString("company");
            if(!o.isNull("font_color_smartphone") && !o.getString("font_color_smartphone").equals("")) this.fontColorSmartphone = ld.colorFromRGBAString(o.getString("font_color_smartphone"));
            if(!o.isNull("logo")) this.logo = o.getString("logo");
            if(!o.isNull("background_color_smartphone") && !o.getString("background_color_smartphone").equals("")) this.backgroundColorSmartphone = ld.colorFromRGBAString(o.getString("background_color_smartphone"));
            if(!o.isNull("color_top") && !o.getString("color_top").equals("")) this.colorTop = ld.colorFromRGBAString(o.getString("color_top"));
            if(!o.isNull("footer_active")) this.footerActive = o.getBoolean("footer_active");
            if(!o.isNull("paid_services")) this.paidServices = o.getBoolean("paid_services");
            if(!o.isNull("email")) this.email = o.getString("email");
            if(!o.isNull("name")) this.name = o.getString("name");
            if(!o.isNull("background_image")) this.backgroundImage = o.getString("background_image");
            if(!o.isNull("background_color_icon_smartphone") && !o.getString("background_color_icon_smartphone").equals("")) this.backgroundColorIconSmartphone = ld.colorFromRGBAString(o.getString("background_color_icon_smartphone"));
            if(!o.isNull("line_color_smartphone") && !o.getString("line_color_smartphone").equals(""))this.lineColorSmartphone = ld.colorFromRGBAString(o.getString("line_color_smartphone"));
            if(!o.isNull("background_smartphone_image")) this.backgroundSmartphoneImage = o.getString("background_smartphone_image");
            if(!o.isNull("smartphone_app_name")) this.smartphoneAppName = o.getString("smartphone_app_name");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
