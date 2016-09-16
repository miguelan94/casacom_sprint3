package com.streamnow.lsmobile.utils;

import android.app.Application;

import com.streamnow.lsmobile.datamodel.DMCategory;
import com.streamnow.lsmobile.datamodel.LDSessionUser;
import com.streamnow.lsmobile.interfaces.IMenuPrintable;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** !
 * Created by Miguel Estévez on 31/1/16.
 */
public class Lindau extends Application
{
    private static Lindau sharedInstance;
    public String appId = "com.streamnow.lindaumobile";
    public String appDemoAccount = "demo.lindau";
    public String BP = "Lindau";
    private LDSessionUser currentSessionUser;
    private ArrayList<IMenuPrintable> userTree;
    private ArrayList<IMenuPrintable> repoTree;

    public LDSessionUser getCurrentSessionUser()
    {
        return this.currentSessionUser;
    }

    public void setCurrentSessionUser(LDSessionUser userInfo)
    {
        currentSessionUser = userInfo;
    }

    public void setUserTree(ArrayList<IMenuPrintable> pUserTree)
    {
        userTree = pUserTree;
    }

    public void setRepoTree(ArrayList<IMenuPrintable> pRepoTree)
    {
        repoTree = pRepoTree;
    }

    public ArrayList<IMenuPrintable> getUserTree()
    {
        return this.userTree;
    }

    public ArrayList<IMenuPrintable> getRepoTree()
    {
        return this.repoTree;
    }

    public ArrayList<IMenuPrintable> getTreeWithCategoryId(String categoryId)
    {
        ArrayList <IMenuPrintable> retArray = null;

        for( int i = 0; i < this.userTree.size(); i++ )
        {
            DMCategory category = (DMCategory) this.userTree.get(i);

            if( category.id.equals(categoryId) )
            {
                retArray = new ArrayList<>();
                if( category.categories != null )
                {
                    retArray.addAll(category.categories);
                }
                if( category.docs != null )
                {
                    retArray.addAll(category.docs);
                }
                break;
            }
        }

        if( retArray == null )
        {
            retArray = new ArrayList<>();
            for( int i = 0; i < this.repoTree.size(); i++ )
            {
                DMCategory category = (DMCategory) this.repoTree.get(i);

                if( category.id.equals(categoryId) )
                {
                    if( category.categories != null )
                    {
                        retArray.addAll(category.categories);
                    }
                    if( category.docs != null )
                    {
                        retArray.addAll(category.docs);
                    }
                    break;
                }
            }
        }
        return retArray;
    }

    public int colorFromUIColor(float red, float green, float blue, float alpha)
    {
        int r = (int)(red * 255);
        int g = (int)(green * 255);
        int b = (int)(blue * 255);
        int a = (int)(alpha * 255);

        return (r << 16) | (g << 8) | (b) | (a << 24);
    }

    public int colorFromRGBAString2(String rgbaString)
    {
        Pattern c = Pattern.compile("rgba *\\( *([0-9]+), *([0-9]+), *([0-9]+) *\\)");
        Matcher m = c.matcher(rgbaString);

        if( m.matches() )
        {
            int r = Integer.valueOf(m.group(1));
            int g = Integer.valueOf(m.group(2));
            int b = Integer.valueOf(m.group(3));
            int a = Integer.valueOf(m.group(4));

            return (r << 16) | (g << 8) | (b) | (a << 24);
        }

        return 0;
    }

    public int colorFromRGBAString(String rgbaString)
    {
        String[] colors = rgbaString.substring(5, rgbaString.length() - 1 ).split(",");
        int r = Integer.parseInt(colors[0].trim());
        int g = Integer.parseInt(colors[1].trim());
        int b = Integer.parseInt(colors[2].trim());
        int a = Integer.parseInt(colors[3].trim()) * 255;

        return (r << 16) | (g << 8) | (b) | (a << 24);
    }

    public static Lindau getInstance()
    {
        return sharedInstance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        sharedInstance = this;
    }


}
