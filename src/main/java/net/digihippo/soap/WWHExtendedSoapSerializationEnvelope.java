package net.digihippo.soap;

//----------------------------------------------------
//
// Generated by www.easywsdl.com
// Version: 4.1.9.1
//
// Created by Quasar Development at 02-12-2015
//
//---------------------------------------------------


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.*;
import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Vector;

public class WWHExtendedSoapSerializationEnvelope extends SoapSerializationEnvelope {
    static HashMap< String,Class> classNames = new HashMap< String, Class>();
    static {
        classNames.put("http://thalesgroup.com/RTTI/2013-11-28/Token/types^^AccessToken",WWHAccessToken.class);
        classNames.put("http://thalesgroup.com/RTTI/2012-01-13/ldb/types^^CallingPoint",WWHCallingPoint.class);
        classNames.put("http://thalesgroup.com/RTTI/2014-02-20/ldb/types^^ServiceLocation",WWHServiceLocation.class);
        classNames.put("http://thalesgroup.com/RTTI/2014-02-20/ldb/types^^ArrayOfCallingPoints",WWHArrayOfCallingPoints.class);
        classNames.put("http://thalesgroup.com/RTTI/2014-02-20/ldb/types^^ServiceDetails",WWHServiceDetails.class);
        classNames.put("http://thalesgroup.com/RTTI/2015-05-14/ldb/types^^BaseStationBoard",WWHBaseStationBoard.class);
        classNames.put("http://thalesgroup.com/RTTI/2015-05-14/ldb/types^^StationBoard",WWHStationBoard.class);
        classNames.put("http://thalesgroup.com/RTTI/2015-05-14/ldb/types^^StationBoardWithDetails",WWHStationBoardWithDetails.class);
        classNames.put("http://thalesgroup.com/RTTI/2015-05-14/ldb/types^^BaseServiceItem",WWHBaseServiceItem.class);
        classNames.put("http://thalesgroup.com/RTTI/2015-05-14/ldb/types^^ServiceItem",WWHServiceItem.class);
        classNames.put("http://thalesgroup.com/RTTI/2015-05-14/ldb/types^^ServiceItemWithCallingPoints",WWHServiceItemWithCallingPoints.class);
        classNames.put("http://thalesgroup.com/RTTI/2015-05-14/ldb/types^^DeparturesBoard",WWHDeparturesBoard.class);
        classNames.put("http://thalesgroup.com/RTTI/2015-05-14/ldb/types^^DeparturesBoardWithDetails",WWHDeparturesBoardWithDetails.class);
        classNames.put("http://thalesgroup.com/RTTI/2015-05-14/ldb/types^^DepartureItem",WWHDepartureItem.class);
        classNames.put("http://thalesgroup.com/RTTI/2015-05-14/ldb/types^^DepartureItemWithCallingPoints",WWHDepartureItemWithCallingPoints.class);
        classNames.put("http://thalesgroup.com/RTTI/2012-01-13/ldb/types^^ArrayOfNRCCMessages",WWHArrayOfNRCCMessages.class);
        classNames.put("http://thalesgroup.com/RTTI/2012-01-13/ldb/types^^ArrayOfAdhocAlert",WWHArrayOfAdhocAlert.class);
        classNames.put("http://thalesgroup.com/RTTI/2014-02-20/ldb/types^^ArrayOfServiceLocations",WWHArrayOfServiceLocations.class);
        classNames.put("http://thalesgroup.com/RTTI/2014-02-20/ldb/types^^ArrayOfArrayOfCallingPoints",WWHArrayOfArrayOfCallingPoints.class);
        classNames.put("http://thalesgroup.com/RTTI/2015-05-14/ldb/types^^ArrayOfServiceItems",WWHArrayOfServiceItems.class);
        classNames.put("http://thalesgroup.com/RTTI/2015-05-14/ldb/types^^ArrayOfServiceItemsWithCallingPoints",WWHArrayOfServiceItemsWithCallingPoints.class);
        classNames.put("http://thalesgroup.com/RTTI/2015-05-14/ldb/types^^ArrayOfDepartureItems",WWHArrayOfDepartureItems.class);
        classNames.put("http://thalesgroup.com/RTTI/2015-05-14/ldb/types^^ArrayOfDepartureItemsWithCallingPoints",WWHArrayOfDepartureItemsWithCallingPoints.class);
    }   

    protected static final int QNAME_NAMESPACE = 0;
    private static final String TYPE_LABEL = "type";

    public WWHExtendedSoapSerializationEnvelope() {
        this(SoapEnvelope.VER11);
    }

    public WWHExtendedSoapSerializationEnvelope(int soapVersion) {
        super(soapVersion);
        implicitTypes = true;
        setAddAdornments(false);
        new WWHMarshalGuid().register(this);
        new MarshalFloat().register(this);
    }

    

    @Override
    protected void writeProperty(XmlSerializer writer, Object obj, PropertyInfo type) throws IOException {
        //!!!!! If you have a compilation error here then you are using old version of ksoap2 library. Please upgrade to the latest version.
        //!!!!! You can find a correct version in Lib folder from generated zip file!!!!!
        if (obj == null || obj== SoapPrimitive.NullNilElement) {
            writer.attribute(xsi, version >= VER12 ? NIL_LABEL : NULL_LABEL, "true");
            return;
        }
        Object[] qName = getInfo(null, obj);
        if (!type.multiRef && qName[2] == null )
        {
            
            if (!implicitTypes || (obj.getClass() != type.type && !(obj instanceof Vector ) && type.type!=String.class)) {
                String xmlName=WWHHelper.getKeyByValue(classNames,obj.getClass());
                if(xmlName!=null) {
                    String[] parts = xmlName.split("\\^\\^");
                    String prefix = writer.getPrefix(parts[0], true);
                    writer.attribute(xsi, TYPE_LABEL, prefix + ":" + parts[1]);
                }
                else
                {
                    if(type.type instanceof String) {
                        String xsdPrefix = writer.getPrefix(xsd, true);
                        String myType = (String) type.type;
                        String[] parts = myType.split("\\^\\^");
                        if (parts.length == 2) {
                            xsdPrefix = writer.getPrefix(parts[0], true);
                            myType = parts[1];
                        }

                        writer.attribute(xsi, TYPE_LABEL, xsdPrefix + ":" + myType);
                    }
                    else
                    {
                        String prefix = writer.getPrefix(type.namespace, true);
                        writer.attribute(xsi, TYPE_LABEL, prefix + ":" + obj.getClass().getSimpleName());
                    }

                }
            }
            //super.writeProperty(writer,obj,type);

            //!!!!! If you have a compilation error here then you are using old version of ksoap2 library. Please upgrade to the latest version.
            //!!!!! You can find a correct version in Lib folder from generated zip file!!!!!
            writeElement(writer, obj, type, qName[QNAME_MARSHAL]);
        }
        else {
            super.writeProperty(writer, obj, type);
        }
    }

    public SoapObject GetExceptionDetail(Element detailElement,String exceptionElementNS,String exceptionElementName)
    {
        int index=detailElement.indexOf(exceptionElementNS,exceptionElementName,0);
        if(index>-1)
        {
            Element errorElement=detailElement.getElement(index);
            return GetSoapObject(errorElement);
        }
        return null;
    }

    public SoapObject GetSoapObject(Element detailElement) {
        try{
            XmlSerializer xmlSerializer = XmlPullParserFactory.newInstance().newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            detailElement.write(xmlSerializer);
            xmlSerializer.flush();

            XmlPullParser xpp = new KXmlParser();
            xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);

            xpp.setInput(new StringReader(writer.toString()));
            xpp.nextTag();
            SoapObject soapObj = new SoapObject(detailElement.getNamespace(),detailElement.getName());
            readSerializable(xpp,soapObj);
            return soapObj;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public Object GetHeader(Element detailElement) {
        if(detailElement.getText(0)!=null)
        {
            SoapPrimitive primitive = new SoapPrimitive(detailElement.getNamespace(),detailElement.getName(),detailElement.getText(0));
            return  primitive;
        }
    
        return GetSoapObject(detailElement);
    }
    
    public Object get(Object soap,Class cl)
    {
        if(soap==null)
        {
            return null;
        }
        try
        {
            if(soap instanceof  Vector)
            {
                Constructor ctor = cl.getConstructor(Object.class,WWHExtendedSoapSerializationEnvelope.class);
                return ctor.newInstance(soap,this);
            }
            {
                if(soap instanceof SoapObject)
                {
                    if(cl ==SoapObject.class)
                    {
                        return soap;
                    }
                    String key=String.format("%s^^%s",((SoapObject)soap).getNamespace(),((SoapObject)soap).getName());
                    if(classNames.containsKey(key))
                    {
                        cl=classNames.get(key);
                    }
                }
                Constructor ctor = cl.getConstructor(Object.class,WWHExtendedSoapSerializationEnvelope.class);
                return ctor.newInstance(soap,this);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

} 

