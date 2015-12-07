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

public class WWHBaseServiceItem extends AttributeContainer implements KvmSerializable
{

    
    public String sta;
    
    public String eta;
    
    public String std;
    
    public String etd;
    
    public String platform;
    
    public String _operator;
    
    public String operatorCode;
    
    public Boolean isCircularRoute;
    
    public Boolean filterLocationCancelled;
    
    public WWHEnums.ServiceType serviceType=WWHEnums.ServiceType.train;
    
    public String serviceID;
    
    public WWHArrayOfAdhocAlert adhocAlerts=new WWHArrayOfAdhocAlert();

    public WWHBaseServiceItem ()
    {
    }

    public WWHBaseServiceItem (Object paramObj,WWHExtendedSoapSerializationEnvelope __envelope)
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
                if (info.name.equals("sta"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.sta = j.toString();
                        }
                    }
                    else if (obj!= null && obj instanceof String){
                        this.sta = (String)obj;
                    }
                    continue;
                }
                if (info.name.equals("eta"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.eta = j.toString();
                        }
                    }
                    else if (obj!= null && obj instanceof String){
                        this.eta = (String)obj;
                    }
                    continue;
                }
                if (info.name.equals("std"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.std = j.toString();
                        }
                    }
                    else if (obj!= null && obj instanceof String){
                        this.std = (String)obj;
                    }
                    continue;
                }
                if (info.name.equals("etd"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.etd = j.toString();
                        }
                    }
                    else if (obj!= null && obj instanceof String){
                        this.etd = (String)obj;
                    }
                    continue;
                }
                if (info.name.equals("platform"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.platform = j.toString();
                        }
                    }
                    else if (obj!= null && obj instanceof String){
                        this.platform = (String)obj;
                    }
                    continue;
                }
                if (info.name.equals("operator"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this._operator = j.toString();
                        }
                    }
                    else if (obj!= null && obj instanceof String){
                        this._operator = (String)obj;
                    }
                    continue;
                }
                if (info.name.equals("operatorCode"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.operatorCode = j.toString();
                        }
                    }
                    else if (obj!= null && obj instanceof String){
                        this.operatorCode = (String)obj;
                    }
                    continue;
                }
                if (info.name.equals("isCircularRoute"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.isCircularRoute = new Boolean(j.toString());
                        }
                    }
                    else if (obj!= null && obj instanceof Boolean){
                        this.isCircularRoute = (Boolean)obj;
                    }
                    continue;
                }
                if (info.name.equals("filterLocationCancelled"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.filterLocationCancelled = new Boolean(j.toString());
                        }
                    }
                    else if (obj!= null && obj instanceof Boolean){
                        this.filterLocationCancelled = (Boolean)obj;
                    }
                    continue;
                }
                if (info.name.equals("serviceType"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.serviceType = WWHEnums.ServiceType.fromString(j.toString());
                        }
                    }
                    else if (obj!= null && obj instanceof WWHEnums.ServiceType){
                        this.serviceType = (WWHEnums.ServiceType)obj;
                    }
                    continue;
                }
                if (info.name.equals("serviceID"))
                {
        
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                    {
                        SoapPrimitive j =(SoapPrimitive) obj;
                        if(j.toString()!=null)
                        {
                            this.serviceID = j.toString();
                        }
                    }
                    else if (obj!= null && obj instanceof String){
                        this.serviceID = (String)obj;
                    }
                    continue;
                }
                if (info.name.equals("adhocAlerts"))
                {
                    Object j = obj;
                    this.adhocAlerts = new WWHArrayOfAdhocAlert(j,__envelope);
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
            return this.sta!=null?this.sta:SoapPrimitive.NullSkip;
        }
        if(propertyIndex==1)
        {
            return this.eta!=null?this.eta:SoapPrimitive.NullSkip;
        }
        if(propertyIndex==2)
        {
            return this.std!=null?this.std:SoapPrimitive.NullSkip;
        }
        if(propertyIndex==3)
        {
            return this.etd!=null?this.etd:SoapPrimitive.NullSkip;
        }
        if(propertyIndex==4)
        {
            return this.platform!=null?this.platform:SoapPrimitive.NullSkip;
        }
        if(propertyIndex==5)
        {
            return _operator;
        }
        if(propertyIndex==6)
        {
            return operatorCode;
        }
        if(propertyIndex==7)
        {
            return this.isCircularRoute!=null?this.isCircularRoute:SoapPrimitive.NullSkip;
        }
        if(propertyIndex==8)
        {
            return this.filterLocationCancelled!=null?this.filterLocationCancelled:SoapPrimitive.NullSkip;
        }
        if(propertyIndex==9)
        {
            return this.serviceType!=null?this.serviceType.toString():SoapPrimitive.NullSkip;
        }
        if(propertyIndex==10)
        {
            return serviceID;
        }
        if(propertyIndex==11)
        {
            return this.adhocAlerts!=null?this.adhocAlerts:SoapPrimitive.NullSkip;
        }
        return null;
    }


    @Override
    public int getPropertyCount() {
        return 12;
    }

    @Override
    public void getPropertyInfo(int propertyIndex, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info)
    {
        if(propertyIndex==0)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "sta";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
        if(propertyIndex==1)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "eta";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
        if(propertyIndex==2)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "std";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
        if(propertyIndex==3)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "etd";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
        if(propertyIndex==4)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "platform";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
        if(propertyIndex==5)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "operator";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
        if(propertyIndex==6)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "operatorCode";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
        if(propertyIndex==7)
        {
            info.type = PropertyInfo.BOOLEAN_CLASS;
            info.name = "isCircularRoute";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
        if(propertyIndex==8)
        {
            info.type = PropertyInfo.BOOLEAN_CLASS;
            info.name = "filterLocationCancelled";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
        if(propertyIndex==9)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "serviceType";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
        if(propertyIndex==10)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "serviceID";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
        if(propertyIndex==11)
        {
            info.type = PropertyInfo.VECTOR_CLASS;
            info.name = "adhocAlerts";
            info.namespace= "http://thalesgroup.com/RTTI/2015-05-14/ldb/types";
        }
    }
    
    @Override
    public void setProperty(int arg0, Object arg1)
    {
    }

}