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

public class WWHBaseStationBoard extends AttributeContainer implements KvmSerializable
{

    
    public java.util.Date generatedAt;
    
    public String locationName;
    
    public String crs;
    
    public String filterLocationName;
    
    public String filtercrs;
    
    public WWHEnums.FilterType filterType;
    
    public WWHArrayOfNRCCMessages nrccMessages=new WWHArrayOfNRCCMessages();
    
    public Boolean platformAvailable;
    
    public Boolean areServicesAvailable;

    public WWHBaseStationBoard ()
    {
    }

    public WWHBaseStationBoard (Object paramObj,WWHExtendedSoapSerializationEnvelope __envelope)
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
                if (info.name.equals("generatedAt"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.generatedAt = WWHHelper.ConvertFromWebService(j.toString());
                        }
                    }
                    else if (obj!= null && obj instanceof java.util.Date){
                        this.generatedAt = (java.util.Date)obj;
                    }
                    continue;
                }
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
                if (info.name.equals("filterLocationName"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.filterLocationName = j.toString();
                        }
                    }
                    else if (obj!= null && obj instanceof String){
                        this.filterLocationName = (String)obj;
                    }
                    continue;
                }
                if (info.name.equals("filtercrs"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.filtercrs = j.toString();
                        }
                    }
                    else if (obj!= null && obj instanceof String){
                        this.filtercrs = (String)obj;
                    }
                    continue;
                }
                if (info.name.equals("filterType"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.filterType = WWHEnums.FilterType.fromString(j.toString());
                        }
                    }
                    else if (obj!= null && obj instanceof WWHEnums.FilterType){
                        this.filterType = (WWHEnums.FilterType)obj;
                    }
                    continue;
                }
                if (info.name.equals("nrccMessages"))
                {
                    Object j = obj;
                    this.nrccMessages = new WWHArrayOfNRCCMessages(j,__envelope);
                    continue;
                }
                if (info.name.equals("platformAvailable"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.platformAvailable = new Boolean(j.toString());
                        }
                    }
                    else if (obj!= null && obj instanceof Boolean){
                        this.platformAvailable = (Boolean)obj;
                    }
                    continue;
                }
                if (info.name.equals("areServicesAvailable"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.areServicesAvailable = new Boolean(j.toString());
                        }
                    }
                    else if (obj!= null && obj instanceof Boolean){
                        this.areServicesAvailable = (Boolean)obj;
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
            return this.generatedAt!=null?WWHHelper.getDateTimeFormat().format(this.generatedAt):SoapPrimitive.NullSkip;
        }
        if(propertyIndex==1)
        {
            return locationName;
        }
        if(propertyIndex==2)
        {
            return crs;
        }
        if(propertyIndex==3)
        {
            return this.filterLocationName!=null?this.filterLocationName:SoapPrimitive.NullSkip;
        }
        if(propertyIndex==4)
        {
            return this.filtercrs!=null?this.filtercrs:SoapPrimitive.NullSkip;
        }
        if(propertyIndex==5)
        {
            return this.filterType!=null?this.filterType.toString():SoapPrimitive.NullSkip;
        }
        if(propertyIndex==6)
        {
            return this.nrccMessages!=null?this.nrccMessages:SoapPrimitive.NullSkip;
        }
        if(propertyIndex==7)
        {
            return this.platformAvailable!=null?this.platformAvailable:SoapPrimitive.NullSkip;
        }
        if(propertyIndex==8)
        {
            return this.areServicesAvailable!=null?this.areServicesAvailable:SoapPrimitive.NullSkip;
        }
        return null;
    }


    @Override
    public int getPropertyCount() {
        return 9;
    }

    @Override
    public void getPropertyInfo(int propertyIndex, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info)
    {
        if(propertyIndex==0)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "generatedAt";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
        if(propertyIndex==1)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "locationName";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
        if(propertyIndex==2)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "crs";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
        if(propertyIndex==3)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "filterLocationName";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
        if(propertyIndex==4)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "filtercrs";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
        if(propertyIndex==5)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "filterType";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
        if(propertyIndex==6)
        {
            info.type = PropertyInfo.VECTOR_CLASS;
            info.name = "nrccMessages";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
        if(propertyIndex==7)
        {
            info.type = PropertyInfo.BOOLEAN_CLASS;
            info.name = "platformAvailable";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
        if(propertyIndex==8)
        {
            info.type = PropertyInfo.BOOLEAN_CLASS;
            info.name = "areServicesAvailable";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
    }
    
    @Override
    public void setProperty(int arg0, Object arg1)
    {
    }

}