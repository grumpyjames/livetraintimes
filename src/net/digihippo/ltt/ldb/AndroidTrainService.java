package net.digihippo.ltt.ldb;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("CharsetObjectCanBeUsed")
public class AndroidTrainService
{
    private final String token;

    private static final String UTF_8 = "UTF-8";
    private String protocol = "https";

    AndroidTrainService(String token)
    {
        this.token = token;
    }

    Response fetchTrains(final String fromCrs, final String toCrs)
        throws IOException, XmlPullParserException
    {
        InputStream inputStream = makeBoardRequest(protocol, token, fromCrs, toCrs);

        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        parser.setInput(inputStream, UTF_8);
        parser.nextTag();
        return readResponse(parser);
    }

    public static InputStream makeBoardRequest(
        String protocol, String token, String fromCrs, String toCrs) throws IOException
    {
        URL url = new URL(protocol + "://lite.realtime.nationalrail.co.uk/OpenLDBWS/ldb7.asmx");
        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty(
            "SOAPAction", "http://thalesgroup.com/RTTI/2015-05-14/ldb/GetDepBoardWithDetails");
        urlConnection.setRequestProperty(
            "Content-Type", "text/xml;charset=utf-8");
        final String content =
            "<v:Envelope xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:d=\"http://www.w3.org/2001/XMLSchema\"\n" +
                "            xmlns:c=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:v=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <v:Header>\n" +
                "        <n0:AccessToken xmlns:n0=\"http://thalesgroup.com/RTTI/2013-11-28/Token/types\">\n" +
                "            <n0:TokenValue>" + token + "</n0:TokenValue>\n" +
                "        </n0:AccessToken>\n" +
                "    </v:Header>\n" +
                "    <v:Body>\n" +
                "        <n1:GetDepBoardWithDetailsRequest xmlns:n1=\"http://thalesgroup.com/RTTI/2015-05-14/ldb/\">\n" +
                "            <n1:numRows>20</n1:numRows>\n" +
                "            <n1:crs>" + fromCrs + "</n1:crs>\n" +
                (toCrs != null ? ("            <n1:filterCrs>" + toCrs + "</n1:filterCrs>\n") : "") +
                "        </n1:GetDepBoardWithDetailsRequest>\n" +
                "    </v:Body>\n" +
                "</v:Envelope>";
        urlConnection.setDoOutput(true);
        urlConnection.getOutputStream().write(content.getBytes(UTF_8));

        return urlConnection.getInputStream();
    }

    public void httpIsBroken()
    {
        protocol = "http";
    }

    public static final class CallingPoint
    {
        public final String locationName;
        public final String crs;
        public final String scheduledTime;
        public final String expectedTime;

        CallingPoint(
            String locationName,
            String crs,
            String scheduledTime,
            String expectedTime)
        {
            this.locationName = locationName;
            this.crs = crs;
            this.scheduledTime = scheduledTime;
            this.expectedTime = expectedTime;
        }

        @Override
        public String toString()
        {
            return "CallingPoint{" +
                "locationName='" + locationName + '\'' +
                ", crs='" + crs + '\'' +
                ", scheduledTime='" + scheduledTime + '\'' +
                ", expectedTime='" + expectedTime + '\'' +
                '}';
        }
    }

    /*
    "    <soap:Body>\n" +
        "        <GetDepBoardWithDetailsResponse xmlns=\"http://thalesgroup.com/RTTI/2015-05-14/ldb/\">\n" +
        "            <GetStationBoardResult xmlns:lt=\"http://thalesgroup.com/RTTI/2012-01-13/ldb/types\"\n" +

     */
    public static Response readResponse(XmlPullParser parser)
        throws XmlPullParserException, IOException
    {

        String startTag = "Envelope";
        final List<String> entryTree = Arrays.asList(
            "Body", "GetDepBoardWithDetailsResponse", "GetStationBoardResult"
        );
        for (String tag : entryTree)
        {
            System.out.println("I claim I must be at " + startTag);
            parser.require(XmlPullParser.START_TAG, null, startTag);
            while (parser.next() != XmlPullParser.END_TAG)
            {
                System.out.println("I seek " + tag);
                if (parser.getEventType() != XmlPullParser.START_TAG)
                {
                    continue;
                }
                String name = parser.getName();
                if (name.equals(tag))
                {
                    System.out.println("Found " + tag);
                    startTag = tag;
                    break;
                }
                else
                {
                    skip(parser);
                }
            }
        }

        return readResult(parser);
    }

    public static final class Response
    {
        public final String platformAvailable;
        public final List<Service> services;

        Response(String platformAvailable, List<Service> services)
        {
            this.platformAvailable = platformAvailable;
            this.services = services;
        }

        @Override
        public String toString()
        {
            return "Response{" +
                "platformAvailable='" + platformAvailable + '\'' +
                ", services=" + services +
                '}';
        }
    }

    public static final class Destination
    {
        public final String crs;
        public final String via;

        public Destination(String crs,  String via)
        {
            this.crs = crs;
            this.via = via;
        }

        @Override
        public String toString()
        {
            return "Destination{" +
                "crs='" + crs + '\'' +
                ", via='" + via + '\'' +
                '}';
        }
    }

    public static final class Service
    {
        public final String std;
        public final String etd;
        public final String operator;
        public final String serviceType;
        public final String serviceID;
        public final List<List<CallingPoint>> callingPointLists;
        public final String platform;
        public final boolean isCircularRoute;
        public final List<Destination> destinations;

        Service(
            String std,
            String etd,
            String operator,
            String serviceType,
            String serviceID,
            List<List<CallingPoint>> callingPointLists,
            String platform,
            boolean isCircularRoute,
            List<Destination> destinations)
        {

            this.std = std;
            this.etd = etd;
            this.operator = operator;
            this.serviceType = serviceType;
            this.serviceID = serviceID;
            this.callingPointLists = callingPointLists;
            this.platform = platform;
            this.isCircularRoute = isCircularRoute;
            this.destinations = destinations;
        }

        @Override
        public String toString()
        {
            return "Service{" +
                "std='" + std + '\'' +
                ", etd='" + etd + '\'' +
                ", operator='" + operator + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", serviceID='" + serviceID + '\'' +
                ", callingPointLists=" + callingPointLists +
                ", platform='" + platform + '\'' +
                ", isCircularRoute=" + isCircularRoute +
                ", destinations=" + destinations +
                '}';
        }
    }

    private static Response readResult(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, null, "GetStationBoardResult");

        String platformAvailable = null;
        List<Service> services = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("platformAvailable")) {
                platformAvailable = readTextField(parser, "platformAvailable");
            } else if (name.equals("trainServices")) {
                services = readServices(parser);
            } else {
                skip(parser);
            }
        }

        return new Response(platformAvailable, services);
    }

    /*
    "                        <lt3:std>20:44</lt3:std>\n" +
        "                        <lt3:etd>20:49</lt3:etd>\n" +
        "                        <lt3:operator>Gatwick Express</lt3:operator>\n" +
        "                        <lt3:operatorCode>GX</lt3:operatorCode>\n" +
        "                        <lt3:serviceType>train</lt3:serviceType>\n" +
        "                        <lt3:serviceID>YXGLgvPFXQgwbuqEaTrqiA==</lt3:serviceID>\n" +

     */
    private static List<Service> readServices(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        final List<Service> services = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, "trainServices");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("service")) {
                services.add(readService(parser));
            } else {
                skip(parser);
            }
        }

        return services;
    }

    /*

    /*
    <lt3:platformAvailable>true</lt3:platformAvailable>\n" +
        "                <lt3:trainServices>\n" +
        "                    <lt3:service>\n" +
        "                        <lt3:std>20:44</lt3:std>\n" +
        "                        <lt3:etd>20:49</lt3:etd>\n" +
        "                        <lt3:operator>Gatwick Express</lt3:operator>\n" +
        "                        <lt3:operatorCode>GX</lt3:operatorCode>\n" +
        "                        <lt3:serviceType>train</lt3:serviceType>\n" +
        "                        <lt3:serviceID>YXGLgvPFXQgwbuqEaTrqiA==</lt3:serviceID>\n" +
        "                        <lt3:origin>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>London Victoria</lt2:locationName>\n" +
        "                                <lt2:crs>VIC</lt2:crs>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:origin>\n" +
        "                        <lt3:destination>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>Brighton</lt2:locationName>\n" +
        "                                <lt2:crs>BTN</lt2:crs>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:destination>\n" +
        "                        <lt3:previousCallingPoints/>\n" +
        "                        <lt3:subsequentCallingPoints>
     */
    private static Service readService(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        parser.require(XmlPullParser.START_TAG, null, "service");

        String std = null;
        String etd = null;
        String operator = null;
        String serviceType = null;
        String serviceID = null;
        String platform = null;
        List<List<CallingPoint>> callingPointLists = null;
        boolean isCircularRoute = false;
        List<Destination> destinations = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            switch (name)
            {
                case "etd":
                    etd = readTextField(parser, "etd");
                    break;
                case "std":
                    std = readTextField(parser, "std");
                    break;
                case "operator":
                    operator = readTextField(parser, "operator");
                    break;
                case "serviceType":
                    serviceType = readTextField(parser, "serviceType");
                    break;
                case "serviceID":
                    serviceID = readTextField(parser, "serviceID");
                    break;
                case "subsequentCallingPoints":
                    callingPointLists = readCallingPointLists(parser);
                    break;
                case "platform":
                    platform = readTextField(parser, "platform");
                    break;
                case "isCircularRoute":
                    isCircularRoute =
                        Boolean.parseBoolean(readTextField(parser, "isCircularRoute"));
                    break;
                case "destination":
                    destinations =
                        readDestinations(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        return new Service(
            std, etd, operator, serviceType,
            serviceID, callingPointLists, platform, isCircularRoute,
            destinations);
    }

    /*<lt3:destination>
                            <lt2:location>
                                <lt2:locationName>Gatwick Airport</lt2:locationName>
                                <lt2:crs>GTW</lt2:crs>
                            </lt2:location>
                        </lt3:destination>*/
    private static List<Destination> readDestinations(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        parser.require(XmlPullParser.START_TAG, null, "destination");
        final List<Destination> destinations = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG)
            {
                continue;
            }
            String name = parser.getName();
            if (name.equals("location")) {
                destinations.add(readDestination(parser));
            }
            else {
                skip(parser);
            }
        }

        return destinations;
    }

    private static Destination readDestination(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        parser.require(XmlPullParser.START_TAG, null, "location");
        String crs = null;
        String via = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if ("crs".equals(name))
            {
                crs = readTextField(parser, "crs");
            }
            else if ("via".equals(name))
            {
                // FIXME: is this really a text field?
                via = readTextField(parser, "via");
            }
            else
            {
                skip(parser);
            }
        }

        return new Destination(crs, via);
    }

    /*
    <lt3:subsequentCallingPoints>\n" +
        "                            <lt2:callingPointList serviceType=\"train\" serviceChangeRequired=\"false\"\n" +
        "                                                  assocIsCancelled=\"false\">\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Clapham Junction</lt:locationName>\n" +
        "                                    <lt:crs>CLJ</lt:crs>\n" +
        "                                    <lt:st>21:42</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>
     */

    private static List<List<CallingPoint>> readCallingPointLists(XmlPullParser parser)
        throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, null, "subsequentCallingPoints");
        final List<List<CallingPoint>> callingPointList = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG)
            {
                continue;
            }
            String name = parser.getName();
            if (name.equals("callingPointList")) {
                callingPointList.add(readCallingPointList(parser));
            }
            else {
                skip(parser);
            }
        }

        return callingPointList;
    }

    private static List<CallingPoint> readCallingPointList(XmlPullParser parser)
        throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, null, "callingPointList");
        final List<CallingPoint> callingPoints = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG)
            {
                continue;
            }
            String name = parser.getName();
            if (name.equals("callingPoint")) {
                callingPoints.add(readCallingPoint(parser));
            }
            else {
                skip(parser);
            }
        }

        return callingPoints;
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.

    /*
    <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Three Bridges</lt:locationName>\n" +
        "                                    <lt:crs>TBD</lt:crs>\n" +
        "                                    <lt:st>22:14</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>
     */
    private static CallingPoint readCallingPoint(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "callingPoint");
        String locationName = null;
        String crs = null;
        String scheduledTime = null;
        String expectedTime = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name)
            {
                case "locationName":
                    locationName = readTextField(parser, "locationName");
                    break;
                case "crs":
                    crs = readTextField(parser, "crs");
                    break;
                case "st":
                    scheduledTime = readTextField(parser, "st");
                    break;
                case "et":
                    expectedTime = readTextField(parser, "et");
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new CallingPoint(locationName, crs, scheduledTime, expectedTime);
    }

    private static String readTextField(XmlPullParser parser, final String fieldName)
        throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, fieldName);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, fieldName);
        return title;
    }

    // For the tags title and summary, extracts their text values.
    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
