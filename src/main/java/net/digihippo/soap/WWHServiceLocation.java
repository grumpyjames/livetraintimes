package net.digihippo.soap;

//----------------------------------------------------
//
// Generated by www.easywsdl.com
// Version: 4.1.9.1
//
// Created by Quasar Development at 02-12-2015
//
//---------------------------------------------------


import org.ksoap2.serialization.*;

import java.util.Hashtable;

public class WWHServiceLocation extends AttributeContainer implements KvmSerializable
{

    
    public String locationName;
    
    public String crs;
    
    public String via;
    
    public String futureChangeTo;
    
    public Boolean assocIsCancelled;

    public WWHServiceLocation ()
    {
    }

    public WWHServiceLocation (Object paramObj,WWHExtendedSoapSerializationEnvelope __envelope)
    {
	    
	    if (paramObj == null)
            return;
        AttributeContainer inObj=(AttributeContainer)paramObj;


        if(inObj instanceof SoapObject)
        {
            SoapObject soapObject=(SoapObject)inObj;
            int size = soapObject.getPropertyCount();
            for (int i0=0;i0< size;i0++)
            {
                //if you have compilation error here, please use a ksoap2.jar and ExKsoap2.jar from libs folder (in the generated zip file)
                PropertyInfo info=soapObject.getPropertyInfo(i0);
                Object obj = info.getValue();
                if (info.name.equals("locationName"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.locationName = j.toString();
                        }
                    }
                    else if (obj!= null && obj instanceof String){
                        this.locationName = (String)obj;
                    }
                    continue;
                }
                if (info.name.equals("crs"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.crs = j.toString();
                        }
                    }
                    else if (obj!= null && obj instanceof String){
                        this.crs = (String)obj;
                    }
                    continue;
                }
                if (info.name.equals("via"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.via = j.toString();
                        }
                    }
                    else if (obj!= null && obj instanceof String){
                        this.via = (String)obj;
                    }
                    continue;
                }
                if (info.name.equals("futureChangeTo"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.futureChangeTo = j.toString();
                        }
                    }
                    else if (obj!= null && obj instanceof String){
                        this.futureChangeTo = (String)obj;
                    }
                    continue;
                }
                if (info.name.equals("assocIsCancelled"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.assocIsCancelled = new Boolean(j.toString());
                        }
                    }
                    else if (obj!= null && obj instanceof Boolean){
                        this.assocIsCancelled = (Boolean)obj;
                    }
                    continue;
                }

            }

        }



    }

    @Override
    public Object getProperty(int propertyIndex) {
        //!!!!! If you have a compilation error here then you are using old version of ksoap2 library. Please upgrade to the latest version.
        //!!!!! You can find a correct version in Lib folder from generated zip file!!!!!
        if(propertyIndex==0)
        {
            return locationName;
        }
        if(propertyIndex==1)
        {
            return crs;
        }
        if(propertyIndex==2)
        {
            return this.via!=null?this.via:SoapPrimitive.NullSkip;
        }
        if(propertyIndex==3)
        {
            return this.futureChangeTo!=null?this.futureChangeTo:SoapPrimitive.NullSkip;
        }
        if(propertyIndex==4)
        {
            return this.assocIsCancelled!=null?this.assocIsCancelled:SoapPrimitive.NullSkip;
        }
        return null;
    }


    @Override
    public int getPropertyCount() {
        return 5;
    }

    @Override
    public void getPropertyInfo(int propertyIndex, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info)
    {
        if(propertyIndex==0)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "locationName";
            info.namespace= "http://thalesgroup.com/RTTI/2014-02-20/ldb/types";
        }
        if(propertyIndex==1)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "crs";
            info.namespace= "http://thalesgroup.com/RTTI/2014-02-20/ldb/types";
        }
        if(propertyIndex==2)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "via";
            info.namespace= "http://thalesgroup.com/RTTI/2014-02-20/ldb/types";
        }
        if(propertyIndex==3)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "futureChangeTo";
            info.namespace= "http://thalesgroup.com/RTTI/2014-02-20/ldb/types";
        }
        if(propertyIndex==4)
        {
            info.type = PropertyInfo.BOOLEAN_CLASS;
            info.name = "assocIsCancelled";
            info.namespace= "http://thalesgroup.com/RTTI/2014-02-20/ldb/types";
        }
    }
    
    @Override
    public void setProperty(int arg0, Object arg1)
    {
    }

}