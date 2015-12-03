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

import java.util.ArrayList;
import java.util.Hashtable;

public class WWHArrayOfCallingPoints extends AttributeContainer implements KvmSerializable
{

    
    public ArrayList< WWHCallingPoint> callingPoint =new ArrayList<WWHCallingPoint >();
    
    public WWHEnums.ServiceType serviceType=WWHEnums.ServiceType.train;
    
    public Boolean serviceChangeRequired=false;
    
    public Boolean assocIsCancelled=false;

    public WWHArrayOfCallingPoints ()
    {
    }

    public WWHArrayOfCallingPoints (Object paramObj,WWHExtendedSoapSerializationEnvelope __envelope)
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
                if (info.name.equals("callingPoint"))
                {
        
                    if(this.callingPoint==null)
                    {
                        this.callingPoint = new ArrayList<WWHCallingPoint>();
                    }
                    Object j =obj;
                    WWHCallingPoint j1= (WWHCallingPoint)__envelope.get(j,WWHCallingPoint.class);
                    this.callingPoint.add(j1);
        
                    continue;
                }

            }

        }



        if (inObj.hasAttribute("serviceType"))
        {	
            Object j = inObj.getAttribute("serviceType");
            if (j != null)
            {
                serviceType = WWHEnums.ServiceType.fromString(j.toString());
	            
            }
        }

        if (inObj.hasAttribute("serviceChangeRequired"))
        {	
            Object j = inObj.getAttribute("serviceChangeRequired");
            if (j != null)
            {
                serviceChangeRequired = new Boolean(j.toString());
	            
            }
        }

        if (inObj.hasAttribute("assocIsCancelled"))
        {	
            Object j = inObj.getAttribute("assocIsCancelled");
            if (j != null)
            {
                assocIsCancelled = new Boolean(j.toString());
	            
            }
        }

    }

    @Override
    public Object getProperty(int propertyIndex) {
        //!!!!! If you have a compilation error here then you are using old version of ksoap2 library. Please upgrade to the latest version.
        //!!!!! You can find a correct version in Lib folder from generated zip file!!!!!
        if(propertyIndex>=+0 && propertyIndex< + 0+this.callingPoint.size())
        {
            return this.callingPoint.get(propertyIndex-(+0));
        }
        return null;
    }


    @Override
    public int getPropertyCount() {
        return 0+callingPoint.size();
    }

    @Override
    public void getPropertyInfo(int propertyIndex, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info)
    {
        if(propertyIndex>=+0 && propertyIndex <= +0+this.callingPoint.size())
        {
            info.type = WWHCallingPoint.class;
            info.name = "callingPoint";
            info.namespace= "http://thalesgroup.com/RTTI/2014-02-20/ldb/types";
        }
    }
    
    @Override
    public void setProperty(int arg0, Object arg1)
    {
    }


    @Override
    public int getAttributeCount() {
        return 3;
    }
    
    @Override
    public void getAttributeInfo(int index, AttributeInfo info) {
        if(index==0)
        {
            info.name = "serviceType";
            info.namespace= "";
            if(this.serviceType!=null)
            {
                info.setValue(this.serviceType);
            }
            
        }
        if(index==1)
        {
            info.name = "serviceChangeRequired";
            info.namespace= "";
            if(this.serviceChangeRequired!=null)
            {
                info.setValue(this.serviceChangeRequired);
            }
            
        }
        if(index==2)
        {
            info.name = "assocIsCancelled";
            info.namespace= "";
            if(this.assocIsCancelled!=null)
            {
                info.setValue(this.assocIsCancelled);
            }
            
        }
    }

    @Override
    public void getAttribute(int i, AttributeInfo attributeInfo) {

    }

    @Override
    public void setAttribute(AttributeInfo attributeInfo) {

    }}
